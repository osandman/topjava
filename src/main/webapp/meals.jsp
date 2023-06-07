<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html lang="ru">
<head>
    <title>Meals</title>
    <link href="tables.css" rel="stylesheet" type="text/css">
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<h3><a href="meals?action=add">Add meal</a></h3>
<table>
    <thead>
    <tr>
        <%--        <th scope="col">id</th>--%>
        <th scope="col">Date</th>
        <th scope="col">Description</th>
        <th scope="col">Calories</th>
        <th scope="col"></th>
        <th scope="col"></th>
    </tr>
    </thead>
    <tbody>
    <jsp:useBean id="mealsTo" scope="request"
                 type="java.util.List<ru.javawebinar.topjava.model.MealTo>"/>
    <jsp:useBean id="dtFormatter" scope="request"
                 type="java.time.format.DateTimeFormatter"/>
    <c:forEach var="mealTo" items="${mealsTo}">
        <tr class="${mealTo.excess ? "isExcess": "notExcess"}">
                <%--            <td>${mealTo.id}</td>--%>
            <td>${mealTo.dateTime.format(dtFormatter)}</td>
            <td>${mealTo.description}</td>
            <td>${mealTo.calories}</td>
            <td><a href="meals?action=edit&id=${mealTo.id}">Update</a></td>
            <td><a href="meals?action=delete&id=${mealTo.id}">Delete</a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>