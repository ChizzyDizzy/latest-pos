package com.syos.web.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Authentication Filter
 * Ensures that users are logged in before accessing protected resources.
 *
 * THREAD-SAFE: This filter is designed to handle concurrent requests.
 * Each request gets its own session, preventing interference between users.
 */
@WebFilter(filterName = "AuthenticationFilter",
           urlPatterns = {"/dashboard/*", "/sales/*", "/inventory/*", "/reports/*"})
public class AuthenticationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthenticationFilter initialized - protecting secured resources");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Get session (don't create if doesn't exist)
        HttpSession session = httpRequest.getSession(false);

        // Check if user is logged in
        boolean isLoggedIn = (session != null) && (session.getAttribute("user") != null);

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        if (isLoggedIn) {
            // User is authenticated, allow request to proceed
            chain.doFilter(request, response);
        } else {
            // User not authenticated, redirect to login
            logger.warn("Unauthorized access attempt to: {} from IP: {}",
                    requestURI, httpRequest.getRemoteAddr());

            // Save the original request URI to redirect after login
            session = httpRequest.getSession(true);
            session.setAttribute("redirectAfterLogin", requestURI);

            httpResponse.sendRedirect(contextPath + "/login");
        }
    }

    @Override
    public void destroy() {
        logger.info("AuthenticationFilter destroyed");
    }
}
