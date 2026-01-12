package com.syos.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogoutServletTest {

    private LogoutServlet logoutServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @BeforeEach
    public void setUp() {
        logoutServlet = new LogoutServlet();
    }

    @Test
    public void testDoGet_WithValidSession_InvalidatesSessionAndRedirects() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(request.getContextPath()).thenReturn("/pos");

        // Act
        logoutServlet.doGet(request, response);

        // Assert
        verify(session).invalidate();
        verify(response).sendRedirect("/pos/login");
    }

    @Test
    public void testDoGet_WithoutSession_RedirectsToLogin() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/pos");

        // Act
        logoutServlet.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/pos/login");
        verify(session, never()).invalidate();
    }

    @Test
    public void testDoGet_SessionWithNullUsername_InvalidatesSession() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn(null);
        when(request.getContextPath()).thenReturn("/pos");

        // Act
        logoutServlet.doGet(request, response);

        // Assert
        verify(session).invalidate();
        verify(response).sendRedirect("/pos/login");
    }

    @Test
    public void testDoGet_MultipleLogouts_AllInvalidateSessions() throws ServletException, IOException {
        // First logout
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(request.getContextPath()).thenReturn("/pos");

        logoutServlet.doGet(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect("/pos/login");

        // Reset mocks for second logout
        reset(session, response);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("anotheruser");

        logoutServlet.doGet(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect("/pos/login");
    }

    @Test
    public void testDoGet_ClearsAllSessionAttributes() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(request.getContextPath()).thenReturn("/pos");

        // Act
        logoutServlet.doGet(request, response);

        // Assert
        verify(session).invalidate();
    }

    @Test
    public void testDoGet_ContextPathWithMultipleLevels() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(request.getContextPath()).thenReturn("/myapp/pos");

        // Act
        logoutServlet.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/myapp/pos/login");
    }

    @Test
    public void testDoGet_EmptyContextPath() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(request.getContextPath()).thenReturn("");

        // Act
        logoutServlet.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/login");
    }

    @Test
    public void testDoPost_CallsDoGet() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(request.getContextPath()).thenReturn("/pos");

        // Act
        logoutServlet.doPost(request, response);

        // Assert
        verify(session).invalidate();
        verify(response).sendRedirect("/pos/login");
    }

    @Test
    public void testDoPost_WithoutSession_RedirectsToLogin() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/pos");

        // Act
        logoutServlet.doPost(request, response);

        // Assert
        verify(response).sendRedirect("/pos/login");
    }

    @Test
    public void testDoGet_WithAdminUser() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("admin");
        when(request.getContextPath()).thenReturn("/pos");

        // Act
        logoutServlet.doGet(request, response);

        // Assert
        verify(session).invalidate();
        verify(response).sendRedirect("/pos/login");
    }

    @Test
    public void testDoGet_WithCashierUser() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("cashier1");
        when(request.getContextPath()).thenReturn("/pos");

        // Act
        logoutServlet.doGet(request, response);

        // Assert
        verify(session).invalidate();
        verify(response).sendRedirect("/pos/login");
    }

    @Test
    public void testDoGet_WithManagerUser() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("manager");
        when(request.getContextPath()).thenReturn("/pos");

        // Act
        logoutServlet.doGet(request, response);

        // Assert
        verify(session).invalidate();
        verify(response).sendRedirect("/pos/login");
    }

    @Test
    public void testDoGet_SessionInvalidateOncePerRequest() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(request.getContextPath()).thenReturn("/pos");

        // Act
        logoutServlet.doGet(request, response);

        // Assert - verify invalidate is called exactly once
        verify(session, times(1)).invalidate();
    }

    @Test
    public void testDoGet_RedirectCalledOncePerRequest() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(request.getContextPath()).thenReturn("/pos");

        // Act
        logoutServlet.doGet(request, response);

        // Assert - verify redirect is called exactly once
        verify(response, times(1)).sendRedirect(anyString());
    }

    @Test
    public void testDoPost_DelegatesCompletelyToDoGet() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(request.getContextPath()).thenReturn("/pos");

        // Act
        logoutServlet.doPost(request, response);

        // Assert - same behavior as doGet
        verify(session).invalidate();
        verify(response).sendRedirect("/pos/login");
    }

    @Test
    public void testDoGet_ExceptionHandling_NoExceptionThrown() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(request.getContextPath()).thenReturn("/pos");

        // Act - should not throw any exception
        try {
            logoutServlet.doGet(request, response);
        } catch (Exception e) {
            // If we get here, test fails
            throw new AssertionError("doGet should not throw exception", e);
        }

        // Assert
        verify(response).sendRedirect("/pos/login");
    }
}
