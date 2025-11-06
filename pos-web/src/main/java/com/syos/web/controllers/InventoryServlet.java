package com.syos.web.controllers;

import com.google.gson.Gson;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Inventory Controller (MVC Pattern)
 * Manages inventory operations.
 *
 * THREAD-SAFE: InventoryService is synchronized for concurrent modifications.
 */
@WebServlet(name = "InventoryServlet", urlPatterns = {"/inventory/*"})
public class InventoryServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(InventoryServlet.class);
    private InventoryService inventoryService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        ServiceFactory serviceFactory = (ServiceFactory) getServletContext().getAttribute("serviceFactory");
        this.inventoryService = serviceFactory.getInventoryService();
        this.gson = new Gson();
        logger.info("InventoryServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            showInventoryList(request, response);
        } else if (pathInfo.equals("/add")) {
            showAddStockForm(request, response);
        } else if (pathInfo.equals("/low-stock")) {
            showLowStockItems(request, response);
        } else if (pathInfo.equals("/expiring")) {
            showExpiringItems(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.equals("/add")) {
            addStock(request, response);
        } else if (pathInfo != null && pathInfo.equals("/move-to-shelf")) {
            moveToShelf(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void showInventoryList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Item> allItems = inventoryService.getAllItems();
            request.setAttribute("items", allItems);
            request.getRequestDispatcher("/WEB-INF/views/inventory/list.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error loading inventory", e);
            request.setAttribute("errorMessage", "Error loading inventory");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    private void showAddStockForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/inventory/add-stock.jsp").forward(request, response);
    }

    private void addStock(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String code = request.getParameter("code");
            String name = request.getParameter("name");
            BigDecimal price = new BigDecimal(request.getParameter("price"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            LocalDate expiryDate = LocalDate.parse(request.getParameter("expiryDate"));

            // Thread-safe synchronized method
            inventoryService.addStock(code, name, price, quantity, expiryDate);

            logger.info("Stock added: {} ({})", name, code);
            request.setAttribute("successMessage", "Stock added successfully");
            showInventoryList(request, response);

        } catch (Exception e) {
            logger.error("Error adding stock", e);
            request.setAttribute("errorMessage", "Error adding stock: " + e.getMessage());
            showAddStockForm(request, response);
        }
    }

    private void moveToShelf(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        Map<String, Object> result = new HashMap<>();

        try {
            String itemCode = request.getParameter("itemCode");
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            // Thread-safe synchronized method
            inventoryService.moveToShelf(itemCode, quantity);

            result.put("success", true);
            result.put("message", "Items moved to shelf");

        } catch (Exception e) {
            logger.error("Error moving items to shelf", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        response.getWriter().write(gson.toJson(result));
    }

    private void showLowStockItems(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Item> lowStockItems = inventoryService.getLowStockItems();
            request.setAttribute("items", lowStockItems);
            request.getRequestDispatcher("/WEB-INF/views/inventory/low-stock.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error loading low stock items", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void showExpiringItems(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Item> expiringItems = inventoryService.getExpiringItems();
            request.setAttribute("items", expiringItems);

            // Calculate days until expiry for each item
            Map<String, Long> daysUntilExpiryMap = new HashMap<>();
            LocalDate today = LocalDate.now();
            for (Item item : expiringItems) {
                if (item.getExpiryDate() != null) {
                    long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS.between(today, item.getExpiryDate());
                    daysUntilExpiryMap.put(item.getCode().getValue(), daysUntilExpiry);
                }
            }
            request.setAttribute("daysUntilExpiryMap", daysUntilExpiryMap);

            request.getRequestDispatcher("/WEB-INF/views/inventory/expiring.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error loading expiring items", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
