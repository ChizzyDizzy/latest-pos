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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServletTest {

    private LoginServlet loginServlet;

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
        loginServlet = new LoginServlet();

        // Setup servlet context and service factory
        when(servletContext.getAttribute("serviceFactory")).thenReturn(serviceFactory);
        when(serviceFactory.getUserService()).thenReturn(userService);

        // Set servlet context
        loginServlet.init(new MockServletConfig(servletContext));
    }

    @Test
    public void testDoGet_WithValidSession_RedirectsToDashboard() throws ServletException, IOException {
        // Arrange
        User mockUser = new User(
            new UserId(1L),
            "testuser",
            "test@example.com",
            "hashedPassword",
            UserRole.CASHIER
        );

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(mockUser);
        when(request.getContextPath()).thenReturn("/pos");

        // Act
        loginServlet.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/pos/dashboard");
        verify(requestDispatcher, never()).forward(request, response);
    }

    @Test
    public void testDoGet_WithoutSession_ForwardsToLoginPage() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(null);
        when(request.getRequestDispatcher("/WEB-INF/views/login.jsp")).thenReturn(requestDispatcher);

        // Act
        loginServlet.doGet(request, response);

        // Assert
        verify(request).getRequestDispatcher("/WEB-INF/views/login.jsp");
        verify(requestDispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void testDoGet_WithExpiredSession_ForwardsToLoginPage() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        when(request.getRequestDispatcher("/WEB-INF/views/login.jsp")).thenReturn(requestDispatcher);

        // Act
        loginServlet.doGet(request, response);

        // Assert
        verify(request).getRequestDispatcher("/WEB-INF/views/login.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_WithValidCredentials_CreatesSessionAndRedirectsToDashboard() throws ServletException, IOException {
        // Arrange
        User mockUser = new User(
            new UserId(1L),
            "testuser",
            "test@example.com",
            "hashedPassword",
            UserRole.CASHIER
        );

        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("password")).thenReturn("password123");
        when(userService.authenticate("testuser", "password123")).thenReturn(mockUser);
        when(request.getSession(true)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/pos");
        when(session.getAttribute("redirectAfterLogin")).thenReturn(null);

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(userService).authenticate("testuser", "password123");
        verify(session).setAttribute("user", mockUser);
        verify(session).setAttribute("userId", 1L);
        verify(session).setAttribute("username", "testuser");
        verify(session).setAttribute("userRole", "CASHIER");
        verify(session).setMaxInactiveInterval(30 * 60);
        verify(response).sendRedirect("/pos/dashboard");
    }

    @Test
    public void testDoPost_WithValidCredentials_RedirectsToStoredURL() throws ServletException, IOException {
        // Arrange
        User mockUser = new User(
            new UserId(1L),
            "testuser",
            "test@example.com",
            "hashedPassword",
            UserRole.ADMIN
        );

        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("password")).thenReturn("password123");
        when(userService.authenticate("testuser", "password123")).thenReturn(mockUser);
        when(request.getSession(true)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/pos");
        when(session.getAttribute("redirectAfterLogin")).thenReturn("/pos/inventory");

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(session).removeAttribute("redirectAfterLogin");
        verify(response).sendRedirect("/pos/inventory");
    }

    @Test
    public void testDoPost_WithInvalidCredentials_ForwardsToLoginPageWithError() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("password")).thenReturn("wrongpassword");
        when(userService.authenticate("testuser", "wrongpassword")).thenReturn(null);
        when(request.getRequestDispatcher("/WEB-INF/views/login.jsp")).thenReturn(requestDispatcher);

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(userService).authenticate("testuser", "wrongpassword");
        verify(request).setAttribute("errorMessage", "Invalid username or password");
        verify(requestDispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void testDoPost_WithNullUsername_ForwardsToLoginPage() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("username")).thenReturn(null);
        when(request.getParameter("password")).thenReturn("password123");
        when(userService.authenticate(null, "password123")).thenReturn(null);
        when(request.getRequestDispatcher("/WEB-INF/views/login.jsp")).thenReturn(requestDispatcher);

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(request).setAttribute("errorMessage", "Invalid username or password");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_WithServiceException_ForwardsToLoginPageWithErrorMessage() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("password")).thenReturn("password123");
        when(userService.authenticate("testuser", "password123"))
            .thenThrow(new RuntimeException("Database connection failed"));
        when(request.getRequestDispatcher("/WEB-INF/views/login.jsp")).thenReturn(requestDispatcher);

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(request).setAttribute("errorMessage", "An error occurred during login. Please try again.");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_WithDifferentUserRoles_SetsCorrectRole() throws ServletException, IOException {
        // Arrange
        User adminUser = new User(
            new UserId(2L),
            "admin",
            "admin@example.com",
            "hashedPassword",
            UserRole.ADMIN
        );

        when(request.getParameter("username")).thenReturn("admin");
        when(request.getParameter("password")).thenReturn("adminpass");
        when(userService.authenticate("admin", "adminpass")).thenReturn(adminUser);
        when(request.getSession(true)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/pos");
        when(session.getAttribute("redirectAfterLogin")).thenReturn(null);

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(session).setAttribute("userRole", "ADMIN");
    }

    @Test
    public void testDoPost_SessionTimeout_SetsMaxInactiveInterval() throws ServletException, IOException {
        // Arrange
        User mockUser = new User(
            new UserId(1L),
            "testuser",
            "test@example.com",
            "hashedPassword",
            UserRole.CASHIER
        );

        when(request.getParameter("username")).thenReturn("testuser");
        when(request.getParameter("password")).thenReturn("password123");
        when(userService.authenticate("testuser", "password123")).thenReturn(mockUser);
        when(request.getSession(true)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/pos");
        when(session.getAttribute("redirectAfterLogin")).thenReturn(null);

        // Act
        loginServlet.doPost(request, response);

        // Assert
        verify(session).setMaxInactiveInterval(30 * 60);
    }
}
