<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Sale Receipt"/>
</jsp:include>

<div class="page-container">
    <div class="page-header">
        <h1>Sale Completed Successfully!</h1>
        <div class="button-group-inline">
            <button onclick="window.print()" class="btn btn-primary">Print Receipt</button>
            <a href="${pageContext.request.contextPath}/sales/new" class="btn btn-success">New Sale</a>
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary">Back to Dashboard</a>
        </div>
    </div>

    <div class="receipt-container">
        <div class="receipt-paper">
            <div class="receipt-header">
                <h2>SYOS</h2>
                <p>Synex Outlet Store</p>
                <p>Point of Sale System</p>
                <div class="receipt-divider">================================</div>
            </div>

            <div class="receipt-info">
                <p><strong>RECEIPT #${bill.billNumber.value}</strong></p>
                <p>Date: <c:out value="${bill.billDate.toString().replace('T', ' ').substring(0, 19)}"/></p>
                <p>Type: ${bill.transactionType}</p>
                <div class="receipt-divider">================================</div>
            </div>

            <div class="receipt-items">
                <c:forEach items="${bill.items}" var="billItem">
                    <div class="receipt-item">
                        <div class="item-header">
                            <span class="item-name">${billItem.item.name}</span>
                        </div>
                        <div class="item-details">
                            <span class="item-code">${billItem.item.code.value}</span>
                            <span class="item-calc">
                                ${billItem.quantity.value} x $<fmt:formatNumber value="${billItem.item.price.value}" pattern="#,##0.00"/>
                            </span>
                            <span class="item-total">
                                $<fmt:formatNumber value="${billItem.totalPrice.value}" pattern="#,##0.00"/>
                            </span>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <div class="receipt-divider">================================</div>

            <div class="receipt-totals">
                <div class="total-row">
                    <span>SUBTOTAL:</span>
                    <span>$<fmt:formatNumber value="${bill.totalAmount.value}" pattern="#,##0.00"/></span>
                </div>
                <div class="total-row">
                    <span>DISCOUNT:</span>
                    <span>-$<fmt:formatNumber value="${bill.discount.value}" pattern="#,##0.00"/></span>
                </div>
                <div class="receipt-divider">- - - - - - - - - - - - - - - -</div>
                <div class="total-row grand-total">
                    <span>TOTAL:</span>
                    <span>$<fmt:formatNumber value="${bill.finalAmount.value}" pattern="#,##0.00"/></span>
                </div>
                <div class="receipt-divider">================================</div>
                <div class="total-row">
                    <span>CASH TENDERED:</span>
                    <span>$<fmt:formatNumber value="${bill.cashTendered.value}" pattern="#,##0.00"/></span>
                </div>
                <div class="total-row change-row">
                    <span>CHANGE:</span>
                    <span>$<fmt:formatNumber value="${bill.change.value}" pattern="#,##0.00"/></span>
                </div>
            </div>

            <div class="receipt-divider">================================</div>

            <div class="receipt-footer">
                <p>Thank you for shopping!</p>
                <p>Please come again</p>
                <p class="receipt-time">Served by: ${sessionScope.username}</p>
            </div>
        </div>
    </div>

    <div class="action-buttons-bottom">
        <a href="${pageContext.request.contextPath}/sales/new" class="btn btn-success btn-large">
            Create Another Sale
        </a>
        <a href="${pageContext.request.contextPath}/sales/list" class="btn btn-secondary btn-large">
            View All Bills
        </a>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary btn-large">
            Back to Dashboard
        </a>
    </div>
</div>

<style>
.button-group-inline {
    display: flex;
    gap: 1rem;
    flex-wrap: wrap;
}

.btn-success {
    background-color: #27ae60;
    color: white;
    border: 3px solid #1e8449;
    box-shadow:
        3px 3px 0px #1e8449,
        6px 6px 0px rgba(0,0,0,0.2);
}

.btn-success:hover {
    background-color: #229954;
    transform: translate(2px, 2px);
    box-shadow:
        1px 1px 0px #1e8449,
        4px 4px 0px rgba(0,0,0,0.2);
}

.receipt-container {
    display: flex;
    justify-content: center;
    margin: 2rem 0;
}

.receipt-paper {
    width: 400px;
    background: white;
    padding: 2rem 1.5rem;
    border: 3px solid #8b4513;
    box-shadow:
        4px 4px 0px #a0522d,
        8px 8px 0px rgba(0,0,0,0.2);
    font-family: 'Courier New', monospace;
    position: relative;
}

.receipt-paper::before {
    content: '';
    position: absolute;
    top: 10px;
    left: 10px;
    right: 10px;
    bottom: 10px;
    border: 1px solid #d2b48c;
    pointer-events: none;
}

.receipt-header {
    text-align: center;
    margin-bottom: 1rem;
}

.receipt-header h2 {
    font-size: 2rem;
    margin: 0;
    color: #8b4513;
    text-shadow: 2px 2px 0px #d2b48c;
}

.receipt-header p {
    margin: 0.25rem 0;
    font-size: 0.9rem;
    color: #5d4037;
}

.receipt-info {
    margin-bottom: 1rem;
}

.receipt-info p {
    margin: 0.3rem 0;
    font-size: 0.95rem;
    color: #3e2723;
}

.receipt-divider {
    color: #a0522d;
    margin: 0.5rem 0;
    font-size: 0.9rem;
    text-align: center;
}

.receipt-items {
    margin: 1rem 0;
}

.receipt-item {
    margin-bottom: 1rem;
}

.item-header {
    margin-bottom: 0.25rem;
}

.item-name {
    font-weight: bold;
    font-size: 1rem;
    color: #3e2723;
    display: block;
}

.item-details {
    display: flex;
    justify-content: space-between;
    font-size: 0.85rem;
    color: #5d4037;
    padding-left: 1rem;
}

.item-code {
    flex: 1;
}

.item-calc {
    flex: 2;
    text-align: center;
}

.item-total {
    flex: 1;
    text-align: right;
    font-weight: bold;
}

.receipt-totals {
    margin: 1rem 0;
}

.total-row {
    display: flex;
    justify-content: space-between;
    margin: 0.5rem 0;
    font-size: 1rem;
    color: #3e2723;
}

.total-row.grand-total {
    font-size: 1.4rem;
    font-weight: bold;
    color: #8b4513;
    margin: 0.75rem 0;
}

.total-row.change-row {
    font-size: 1.2rem;
    font-weight: bold;
    color: #27ae60;
    margin-top: 0.75rem;
}

.receipt-footer {
    text-align: center;
    margin-top: 1rem;
}

.receipt-footer p {
    margin: 0.5rem 0;
    font-size: 0.9rem;
    color: #5d4037;
}

.receipt-time {
    font-size: 0.8rem !important;
    color: #95a5a6 !important;
    margin-top: 1rem !important;
}

.action-buttons-bottom {
    display: flex;
    justify-content: center;
    gap: 1rem;
    margin: 2rem 0;
    flex-wrap: wrap;
}

.btn-large {
    padding: 1rem 2rem;
    font-size: 1.1rem;
}

@media print {
    .navbar, .page-header, .footer, .action-buttons-bottom {
        display: none !important;
    }

    .receipt-paper {
        border: none;
        box-shadow: none;
        margin: 0;
        padding: 1rem;
    }

    .receipt-paper::before {
        display: none;
    }
}

@media (max-width: 768px) {
    .receipt-paper {
        width: 100%;
        max-width: 400px;
    }

    .button-group-inline {
        justify-content: center;
    }

    .action-buttons-bottom {
        flex-direction: column;
        align-items: stretch;
    }

    .btn-large {
        width: 100%;
    }
}
</style>

<jsp:include page="../common/footer.jsp"/>
