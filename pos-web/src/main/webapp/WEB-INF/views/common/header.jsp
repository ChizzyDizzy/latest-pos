<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.title} - SYOS POS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="nav-container">
            <div class="nav-brand">
                <h1>SYOS POS System</h1>
            </div>
            <ul class="nav-menu">
                <li><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>

                <%-- CUSTOMER: Only Products --%>
                <c:if test="${sessionScope.userRole == 'CUSTOMER'}">
                    <li><a href="${pageContext.request.contextPath}/products">Products</a></li>
                </c:if>

                <%-- CASHIER: Sales and Reports only --%>
                <c:if test="${sessionScope.userRole == 'CASHIER'}">
                    <li><a href="${pageContext.request.contextPath}/sales">Sales</a></li>
                    <li><a href="${pageContext.request.contextPath}/reports">Reports</a></li>
                </c:if>

                <%-- MANAGER: All except User Management --%>
                <c:if test="${sessionScope.userRole == 'MANAGER'}">
                    <li><a href="${pageContext.request.contextPath}/sales">Sales</a></li>
                    <li><a href="${pageContext.request.contextPath}/inventory">Inventory</a></li>
                    <li><a href="${pageContext.request.contextPath}/reports">Reports</a></li>
                </c:if>

                <%-- ADMIN: Full Access --%>
                <c:if test="${sessionScope.userRole == 'ADMIN'}">
                    <li><a href="${pageContext.request.contextPath}/sales">Sales</a></li>
                    <li><a href="${pageContext.request.contextPath}/inventory">Inventory</a></li>
                    <li><a href="${pageContext.request.contextPath}/reports">Reports</a></li>
                    <li><a href="${pageContext.request.contextPath}/users">Users</a></li>
                </c:if>
            </ul>
            <div class="nav-user">
                <span>Welcome, <strong>${sessionScope.username}</strong> (${sessionScope.userRole})</span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-logout">Logout</a>
            </div>
        </div>
    </nav>
    <div class="main-container">
