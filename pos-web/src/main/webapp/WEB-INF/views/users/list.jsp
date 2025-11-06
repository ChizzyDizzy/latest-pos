<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="User Management"/>
</jsp:include>

<div class="page-container">
    <div class="page-header">
        <h1>User Management</h1>
        <a href="${pageContext.request.contextPath}/users/register" class="btn btn-primary">Register New User</a>
    </div>

    <c:if test="${not empty param.success}">
        <div class="alert alert-success">${param.success}</div>
    </c:if>

    <div class="content-box">
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Created At</th>
                    <th>Last Login</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${users}" var="user">
                    <tr>
                        <td>${user.id.value}</td>
                        <td><strong>${user.username}</strong></td>
                        <td>${user.email}</td>
                        <td><span class="role-badge role-${user.role}">${user.role}</span></td>
                        <td><fmt:formatDate value="${user.createdAt}" pattern="MMM dd, yyyy HH:mm"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${user.lastLoginAt != null}">
                                    <fmt:formatDate value="${user.lastLoginAt}" pattern="MMM dd, yyyy HH:mm"/>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted">Never</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty users}">
                    <tr>
                        <td colspan="6" class="text-center">No users found</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<style>
.role-badge {
    padding: 0.25rem 0.75rem;
    border-radius: 12px;
    font-size: 0.85rem;
    font-weight: bold;
    color: white;
}

.role-ADMIN {
    background-color: #e74c3c;
}

.role-MANAGER {
    background-color: #3498db;
}

.role-CASHIER {
    background-color: #27ae60;
}

.text-muted {
    color: #95a5a6;
    font-style: italic;
}
</style>

<jsp:include page="../common/footer.jsp"/>
