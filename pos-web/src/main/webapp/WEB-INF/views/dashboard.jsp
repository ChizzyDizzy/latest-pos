<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="title" value="Dashboard"/>
</jsp:include>

<div class="dashboard">
    <h1>Dashboard</h1>

    <div class="dashboard-stats">
        <div class="stat-card">
            <h3>Today's Revenue</h3>
            <p class="stat-value">$<fmt:formatNumber value="${todaysRevenue}" pattern="#,##0.00"/></p>
        </div>

        <div class="stat-card">
            <h3>Today's Transactions</h3>
            <p class="stat-value">${todaysTransactionCount}</p>
        </div>

        <div class="stat-card">
            <h3>Low Stock Items</h3>
            <p class="stat-value alert-warning">${lowStockItems.size()}</p>
        </div>

        <div class="stat-card">
            <h3>Items Expiring Soon</h3>
            <p class="stat-value alert-danger">${expiringItems.size()}</p>
        </div>
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
                            <td>${bill.billNumber}</td>
                            <td><fmt:formatDate value="${bill.billDate}" pattern="HH:mm:ss"/></td>
                            <td>$<fmt:formatNumber value="${bill.totalAmount.value}" pattern="#,##0.00"/></td>
                            <td><span class="badge">${bill.transactionType}</span></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/sales/view/${bill.billNumber}"
                                   class="btn btn-sm">View</a>
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

    <div class="dashboard-section">
        <h2>Alerts</h2>
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
</div>

<jsp:include page="common/footer.jsp"/>
