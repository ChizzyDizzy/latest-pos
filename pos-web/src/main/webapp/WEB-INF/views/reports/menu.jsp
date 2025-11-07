<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Reports"/>
</jsp:include>

<div class="page-container">
    <div class="page-header-inline">
        <div>
            <h1>Reports & Analytics</h1>
            <p class="subtitle">Generate various business reports to analyze sales, inventory, and operations</p>
        </div>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary">Back to Dashboard</a>
    </div>

    <div class="reports-grid">
        <div class="report-card">
            <div class="report-icon sales-icon">📊</div>
            <h3>Daily Sales Report</h3>
            <p>View total sales, revenue, and items sold for a specific date</p>
            <a href="${pageContext.request.contextPath}/reports/daily-sales" class="btn btn-primary">
                Generate Report
            </a>
        </div>

        <div class="report-card">
            <div class="report-icon inventory-icon">📦</div>
            <h3>Stock Report</h3>
            <p>Complete inventory status with batch-wise details</p>
            <a href="${pageContext.request.contextPath}/reports/stock" class="btn btn-primary">
                Generate Report
            </a>
        </div>

        <div class="report-card">
            <div class="report-icon reorder-icon">🔄</div>
            <h3>Reorder Level Report</h3>
            <p>Items falling below reorder threshold (50 units)</p>
            <a href="${pageContext.request.contextPath}/reports/reorder" class="btn btn-primary">
                Generate Report
            </a>
        </div>

        <div class="report-card">
            <div class="report-icon bills-icon">🧾</div>
            <h3>Bill History</h3>
            <p>View all customer transactions and bills</p>
            <a href="${pageContext.request.contextPath}/sales/list" class="btn btn-primary">
                View Bills
            </a>
        </div>

        <div class="report-card">
            <div class="report-icon alert-icon">⚠️</div>
            <h3>Low Stock Alert</h3>
            <p>Items needing immediate reordering</p>
            <a href="${pageContext.request.contextPath}/inventory/low-stock" class="btn btn-warning">
                View Alert
            </a>
        </div>

        <div class="report-card">
            <div class="report-icon expiry-icon">⏰</div>
            <h3>Expiring Items</h3>
            <p>Items expiring within the next 7 days</p>
            <a href="${pageContext.request.contextPath}/inventory/expiring" class="btn btn-danger">
                View Alert
            </a>
        </div>
    </div>
</div>

<style>
.subtitle {
    color: #7f8c8d;
    margin-bottom: 2rem;
    font-size: 1.1rem;
}

.reports-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 2rem;
    margin-top: 2rem;
}

.report-card {
    background: white;
    padding: 2rem;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    text-align: center;
    transition: transform 0.3s, box-shadow 0.3s;
}

.report-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.report-icon {
    font-size: 4rem;
    margin-bottom: 1rem;
}

.report-card h3 {
    color: #2c3e50;
    margin-bottom: 1rem;
}

.report-card p {
    color: #7f8c8d;
    margin-bottom: 1.5rem;
    min-height: 3rem;
}

.report-card .btn {
    width: 100%;
}

.page-header-inline {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 1.5rem;
}

.page-header-inline h1 {
    margin: 0 0 0.5rem 0;
}

.page-header-inline .subtitle {
    margin-bottom: 0;
}
</style>

<jsp:include page="../common/footer.jsp"/>
