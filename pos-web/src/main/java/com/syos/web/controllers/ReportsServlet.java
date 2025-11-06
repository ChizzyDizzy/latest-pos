package com.syos.web.controllers;

import com.syos.application.services.ReportService;
import com.syos.application.services.SalesService;
import com.syos.infrastructure.factories.ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Reports Controller (MVC Pattern)
 * Generates various business reports.
 *
 * THREAD-SAFE: Read-only operations on thread-safe services.
 */
@WebServlet(name = "ReportsServlet", urlPatterns = {"/reports/*"})
public class ReportsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ReportsServlet.class);
    private ReportService reportService;
    private SalesService salesService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServiceFactory serviceFactory = (ServiceFactory) getServletContext().getAttribute("serviceFactory");
        this.reportService = serviceFactory.getReportService();
        this.salesService = serviceFactory.getSalesService();
        logger.info("ReportsServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            showReportsMenu(request, response);
        } else if (pathInfo.equals("/daily-sales")) {
            showDailySalesReport(request, response);
        } else if (pathInfo.equals("/stock")) {
            showStockReport(request, response);
        } else if (pathInfo.equals("/reorder")) {
            showReorderReport(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showReportsMenu(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/reports/menu.jsp").forward(request, response);
    }

    private void showDailySalesReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String dateParam = request.getParameter("date");
            LocalDate date = (dateParam != null) ? LocalDate.parse(dateParam) : LocalDate.now();

            var report = reportService.generateDailySalesReport(date);
            request.setAttribute("report", report);
            request.setAttribute("reportDate", date);
            request.getRequestDispatcher("/WEB-INF/views/reports/daily-sales.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error generating daily sales report", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void showStockReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            var report = reportService.generateStockReport();
            request.setAttribute("report", report);
            request.getRequestDispatcher("/WEB-INF/views/reports/stock.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error generating stock report", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void showReorderReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            var report = reportService.generateReorderReport();
            request.setAttribute("report", report);
            request.getRequestDispatcher("/WEB-INF/views/reports/reorder.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error generating reorder report", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
