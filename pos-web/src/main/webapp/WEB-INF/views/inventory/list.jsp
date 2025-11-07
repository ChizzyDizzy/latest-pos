<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Inventory"/>
</jsp:include>

<div class="page-container">
    <div class="page-header">
        <h1>Inventory Management</h1>
        <div class="button-group-inline">
            <a href="${pageContext.request.contextPath}/inventory/add" class="btn btn-primary">Add Stock</a>
            <a href="${pageContext.request.contextPath}/inventory/low-stock" class="btn btn-warning">Low Stock</a>
            <a href="${pageContext.request.contextPath}/inventory/expiring" class="btn btn-danger">Expiring Soon</a>
        </div>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">${errorMessage}</div>
    </c:if>

    <div class="content-box">
        <div class="search-filter-box">
            <input type="text" id="searchItem" placeholder="Search by code or name..." class="search-input">
            <select id="filterState" class="filter-select">
                <option value="">All States</option>
                <option value="IN_STORE">In Store</option>
                <option value="ON_SHELF">On Shelf</option>
                <option value="EXPIRED">Expired</option>
                <option value="SOLD_OUT">Sold Out</option>
            </select>
        </div>

        <table class="data-table" id="inventoryTable">
            <thead>
                <tr>
                    <th>Code</th>
                    <th>Name</th>
                    <th>Price</th>
                    <th>Quantity</th>
                    <th>State</th>
                    <th>Purchase Date</th>
                    <th>Expiry Date</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${items}" var="item">
                    <tr data-code="${item.code.value}" data-name="${item.name}" data-state="${item.state.stateName}">
                        <td><strong>${item.code.value}</strong></td>
                        <td>${item.name}</td>
                        <td class="price">$<fmt:formatNumber value="${item.price.value}" pattern="#,##0.00"/></td>
                        <td class="text-center">
                            <span class="quantity-badge ${item.quantity.value < 50 ? 'low-stock' : ''}">${item.quantity.value}</span>
                        </td>
                        <td>
                            <span class="state-badge state-${item.state.stateName}">${item.state.stateName}</span>
                        </td>
                        <td>${item.purchaseDate}</td>
                        <td>
                            ${item.expiryDate}
                            <c:if test="${item.expired}">
                                <span class="expired-label">EXPIRED</span>
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${item.state.stateName == 'IN_STORE'}">
                                <button class="btn btn-sm btn-primary" onclick="moveToShelf('${item.code.value}')">
                                    Move to Shelf
                                </button>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty items}">
                    <tr>
                        <td colspan="8" class="text-center">No items in inventory</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<style>
.button-group-inline {
    display: flex;
    gap: 1rem;
}

.btn-warning {
    background-color: #f39c12;
    color: white;
}

.btn-warning:hover {
    background-color: #e67e22;
}

.search-filter-box {
    display: flex;
    gap: 1rem;
    margin-bottom: 1.5rem;
}

.search-input, .filter-select {
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
}

.search-input {
    flex: 1;
}

.filter-select {
    width: 200px;
}

.quantity-badge {
    padding: 0.25rem 0.75rem;
    border-radius: 12px;
    background-color: #27ae60;
    color: white;
    font-weight: bold;
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

.state-EXPIRED {
    background-color: #e74c3c;
}

.state-SOLD_OUT {
    background-color: #95a5a6;
}

.expired-label {
    color: #e74c3c;
    font-weight: bold;
    margin-left: 0.5rem;
}
</style>

<script>
function moveToShelf(itemCode) {
    const quantity = prompt('How many items to move to shelf?', '1');
    if (quantity && parseInt(quantity) > 0) {
        fetch('${pageContext.request.contextPath}/inventory/move-to-shelf', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'itemCode=' + encodeURIComponent(itemCode) + '&quantity=' + quantity
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                location.reload();
            } else {
                alert('Error: ' + data.message);
            }
        });
    }
}

// Search functionality
document.getElementById('searchItem').addEventListener('keyup', function(e) {
    filterTable();
});

document.getElementById('filterState').addEventListener('change', function(e) {
    filterTable();
});

function filterTable() {
    const searchTerm = document.getElementById('searchItem').value.toLowerCase();
    const stateFilter = document.getElementById('filterState').value;
    const rows = document.querySelectorAll('#inventoryTable tbody tr');

    rows.forEach(row => {
        const code = row.dataset.code ? row.dataset.code.toLowerCase() : '';
        const name = row.dataset.name ? row.dataset.name.toLowerCase() : '';
        const state = row.dataset.state || '';

        const matchesSearch = code.includes(searchTerm) || name.includes(searchTerm);
        const matchesState = !stateFilter || state === stateFilter;

        if (matchesSearch && matchesState) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}
</script>

<jsp:include page="../common/footer.jsp"/>
