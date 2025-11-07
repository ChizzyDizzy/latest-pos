<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Low Stock Items"/>
</jsp:include>

<div class="page-container">
    <div class="page-header">
        <h1>Low Stock Items (Below 50 units)</h1>
        <a href="${pageContext.request.contextPath}/inventory" class="btn btn-secondary">Back to Inventory</a>
    </div>

    <div class="alert alert-warning">
        <strong>Reorder Alert:</strong> ${items.size()} items are below the reorder threshold.
    </div>

    <div class="content-box">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Code</th>
                    <th>Name</th>
                    <th>Current Stock</th>
                    <th>Price</th>
                    <th>State</th>
                    <th>Expiry Date</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${items}" var="item">
                    <tr>
                        <td><strong>${item.code.value}</strong></td>
                        <td>${item.name}</td>
                        <td class="text-center">
                            <span class="quantity-badge low-stock">${item.quantity.value}</span>
                        </td>
                        <td class="price">$<fmt:formatNumber value="${item.price.value}" pattern="#,##0.00"/></td>
                        <td><span class="state-badge state-${item.state.stateName}">${item.state.stateName}</span></td>
                        <td>${item.expiryDate}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/inventory/add" class="btn btn-sm btn-primary">
                                Reorder
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty items}">
                    <tr>
                        <td colspan="7" class="text-center">No low stock items - all good!</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<style>
.quantity-badge {
    padding: 0.25rem 0.75rem;
    border-radius: 12px;
    font-weight: bold;
    color: white;
}

.quantity-badge.low-stock {
    background-color: #e74c3c;
}

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
</style>

<jsp:include page="../common/footer.jsp"/>
