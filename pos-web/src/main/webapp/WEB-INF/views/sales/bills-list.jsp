<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Bills List"/>
</jsp:include>

<div class="page-container">
    <div class="page-header">
        <h1>Today's Bills</h1>
        <a href="${pageContext.request.contextPath}/sales/new" class="btn btn-primary">New Sale</a>
    </div>

    <div class="content-box">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Bill #</th>
                    <th>Date & Time</th>
                    <th>Items</th>
                    <th>Total Amount</th>
                    <th>Cash Tendered</th>
                    <th>Change</th>
                    <th>Type</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${bills}" var="bill">
                    <tr>
                        <td><strong>#${bill.billNumber.value}</strong></td>
                        <td><fmt:formatDate value="${bill.billDate}" pattern="MMM dd, yyyy HH:mm:ss"/></td>
                        <td>${bill.items.size()} items</td>
                        <td class="price">$<fmt:formatNumber value="${bill.totalAmount.value}" pattern="#,##0.00"/></td>
                        <td class="price">$<fmt:formatNumber value="${bill.cashTendered.value}" pattern="#,##0.00"/></td>
                        <td class="price">$<fmt:formatNumber value="${bill.change.value}" pattern="#,##0.00"/></td>
                        <td><span class="badge badge-${bill.transactionType}">${bill.transactionType}</span></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/sales/view/${bill.billNumber.value}"
                               class="btn btn-sm btn-primary">View</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty bills}">
                    <tr>
                        <td colspan="8" class="text-center">No bills found for today</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<style>
.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
}

.content-box {
    background: white;
    padding: 1.5rem;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.price {
    text-align: right;
    font-weight: bold;
    color: #27ae60;
}

.badge-IN_STORE {
    background-color: #3498db;
}

.badge-ONLINE {
    background-color: #9b59b6;
}
</style>

<jsp:include page="../common/footer.jsp"/>
