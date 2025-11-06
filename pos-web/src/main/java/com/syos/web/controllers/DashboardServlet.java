package com.syos.web.controllers;

import com.syos.application.services.SalesService;
import com.syos.application.services.InventoryService;
import com.syos.domain.entities.Bill;
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
import java.util.List;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DashboardServlet.class);
    private SalesService salesService;
    private InventoryService inventoryService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServiceFactory serviceFactory = (ServiceFactory) getServletContext().getAttribute("serviceFactory");
        this.salesService = serviceFactory.getSalesService();
        this.inventoryService = serviceFactory.getInventoryService();
        logger.info("DashboardServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Fetch dashboard metrics
            List<Bill> todaysBills = salesService.getBillsForToday();
            List<Item> lowStockItems = inventoryService.getLowStockItems();
            List<Item> expiringItems = inventoryService.getExpiringItems();

            // Calculate today's revenue
            BigDecimal todaysRevenue = todaysBills.stream()
                    .map(bill -> bill.getTotalAmount().getValue())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Set attributes for JSP view
            request.setAttribute("todaysBills", todaysBills);
            request.setAttribute("todaysRevenue", todaysRevenue);
            request.setAttribute("todaysTransactionCount", todaysBills.size());
            request.setAttribute("lowStockItems", lowStockItems);
            request.setAttribute("expiringItems", expiringItems);

            // Forward to dashboard view
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading dashboard", e);
            request.setAttribute("errorMessage", "Error loading dashboard data");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }
}