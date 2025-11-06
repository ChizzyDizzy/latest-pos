<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Daily Sales Report"/>
</jsp:include>

<div class="page-container">
    <div class="page-header">
        <h1>Daily Sales Report</h1>
        <button onclick="window.print()" class="btn btn-primary">Print Report</button>
    </div>

    <div class="report-controls">
        <form method="get" action="${pageContext.request.contextPath}/reports/daily-sales" class="date-form">
            <label for="date">Select Date:</label>
            <input type="date" id="date" name="date" value="${reportDate}"
                   max="<fmt:formatDate value='${java.util.Date()}' pattern='yyyy-MM-dd'/>">
            <button type="submit" class="btn btn-primary">Generate</button>
        </form>
    </div>

    <div class="report-container">
        <div class="report-header">
            <h2>SYOS - Daily Sales Report</h2>
            <p>Date: <fmt:formatDate value="${reportDate}" pattern="MMMM dd, yyyy"/></p>
            <p>Generated: <fmt:formatDate value="${java.util.Date()}" pattern="MMM dd, yyyy HH:mm:ss"/></p>
        </div>

        <div class="report-content">
            <pre>${report}</pre>
        </div>
    </div>

    <div class="report-actions">
        <a href="${pageContext.request.contextPath}/reports" class="btn btn-secondary">Back to Reports</a>
    </div>
</div>

<style>
.report-controls {
    background: white;
    padding: 1.5rem;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    margin-bottom: 2rem;
}

.date-form {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.date-form label {
    font-weight: 500;
}

.date-form input[type="date"] {
    padding: 0.5rem;
    border: 1px solid #ddd;
    border-radius: 4px;
}

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
    text-align: center;
}

@media print {
    .navbar, .page-header, .report-controls, .report-actions, .footer, .btn {
        display: none;
    }

    .report-container {
        box-shadow: none;
    }
}
</style>

<script>
// Set max date to today
document.getElementById('date').max = new Date().toISOString().split('T')[0];
</script>

<jsp:include page="../common/footer.jsp"/>
