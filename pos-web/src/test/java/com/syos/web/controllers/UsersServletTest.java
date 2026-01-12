package com.syos.web.controllers;

import com.syos.application.services.UserService;
import com.syos.domain.entities.User;
import com.syos.domain.valueobjects.UserId;
import com.syos.domain.valueobjects.UserRole;
import com.syos.infrastructure.factories.ServiceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersServletTest {

    private UsersServlet usersServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private ServletContext servletContext;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private UserService userService;

    @Mock
    private ServiceFactory serviceFactory;

    @BeforeEach
    public void setUp() throws ServletException {
        usersServlet = new UsersServlet();

        // Setup servlet context and service factory
        when(servletContext.getAttribute("serviceFactory")).thenReturn(serviceFactory);
        when(serviceFactory.getUserService()).thenReturn(userService);

        // Set servlet context
        usersServlet.init(new MockServletConfig(servletContext));
    }

    @Test
    public void testDoGet_ListUsers_AsAdmin_DisplaysUsersList() throws ServletException, IOException {
        // Arrange
        User user1 = new User(new UserId(1L), "admin", "admin@example.com", "hash", UserRole.ADMIN);
        User user2 = new User(new UserId(2L), "cashier", "cashier@example.com", "hash", UserRole.CASHIER);
        List<User> users = Arrays.asList(user1, user2);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userRole")).thenReturn("ADMIN");
        when(request.getPathInfo()).thenReturn("/");
        when(userService.getAllUsers()).thenReturn(users);
        when(request.getRequestDispatcher("/WEB-INF/views/users/list.jsp")).thenReturn(requestDispatcher);

        // Act
        usersServlet.doGet(request, response);

        // Assert
        verify(userService).getAllUsers();
        verify(request).setAttribute("users", users);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_ListUsers_WithNullPathInfo_DisplaysUsersList() throws ServletException, IOException {
        // Arrange
        User user1 = new User(new UserId(1L), "admin", "admin@example.com", "hash", UserRole.ADMIN);
        List<User> users = Arrays.asList(user1);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userRole")).thenReturn("ADMIN");
        when(request.getPathInfo()).thenReturn(null);
        when(userService.getAllUsers()).thenReturn(users);
        when(request.getRequestDispatcher("/WEB-INF/views/users/list.jsp")).thenReturn(requestDispatcher);

        // Act
        usersServlet.doGet(request, response);

        // Assert
        verify(userService).getAllUsers();
        verify(request).setAttribute("users", users);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_RegisterForm_AsAdmin_ShowsRegisterForm() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userRole")).thenReturn("ADMIN");
        when(request.getPathInfo()).thenReturn("/register");
        when(request.getRequestDispatcher("/WEB-INF/views/users/register.jsp")).thenReturn(requestDispatcher);

        // Act
        usersServlet.doGet(request, response);

        // Assert
        verify(requestDispatcher).forward(request, response);
        verify(userService, never()).getAllUsers();
    }

    @Test
    public void testDoGet_AsNonAdmin_ReturnsForbidden() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userRole")).thenReturn("CASHIER");

        // Act
        usersServlet.doGet(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin role required.");
        verify(userService, never()).getAllUsers();
    }

    @Test
    public void testDoGet_WithoutSession_ReturnsForbidden() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(null);

        // Act
        usersServlet.doGet(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin role required.");
        verify(userService, never()).getAllUsers();
    }

    @Test
    public void testDoGet_WithInvalidPath_ReturnsNotFound() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userRole")).thenReturn("ADMIN");
        when(request.getPathInfo()).thenReturn("/invalid");

        // Act
        usersServlet.doGet(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testDoGet_WithServiceException_ForwardsToErrorPage() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userRole")).thenReturn("ADMIN");
        when(request.getPathInfo()).thenReturn("/");
        when(userService.getAllUsers()).thenThrow(new RuntimeException("Database error"));
        when(request.getRequestDispatcher("/WEB-INF/views/error.jsp")).thenReturn(requestDispatcher);

        // Act
        usersServlet.doGet(request, response);

        // Assert
        verify(request).setAttribute("errorMessage", "Error loading users");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_RegisterUser_AsAdmin_CreatesUserSuccessfully() throws ServletException, IOException {
        // Arrange
        User newUser = new User(new UserId(3L), "newuser", "new@example.com", "hash", UserRole.CASHIER);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userRole")).thenReturn("ADMIN");
        when(request.getPathInfo()).thenReturn("/register");
        when(request.getParameter("username")).thenReturn("newuser");
        when(request.getParameter("email")).thenReturn("new@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("role")).thenReturn("CASHIER");
        when(userService.registerUser("newuser", "new@example.com", "password123", UserRole.CASHIER))
            .thenReturn(newUser);
        when(request.getContextPath()).thenReturn("/pos-web");

        // Act
        usersServlet.doPost(request, response);

        // Assert
        verify(userService).registerUser("newuser", "new@example.com", "password123", UserRole.CASHIER);
        verify(response).sendRedirect("/pos-web/users?success=User registered successfully");
    }

    @Test
    public void testDoPost_RegisterUser_WithAdminRole_CreatesAdminUser() throws ServletException, IOException {
        // Arrange
        User newAdmin = new User(new UserId(4L), "newadmin", "admin@example.com", "hash", UserRole.ADMIN);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userRole")).thenReturn("ADMIN");
        when(request.getPathInfo()).thenReturn("/register");
        when(request.getParameter("username")).thenReturn("newadmin");
        when(request.getParameter("email")).thenReturn("admin@example.com");
        when(request.getParameter("password")).thenReturn("adminpass");
        when(request.getParameter("role")).thenReturn("ADMIN");
        when(userService.registerUser("newadmin", "admin@example.com", "adminpass", UserRole.ADMIN))
            .thenReturn(newAdmin);
        when(request.getContextPath()).thenReturn("/pos-web");

        // Act
        usersServlet.doPost(request, response);

        // Assert
        verify(userService).registerUser("newadmin", "admin@example.com", "adminpass", UserRole.ADMIN);
        verify(response).sendRedirect("/pos-web/users?success=User registered successfully");
    }

    @Test
    public void testDoPost_AsNonAdmin_ReturnsForbidden() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userRole")).thenReturn("MANAGER");

        // Act
        usersServlet.doPost(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin role required.");
        verify(userService, never()).registerUser(anyString(), anyString(), anyString(), any(UserRole.class));
    }

    @Test
    public void testDoPost_WithoutSession_ReturnsForbidden() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(null);

        // Act
        usersServlet.doPost(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin role required.");
        verify(userService, never()).registerUser(anyString(), anyString(), anyString(), any(UserRole.class));
    }

    @Test
    public void testDoPost_WithInvalidPath_ReturnsBadRequest() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userRole")).thenReturn("ADMIN");
        when(request.getPathInfo()).thenReturn("/invalid");

        // Act
        usersServlet.doPost(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_WithNullPathInfo_ReturnsBadRequest() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userRole")).thenReturn("ADMIN");
        when(request.getPathInfo()).thenReturn(null);

        // Act
        usersServlet.doPost(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_WithServiceException_ForwardsToRegisterPageWithError() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userRole")).thenReturn("ADMIN");
        when(request.getPathInfo()).thenReturn("/register");
        when(request.getParameter("username")).thenReturn("newuser");
        when(request.getParameter("email")).thenReturn("new@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("role")).thenReturn("CASHIER");
        when(userService.registerUser("newuser", "new@example.com", "password123", UserRole.CASHIER))
            .thenThrow(new RuntimeException("Username already exists"));
        when(request.getRequestDispatcher("/WEB-INF/views/users/register.jsp")).thenReturn(requestDispatcher);

        // Act
        usersServlet.doPost(request, response);

        // Assert
        verify(request).setAttribute("errorMessage", "Error: Username already exists");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_WithInvalidRole_ForwardsToRegisterPageWithError() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userRole")).thenReturn("ADMIN");
        when(request.getPathInfo()).thenReturn("/register");
        when(request.getParameter("username")).thenReturn("newuser");
        when(request.getParameter("email")).thenReturn("new@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("role")).thenReturn("INVALID_ROLE");
        when(request.getRequestDispatcher("/WEB-INF/views/users/register.jsp")).thenReturn(requestDispatcher);

        // Act
        usersServlet.doPost(request, response);

        // Assert
        verify(request).setAttribute(eq("errorMessage"), anyString());
        verify(requestDispatcher).forward(request, response);
        verify(userService, never()).registerUser(anyString(), anyString(), anyString(), any(UserRole.class));
    }
}
