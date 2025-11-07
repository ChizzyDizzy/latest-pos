<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="title" value="Available Products"/>
</jsp:include>

<div class="page-container">
    <h1>Available Products</h1>
    <p class="subtitle">Browse our current inventory</p>

    <c:if test="${not empty items}">
        <div class="products-grid">
            <c:forEach items="${items}" var="item">
                <div class="product-card">
                    <div class="product-header">
                        <h3>${item.name}</h3>
                        <span class="product-code">${item.code.value}</span>
                    </div>
                    <div class="product-details">
                        <div class="product-price">
                            $<fmt:formatNumber value="${item.price.value}" pattern="#,##0.00"/>
                        </div>
                        <div class="product-stock">
                            <span class="stock-label">In Stock:</span>
                            <span class="stock-quantity">${item.quantity.value} units</span>
                        </div>
                        <c:if test="${item.expiryDate != null}">
                            <div class="product-expiry">
                                <span class="expiry-label">Fresh until:</span>
                                <span class="expiry-date">${item.expiryDate}</span>
                            </div>
                        </c:if>
                    </div>
                    <div class="product-footer">
                        <span class="product-status available">Available</span>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:if>

    <c:if test="${empty items}">
        <div class="empty-state">
            <p>No products available at the moment.</p>
            <p>Please check back later.</p>
        </div>
    </c:if>
</div>

<style>
.subtitle {
    color: #7f8c8d;
    margin-bottom: 2rem;
    font-size: 1.1rem;
}

.products-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 1.5rem;
    margin-top: 2rem;
}

.product-card {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    padding: 1.5rem;
    transition: transform 0.3s, box-shadow 0.3s;
}

.product-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 8px rgba(0,0,0,0.15);
}

.product-header {
    border-bottom: 2px solid #ecf0f1;
    padding-bottom: 1rem;
    margin-bottom: 1rem;
}

.product-header h3 {
    margin: 0 0 0.5rem 0;
    color: #2c3e50;
    font-size: 1.3rem;
}

.product-code {
    font-family: monospace;
    background: #ecf0f1;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
    font-size: 0.85rem;
    color: #7f8c8d;
}

.product-details {
    margin: 1rem 0;
}

.product-price {
    font-size: 2rem;
    font-weight: bold;
    color: #27ae60;
    margin-bottom: 1rem;
}

.product-stock,
.product-expiry {
    margin: 0.5rem 0;
    font-size: 0.95rem;
}

.stock-label,
.expiry-label {
    color: #7f8c8d;
    margin-right: 0.5rem;
}

.stock-quantity {
    font-weight: bold;
    color: #2c3e50;
}

.expiry-date {
    color: #2c3e50;
}

.product-footer {
    border-top: 2px solid #ecf0f1;
    padding-top: 1rem;
    margin-top: 1rem;
}

.product-status {
    display: inline-block;
    padding: 0.5rem 1rem;
    border-radius: 20px;
    font-weight: bold;
    font-size: 0.9rem;
}

.product-status.available {
    background: #d4edda;
    color: #155724;
}

.empty-state {
    text-align: center;
    padding: 4rem 2rem;
    background: white;
    border-radius: 8px;
    margin-top: 2rem;
}

.empty-state p {
    font-size: 1.1rem;
    color: #7f8c8d;
    margin: 0.5rem 0;
}
</style>

<jsp:include page="common/footer.jsp"/>
