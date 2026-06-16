<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="pageTitle" value="Список Записів"/>
</jsp:include>

<h2>Список Записів на Прийом</h2>

<c:if test="${not empty param.success}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <c:choose>
            <c:when test="${param.success == 'created'}">Запис успішно створено!</c:when>
            <c:when test="${param.success == 'updated'}">Запис успішно оновлено!</c:when>
            <c:when test="${param.success == 'deleted'}">Запис успішно видалено!</c:when>
            <c:otherwise>Операція успішна!</c:otherwise>
        </c:choose>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>
<c:if test="${not empty errorMessageOnPage}"> <%-- Атрибут з контролера/команди --%>
    <div class="alert alert-danger">${errorMessageOnPage}</div>
</c:if>

<p><a href="${pageContext.request.contextPath}/app?command=new-appointment-form" class="btn btn-primary mb-3">Створити Новий Запис</a></p>

<c:choose>
    <c:when test="${not empty appointments}">
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <th>ID</th>
                <th>Пацієнт</th>
                <th>Лікар</th>
                <th>Спеціалізація</th>
                <th>Час Прийому</th>
                <th>Дії</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="appointment" items="${appointments}">
                <tr>
                    <td>${appointment.id}</td>
                    <td>${appointment.patient.firstName} ${appointment.patient.lastName}</td>
                    <td>${appointment.doctor.firstName} ${appointment.doctor.lastName}</td>
                    <td>${appointment.doctor.specialization}</td>
                    <td>
                            <%-- Для коректного форматування LocalDateTime з JSTL fmt --%>
                        <fmt:formatDate value="${parsedDateTime}" pattern="dd.MM.yyyy HH:mm" />
                        <fmt:formatDate value="${parsedDateTime}" pattern="dd.MM.yyyy HH:mm" />
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/app?command=edit-appointment-form&id=${appointment.id}" class="btn btn-sm btn-warning me-1">Редагувати</a>
                        <form action="${pageContext.request.contextPath}/app" method="post" style="display: inline;"
                              onsubmit="return confirm('Ви впевнені, що хочете видалити цей запис?');">
                            <input type="hidden" name="command" value="delete-appointment"/>
                            <input type="hidden" name="id" value="${appointment.id}"/>
                            <button type="submit" class="btn btn-sm btn-danger">Видалити</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p class="text-muted">Немає записів на прийом.</p>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>