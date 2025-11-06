package com.syos.web.controllers;

import com.syos.application.services.InventoryService;
import com.syos.domain.entities.Item;
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
import java.util.stream.Collectors;

/**
 * Products Controller - Public view for customers
 * Shows available items (ON_SHELF status only)
 */
@WebServlet(name = "ProductsServlet", urlPatterns = {"/products"})
public class ProductsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ProductsServlet.class);
    private InventoryService inventoryService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServiceFactory serviceFactory = (ServiceFactory) getServletContext().getAttribute("serviceFactory");
        this.inventoryService = serviceFactory.getInventoryService();
        logger.info("ProductsServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Check if user is logged in
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Get all items and filter for ON_SHELF items only
            List<Item> allItems = inventoryService.getAllItems();
            List<Item> availableItems = allItems.stream()
                    .filter(item -> "ON_SHELF".equals(item.getState().getStateName()))
                    .filter(item -> !item.isExpired())
                    .filter(item -> item.getQuantity().getValue() > 0)
                    .collect(Collectors.toList());

            request.setAttribute("items", availableItems);
            request.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading products", e);
            request.setAttribute("errorMessage", "Error loading products");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
