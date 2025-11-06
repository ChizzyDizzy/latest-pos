<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Expiring Items"/>
</jsp:include>

<div class="page-container">
    <div class="page-header">
        <h1>Items Expiring Within 7 Days</h1>
        <a href="${pageContext.request.contextPath}/inventory" class="btn btn-secondary">Back to Inventory</a>
    </div>

    <div class="alert alert-danger">
        <strong>Expiry Warning:</strong> ${items.size()} items are expiring soon. Take action to minimize waste.
    </div>

    <div class="content-box">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Code</th>
                    <th>Name</th>
                    <th>Quantity</th>
                    <th>Price</th>
                    <th>State</th>
                    <th>Expiry Date</th>
                    <th>Days Until Expiry</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${items}" var="item">
                    <tr class="${item.expired ? 'expired-row' : ''}">
                        <td><strong>${item.code.value}</strong></td>
                        <td>${item.name}</td>
                        <td class="text-center">${item.quantity.value}</td>
                        <td class="price">$<fmt:formatNumber value="${item.price.value}" pattern="#,##0.00"/></td>
                        <td><span class="state-badge state-${item.state.stateName}">${item.state.stateName}</span></td>
                        <td>
                            <fmt:formatDate value="${item.expiryDate}" pattern="MMM dd, yyyy"/>
                            <c:if test="${item.expired}">
                                <span class="expired-label">EXPIRED</span>
                            </c:if>
                        </td>
                        <td class="text-center">
                            <jsp:useBean id="now" class="java.util.Date"/>
                            <c:set var="daysUntilExpiry" value="${(item.expiryDate.time - now.time) / (1000 * 60 * 60 * 24)}"/>
                            <span class="days-badge ${daysUntilExpiry <= 0 ? 'expired' : (daysUntilExpiry <= 3 ? 'critical' : 'warning')}">
                                <fmt:formatNumber value="${daysUntilExpiry}" maxFractionDigits="0"/> days
                            </span>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty items}">
                    <tr>
                        <td colspan="7" class="text-center">No items expiring soon</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <div class="alert alert-success" style="margin-top: 2rem;">
        <strong>Recommended Actions:</strong>
        <ul>
            <li>Mark down prices for items expiring in 1-3 days</li>
            <li>Remove expired items from shelves immediately</li>
            <li>Consider promotional offers to clear near-expiry stock</li>
        </ul>
    </div>
</div>

<style>
.state-badge {
    padding: 0.25rem 0.75rem;
    border-radius: 12px;
    font-size: 0.85rem;
    font-weight: bold;
    color: white;
}

.state-IN_STORE {
    background-color: #3498db;
}

.state-ON_SHELF {
    background-color: #27ae60;
}

.state-EXPIRED {
    background-color: #e74c3c;
}

.expired-label {
    color: #e74c3c;
    font-weight: bold;
    margin-left: 0.5rem;
}

.expired-row {
    background-color: #ffebee;
}

.days-badge {
    padding: 0.25rem 0.75rem;
    border-radius: 12px;
    font-weight: bold;
    color: white;
}

.days-badge.expired {
    background-color: #e74c3c;
}

.days-badge.critical {
    background-color: #e67e22;
}

.days-badge.warning {
    background-color: #f39c12;
}

.alert ul {
    margin: 0.5rem 0 0 1.5rem;
    padding: 0;
}

.alert li {
    margin: 0.25rem 0;
}
</style>

<jsp:include page="../common/footer.jsp"/>
