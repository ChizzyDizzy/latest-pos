<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - SYOS POS System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="login-page">
    <div class="login-container">
        <div class="login-box">
            <h1>SYOS POS System</h1>
            <h2>Point of Sale - Web Application</h2>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error">
                    ${errorMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post" class="login-form">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" required autofocus
                           placeholder="Enter username">
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required
                           placeholder="Enter password">
                </div>

                <button type="submit" class="btn btn-primary btn-block">Login</button>
            </form>

            <div class="login-info">
                <h3>Demo Credentials:</h3>
                <p><strong>Admin:</strong> admin / admin123</p>
                <p><strong>Manager:</strong> manager1 / manager123</p>
                <p><strong>Cashier:</strong> cashier1 / cashier123</p>
            </div>

            <div class="system-info">
                <p>Multi-threaded concurrent POS system with MVC architecture</p>
                <p>Powered by Tomcat 9 Thread Pool</p>
            </div>
        </div>
    </div>
</body>
</html>
