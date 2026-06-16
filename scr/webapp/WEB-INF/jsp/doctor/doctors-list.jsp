<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="pageTitle" value="Список Лікарів"/>
</jsp:include>

<h2>Список Лікарів</h2>

<c:if test="${not empty param.success}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <c:choose>
            <c:when test="${param.success == 'doctor_created'}">Лікаря успішно додано!</c:when>
            <c:when test="${param.success == 'doctor_updated'}">Дані лікаря успішно оновлено!</c:when>
            <c:when test="${param.success == 'doctor_deleted'}">Лікаря успішно видалено!</c:when>
        </c:choose>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>
<c:if test="${not empty param.error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <c:choose>
            <c:when test="${param.error == 'doctor_missingId'}">ID лікаря не вказано.</c:when>
            <c:when test="${param.error == 'doctor_invalidIdFormat'}">Невірний формат ID лікаря.</c:when>
            <c:when test="${param.error == 'doctor_deleteFailed'}">Не вдалося видалити лікаря (можливо, є пов'язані записи).</c:when>
            <c:when test="${param.error == 'doctor_deleteFailed_encoding'}">Помилка кодування повідомлення про невдале видалення.</c:when>
            <c:otherwise>Сталася помилка.</c:otherwise>
        </c:choose>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>
<c:if test="${not empty errorMessage}"> <%-- Загальна помилка, передана через request.setAttribute --%>
    <div class="alert alert-danger">${errorMessage}</div>
</c:if>


<p><a href="${pageContext.request.contextPath}/app?command=new-doctor-form" class="btn btn-primary mb-3">Додати Нового Лікаря</a></p>

<c:choose>
    <c:when test="${not empty doctors}">
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <th>ID</th>
                <th>Ім'я</th>
                <th>Прізвище</th>
                <th>Спеціалізація</th>
                <th>Дії</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="doctor" items="${doctors}">
                <tr>
                    <td>${doctor.id}</td>
                    <td>${doctor.firstName}</td>
                    <td>${doctor.lastName}</td>
                    <td>${doctor.specialization}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/app?command=edit-doctor-form&id=${doctor.id}" class="btn btn-sm btn-warning me-1">Редагувати</a>
                        <form action="${pageContext.request.contextPath}/app" method="post" style="display: inline;"
                              onsubmit="return confirm('Ви впевнені, що хочете видалити цього лікаря? Це може видалити пов\'язані записи на прийом, якщо налаштовано каскадне видалення.');">
                            <input type="hidden" name="command" value="delete-doctor"/>
                            <input type="hidden" name="id" value="${doctor.id}"/>
                            <button type="submit" class="btn btn-sm btn-danger">Видалити</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p class="text-muted">Немає зареєстрованих лікарів.</p>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>