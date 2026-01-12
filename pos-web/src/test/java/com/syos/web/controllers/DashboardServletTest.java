package com.syos.web.controllers;

import com.syos.application.services.InventoryService;
import com.syos.application.services.SalesService;
import com.syos.domain.entities.Bill;
import com.syos.domain.entities.Item;
import com.syos.domain.valueobjects.*;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DashboardServletTest {

    private DashboardServlet dashboardServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletContext servletContext;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private SalesService salesService;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private ServiceFactory serviceFactory;

    @BeforeEach
    public void setUp() throws ServletException {
        dashboardServlet = new DashboardServlet();

        // Setup servlet context and service factory
        when(servletContext.getAttribute("serviceFactory")).thenReturn(serviceFactory);
        when(serviceFactory.getSalesService()).thenReturn(salesService);
        when(serviceFactory.getInventoryService()).thenReturn(inventoryService);

        // Set servlet context
        dashboardServlet.init(new MockServletConfig(servletContext));
    }

    @Test
    public void testDoGet_LoadsDashboardDataSuccessfully() throws ServletException, IOException {
        // Arrange
        List<Bill> todaysBills = createMockBills(3);
        List<Item> lowStockItems = createMockItems(2);
        List<Item> expiringItems = createMockItems(1);

        when(salesService.getBillsForToday()).thenReturn(todaysBills);
        when(inventoryService.getLowStockItems()).thenReturn(lowStockItems);
        when(inventoryService.getExpiringItems()).thenReturn(expiringItems);
        when(request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp")).thenReturn(requestDispatcher);

        // Act
        dashboardServlet.doGet(request, response);

        // Assert
        verify(salesService).getBillsForToday();
        verify(inventoryService).getLowStockItems();
        verify(inventoryService).getExpiringItems();
        verify(request).setAttribute(eq("todaysBills"), eq(todaysBills));
        verify(request).setAttribute(eq("todaysTransactionCount"), eq(3));
        verify(request).setAttribute(eq("lowStockItems"), eq(lowStockItems));
        verify(request).setAttribute(eq("expiringItems"), eq(expiringItems));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_CalculatesTodaysRevenue() throws ServletException, IOException {
        // Arrange
        List<Bill> todaysBills = new ArrayList<>();
        todaysBills.add(createMockBillWithAmount("100.00"));
        todaysBills.add(createMockBillWithAmount("250.50"));
        todaysBills.add(createMockBillWithAmount("149.50"));

        when(salesService.getBillsForToday()).thenReturn(todaysBills);
        when(inventoryService.getLowStockItems()).thenReturn(new ArrayList<>());
        when(inventoryService.getExpiringItems()).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp")).thenReturn(requestDispatcher);

        // Act
        dashboardServlet.doGet(request, response);

        // Assert
        verify(request).setAttribute(
            eq("todaysRevenue"),
            argThat(bd -> bd instanceof BigDecimal && bd.equals(new BigDecimal("500.00")))
        );
    }

    @Test
    public void testDoGet_WithEmptyData_LoadsEmptyDashboard() throws ServletException, IOException {
        // Arrange
        when(salesService.getBillsForToday()).thenReturn(new ArrayList<>());
        when(inventoryService.getLowStockItems()).thenReturn(new ArrayList<>());
        when(inventoryService.getExpiringItems()).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp")).thenReturn(requestDispatcher);

        // Act
        dashboardServlet.doGet(request, response);

        // Assert
        verify(request).setAttribute(eq("todaysBills"), eq(new ArrayList<>()));
        verify(request).setAttribute(eq("todaysTransactionCount"), eq(0));
        verify(request).setAttribute(
            eq("todaysRevenue"),
            argThat(bd -> bd instanceof BigDecimal && bd.equals(BigDecimal.ZERO))
        );
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_WithServiceException_ForwardsToErrorPage() throws ServletException, IOException {
        // Arrange
        when(salesService.getBillsForToday()).thenThrow(new RuntimeException("Database error"));
        when(request.getRequestDispatcher("/WEB-INF/views/error/error.jsp")).thenReturn(requestDispatcher);

        // Act
        dashboardServlet.doGet(request, response);

        // Assert
        verify(request).setAttribute("errorMessage", "Error loading dashboard data");
        verify(request).getRequestDispatcher("/WEB-INF/views/error/error.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_WithMultipleLowStockItems_DisplaysAllItems() throws ServletException, IOException {
        // Arrange
        List<Bill> bills = new ArrayList<>();
        List<Item> lowStockItems = createMockItems(5);
        List<Item> expiringItems = new ArrayList<>();

        when(salesService.getBillsForToday()).thenReturn(bills);
        when(inventoryService.getLowStockItems()).thenReturn(lowStockItems);
        when(inventoryService.getExpiringItems()).thenReturn(expiringItems);
        when(request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp")).thenReturn(requestDispatcher);

        // Act
        dashboardServlet.doGet(request, response);

        // Assert
        verify(request).setAttribute(eq("lowStockItems"), argThat(items -> items instanceof List && ((List<?>) items).size() == 5));
    }

    @Test
    public void testDoGet_WithMultipleExpiringItems_DisplaysAllItems() throws ServletException, IOException {
        // Arrange
        List<Bill> bills = new ArrayList<>();
        List<Item> lowStockItems = new ArrayList<>();
        List<Item> expiringItems = createMockItems(3);

        when(salesService.getBillsForToday()).thenReturn(bills);
        when(inventoryService.getLowStockItems()).thenReturn(lowStockItems);
        when(inventoryService.getExpiringItems()).thenReturn(expiringItems);
        when(request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp")).thenReturn(requestDispatcher);

        // Act
        dashboardServlet.doGet(request, response);

        // Assert
        verify(request).setAttribute(eq("expiringItems"), argThat(items -> items instanceof List && ((List<?>) items).size() == 3));
    }

    @Test
    public void testDoGet_InventoryServiceException_ForwardsToErrorPage() throws ServletException, IOException {
        // Arrange
        when(salesService.getBillsForToday()).thenReturn(new ArrayList<>());
        when(inventoryService.getLowStockItems()).thenThrow(new RuntimeException("Inventory error"));
        when(request.getRequestDispatcher("/WEB-INF/views/error/error.jsp")).thenReturn(requestDispatcher);

        // Act
        dashboardServlet.doGet(request, response);

        // Assert
        verify(request).setAttribute("errorMessage", "Error loading dashboard data");
        verify(requestDispatcher).forward(request, response);
    }

    // Helper methods
    private List<Bill> createMockBills(int count) {
        List<Bill> bills = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            bills.add(createMockBillWithAmount("100.00"));
        }
        return bills;
    }

    private Bill createMockBillWithAmount(String amount) {
        BillNumber billNumber = new BillNumber(1);
        BillDate billDate = new BillDate(LocalDateTime.now());
        BillTotal totalAmount = new BillTotal(new BigDecimal(amount));
        Bill bill = new Bill(billNumber, billDate, totalAmount);
        return bill;
    }

    private List<Item> createMockItems(int count) {
        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            ItemCode code = new ItemCode("ITEM-00" + i);
            ItemName name = new ItemName("Item " + i);
            ItemPrice price = new ItemPrice(new BigDecimal("10.00"));
            ItemQuantity quantity = new ItemQuantity(10 - i);
            LocalDate expiryDate = LocalDate.now().plusDays(i);

            Item item = new Item(code, name, price, quantity, expiryDate);
            items.add(item);
        }
        return items;
    }
}
