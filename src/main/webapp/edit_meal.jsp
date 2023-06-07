<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html lang="ru">
<head>
    <title>Add/edit meal</title>
    <link href="tables.css" rel="stylesheet" type="text/css">
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>
    <c:if test="${requestScope.meal != null}">
        <span>Edit meal</span>
        <jsp:useBean id="meal" scope="request"
                     type="ru.javawebinar.topjava.model.Meal"/>
    </c:if>
    <c:if test="${meal == null}">
        <span>Add meal</span>
    </c:if>
</h2>
<form method="POST"
      action="${pageContext.request.contextPath}/meals?id=${meal.id}"
      name="formEditMeal">
    <%--    <label for="mealId">Meal ID:</label>--%>
    <%--    <input type="text" readonly="readonly" id="mealId"--%>
    <%--           value=${meal.id}>--%>
    <%--    <p>--%>
    <label for="dateTimeId">Date time:</label>
    <input type="datetime-local" name="dateTime" id="dateTimeId" required
           value=${meal.dateTime}>
    <p>
        <label for="descrId">Description:</label>
        <input type="text" name="description" id="descrId" required
               value="${meal.description}">
    <p>
        <label for="caloriesId">Calories:</label>
        <input type="number" name="calories" id="caloriesId" required
               value=${meal.calories}>
    <p>
        <input type="submit" value="Save">
        <button onclick="window.history.back()" type="button">Cancel</button>
</form>
</body>
</html>