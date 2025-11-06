package com.syos.web.controllers;

import com.syos.application.services.UserService;
import com.syos.domain.entities.User;
import com.syos.domain.valueobjects.UserRole;
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
import java.util.List;

@WebServlet(name = "UsersServlet", urlPatterns = {"/users", "/users/*"})
public class UsersServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UsersServlet.class);
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServiceFactory serviceFactory = (ServiceFactory) getServletContext().getAttribute("serviceFactory");
        this.userService = serviceFactory.getUserService();
        logger.info("UsersServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user is ADMIN
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("userRole"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin role required.");
            return;
        }

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Show users list
                showUsersList(request, response);
            } else if (pathInfo.equals("/register")) {
                // Show register user form
                request.getRequestDispatcher("/WEB-INF/views/users/register.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in UsersServlet", e);
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user is ADMIN
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("userRole"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin role required.");
            return;
        }

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo != null && pathInfo.equals("/register")) {
                registerUser(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error processing user registration", e);
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/users/register.jsp").forward(request, response);
        }
    }

    private void showUsersList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<User> users = userService.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/WEB-INF/views/users/list.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error loading users list", e);
            request.setAttribute("errorMessage", "Error loading users");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void registerUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String roleStr = request.getParameter("role");

            UserRole role = UserRole.valueOf(roleStr);

            User newUser = userService.registerUser(username, email, password, role);

            logger.info("User registered successfully: {} ({})", username, role);

            response.sendRedirect(request.getContextPath() + "/users?success=User registered successfully");

        } catch (Exception e) {
            logger.error("Error registering user", e);
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/users/register.jsp").forward(request, response);
        }
    }
}
