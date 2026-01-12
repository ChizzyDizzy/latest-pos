package com.syos.web.controllers;

import com.syos.application.services.ReportService;
import com.syos.application.services.SalesService;
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
import java.io.IOException;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportsServletTest {

    private ReportsServlet reportsServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletContext servletContext;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private ReportService reportService;

    @Mock
    private SalesService salesService;

    @Mock
    private ServiceFactory serviceFactory;

    @Mock
    private Object mockReport;

    @BeforeEach
    public void setUp() throws ServletException {
        reportsServlet = new ReportsServlet();

        // Setup servlet context and service factory
        when(servletContext.getAttribute("serviceFactory")).thenReturn(serviceFactory);
        when(serviceFactory.getReportService()).thenReturn(reportService);
        when(serviceFactory.getSalesService()).thenReturn(salesService);

        // Setup servlet config
        reportsServlet.init(new MockServletConfig(servletContext));
    }

    // ===== GET Methods =====

    @Test
    public void testDoGet_RootPath_ShowsReportsMenu() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/");
        when(request.getRequestDispatcher("/WEB-INF/views/reports/menu.jsp")).thenReturn(requestDispatcher);

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(request).getRequestDispatcher("/WEB-INF/views/reports/menu.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_NullPath_ShowsReportsMenu() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn(null);
        when(request.getRequestDispatcher("/WEB-INF/views/reports/menu.jsp")).thenReturn(requestDispatcher);

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(request).getRequestDispatcher("/WEB-INF/views/reports/menu.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_DailySalesPath_WithoutDateParameter_UsesToday() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/daily-sales");
        when(request.getParameter("date")).thenReturn(null);
        when(reportService.generateDailySalesReport(LocalDate.now())).thenReturn(mockReport);
        when(request.getRequestDispatcher("/WEB-INF/views/reports/daily-sales.jsp")).thenReturn(requestDispatcher);

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(reportService).generateDailySalesReport(any(LocalDate.class));
        verify(request).setAttribute(eq("report"), eq(mockReport));
        verify(request).setAttribute(eq("reportDate"), any(LocalDate.class));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_DailySalesPath_WithDateParameter() throws ServletException, IOException {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 1, 15);
        when(request.getPathInfo()).thenReturn("/daily-sales");
        when(request.getParameter("date")).thenReturn("2025-01-15");
        when(reportService.generateDailySalesReport(testDate)).thenReturn(mockReport);
        when(request.getRequestDispatcher("/WEB-INF/views/reports/daily-sales.jsp")).thenReturn(requestDispatcher);

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(reportService).generateDailySalesReport(testDate);
        verify(request).setAttribute("report", mockReport);
        verify(request).setAttribute("reportDate", testDate);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_DailySalesPath_ServiceException_SendsInternalServerError() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/daily-sales");
        when(request.getParameter("date")).thenReturn(null);
        when(reportService.generateDailySalesReport(any(LocalDate.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testDoGet_StockReportPath_GeneratesStockReport() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/stock");
        when(reportService.generateStockReport()).thenReturn(mockReport);
        when(request.getRequestDispatcher("/WEB-INF/views/reports/stock.jsp")).thenReturn(requestDispatcher);

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(reportService).generateStockReport();
        verify(request).setAttribute("report", mockReport);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_StockReportPath_ServiceException_SendsInternalServerError() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/stock");
        when(reportService.generateStockReport()).thenThrow(new RuntimeException("Service error"));

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testDoGet_ReorderReportPath_GeneratesReorderReport() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/reorder");
        when(reportService.generateReorderReport()).thenReturn(mockReport);
        when(request.getRequestDispatcher("/WEB-INF/views/reports/reorder.jsp")).thenReturn(requestDispatcher);

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(reportService).generateReorderReport();
        verify(request).setAttribute("report", mockReport);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_ReorderReportPath_ServiceException_SendsInternalServerError() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/reorder");
        when(reportService.generateReorderReport()).thenThrow(new RuntimeException("Service error"));

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testDoGet_InvalidPath_Returns404() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/invalid");

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testDoGet_DailySalesPath_InvalidDateFormat_ThrowsException() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/daily-sales");
        when(request.getParameter("date")).thenReturn("invalid-date");
        when(request.getRequestDispatcher("/WEB-INF/views/reports/daily-sales.jsp")).thenReturn(requestDispatcher);

        // Act & Assert - will throw DateTimeParseException which should be caught
        try {
            reportsServlet.doGet(request, response);
            // If we reach here, either the exception was caught or the test setup is wrong
            verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // Exception might be thrown if not caught by servlet
            verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Test
    public void testDoGet_DailySalesReport_MultipleTimes() throws ServletException, IOException {
        // Arrange
        LocalDate date1 = LocalDate.of(2025, 1, 10);
        LocalDate date2 = LocalDate.of(2025, 1, 15);

        when(request.getPathInfo()).thenReturn("/daily-sales");
        when(reportService.generateDailySalesReport(any())).thenReturn(mockReport);
        when(request.getRequestDispatcher("/WEB-INF/views/reports/daily-sales.jsp")).thenReturn(requestDispatcher);

        // First call
        when(request.getParameter("date")).thenReturn("2025-01-10");
        reportsServlet.doGet(request, response);

        // Second call
        when(request.getParameter("date")).thenReturn("2025-01-15");
        reportsServlet.doGet(request, response);

        // Assert
        verify(reportService, times(2)).generateDailySalesReport(any());
    }

    @Test
    public void testDoGet_AllReportTypesAvailable() throws ServletException, IOException {
        // Arrange
        when(reportService.generateDailySalesReport(any())).thenReturn(mockReport);
        when(reportService.generateStockReport()).thenReturn(mockReport);
        when(reportService.generateReorderReport()).thenReturn(mockReport);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        // Act - Daily Sales
        when(request.getPathInfo()).thenReturn("/daily-sales");
        when(request.getParameter("date")).thenReturn(null);
        reportsServlet.doGet(request, response);

        // Act - Stock
        when(request.getPathInfo()).thenReturn("/stock");
        reportsServlet.doGet(request, response);

        // Act - Reorder
        when(request.getPathInfo()).thenReturn("/reorder");
        reportsServlet.doGet(request, response);

        // Assert
        verify(reportService).generateDailySalesReport(any());
        verify(reportService).generateStockReport();
        verify(reportService).generateReorderReport();
    }

    @Test
    public void testDoGet_DailySalesReport_EndOfMonth() throws ServletException, IOException {
        // Arrange
        LocalDate endOfMonth = LocalDate.of(2025, 1, 31);
        when(request.getPathInfo()).thenReturn("/daily-sales");
        when(request.getParameter("date")).thenReturn("2025-01-31");
        when(reportService.generateDailySalesReport(endOfMonth)).thenReturn(mockReport);
        when(request.getRequestDispatcher("/WEB-INF/views/reports/daily-sales.jsp")).thenReturn(requestDispatcher);

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(reportService).generateDailySalesReport(endOfMonth);
        verify(request).setAttribute("reportDate", endOfMonth);
    }

    @Test
    public void testDoGet_DailySalesReport_BeginningOfMonth() throws ServletException, IOException {
        // Arrange
        LocalDate beginningOfMonth = LocalDate.of(2025, 1, 1);
        when(request.getPathInfo()).thenReturn("/daily-sales");
        when(request.getParameter("date")).thenReturn("2025-01-01");
        when(reportService.generateDailySalesReport(beginningOfMonth)).thenReturn(mockReport);
        when(request.getRequestDispatcher("/WEB-INF/views/reports/daily-sales.jsp")).thenReturn(requestDispatcher);

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(reportService).generateDailySalesReport(beginningOfMonth);
        verify(request).setAttribute("reportDate", beginningOfMonth);
    }

    @Test
    public void testDoGet_ReportsMenu_NeverCallsReportService() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/");
        when(request.getRequestDispatcher("/WEB-INF/views/reports/menu.jsp")).thenReturn(requestDispatcher);

        // Act
        reportsServlet.doGet(request, response);

        // Assert
        verify(reportService, never()).generateDailySalesReport(any());
        verify(reportService, never()).generateStockReport();
        verify(reportService, never()).generateReorderReport();
    }
}
