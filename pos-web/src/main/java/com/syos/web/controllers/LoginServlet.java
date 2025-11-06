package com.syos.web.controllers;

import com.syos.application.services.UserService;
import com.syos.domain.entities.User;
import com.syos.infrastructure.factories.ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServiceFactory serviceFactory = (ServiceFactory) getServletContext().getAttribute("serviceFactory");
        this.userService = serviceFactory.getUserService();
        logger.info("LoginServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        logger.info("Login attempt for user: {}", username);

        try {
            // Authenticate user with password verification
            User user = userService.authenticate(username, password);

            if (user != null) {
                // Create new session for authenticated user
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId().getValue());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("userRole", user.getRole().name());
                session.setMaxInactiveInterval(30 * 60);

                logger.info("User {} logged in successfully with role {}", username, user.getRole());

                String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
                if (redirectUrl != null && !redirectUrl.isEmpty()) {
                    session.removeAttribute("redirectAfterLogin");
                    response.sendRedirect(redirectUrl);
                } else {
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                }

            } else {
                logger.warn("Failed login attempt for user: {}", username);
                request.setAttribute("errorMessage", "Invalid username or password");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            logger.error("Error during login for user: " + username, e);
            request.setAttribute("errorMessage", "An error occurred during login. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}