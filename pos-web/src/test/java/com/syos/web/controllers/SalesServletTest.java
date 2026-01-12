package com.syos.web.controllers;

import com.google.gson.Gson;
import com.syos.application.services.SalesService;
import com.syos.domain.entities.Bill;
import com.syos.domain.entities.Item;
import com.syos.domain.exceptions.InsufficientStockException;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SalesServletTest {

    private SalesServlet salesServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private ServletContext servletContext;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private SalesService salesService;

    @Mock
    private SalesService.SaleBuilder saleBuilder;

    @Mock
    private ServiceFactory serviceFactory;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    public void setUp() throws ServletException, IOException {
        salesServlet = new SalesServlet();

        // Setup servlet context and service factory
        when(servletContext.getAttribute("serviceFactory")).thenReturn(serviceFactory);
        when(serviceFactory.getSalesService()).thenReturn(salesService);

        // Setup servlet config
        salesServlet.init(new MockServletConfig(servletContext));

        // Setup response writer
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    // ===== GET Methods =====

    @Test
    public void testDoGet_RootPath_RedirectsToNewSale() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/");
        when(request.getContextPath()).thenReturn("/pos");

        // Act
        salesServlet.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/pos/sales/new");
    }

    @Test
    public void testDoGet_NewPath_ShowsNewSaleForm() throws ServletException, IOException {
        // Arrange
        List<Item> availableItems = createMockItems(3);

        when(request.getPathInfo()).thenReturn("/new");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentSale")).thenReturn(null);
        when(salesService.startNewSale()).thenReturn(saleBuilder);
        when(salesService.getAvailableItems()).thenReturn(availableItems);
        when(request.getRequestDispatcher("/WEB-INF/views/sales/new-sale.jsp")).thenReturn(requestDispatcher);

        // Act
        salesServlet.doGet(request, response);

        // Assert
        verify(salesService).startNewSale();
        verify(session).setAttribute("currentSale", saleBuilder);
        verify(salesService).getAvailableItems();
        verify(request).setAttribute(eq("availableItems"), eq(availableItems));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_NewPath_UseExistingSaleBuilder() throws ServletException, IOException {
        // Arrange
        List<Item> availableItems = createMockItems(2);

        when(request.getPathInfo()).thenReturn("/new");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentSale")).thenReturn(saleBuilder);
        when(salesService.getAvailableItems()).thenReturn(availableItems);
        when(request.getRequestDispatcher("/WEB-INF/views/sales/new-sale.jsp")).thenReturn(requestDispatcher);

        // Act
        salesServlet.doGet(request, response);

        // Assert
        verify(salesService, never()).startNewSale();
        verify(request).setAttribute(eq("currentSale"), eq(saleBuilder));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_ListPath_ShowsBillsList() throws ServletException, IOException {
        // Arrange
        List<Bill> bills = createMockBills(2);

        when(request.getPathInfo()).thenReturn("/list");
        when(salesService.getBillsForToday()).thenReturn(bills);
        when(request.getRequestDispatcher("/WEB-INF/views/sales/bills-list.jsp")).thenReturn(requestDispatcher);

        // Act
        salesServlet.doGet(request, response);

        // Assert
        verify(salesService).getBillsForToday();
        verify(request).setAttribute("bills", bills);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_ViewPath_ViewsBill() throws ServletException, IOException {
        // Arrange
        Bill mockBill = createMockBill();

        when(request.getPathInfo()).thenReturn("/view/12345");
        when(salesService.getBillByNumber(12345)).thenReturn(mockBill);
        when(request.getRequestDispatcher("/WEB-INF/views/sales/view-bill.jsp")).thenReturn(requestDispatcher);

        // Act
        salesServlet.doGet(request, response);

        // Assert
        verify(salesService).getBillByNumber(12345);
        verify(request).setAttribute("bill", mockBill);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_ViewPath_BillWithBILLPrefix() throws ServletException, IOException {
        // Arrange
        Bill mockBill = createMockBill();

        when(request.getPathInfo()).thenReturn("/view/BILL-12345");
        when(salesService.getBillByNumber(12345)).thenReturn(mockBill);
        when(request.getRequestDispatcher("/WEB-INF/views/sales/view-bill.jsp")).thenReturn(requestDispatcher);

        // Act
        salesServlet.doGet(request, response);

        // Assert
        verify(salesService).getBillByNumber(12345);
        verify(request).setAttribute("bill", mockBill);
    }

    @Test
    public void testDoGet_ViewPath_BillNotFound() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/view/99999");
        when(salesService.getBillByNumber(99999)).thenReturn(null);

        // Act
        salesServlet.doGet(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Bill not found");
    }

    @Test
    public void testDoGet_ReceiptPath_ViewsReceipt() throws ServletException, IOException {
        // Arrange
        Bill mockBill = createMockBill();

        when(request.getPathInfo()).thenReturn("/receipt/12345");
        when(salesService.getBillByNumber(12345)).thenReturn(mockBill);
        when(request.getRequestDispatcher("/WEB-INF/views/sales/receipt.jsp")).thenReturn(requestDispatcher);

        // Act
        salesServlet.doGet(request, response);

        // Assert
        verify(salesService).getBillByNumber(12345);
        verify(request).setAttribute("bill", mockBill);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_AvailableItemsPath_ReturnsJSON() throws ServletException, IOException {
        // Arrange
        List<Item> items = createMockItems(2);

        when(request.getPathInfo()).thenReturn("/available-items");
        when(salesService.getAvailableItems()).thenReturn(items);

        // Act
        salesServlet.doGet(request, response);

        // Assert
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(salesService).getAvailableItems();
        printWriter.flush();
        assert stringWriter.toString().contains("ITEM");
    }

    @Test
    public void testDoGet_InvalidPath_Returns404() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/invalid");

        // Act
        salesServlet.doGet(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    // ===== POST Methods =====

    @Test
    public void testDoPost_AddItemPath_AddsItemToSale() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/add-item");
        when(request.getParameter("itemCode")).thenReturn("ITEM-001");
        when(request.getParameter("quantity")).thenReturn("2");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentSale")).thenReturn(saleBuilder);
        when(saleBuilder.getSubtotal()).thenReturn(new BillTotal(new BigDecimal("200.00")));

        // Act
        salesServlet.doPost(request, response);

        // Assert
        verify(response).setContentType("application/json");
        verify(saleBuilder).addItem("ITEM-001", 2);
        printWriter.flush();
        String output = stringWriter.toString();
        assert output.contains("\"success\":true");
        assert output.contains("\"message\":\"Item added to sale\"");
    }

    @Test
    public void testDoPost_AddItem_InsufficientStock() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/add-item");
        when(request.getParameter("itemCode")).thenReturn("ITEM-001");
        when(request.getParameter("quantity")).thenReturn("999");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentSale")).thenReturn(saleBuilder);
        doThrow(new InsufficientStockException("Not enough stock for ITEM-001"))
            .when(saleBuilder).addItem("ITEM-001", 999);

        // Act
        salesServlet.doPost(request, response);

        // Assert
        verify(response).setContentType("application/json");
        printWriter.flush();
        String output = stringWriter.toString();
        assert output.contains("\"success\":false");
        assert output.contains("Not enough stock");
    }

    @Test
    public void testDoPost_RemoveItemPath_RemovesItemFromSale() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/remove-item");
        when(request.getParameter("itemCode")).thenReturn("ITEM-001");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentSale")).thenReturn(saleBuilder);
        when(saleBuilder.getSubtotal()).thenReturn(new BillTotal(new BigDecimal("100.00")));

        // Act
        salesServlet.doPost(request, response);

        // Assert
        verify(saleBuilder).removeItem("ITEM-001");
        printWriter.flush();
        String output = stringWriter.toString();
        assert output.contains("\"success\":true");
        assert output.contains("\"message\":\"Item removed\"");
    }

    @Test
    public void testDoPost_RemoveItem_NoActiveSale() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/remove-item");
        when(request.getParameter("itemCode")).thenReturn("ITEM-001");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentSale")).thenReturn(null);

        // Act
        salesServlet.doPost(request, response);

        // Assert
        printWriter.flush();
        String output = stringWriter.toString();
        assert output.contains("\"success\":false");
        assert output.contains("No active sale");
    }

    @Test
    public void testDoPost_CompleteSale_Success() throws ServletException, IOException {
        // Arrange
        Bill completedBill = createMockBill();
        List<Item> items = new ArrayList<>();
        items.add(createMockItem());

        when(request.getPathInfo()).thenReturn("/complete");
        when(request.getParameter("cashTendered")).thenReturn("500.00");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentSale")).thenReturn(saleBuilder);
        when(saleBuilder.getItems()).thenReturn(items);
        when(saleBuilder.completeSale(new BigDecimal("500.00"))).thenReturn(completedBill);
        when(salesService.saveBill(completedBill)).thenReturn(12345);
        when(request.getContextPath()).thenReturn("/pos");
        when(session.getAttribute("username")).thenReturn("cashier1");

        // Act
        salesServlet.doPost(request, response);

        // Assert
        verify(saleBuilder).completeSale(new BigDecimal("500.00"));
        verify(salesService).saveBill(completedBill);
        verify(session).removeAttribute("currentSale");
        verify(response).sendRedirect("/pos/sales/receipt/12345");
    }

    @Test
    public void testDoPost_CompleteSale_NoItems() throws ServletException, IOException {
        // Arrange
        List<Item> emptyItems = new ArrayList<>();

        when(request.getPathInfo()).thenReturn("/complete");
        when(request.getParameter("cashTendered")).thenReturn("500.00");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentSale")).thenReturn(saleBuilder);
        when(saleBuilder.getItems()).thenReturn(emptyItems);
        when(request.getRequestDispatcher(contains("new-sale"))).thenReturn(requestDispatcher);

        // Act
        salesServlet.doPost(request, response);

        // Assert
        verify(request).setAttribute("errorMessage", "No items in sale");
    }

    @Test
    public void testDoPost_ClearSale() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/clear");
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/pos");

        // Act
        salesServlet.doPost(request, response);

        // Assert
        verify(session).removeAttribute("currentSale");
        verify(response).sendRedirect("/pos/sales/new");
    }

    @Test
    public void testDoPost_RootPath_ReturnsBadRequest() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/");

        // Act
        salesServlet.doPost(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_InvalidPath_Returns404() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/invalid");

        // Act
        salesServlet.doPost(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    // Helper methods
    private List<Item> createMockItems(int count) {
        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            items.add(createMockItemWithCode("ITEM-00" + i));
        }
        return items;
    }

    private Item createMockItem() {
        ItemCode code = new ItemCode("ITEM-001");
        ItemName name = new ItemName("Test Item");
        ItemPrice price = new ItemPrice(new BigDecimal("100.00"));
        ItemQuantity quantity = new ItemQuantity(10);
        LocalDate expiryDate = LocalDate.now().plusDays(30);
        return new Item(code, name, price, quantity, expiryDate);
    }

    private Item createMockItemWithCode(String code) {
        ItemCode itemCode = new ItemCode(code);
        ItemName name = new ItemName("Item " + code);
        ItemPrice price = new ItemPrice(new BigDecimal("50.00"));
        ItemQuantity quantity = new ItemQuantity(20);
        LocalDate expiryDate = LocalDate.now().plusDays(30);
        return new Item(itemCode, name, price, quantity, expiryDate);
    }

    private List<Bill> createMockBills(int count) {
        List<Bill> bills = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            bills.add(createMockBill());
        }
        return bills;
    }

    private Bill createMockBill() {
        BillNumber billNumber = new BillNumber(12345);
        BillDate billDate = new BillDate(LocalDateTime.now());
        BillTotal totalAmount = new BillTotal(new BigDecimal("500.00"));
        return new Bill(billNumber, billDate, totalAmount);
    }
}
