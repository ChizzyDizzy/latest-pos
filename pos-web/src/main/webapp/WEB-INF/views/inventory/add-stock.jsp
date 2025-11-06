<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Add Stock"/>
</jsp:include>

<div class="page-container">
    <div class="page-header">
        <h1>Add Stock to Inventory</h1>
        <a href="${pageContext.request.contextPath}/inventory" class="btn btn-secondary">Back to Inventory</a>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">${errorMessage}</div>
    </c:if>

    <div class="form-container">
        <form action="${pageContext.request.contextPath}/inventory/add" method="post" class="form-styled">
            <div class="form-group">
                <label for="code">Item Code *</label>
                <input type="text" id="code" name="code" required placeholder="e.g., ITEM001">
                <small>Unique identifier for the item. If code exists, quantity will be added to existing item.</small>
            </div>

            <div class="form-group">
                <label for="name">Item Name *</label>
                <input type="text" id="name" name="name" required placeholder="e.g., Rice 5kg">
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="price">Price ($) *</label>
                    <input type="number" id="price" name="price" step="0.01" min="0.01" required placeholder="0.00">
                </div>

                <div class="form-group">
                    <label for="quantity">Quantity *</label>
                    <input type="number" id="quantity" name="quantity" min="1" required placeholder="1">
                </div>
            </div>

            <div class="form-group">
                <label for="expiryDate">Expiry Date *</label>
                <input type="date" id="expiryDate" name="expiryDate" required>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-block">Add Stock</button>
                <a href="${pageContext.request.contextPath}/inventory" class="btn btn-secondary btn-block">Cancel</a>
            </div>
        </form>
    </div>
</div>

<style>
.form-container {
    max-width: 800px;
    margin: 2rem auto;
    background: white;
    padding: 2rem;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.form-styled .form-group {
    margin-bottom: 1.5rem;
}

.form-styled label {
    display: block;
    margin-bottom: 0.5rem;
    color: #2c3e50;
    font-weight: 500;
}

.form-styled input,
.form-styled select,
.form-styled textarea {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
}

.form-styled input:focus,
.form-styled select:focus,
.form-styled textarea:focus {
    outline: none;
    border-color: #3498db;
    box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.form-styled small {
    display: block;
    margin-top: 0.25rem;
    color: #7f8c8d;
    font-size: 0.85rem;
}

.form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1rem;
}

.form-actions {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    margin-top: 2rem;
}

@media (max-width: 768px) {
    .form-row {
        grid-template-columns: 1fr;
    }
}
</style>

<script>
// Set minimum date to today
document.getElementById('expiryDate').min = new Date().toISOString().split('T')[0];
</script>

<jsp:include page="../common/footer.jsp"/>
