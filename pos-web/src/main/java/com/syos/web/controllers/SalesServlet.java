package com.syos.web.controllers;

import com.google.gson.Gson;
import com.syos.application.services.SalesService;
import com.syos.domain.entities.Bill;
import com.syos.domain.entities.Item;
import com.syos.domain.exceptions.InsufficientStockException;
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
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sales Controller (MVC Pattern)
 * Handles all sales-related operations: creating sales, viewing bills, etc.
 *
 * THREAD-SAFE: Designed for concurrent access. Each user's sale is stored
 * in their individual HTTP session, preventing interference between users.
 * The SalesService is thread-safe for saving bills concurrently.
 */
@WebServlet(name = "SalesServlet", urlPatterns = {"/sales/*"})
public class SalesServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SalesServlet.class);
    private SalesService salesService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        ServiceFactory serviceFactory = (ServiceFactory) getServletContext().getAttribute("serviceFactory");
        this.salesService = serviceFactory.getSalesService();
        this.gson = new Gson();
        logger.info("SalesServlet initialized with thread-safe services");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // Show sales page
            showSalesPage(request, response);
        } else if (pathInfo.equals("/new")) {
            // Show new sale form
            showNewSaleForm(request, response);
        } else if (pathInfo.equals("/list")) {
            // Show list of bills
            showBillsList(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            // View specific bill
            viewBill(request, response, pathInfo);
        } else if (pathInfo.equals("/available-items")) {
            // API endpoint: Get available items (JSON)
            getAvailableItems(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } else if (pathInfo.equals("/add-item")) {
            // Add item to current sale (AJAX)
            addItemToSale(request, response);
        } else if (pathInfo.equals("/remove-item")) {
            // Remove item from current sale (AJAX)
            removeItemFromSale(request, response);
        } else if (pathInfo.equals("/complete")) {
            // Complete sale and save
            completeSale(request, response);
        } else if (pathInfo.equals("/clear")) {
            // Clear current sale
            clearSale(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showSalesPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect to new sale by default
        response.sendRedirect(request.getContextPath() + "/sales/new");
    }

    private void showNewSaleForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Get or create sale builder in session (isolates each user's sale)
            HttpSession session = request.getSession();
            SalesService.SaleBuilder saleBuilder = (SalesService.SaleBuilder) session.getAttribute("currentSale");

            if (saleBuilder == null) {
                saleBuilder = salesService.startNewSale();
                session.setAttribute("currentSale", saleBuilder);
            }

            // Get available items for sale
            List<Item> availableItems = salesService.getAvailableItems();
            request.setAttribute("availableItems", availableItems);
            request.setAttribute("currentSale", saleBuilder);

            request.getRequestDispatcher("/WEB-INF/views/sales/new-sale.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error showing new sale form", e);
            request.setAttribute("errorMessage", "Error loading sale form");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    private void addItemToSale(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            String itemCode = request.getParameter("itemCode");
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            // Get sale builder from session (user-specific)
            HttpSession session = request.getSession();
            SalesService.SaleBuilder saleBuilder = (SalesService.SaleBuilder) session.getAttribute("currentSale");

            if (saleBuilder == null) {
                saleBuilder = salesService.startNewSale();
                session.setAttribute("currentSale", saleBuilder);
            }

            // Add item (thread-safe operation per user)
            saleBuilder.addItem(itemCode, quantity);

            result.put("success", true);
            result.put("message", "Item added to sale");
            result.put("subtotal", saleBuilder.getSubtotal().getValue().toString());

        } catch (InsufficientStockException e) {
            logger.warn("Insufficient stock: {}", e.getMessage());
            result.put("success", false);
            result.put("message", e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding item to sale", e);
            result.put("success", false);
            result.put("message", "Error adding item: " + e.getMessage());
        }

        response.getWriter().write(gson.toJson(result));
    }

    private void removeItemFromSale(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            String itemCode = request.getParameter("itemCode");

            HttpSession session = request.getSession();
            SalesService.SaleBuilder saleBuilder = (SalesService.SaleBuilder) session.getAttribute("currentSale");

            if (saleBuilder != null) {
                saleBuilder.removeItem(itemCode);
                result.put("success", true);
                result.put("message", "Item removed");
                result.put("subtotal", saleBuilder.getSubtotal().getValue().toString());
            } else {
                result.put("success", false);
                result.put("message", "No active sale");
            }

        } catch (Exception e) {
            logger.error("Error removing item from sale", e);
            result.put("success", false);
            result.put("message", "Error removing item");
        }

        response.getWriter().write(gson.toJson(result));
    }

    private void completeSale(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            BigDecimal cashTendered = new BigDecimal(request.getParameter("cashTendered"));

            HttpSession session = request.getSession();
            SalesService.SaleBuilder saleBuilder = (SalesService.SaleBuilder) session.getAttribute("currentSale");

            if (saleBuilder == null || saleBuilder.getItems().isEmpty()) {
                request.setAttribute("errorMessage", "No items in sale");
                showNewSaleForm(request, response);
                return;
            }

            // Complete sale and save (THREAD-SAFE operation with double-check locking)
            Bill bill = saleBuilder.completeSale(cashTendered);
            salesService.saveBill(bill); // Synchronized method prevents race conditions

            // Clear sale from session
            session.removeAttribute("currentSale");

            logger.info("Sale completed: Bill #{} by user: {}",
                    bill.getBillNumber(), session.getAttribute("username"));

            // Redirect to bill view
            response.sendRedirect(request.getContextPath() + "/sales/view/" + bill.getBillNumber());

        } catch (InsufficientStockException e) {
            logger.warn("Sale failed due to insufficient stock: {}", e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            showNewSaleForm(request, response);
        } catch (Exception e) {
            logger.error("Error completing sale", e);
            request.setAttribute("errorMessage", "Error completing sale: " + e.getMessage());
            showNewSaleForm(request, response);
        }
    }

    private void clearSale(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        session.removeAttribute("currentSale");
        response.sendRedirect(request.getContextPath() + "/sales/new");
    }

    private void showBillsList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Bill> bills = salesService.getBillsForToday();
            request.setAttribute("bills", bills);
            request.getRequestDispatcher("/WEB-INF/views/sales/bills-list.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading bills list", e);
            request.setAttribute("errorMessage", "Error loading bills");
            request.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(request, response);
        }
    }

    private void viewBill(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws ServletException, IOException {
        try {
            // Extract bill number (handles both "BILL-123456" and "123456" formats)
            String billNumberStr = pathInfo.substring("/view/".length());

            // Remove "BILL-" prefix if present
            if (billNumberStr.startsWith("BILL-")) {
                billNumberStr = billNumberStr.substring(5);
            }

            int billNumber = Integer.parseInt(billNumberStr);

            Bill bill = salesService.getBillByNumber(billNumber);

            if (bill != null) {
                request.setAttribute("bill", bill);
                request.getRequestDispatcher("/WEB-INF/views/sales/view-bill.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Bill not found");
            }

        } catch (NumberFormatException e) {
            logger.error("Invalid bill number format: {}", pathInfo, e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid bill number");
        } catch (Exception e) {
            logger.error("Error viewing bill", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Helper method to convert LocalDateTime to java.util.Date for JSP compatibility
     * JSP's fmt:formatDate doesn't support Java 8 LocalDateTime
     */
    private Date toDate(java.time.LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private void getAvailableItems(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Item> items = salesService.getAvailableItems();
            response.getWriter().write(gson.toJson(items));

        } catch (Exception e) {
            logger.error("Error fetching available items", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
