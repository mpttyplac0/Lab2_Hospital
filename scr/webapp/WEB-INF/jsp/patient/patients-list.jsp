<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="pageTitle" value="Список Пацієнтів"/>
</jsp:include>

<h2>Список Пацієнтів</h2>

<c:if test="${not empty param.success}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <c:choose>
            <c:when test="${param.success == 'patient_created'}">Пацієнта успішно додано!</c:when>
            <c:when test="${param.success == 'patient_updated'}">Дані пацієнта успішно оновлено!</c:when>
            <c:when test="${param.success == 'patient_deleted'}">Пацієнта успішно видалено!</c:when>
        </c:choose>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>
<c:if test="${not empty param.error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <c:choose>
            <c:when test="${param.error == 'patient_missingId'}">ID пацієнта не вказано.</c:when>
            <c:when test="${param.error == 'patient_invalidIdFormat'}">Невірний формат ID пацієнта.</c:when>
            <c:when test="${param.error == 'patient_deleteFailed'}">Не вдалося видалити пацієнта (можливо, є пов'язані записи).</c:when>
            <c:otherwise>Сталася помилка.</c:otherwise>
        </c:choose>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">${errorMessage}</div>
</c:if>

<p><a href="${pageContext.request.contextPath}/app?command=new-patient-form" class="btn btn-primary mb-3">Додати Нового Пацієнта</a></p>

<c:choose>
    <c:when test="${not empty patients}">
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <th>ID</th>
                <th>Ім'я</th>
                <th>Прізвище</th>
                <th>Вік</th>
                <th>Стать</th>
                <th>Дії</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="patient" items="${patients}">
                <tr>
                    <td>${patient.id}</td>
                    <td>${patient.firstName}</td>
                    <td>${patient.lastName}</td>
                    <td>${patient.age}</td>
                    <td>${not empty patient.gender ? patient.gender : '-'}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/app?command=edit-patient-form&id=${patient.id}" class="btn btn-sm btn-warning me-1">Редагувати</a>
                        <form action="${pageContext.request.contextPath}/app" method="post" style="display: inline;"
                              onsubmit="return confirm('Ви впевнені, що хочете видалити цього пацієнта? Це може видалити пов\'язані записи на прийом, якщо налаштовано каскадне видалення.');">
                            <input type="hidden" name="command" value="delete-patient"/>
                            <input type="hidden" name="id" value="${patient.id}"/>
                            <button type="submit" class="btn btn-sm btn-danger">Видалити</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p class="text-muted">Немає зареєстрованих пацієнтів.</p>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>