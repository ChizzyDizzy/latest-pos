<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Register User"/>
</jsp:include>

<div class="page-container">
    <div class="page-header">
        <h1>Register New User</h1>
        <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary">Back to Users</a>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">${errorMessage}</div>
    </c:if>

    <div class="form-container">
        <form action="${pageContext.request.contextPath}/users/register" method="post" class="form-styled">
            <div class="form-group">
                <label for="username">Username *</label>
                <input type="text" id="username" name="username" required
                       placeholder="Enter unique username" pattern="[a-zA-Z0-9_]{3,20}"
                       title="3-20 characters, letters, numbers and underscore only">
                <small>3-20 characters, letters, numbers and underscore only</small>
            </div>

            <div class="form-group">
                <label for="email">Email *</label>
                <input type="email" id="email" name="email" required
                       placeholder="user@example.com">
            </div>

            <div class="form-group">
                <label for="password">Password *</label>
                <input type="password" id="password" name="password" required
                       minlength="6" placeholder="Minimum 6 characters">
                <small>Minimum 6 characters</small>
            </div>

            <div class="form-group">
                <label for="confirmPassword">Confirm Password *</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required
                       minlength="6" placeholder="Re-enter password">
            </div>

            <div class="form-group">
                <label for="role">User Role *</label>
                <select id="role" name="role" required>
                    <option value="">Select Role</option>
                    <option value="CASHIER">Cashier - Can create sales and view bills</option>
                    <option value="MANAGER">Manager - Cashier + Inventory & Reports</option>
                    <option value="ADMIN">Admin - Full system access</option>
                </select>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-block">Register User</button>
                <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary btn-block">Cancel</a>
            </div>
        </form>
    </div>
</div>

<style>
.form-container {
    max-width: 600px;
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
.form-styled select {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
}

.form-styled input:focus,
.form-styled select:focus {
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

.form-actions {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    margin-top: 2rem;
}
</style>

<script>
// Password confirmation validation
document.querySelector('form').addEventListener('submit', function(e) {
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (password !== confirmPassword) {
        e.preventDefault();
        alert('Passwords do not match!');
        return false;
    }
});
</script>

<jsp:include page="../common/footer.jsp"/>
