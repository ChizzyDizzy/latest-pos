<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Reorder Report"/>
</jsp:include>

<div class="page-container">
    <div class="page-header">
        <h1>Reorder Level Report</h1>
        <button onclick="window.print()" class="btn btn-primary">Print Report</button>
    </div>

    <div class="alert alert-warning">
        <strong>Reorder Threshold:</strong> Items with stock below 50 units require reordering
    </div>

    <div class="report-container">
        <div class="report-header">
            <h2>SYOS - Reorder Level Report</h2>
            <p>Generated: <fmt:formatDate value="${java.util.Date()}" pattern="MMM dd, yyyy HH:mm:ss"/></p>
        </div>

        <div class="report-content">
            <pre>${report}</pre>
        </div>
    </div>

    <div class="report-actions">
        <a href="${pageContext.request.contextPath}/reports" class="btn btn-secondary">Back to Reports</a>
        <a href="${pageContext.request.contextPath}/inventory/add" class="btn btn-primary">Add Stock</a>
    </div>
</div>

<style>
.report-container {
    background: white;
    padding: 2rem;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    margin-bottom: 2rem;
}

.report-header {
    text-align: center;
    border-bottom: 2px solid #2c3e50;
    padding-bottom: 1rem;
    margin-bottom: 2rem;
}

.report-header h2 {
    color: #2c3e50;
    margin-bottom: 0.5rem;
}

.report-header p {
    color: #7f8c8d;
    margin: 0.25rem 0;
}

.report-content {
    font-family: 'Courier New', monospace;
    white-space: pre-wrap;
    line-height: 1.6;
}

.report-content pre {
    margin: 0;
    font-size: 0.95rem;
}

.report-actions {
    display: flex;
    justify-content: center;
    gap: 1rem;
}

@media print {
    .navbar, .page-header, .alert, .report-actions, .footer, .btn {
        display: none;
    }

    .report-container {
        box-shadow: none;
    }
}
</style>

<jsp:include page="../common/footer.jsp"/>
