package com.syos.web.controllers;

import com.syos.application.services.InventoryService;
import com.syos.domain.entities.Item;
import com.syos.domain.entities.ItemState;
import com.syos.domain.valueobjects.ItemCode;
import com.syos.domain.valueobjects.Money;
import com.syos.domain.valueobjects.Quantity;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductsServletTest {

    private ProductsServlet productsServlet;

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
    private InventoryService inventoryService;

    @Mock
    private ServiceFactory serviceFactory;

    @BeforeEach
    public void setUp() throws ServletException {
        productsServlet = new ProductsServlet();

        // Setup servlet context and service factory
        when(servletContext.getAttribute("serviceFactory")).thenReturn(serviceFactory);
        when(serviceFactory.getInventoryService()).thenReturn(inventoryService);

        // Set servlet context
        productsServlet.init(new MockServletConfig(servletContext));
    }

    @Test
    public void testDoGet_WithValidSession_DisplaysAvailableProducts() throws ServletException, IOException {
        // Arrange
        Item availableItem = new Item(
            new ItemCode("ITEM-001"),
            "Test Product",
            new Money(new BigDecimal("10.00")),
            new Quantity(50),
            LocalDate.now().plusDays(30)
        );
        availableItem.setState(new ItemState("ON_SHELF"));

        List<Item> allItems = Arrays.asList(availableItem);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(inventoryService.getAllItems()).thenReturn(allItems);
        when(request.getRequestDispatcher("/WEB-INF/views/products.jsp")).thenReturn(requestDispatcher);

        // Act
        productsServlet.doGet(request, response);

        // Assert
        verify(inventoryService).getAllItems();
        verify(request).setAttribute(eq("items"), anyList());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_WithoutSession_RedirectsToLogin() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/pos-web");

        // Act
        productsServlet.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/pos-web/login");
        verify(inventoryService, never()).getAllItems();
    }

    @Test
    public void testDoGet_WithoutUsername_RedirectsToLogin() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn(null);
        when(request.getContextPath()).thenReturn("/pos-web");

        // Act
        productsServlet.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/pos-web/login");
        verify(inventoryService, never()).getAllItems();
    }

    @Test
    public void testDoGet_FiltersOutInStoreItems() throws ServletException, IOException {
        // Arrange
        Item onShelfItem = new Item(
            new ItemCode("ITEM-001"),
            "On Shelf Product",
            new Money(new BigDecimal("10.00")),
            new Quantity(50),
            LocalDate.now().plusDays(30)
        );
        onShelfItem.setState(new ItemState("ON_SHELF"));

        Item inStoreItem = new Item(
            new ItemCode("ITEM-002"),
            "In Store Product",
            new Money(new BigDecimal("15.00")),
            new Quantity(30),
            LocalDate.now().plusDays(30)
        );
        inStoreItem.setState(new ItemState("IN_STORE"));

        List<Item> allItems = Arrays.asList(onShelfItem, inStoreItem);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(inventoryService.getAllItems()).thenReturn(allItems);
        when(request.getRequestDispatcher("/WEB-INF/views/products.jsp")).thenReturn(requestDispatcher);

        // Act
        productsServlet.doGet(request, response);

        // Assert
        verify(request).setAttribute(eq("items"), argThat(items -> {
            List<Item> itemList = (List<Item>) items;
            return itemList.size() == 1 &&
                   itemList.get(0).getCode().getValue().equals("ITEM-001");
        }));
    }

    @Test
    public void testDoGet_FiltersOutExpiredItems() throws ServletException, IOException {
        // Arrange
        Item validItem = new Item(
            new ItemCode("ITEM-001"),
            "Valid Product",
            new Money(new BigDecimal("10.00")),
            new Quantity(50),
            LocalDate.now().plusDays(30)
        );
        validItem.setState(new ItemState("ON_SHELF"));

        Item expiredItem = new Item(
            new ItemCode("ITEM-002"),
            "Expired Product",
            new Money(new BigDecimal("15.00")),
            new Quantity(30),
            LocalDate.now().minusDays(1) // Expired yesterday
        );
        expiredItem.setState(new ItemState("ON_SHELF"));

        List<Item> allItems = Arrays.asList(validItem, expiredItem);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(inventoryService.getAllItems()).thenReturn(allItems);
        when(request.getRequestDispatcher("/WEB-INF/views/products.jsp")).thenReturn(requestDispatcher);

        // Act
        productsServlet.doGet(request, response);

        // Assert
        verify(request).setAttribute(eq("items"), argThat(items -> {
            List<Item> itemList = (List<Item>) items;
            return itemList.size() == 1 &&
                   itemList.get(0).getCode().getValue().equals("ITEM-001");
        }));
    }

    @Test
    public void testDoGet_FiltersOutZeroQuantityItems() throws ServletException, IOException {
        // Arrange
        Item availableItem = new Item(
            new ItemCode("ITEM-001"),
            "Available Product",
            new Money(new BigDecimal("10.00")),
            new Quantity(50),
            LocalDate.now().plusDays(30)
        );
        availableItem.setState(new ItemState("ON_SHELF"));

        Item outOfStockItem = new Item(
            new ItemCode("ITEM-002"),
            "Out of Stock Product",
            new Money(new BigDecimal("15.00")),
            new Quantity(0),
            LocalDate.now().plusDays(30)
        );
        outOfStockItem.setState(new ItemState("ON_SHELF"));

        List<Item> allItems = Arrays.asList(availableItem, outOfStockItem);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(inventoryService.getAllItems()).thenReturn(allItems);
        when(request.getRequestDispatcher("/WEB-INF/views/products.jsp")).thenReturn(requestDispatcher);

        // Act
        productsServlet.doGet(request, response);

        // Assert
        verify(request).setAttribute(eq("items"), argThat(items -> {
            List<Item> itemList = (List<Item>) items;
            return itemList.size() == 1 &&
                   itemList.get(0).getCode().getValue().equals("ITEM-001");
        }));
    }

    @Test
    public void testDoGet_WithEmptyInventory_DisplaysEmptyList() throws ServletException, IOException {
        // Arrange
        List<Item> allItems = new ArrayList<>();

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(inventoryService.getAllItems()).thenReturn(allItems);
        when(request.getRequestDispatcher("/WEB-INF/views/products.jsp")).thenReturn(requestDispatcher);

        // Act
        productsServlet.doGet(request, response);

        // Assert
        verify(request).setAttribute(eq("items"), argThat(items -> {
            List<Item> itemList = (List<Item>) items;
            return itemList.isEmpty();
        }));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_WithServiceException_ForwardsToErrorPage() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(inventoryService.getAllItems()).thenThrow(new RuntimeException("Database error"));
        when(request.getRequestDispatcher("/WEB-INF/views/error.jsp")).thenReturn(requestDispatcher);

        // Act
        productsServlet.doGet(request, response);

        // Assert
        verify(request).setAttribute("errorMessage", "Error loading products");
        verify(requestDispatcher).forward(request, response);
    }

    @SuppressWarnings("unchecked")
    private static org.mockito.ArgumentMatcher<List<Item>> anyList() {
        return obj -> obj instanceof List;
    }
}
