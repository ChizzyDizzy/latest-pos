<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="title" value="Error"/>
</jsp:include>

<div class="page-container">
    <div class="error-container">
        <div class="error-icon">⚠️</div>
        <h1>Oops! Something went wrong</h1>

        <c:if test="${not empty errorMessage}">
            <div class="error-message">
                ${errorMessage}
            </div>
        </c:if>

        <c:if test="${not empty exception}">
            <div class="error-details">
                <h3>Error Details:</h3>
                <p><strong>Type:</strong> ${exception.class.name}</p>
                <p><strong>Message:</strong> ${exception.message}</p>
            </div>
        </c:if>

        <div class="error-actions">
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">Go to Dashboard</a>
            <a href="javascript:history.back()" class="btn btn-secondary">Go Back</a>
        </div>
    </div>
</div>

<style>
.error-container {
    max-width: 600px;
    margin: 3rem auto;
    text-align: center;
    background: white;
    padding: 3rem;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.error-icon {
    font-size: 5rem;
    margin-bottom: 1.5rem;
}

.error-container h1 {
    color: #e74c3c;
    margin-bottom: 1.5rem;
}

.error-message {
    background-color: #f8d7da;
    color: #721c24;
    padding: 1rem;
    border-radius: 4px;
    margin-bottom: 1.5rem;
    border: 1px solid #f5c6cb;
}

.error-details {
    text-align: left;
    background-color: #f8f9fa;
    padding: 1.5rem;
    border-radius: 4px;
    margin-bottom: 2rem;
    border: 1px solid #dee2e6;
}

.error-details h3 {
    margin-bottom: 1rem;
    color: #2c3e50;
}

.error-details p {
    margin: 0.5rem 0;
    color: #495057;
    word-break: break-word;
}

.error-actions {
    display: flex;
    justify-content: center;
    gap: 1rem;
}
</style>

<jsp:include page="common/footer.jsp"/>
