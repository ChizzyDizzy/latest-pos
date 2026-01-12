package com.syos.web.controllers;

import com.google.gson.Gson;
import com.syos.application.services.InventoryService;
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServletTest {

    private InventoryServlet inventoryServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletContext servletContext;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private ServiceFactory serviceFactory;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    public void setUp() throws ServletException, IOException {
        inventoryServlet = new InventoryServlet();

        // Setup servlet context and service factory
        when(servletContext.getAttribute("serviceFactory")).thenReturn(serviceFactory);
        when(serviceFactory.getInventoryService()).thenReturn(inventoryService);

        // Setup servlet config
        inventoryServlet.init(new MockServletConfig(servletContext));

        // Setup response writer
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    // ===== GET Methods =====

    @Test
    public void testDoGet_RootPath_ShowsInventoryList() throws ServletException, IOException {
        // Arrange
        List<Item> allItems = createMockItems(5);

        when(request.getPathInfo()).thenReturn("/");
        when(inventoryService.getAllItems()).thenReturn(allItems);
        when(request.getRequestDispatcher("/WEB-INF/views/inventory/list.jsp")).thenReturn(requestDispatcher);

        // Act
        inventoryServlet.doGet(request, response);

        // Assert
        verify(inventoryService).getAllItems();
        verify(request).setAttribute("items", allItems);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_AddPath_ShowsAddStockForm() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/add");
        when(request.getRequestDispatcher("/WEB-INF/views/inventory/add-stock.jsp")).thenReturn(requestDispatcher);

        // Act
        inventoryServlet.doGet(request, response);

        // Assert
        verify(request).getRequestDispatcher("/WEB-INF/views/inventory/add-stock.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_LowStockPath_ShowsLowStockItems() throws ServletException, IOException {
        // Arrange
        List<Item> lowStockItems = createMockItems(3);

        when(request.getPathInfo()).thenReturn("/low-stock");
        when(inventoryService.getLowStockItems()).thenReturn(lowStockItems);
        when(request.getRequestDispatcher("/WEB-INF/views/inventory/low-stock.jsp")).thenReturn(requestDispatcher);

        // Act
        inventoryServlet.doGet(request, response);

        // Assert
        verify(inventoryService).getLowStockItems();
        verify(request).setAttribute("items", lowStockItems);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_ExpiringPath_ShowsExpiringItems() throws ServletException, IOException {
        // Arrange
        List<Item> expiringItems = createMockItems(2);

        when(request.getPathInfo()).thenReturn("/expiring");
        when(inventoryService.getExpiringItems()).thenReturn(expiringItems);
        when(request.getRequestDispatcher("/WEB-INF/views/inventory/expiring.jsp")).thenReturn(requestDispatcher);

        // Act
        inventoryServlet.doGet(request, response);

        // Assert
        verify(inventoryService).getExpiringItems();
        verify(request).setAttribute("items", expiringItems);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_InventoryListException_ForwardsToErrorPage() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/");
        when(inventoryService.getAllItems()).thenThrow(new RuntimeException("Database error"));
        when(request.getRequestDispatcher("/WEB-INF/views/error/error.jsp")).thenReturn(requestDispatcher);

        // Act
        inventoryServlet.doGet(request, response);

        // Assert
        verify(request).setAttribute("errorMessage", "Error loading inventory");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_LowStockException_SendsInternalServerError() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/low-stock");
        when(inventoryService.getLowStockItems()).thenThrow(new RuntimeException("Service error"));

        // Act
        inventoryServlet.doGet(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testDoGet_ExpiringItemsException_SendsInternalServerError() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/expiring");
        when(inventoryService.getExpiringItems()).thenThrow(new RuntimeException("Service error"));

        // Act
        inventoryServlet.doGet(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testDoGet_InvalidPath_Returns404() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/invalid");

        // Act
        inventoryServlet.doGet(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    // ===== POST Methods =====

    @Test
    public void testDoPost_AddStockPath_AddsStockSuccessfully() throws ServletException, IOException {
        // Arrange
        List<Item> allItems = createMockItems(5);

        when(request.getPathInfo()).thenReturn("/add");
        when(request.getParameter("code")).thenReturn("ITEM-NEW");
        when(request.getParameter("name")).thenReturn("New Product");
        when(request.getParameter("price")).thenReturn("25.50");
        when(request.getParameter("quantity")).thenReturn("100");
        when(request.getParameter("expiryDate")).thenReturn("2025-12-31");
        when(inventoryService.getAllItems()).thenReturn(allItems);
        when(request.getRequestDispatcher("/WEB-INF/views/inventory/list.jsp")).thenReturn(requestDispatcher);

        // Act
        inventoryServlet.doPost(request, response);

        // Assert
        verify(inventoryService).addStock("ITEM-NEW", "New Product", new BigDecimal("25.50"), 100, LocalDate.of(2025, 12, 31));
        verify(request).setAttribute("successMessage", "Stock added successfully");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_AddStock_InvalidPrice_ShowsError() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/add");
        when(request.getParameter("code")).thenReturn("ITEM-NEW");
        when(request.getParameter("name")).thenReturn("New Product");
        when(request.getParameter("price")).thenReturn("invalid");
        when(request.getParameter("quantity")).thenReturn("100");
        when(request.getParameter("expiryDate")).thenReturn("2025-12-31");
        when(request.getRequestDispatcher("/WEB-INF/views/inventory/add-stock.jsp")).thenReturn(requestDispatcher);

        // Act
        inventoryServlet.doPost(request, response);

        // Assert
        verify(request).setAttribute(contains("errorMessage"), anyString());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_AddStock_InvalidQuantity_ShowsError() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/add");
        when(request.getParameter("code")).thenReturn("ITEM-NEW");
        when(request.getParameter("name")).thenReturn("New Product");
        when(request.getParameter("price")).thenReturn("25.50");
        when(request.getParameter("quantity")).thenReturn("notanumber");
        when(request.getParameter("expiryDate")).thenReturn("2025-12-31");
        when(request.getRequestDispatcher("/WEB-INF/views/inventory/add-stock.jsp")).thenReturn(requestDispatcher);

        // Act
        inventoryServlet.doPost(request, response);

        // Assert
        verify(request).setAttribute(contains("errorMessage"), anyString());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_AddStock_ServiceException_ShowsError() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/add");
        when(request.getParameter("code")).thenReturn("ITEM-NEW");
        when(request.getParameter("name")).thenReturn("New Product");
        when(request.getParameter("price")).thenReturn("25.50");
        when(request.getParameter("quantity")).thenReturn("100");
        when(request.getParameter("expiryDate")).thenReturn("2025-12-31");
        doThrow(new RuntimeException("Duplicate item code"))
            .when(inventoryService).addStock(anyString(), anyString(), any(), anyInt(), any());
        when(request.getRequestDispatcher("/WEB-INF/views/inventory/add-stock.jsp")).thenReturn(requestDispatcher);

        // Act
        inventoryServlet.doPost(request, response);

        // Assert
        verify(request).setAttribute(eq("errorMessage"), contains("Error adding stock"));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_MoveToShelfPath_MovesItemsSuccessfully() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/move-to-shelf");
        when(request.getParameter("itemCode")).thenReturn("ITEM-001");
        when(request.getParameter("quantity")).thenReturn("50");
        when(response.setContentType("application/json")).then(invocation -> {
            response.setContentType("application/json");
            return null;
        });

        // Act
        inventoryServlet.doPost(request, response);

        // Assert
        verify(inventoryService).moveToShelf("ITEM-001", 50);
        printWriter.flush();
        String output = stringWriter.toString();
        assert output.contains("\"success\":true");
        assert output.contains("\"message\":\"Items moved to shelf\"");
    }

    @Test
    public void testDoPost_MoveToShelf_InvalidQuantity_ReturnsError() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/move-to-shelf");
        when(request.getParameter("itemCode")).thenReturn("ITEM-001");
        when(request.getParameter("quantity")).thenReturn("invalid");

        // Act
        inventoryServlet.doPost(request, response);

        // Assert
        printWriter.flush();
        String output = stringWriter.toString();
        assert output.contains("\"success\":false");
    }

    @Test
    public void testDoPost_MoveToShelf_ServiceException_ReturnsError() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/move-to-shelf");
        when(request.getParameter("itemCode")).thenReturn("ITEM-001");
        when(request.getParameter("quantity")).thenReturn("50");
        doThrow(new RuntimeException("Insufficient stock"))
            .when(inventoryService).moveToShelf("ITEM-001", 50);

        // Act
        inventoryServlet.doPost(request, response);

        // Assert
        printWriter.flush();
        String output = stringWriter.toString();
        assert output.contains("\"success\":false");
        assert output.contains("Insufficient stock");
    }

    @Test
    public void testDoPost_InvalidPath_ReturnsBadRequest() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn("/invalid");

        // Act
        inventoryServlet.doPost(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_NullPath_ReturnsBadRequest() throws ServletException, IOException {
        // Arrange
        when(request.getPathInfo()).thenReturn(null);

        // Act
        inventoryServlet.doPost(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    // Helper methods
    private List<Item> createMockItems(int count) {
        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            ItemCode code = new ItemCode("ITEM-00" + i);
            ItemName name = new ItemName("Item " + i);
            ItemPrice price = new ItemPrice(new BigDecimal("10.00"));
            ItemQuantity quantity = new ItemQuantity(10 - i);
            LocalDate expiryDate = LocalDate.now().plusDays(i * 10);

            Item item = new Item(code, name, price, quantity, expiryDate);
            items.add(item);
        }
        return items;
    }
}
