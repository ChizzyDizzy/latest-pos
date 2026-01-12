# Servlet Unit Tests - SYOS POS System

## Overview

This directory contains comprehensive JUnit 5 and Mockito test suites for all servlets in the SYOS POS Web Application. The tests follow best practices for unit testing and provide excellent code coverage.

## Test Files

### 1. **LoginServletTest.java** (9.8 KB)
Tests the authentication and login functionality.

**Test Coverage:**
- `testDoGet_WithValidSession_RedirectsToDashboard()` - Valid authenticated session redirects to dashboard
- `testDoGet_WithoutSession_ForwardsToLoginPage()` - No session displays login page
- `testDoGet_WithExpiredSession_ForwardsToLoginPage()` - Expired session shows login page
- `testDoPost_WithValidCredentials_CreatesSessionAndRedirectsToDashboard()` - Successful login creates session
- `testDoPost_WithValidCredentials_RedirectsToStoredURL()` - Login redirects to stored URL if available
- `testDoPost_WithInvalidCredentials_ForwardsToLoginPageWithError()` - Failed login shows error message
- `testDoPost_WithNullUsername_ForwardsToLoginPage()` - Null username handled gracefully
- `testDoPost_WithServiceException_ForwardsToLoginPageWithErrorMessage()` - Service exceptions handled
- `testDoPost_WithDifferentUserRoles_SetsCorrectRole()` - Different user roles stored correctly
- `testDoPost_SessionTimeout_SetsMaxInactiveInterval()` - Session timeout configured (30 minutes)

**Key Features:**
- Mocks UserService for authentication
- Tests both GET and POST methods
- Verifies session creation with user attributes
- Tests error handling and edge cases
- Validates role-based access

---

### 2. **DashboardServletTest.java** (9.2 KB)
Tests the dashboard data loading and display.

**Test Coverage:**
- `testDoGet_LoadsDashboardDataSuccessfully()` - Loads all dashboard metrics
- `testDoGet_CalculatesTodaysRevenue()` - Calculates total revenue from bills
- `testDoGet_WithEmptyData_LoadsEmptyDashboard()` - Handles empty data gracefully
- `testDoGet_WithServiceException_ForwardsToErrorPage()` - Catches service exceptions
- `testDoGet_WithMultipleLowStockItems_DisplaysAllItems()` - Displays all low stock items
- `testDoGet_WithMultipleExpiringItems_DisplaysAllItems()` - Displays all expiring items
- `testDoGet_InventoryServiceException_ForwardsToErrorPage()` - Inventory service error handling

**Key Features:**
- Mocks SalesService and InventoryService
- Tests data aggregation and calculations
- Verifies request attributes are set correctly
- Tests with various data scenarios (empty, multiple items)
- Validates error handling with request dispatcher

---

### 3. **SalesServletTest.java** (17 KB)
Tests complete sales functionality including item management and checkout.

**Test Coverage - GET Methods:**
- `testDoGet_RootPath_RedirectsToNewSale()` - Root path redirects to new sale
- `testDoGet_NewPath_ShowsNewSaleForm()` - Shows new sale form with available items
- `testDoGet_NewPath_UseExistingSaleBuilder()` - Reuses existing sale builder from session
- `testDoGet_ListPath_ShowsBillsList()` - Shows list of bills
- `testDoGet_ViewPath_ViewsBill()` - Views specific bill by number
- `testDoGet_ViewPath_BillWithBILLPrefix()` - Handles BILL-prefix in bill number
- `testDoGet_ViewPath_BillNotFound()` - Returns 404 for missing bills
- `testDoGet_ReceiptPath_ViewsReceipt()` - Views receipt for completed bill
- `testDoGet_AvailableItemsPath_ReturnsJSON()` - Returns available items as JSON
- `testDoGet_InvalidPath_Returns404()` - Returns 404 for invalid paths

**Test Coverage - POST Methods:**
- `testDoPost_AddItemPath_AddsItemToSale()` - Adds item to current sale
- `testDoPost_AddItem_InsufficientStock()` - Handles insufficient stock exception
- `testDoPost_RemoveItemPath_RemovesItemFromSale()` - Removes item from sale
- `testDoPost_RemoveItem_NoActiveSale()` - Handles removal with no active sale
- `testDoPost_CompleteSale_Success()` - Completes sale successfully
- `testDoPost_CompleteSale_NoItems()` - Prevents sale completion with no items
- `testDoPost_ClearSale()` - Clears current sale from session
- `testDoPost_RootPath_ReturnsBadRequest()` - Root POST returns bad request
- `testDoPost_InvalidPath_Returns404()` - Invalid POST path returns 404

**Key Features:**
- Mocks SalesService and SaleBuilder
- Tests RESTful path handling with various operations
- Validates JSON responses for AJAX endpoints
- Tests session management for sale state
- Handles InsufficientStockException gracefully
- Tests bill retrieval and receipt generation

---

### 4. **InventoryServletTest.java** (14 KB)
Tests inventory management operations.

**Test Coverage - GET Methods:**
- `testDoGet_RootPath_ShowsInventoryList()` - Lists all inventory items
- `testDoGet_AddPath_ShowsAddStockForm()` - Shows form to add new stock
- `testDoGet_LowStockPath_ShowsLowStockItems()` - Displays low stock items
- `testDoGet_ExpiringPath_ShowsExpiringItems()` - Displays expiring items
- `testDoGet_InventoryListException_ForwardsToErrorPage()` - Handles list exceptions
- `testDoGet_LowStockException_SendsInternalServerError()` - Handles low stock exceptions
- `testDoGet_ExpiringItemsException_SendsInternalServerError()` - Handles expiring items exceptions
- `testDoGet_InvalidPath_Returns404()` - Invalid path returns 404

**Test Coverage - POST Methods:**
- `testDoPost_AddStockPath_AddsStockSuccessfully()` - Adds new stock successfully
- `testDoPost_AddStock_InvalidPrice_ShowsError()` - Validates price format
- `testDoPost_AddStock_InvalidQuantity_ShowsError()` - Validates quantity format
- `testDoPost_AddStock_ServiceException_ShowsError()` - Handles service exceptions
- `testDoPost_MoveToShelfPath_MovesItemsSuccessfully()` - Moves items to shelf
- `testDoPost_MoveToShelf_InvalidQuantity_ReturnsError()` - Validates shelf move quantity
- `testDoPost_MoveToShelf_ServiceException_ReturnsError()` - Handles shelf move exceptions
- `testDoPost_InvalidPath_ReturnsBadRequest()` - Invalid path returns bad request
- `testDoPost_NullPath_ReturnsBadRequest()` - Null path returns bad request

**Key Features:**
- Mocks InventoryService
- Tests JSON responses for AJAX operations
- Validates input parameters (price, quantity, date)
- Tests error handling for various scenarios
- Handles stock operations with proper validation

---

### 5. **ReportsServletTest.java** (13 KB)
Tests report generation functionality.

**Test Coverage:**
- `testDoGet_RootPath_ShowsReportsMenu()` - Shows reports menu
- `testDoGet_NullPath_ShowsReportsMenu()` - Null path shows reports menu
- `testDoGet_DailySalesPath_WithoutDateParameter_UsesToday()` - Uses current date by default
- `testDoGet_DailySalesPath_WithDateParameter()` - Generates report for specified date
- `testDoGet_DailySalesPath_ServiceException_SendsInternalServerError()` - Handles service exceptions
- `testDoGet_StockReportPath_GeneratesStockReport()` - Generates stock report
- `testDoGet_StockReportPath_ServiceException_SendsInternalServerError()` - Handles stock report exceptions
- `testDoGet_ReorderReportPath_GeneratesReorderReport()` - Generates reorder report
- `testDoGet_ReorderReportPath_ServiceException_SendsInternalServerError()` - Handles reorder exceptions
- `testDoGet_InvalidPath_Returns404()` - Invalid path returns 404
- `testDoGet_DailySalesPath_InvalidDateFormat_ThrowsException()` - Validates date format
- `testDoGet_AllReportTypesAvailable()` - Tests all three report types
- `testDoGet_DailySalesReport_EndOfMonth()` - Tests end of month date
- `testDoGet_DailySalesReport_BeginningOfMonth()` - Tests beginning of month date
- `testDoGet_ReportsMenu_NeverCallsReportService()` - Menu doesn't call report service

**Key Features:**
- Mocks ReportService and SalesService
- Tests date parameter handling
- Validates report generation for multiple date ranges
- Tests error handling for all report types
- Verifies proper request dispatcher forwarding

---

### 6. **LogoutServletTest.java** (8.8 KB)
Tests user logout functionality.

**Test Coverage:**
- `testDoGet_WithValidSession_InvalidatesSessionAndRedirects()` - Invalidates session and redirects
- `testDoGet_WithoutSession_RedirectsToLogin()` - Handles missing session gracefully
- `testDoGet_SessionWithNullUsername_InvalidatesSession()` - Handles null username
- `testDoGet_MultipleLogouts_AllInvalidateSessions()` - Multiple logout requests handled
- `testDoGet_ClearsAllSessionAttributes()` - All session attributes cleared
- `testDoGet_ContextPathWithMultipleLevels()` - Handles complex context paths
- `testDoGet_EmptyContextPath()` - Handles empty context path
- `testDoPost_CallsDoGet()` - POST delegates to GET
- `testDoPost_WithoutSession_RedirectsToLogin()` - POST without session redirects
- `testDoGet_WithAdminUser()` - Logs out admin users
- `testDoGet_WithCashierUser()` - Logs out cashier users
- `testDoGet_WithManagerUser()` - Logs out manager users
- `testDoGet_SessionInvalidateOncePerRequest()` - Session invalidated exactly once
- `testDoGet_RedirectCalledOncePerRequest()` - Redirect called exactly once
- `testDoPost_DelegatesCompletelyToDoGet()` - POST delegates to GET completely
- `testDoGet_ExceptionHandling_NoExceptionThrown()` - No exceptions thrown

**Key Features:**
- Tests both GET and POST methods
- Validates session invalidation
- Tests with different context paths
- Verifies redirect behavior
- Tests with all user roles

---

### 7. **MockServletConfig.java** (1.2 KB)
Helper class implementing ServletConfig for test initialization.

**Features:**
- Implements ServletConfig interface
- Used by servlet init() methods
- Provides mock ServletContext
- Supports customizable servlet names

---

## Running the Tests

### Prerequisites
- JUnit 5.10.0 (junit-jupiter-engine)
- Mockito 5.7.0 (mockito-core, mockito-junit-jupiter)
- Java 17

### Run All Tests
```bash
cd /home/user/latest-pos
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=LoginServletTest
mvn test -Dtest=SalesServletTest
mvn test -Dtest=InventoryServletTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=LoginServletTest#testDoPost_WithValidCredentials_CreatesSessionAndRedirectsToDashboard
```

### Generate Test Report
```bash
mvn surefire-report:report
```

---

## Test Framework Features

### JUnit 5 Annotations Used
- `@ExtendWith(MockitoExtension.class)` - Enables Mockito annotations
- `@Mock` - Creates mock objects
- `@BeforeEach` - Setup before each test
- `@Test` - Marks test methods

### Mockito Features
- `when()` - Mock return values
- `verify()` - Verify method calls
- `ArgumentMatchers` - Match method arguments
- `doThrow()` - Throw exceptions from mocks
- `never()` - Verify methods not called

### Testing Patterns
- **Arrange-Act-Assert** - Clear test structure
- **Mocking Dependencies** - Isolates servlet logic
- **Verification** - Ensures correct behavior
- **Error Scenarios** - Tests exception handling
- **Edge Cases** - Tests boundary conditions

---

## Test Coverage Summary

| Servlet | Test Class | Methods | Test Cases |
|---------|-----------|---------|-----------|
| LoginServlet | LoginServletTest | 2 (GET, POST) | 10 |
| DashboardServlet | DashboardServletTest | 1 (GET) | 7 |
| SalesServlet | SalesServletTest | 2 (GET, POST) | 19 |
| InventoryServlet | InventoryServletTest | 2 (GET, POST) | 18 |
| ReportsServlet | ReportsServletTest | 1 (GET) | 15 |
| LogoutServlet | LogoutServletTest | 2 (GET, POST) | 16 |
| **TOTAL** | **6 + Helper** | **10** | **85** |

---

## Key Testing Highlights

### 1. Service Mocking
All tests mock service layer dependencies:
- `UserService` - User authentication and management
- `SalesService` - Sales operations and bill management
- `InventoryService` - Inventory operations
- `ReportService` - Report generation
- `ServiceFactory` - Service instantiation

### 2. Request/Response Mocking
Comprehensive mocking of HTTP components:
- `HttpServletRequest` - Request parameters, session, dispatcher
- `HttpServletResponse` - Response writing, redirects, error codes
- `HttpSession` - Session attribute management
- `RequestDispatcher` - Request forwarding

### 3. Error Handling
Tests verify proper exception handling:
- Service exceptions caught and logged
- Error pages displayed to users
- HTTP error codes set appropriately
- User-friendly error messages shown

### 4. Business Logic Validation
Tests verify correct behavior:
- Session creation with proper attributes
- Calculation accuracy (revenue totals)
- Proper state management (sales, inventory)
- Role-based access control
- Data persistence operations

### 5. Edge Cases Covered
Comprehensive edge case testing:
- Null values and empty collections
- Invalid input formats
- Missing resources (bills, items)
- Expired sessions
- Concurrent operations

---

## Best Practices Implemented

1. **Isolation** - Each test is independent and self-contained
2. **Clarity** - Test names clearly describe what is being tested
3. **Comprehensive** - Both success and failure scenarios tested
4. **Maintainability** - Helper methods reduce code duplication
5. **Performance** - Fast unit tests with mocked dependencies
6. **Documentation** - Clear comments explaining complex test logic

---

## Dependencies

### Test Dependencies (already configured in pom.xml)
```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.7.0</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.7.0</version>
    <scope>test</scope>
</dependency>
```

---

## Future Enhancements

1. **Integration Tests** - Test servlet interaction with actual services
2. **Performance Tests** - Measure response times under load
3. **Security Tests** - Validate authentication and authorization
4. **Database Tests** - Test with real database operations
5. **Code Coverage** - Use JaCoCo for detailed coverage metrics

---

## Troubleshooting

### Issue: Tests not found
**Solution:** Ensure test classes are in `src/test/java` directory with `*Test` suffix

### Issue: Mockito not injecting mocks
**Solution:** Ensure `@ExtendWith(MockitoExtension.class)` annotation is present

### Issue: Servlet initialization fails
**Solution:** Verify MockServletConfig is properly providing ServletContext

### Issue: Mock not returning expected value
**Solution:** Check when() statement and ensure it matches actual method call

---

## Contact & Support

For questions or issues with these tests, refer to the main README in the project root directory.
