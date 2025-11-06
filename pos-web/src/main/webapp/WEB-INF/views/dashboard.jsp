<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="title" value="Dashboard"/>
</jsp:include>

<div class="dashboard">
    <h1>Dashboard - ${sessionScope.userRole}</h1>
    <p class="dashboard-subtitle">Welcome, ${sessionScope.username}!</p>

    <%-- Quick Actions based on role --%>
    <div class="quick-actions">
        <h2>Quick Actions</h2>
        <div class="action-buttons">
            <%-- CUSTOMER: Only view products --%>
            <c:if test="${sessionScope.userRole == 'CUSTOMER'}">
                <a href="${pageContext.request.contextPath}/products" class="action-btn">
                    <span class="action-icon">🛒</span>
                    <span>View Products</span>
                </a>
            </c:if>

            <%-- CASHIER: Sales and Reports only --%>
            <c:if test="${sessionScope.userRole == 'CASHIER'}">
                <a href="${pageContext.request.contextPath}/sales/new" class="action-btn">
                    <span class="action-icon">💳</span>
                    <span>New Sale</span>
                </a>
                <a href="${pageContext.request.contextPath}/sales/list" class="action-btn">
                    <span class="action-icon">🧾</span>
                    <span>View Bills</span>
                </a>
                <a href="${pageContext.request.contextPath}/reports" class="action-btn">
                    <span class="action-icon">📊</span>
                    <span>Reports</span>
                </a>
            </c:if>

            <%-- MANAGER: Everything except user management --%>
            <c:if test="${sessionScope.userRole == 'MANAGER'}">
                <a href="${pageContext.request.contextPath}/sales/new" class="action-btn">
                    <span class="action-icon">💳</span>
                    <span>New Sale</span>
                </a>
                <a href="${pageContext.request.contextPath}/sales/list" class="action-btn">
                    <span class="action-icon">🧾</span>
                    <span>View Bills</span>
                </a>
                <a href="${pageContext.request.contextPath}/inventory" class="action-btn">
                    <span class="action-icon">📦</span>
                    <span>Inventory</span>
                </a>
                <a href="${pageContext.request.contextPath}/inventory/add" class="action-btn">
                    <span class="action-icon">➕</span>
                    <span>Add Stock</span>
                </a>
                <a href="${pageContext.request.contextPath}/reports" class="action-btn">
                    <span class="action-icon">📊</span>
                    <span>Reports</span>
                </a>
            </c:if>

            <%-- ADMIN: Full access including user management --%>
            <c:if test="${sessionScope.userRole == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/sales/new" class="action-btn">
                    <span class="action-icon">💳</span>
                    <span>New Sale</span>
                </a>
                <a href="${pageContext.request.contextPath}/sales/list" class="action-btn">
                    <span class="action-icon">🧾</span>
                    <span>View Bills</span>
                </a>
                <a href="${pageContext.request.contextPath}/inventory" class="action-btn">
                    <span class="action-icon">📦</span>
                    <span>Inventory</span>
                </a>
                <a href="${pageContext.request.contextPath}/inventory/add" class="action-btn">
                    <span class="action-icon">➕</span>
                    <span>Add Stock</span>
                </a>
                <a href="${pageContext.request.contextPath}/reports" class="action-btn">
                    <span class="action-icon">📊</span>
                    <span>Reports</span>
                </a>
                <a href="${pageContext.request.contextPath}/users" class="action-btn">
                    <span class="action-icon">👥</span>
                    <span>Manage Users</span>
                </a>
                <a href="${pageContext.request.contextPath}/users/register" class="action-btn">
                    <span class="action-icon">👤</span>
                    <span>Register User</span>
                </a>
            </c:if>
        </div>
    </div>

    <%-- Stats section (not for CUSTOMER) --%>
    <c:if test="${sessionScope.userRole != 'CUSTOMER'}">
    <div class="dashboard-stats">
        <div class="stat-card">
            <h3>Today's Revenue</h3>
            <p class="stat-value">$<fmt:formatNumber value="${todaysRevenue}" pattern="#,##0.00"/></p>
        </div>

        <div class="stat-card">
            <h3>Today's Transactions</h3>
            <p class="stat-value">${todaysTransactionCount}</p>
        </div>

        <c:if test="${sessionScope.userRole == 'MANAGER' || sessionScope.userRole == 'ADMIN'}">
            <div class="stat-card">
                <h3>Low Stock Items</h3>
                <p class="stat-value alert-warning">${lowStockItems.size()}</p>
            </div>

            <div class="stat-card">
                <h3>Items Expiring Soon</h3>
                <p class="stat-value alert-danger">${expiringItems.size()}</p>
            </div>
        </c:if>
    </div>

    <div class="dashboard-section">
        <h2>Recent Transactions</h2>
        <table class="data-table">
            <thead>
                <tr>
                    <th>Bill #</th>
                    <th>Date</th>
                    <th>Amount</th>
                    <th>Type</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${todaysBills}" var="bill" varStatus="status">
                    <c:if test="${status.index < 10}">
                        <tr>
                            <td>#${bill.billNumber.value}</td>
                            <td>${bill.billDate}</td>
                            <td class="price">$<fmt:formatNumber value="${bill.totalAmount.value}" pattern="#,##0.00"/></td>
                            <td><span class="badge badge-${bill.transactionType}">${bill.transactionType}</span></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/sales/view/${bill.billNumber.value}"
                                   class="btn btn-sm btn-primary">View</a>
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>
                <c:if test="${empty todaysBills}">
                    <tr>
                        <td colspan="5" class="text-center">No transactions today</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
    </c:if>

    <%-- Customer view: Show available products --%>
    <c:if test="${sessionScope.userRole == 'CUSTOMER'}">
        <div class="dashboard-section">
            <h2>Available Products</h2>
            <p>View our available items in the <a href="${pageContext.request.contextPath}/products">Products</a> section.</p>
        </div>
    </c:if>

    <c:if test="${sessionScope.userRole == 'MANAGER' || sessionScope.userRole == 'ADMIN'}">
        <div class="dashboard-section">
            <h2>Alerts & Notifications</h2>
        <c:if test="${not empty lowStockItems}">
            <div class="alert alert-warning">
                <strong>Low Stock Alert:</strong> ${lowStockItems.size()} items need reordering.
                <a href="${pageContext.request.contextPath}/inventory/low-stock">View Items</a>
            </div>
        </c:if>
        <c:if test="${not empty expiringItems}">
            <div class="alert alert-danger">
                <strong>Expiry Warning:</strong> ${expiringItems.size()} items expiring within 7 days.
                <a href="${pageContext.request.contextPath}/inventory/expiring">View Items</a>
            </div>
        </c:if>
        <c:if test="${empty lowStockItems && empty expiringItems}">
            <div class="alert alert-success">
                All systems operational. No alerts at this time.
            </div>
        </c:if>
        </div>
    </c:if>
</div>

<style>
.dashboard-subtitle {
    color: #7f8c8d;
    margin-bottom: 2rem;
    font-size: 1.1rem;
}

.quick-actions {
    margin-bottom: 2rem;
}

.quick-actions h2 {
    margin-bottom: 1rem;
}

.action-buttons {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 1rem;
}

.action-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 1.5rem;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    text-decoration: none;
    color: #2c3e50;
    transition: all 0.3s;
}

.action-btn:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 8px rgba(0,0,0,0.15);
}

.action-icon {
    font-size: 2.5rem;
    margin-bottom: 0.5rem;
}

.badge-IN_STORE {
    background-color: #3498db;
}

.badge-ONLINE {
    background-color: #9b59b6;
}

.price {
    font-weight: bold;
    color: #27ae60;
}
</style>

<jsp:include page="common/footer.jsp"/>
