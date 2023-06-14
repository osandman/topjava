<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Java Enterprise (Topjava)</title>
</head>
<body>
<h3>Проект <a href="https://github.com/JavaWebinar/topjava" target="_blank">Java Enterprise (Topjava)</a></h3>
<hr>
<ul style="font-size: large">
    <li><a href="users.jsp">Users</a></li>
    <li><a href="meals">Meals</a></li>
</ul>
<section>
    <span>Current user: ${requestScope.authUser.name}</span>
    <p>
    <form method="post" action="users">
        <label for="userId">Select user:
            <br>
            <select name="user" id="userId">
                <c:forEach var="user" items="${requestScope.users}">
                    <option value=${user.email}>${user.name} (${user.email})</option>
                </c:forEach>
            </select>
        </label>
        <p>
            <button type="submit">Save</button>
    </form>
</section>
</body>
</html>
