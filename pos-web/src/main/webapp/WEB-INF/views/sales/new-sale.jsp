<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="New Sale"/>
</jsp:include>

<div class="sales-container">
    <h1>Create New Sale</h1>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">
            ${errorMessage}
        </div>
    </c:if>

    <div class="sales-layout">
        <!-- Left side: Item selection -->
        <div class="item-selection">
            <h2>Available Items</h2>
            <div class="search-box">
                <input type="text" id="searchItem" placeholder="Search by code or name...">
            </div>

            <div class="items-grid">
                <c:forEach items="${availableItems}" var="item">
                    <div class="item-card" data-code="${item.code.value}" data-name="${item.name}"
                         data-price="${item.price.value}" data-stock="${item.quantity.value}">
                        <div class="item-code">${item.code.value}</div>
                        <div class="item-name">${item.name}</div>
                        <div class="item-price">$<fmt:formatNumber value="${item.price.value}" pattern="#,##0.00"/></div>
                        <div class="item-stock">Stock: ${item.quantity.value}</div>
                        <button class="btn btn-primary btn-sm add-item-btn" onclick="addItem('${item.code.value}')">
                            Add to Sale
                        </button>
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- Right side: Current sale -->
        <div class="current-sale">
            <h2>Current Sale</h2>
            <div class="sale-items" id="saleItems">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Code</th>
                            <th>Item</th>
                            <th>Price</th>
                            <th>Qty</th>
                            <th>Total</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody id="saleItemsBody">
                        <c:forEach items="${currentSale.items}" var="billItem">
                            <tr>
                                <td>${billItem.item.code.value}</td>
                                <td>${billItem.item.name}</td>
                                <td>$<fmt:formatNumber value="${billItem.item.price.value}" pattern="#,##0.00"/></td>
                                <td>${billItem.quantity.value}</td>
                                <td>$<fmt:formatNumber value="${billItem.totalPrice.value}" pattern="#,##0.00"/></td>
                                <td>
                                    <button class="btn btn-sm btn-danger" onclick="removeItem('${billItem.item.code.value}')">
                                        Remove
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="sale-summary">
                <div class="summary-row">
                    <span>Subtotal:</span>
                    <span id="subtotal">$<fmt:formatNumber value="${currentSale.subtotal.value}" pattern="#,##0.00"/></span>
                </div>
                <div class="summary-row total-row">
                    <span>Total:</span>
                    <span id="total">$<fmt:formatNumber value="${currentSale.subtotal.value}" pattern="#,##0.00"/></span>
                </div>
            </div>

            <div class="payment-section">
                <h3>Payment</h3>
                <form id="paymentForm" action="${pageContext.request.contextPath}/sales/complete" method="post">
                    <div class="form-group">
                        <label for="cashTendered">Cash Tendered:</label>
                        <input type="number" id="cashTendered" name="cashTendered" step="0.01" required
                               placeholder="0.00" onkeyup="calculateChange()">
                    </div>
                    <div class="form-group">
                        <label>Change:</label>
                        <div id="change" class="change-display">$0.00</div>
                    </div>
                    <div class="button-group">
                        <button type="submit" class="btn btn-primary btn-block">Complete Sale</button>
                        <a href="${pageContext.request.contextPath}/sales/clear" class="btn btn-secondary btn-block">
                            Clear Sale
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<style>
.sales-layout {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 2rem;
    margin-top: 2rem;
}

.item-selection, .current-sale {
    background: white;
    padding: 1.5rem;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.search-box {
    margin-bottom: 1.5rem;
}

.search-box input {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
}

.items-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 1rem;
    max-height: 600px;
    overflow-y: auto;
}

.item-card {
    border: 1px solid #e0e0e0;
    padding: 1rem;
    border-radius: 8px;
    text-align: center;
    transition: all 0.3s;
}

.item-card:hover {
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    transform: translateY(-2px);
}

.item-code {
    font-weight: bold;
    color: #3498db;
    margin-bottom: 0.5rem;
}

.item-name {
    font-size: 0.9rem;
    margin-bottom: 0.5rem;
    min-height: 2.5rem;
}

.item-price {
    font-size: 1.1rem;
    font-weight: bold;
    color: #27ae60;
    margin-bottom: 0.5rem;
}

.item-stock {
    font-size: 0.85rem;
    color: #7f8c8d;
    margin-bottom: 0.75rem;
}

.sale-summary {
    margin: 1.5rem 0;
    padding: 1rem;
    background: #f8f9fa;
    border-radius: 4px;
}

.summary-row {
    display: flex;
    justify-content: space-between;
    padding: 0.5rem 0;
    font-size: 1.1rem;
}

.total-row {
    border-top: 2px solid #2c3e50;
    font-size: 1.5rem;
    font-weight: bold;
    color: #2c3e50;
}

.payment-section {
    margin-top: 1.5rem;
}

.change-display {
    font-size: 1.5rem;
    font-weight: bold;
    color: #27ae60;
    padding: 0.75rem;
    background: #e8f5e9;
    border-radius: 4px;
    text-align: center;
}

.button-group {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
}

.btn-secondary {
    background-color: #95a5a6;
    color: white;
}

.btn-secondary:hover {
    background-color: #7f8c8d;
}

.btn-danger {
    background-color: #e74c3c;
    color: white;
}

.btn-danger:hover {
    background-color: #c0392b;
}
</style>

<script>
function addItem(itemCode) {
    const quantity = prompt("Enter quantity:", "1");
    if (quantity && parseInt(quantity) > 0) {
        fetch('${pageContext.request.contextPath}/sales/add-item', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'itemCode=' + encodeURIComponent(itemCode) + '&quantity=' + quantity
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                location.reload();
            } else {
                alert(data.message);
            }
        });
    }
}

function removeItem(itemCode) {
    if (confirm('Remove this item from the sale?')) {
        fetch('${pageContext.request.contextPath}/sales/remove-item', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'itemCode=' + encodeURIComponent(itemCode)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                location.reload();
            } else {
                alert(data.message);
            }
        });
    }
}

function calculateChange() {
    const totalStr = document.getElementById('total').textContent.replace('$', '').replace(',', '');
    const total = parseFloat(totalStr) || 0;
    const tendered = parseFloat(document.getElementById('cashTendered').value) || 0;
    const change = tendered - total;
    document.getElementById('change').textContent = '$' + change.toFixed(2);
}

// Search functionality
document.getElementById('searchItem').addEventListener('keyup', function(e) {
    const searchTerm = e.target.value.toLowerCase();
    const items = document.querySelectorAll('.item-card');
    items.forEach(item => {
        const code = item.dataset.code.toLowerCase();
        const name = item.dataset.name.toLowerCase();
        if (code.includes(searchTerm) || name.includes(searchTerm)) {
            item.style.display = 'block';
        } else {
            item.style.display = 'none';
        }
    });
});
</script>

<jsp:include page="../common/footer.jsp"/>
