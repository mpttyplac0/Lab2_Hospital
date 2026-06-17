<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="pageTitle" value="${pageTitle}"/>
</jsp:include>

<h2>${pageTitle}</h2>

<c:if test="${not empty errorMessageOnForm}">
    <div class="alert alert-danger">${errorMessageOnForm}</div>
</c:if>
<c:if test="${not empty errors}">
    <div class="alert alert-danger">
        Будь ласка, виправте наступні помилки:
        <ul>
            <c:forEach var="fieldErrors" items="${errors}">
                <c:forEach var="message" items="${fieldErrors.value}">
                    <li>${message}</li>
                </c:forEach>
            </c:forEach>
        </ul>
    </div>
</c:if>

<form action="${pageContext.request.contextPath}/app" method="post">
    <input type="hidden" name="command" value="${formAction}"/>
    <c:if test="${not empty doctor.id}">
        <input type="hidden" name="id" value="${doctor.id}"/>
    </c:if>

    <div class="mb-3">
        <label for="firstName" class="form-label">Ім'я:</label>
        <input type="text" id="firstName" name="firstName" class="form-control"
               value="${not empty doctor.firstName ? doctor.firstName : submittedFirstName}" required minlength="2" maxlength="100">
        <c:if test="${not empty errors.firstName}"><div class="text-danger"><c:forEach var="msg" items="${errors.firstName}">${msg}<br/></c:forEach></div></c:if>
    </div>

    <div class="mb-3">
        <label for="lastName" class="form-label">Прізвище:</label>
        <input type="text" id="lastName" name="lastName" class="form-control"
               value="${not empty doctor.lastName ? doctor.lastName : submittedLastName}" required minlength="2" maxlength="100">
        <c:if test="${not empty errors.lastName}"><div class="text-danger"><c:forEach var="msg" items="${errors.lastName}">${msg}<br/></c:forEach></div></c:if>
    </div>

    <div class="mb-3">
        <label for="specialization" class="form-label">Спеціалізація:</label>
        <input type="text" id="specialization" name="specialization" class="form-control"
               value="${not empty doctor.specialization ? doctor.specialization : submittedSpecialization}" required minlength="2" maxlength="100">
        <c:if test="${not empty errors.specialization}"><div class="text-danger"><c:forEach var="msg" items="${errors.specialization}">${msg}<br/></c:forEach></div></c:if>
    </div>

    <button type="submit" class="btn btn-success">${not empty doctor.id ? 'Оновити Лікаря' : 'Створити Лікаря'}</button>
    <a href="${pageContext.request.contextPath}/app?command=doctors-list" class="btn btn-secondary">Скасувати</a>
</form>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>