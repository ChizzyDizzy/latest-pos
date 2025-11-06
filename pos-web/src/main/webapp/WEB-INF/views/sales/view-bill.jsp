<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Bill Detail"/>
</jsp:include>

<div class="page-container">
    <div class="page-header">
        <h1>Bill #${bill.billNumber.value}</h1>
        <div class="button-group-inline">
            <button onclick="window.print()" class="btn btn-primary">Print Bill</button>
            <a href="${pageContext.request.contextPath}/sales/list" class="btn btn-secondary">Back to List</a>
        </div>
    </div>

    <div class="bill-container">
        <div class="bill-header">
            <h2>SYOS - Synex Outlet Store</h2>
            <p>Point of Sale System</p>
            <p>Date: <fmt:formatDate value="${bill.billDate}" pattern="MMM dd, yyyy HH:mm:ss"/></p>
            <p>Transaction Type: <span class="badge">${bill.transactionType}</span></p>
        </div>

        <table class="bill-table">
            <thead>
                <tr>
                    <th>Item Code</th>
                    <th>Item Name</th>
                    <th class="text-right">Price</th>
                    <th class="text-center">Quantity</th>
                    <th class="text-right">Total</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${bill.items}" var="billItem">
                    <tr>
                        <td>${billItem.item.code.value}</td>
                        <td>${billItem.item.name}</td>
                        <td class="text-right">$<fmt:formatNumber value="${billItem.item.price.value}" pattern="#,##0.00"/></td>
                        <td class="text-center">${billItem.quantity.value}</td>
                        <td class="text-right">$<fmt:formatNumber value="${billItem.totalPrice.value}" pattern="#,##0.00"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <div class="bill-summary">
            <div class="summary-row">
                <span>Subtotal:</span>
                <span>$<fmt:formatNumber value="${bill.totalAmount.value}" pattern="#,##0.00"/></span>
            </div>
            <div class="summary-row">
                <span>Discount:</span>
                <span>$<fmt:formatNumber value="${bill.discount.value}" pattern="#,##0.00"/></span>
            </div>
            <div class="summary-row total">
                <span>Total:</span>
                <span>$<fmt:formatNumber value="${bill.totalAmount.value}" pattern="#,##0.00"/></span>
            </div>
            <div class="summary-row">
                <span>Cash Tendered:</span>
                <span>$<fmt:formatNumber value="${bill.cashTendered.value}" pattern="#,##0.00"/></span>
            </div>
            <div class="summary-row change">
                <span>Change:</span>
                <span>$<fmt:formatNumber value="${bill.change.value}" pattern="#,##0.00"/></span>
            </div>
        </div>

        <div class="bill-footer">
            <p>Thank you for shopping at SYOS!</p>
            <p>Please come again</p>
        </div>
    </div>
</div>

<style>
.button-group-inline {
    display: flex;
    gap: 1rem;
}

.bill-container {
    max-width: 800px;
    margin: 2rem auto;
    background: white;
    padding: 2rem;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.bill-header {
    text-align: center;
    border-bottom: 2px solid #2c3e50;
    padding-bottom: 1rem;
    margin-bottom: 2rem;
}

.bill-header h2 {
    color: #2c3e50;
    margin-bottom: 0.5rem;
}

.bill-header p {
    margin: 0.25rem 0;
    color: #7f8c8d;
}

.bill-table {
    width: 100%;
    margin-bottom: 2rem;
    border-collapse: collapse;
}

.bill-table th,
.bill-table td {
    padding: 0.75rem;
    border-bottom: 1px solid #ecf0f1;
}

.bill-table th {
    background-color: #f8f9fa;
    font-weight: bold;
    color: #2c3e50;
}

.text-right {
    text-align: right;
}

.text-center {
    text-align: center;
}

.bill-summary {
    border-top: 2px solid #2c3e50;
    padding-top: 1rem;
    margin-bottom: 2rem;
}

.summary-row {
    display: flex;
    justify-content: space-between;
    padding: 0.5rem 0;
    font-size: 1.1rem;
}

.summary-row.total {
    font-size: 1.5rem;
    font-weight: bold;
    color: #2c3e50;
    border-top: 1px solid #95a5a6;
    margin-top: 0.5rem;
    padding-top: 0.75rem;
}

.summary-row.change {
    font-size: 1.3rem;
    font-weight: bold;
    color: #27ae60;
}

.bill-footer {
    text-align: center;
    padding-top: 1rem;
    border-top: 1px dashed #95a5a6;
    color: #7f8c8d;
}

@media print {
    .navbar, .page-header, .footer, .btn {
        display: none;
    }

    .bill-container {
        box-shadow: none;
        margin: 0;
    }
}
</style>

<jsp:include page="../common/footer.jsp"/>
