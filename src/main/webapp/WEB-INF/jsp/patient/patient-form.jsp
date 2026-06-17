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
    <c:if test="${not empty patient.id}">
        <input type="hidden" name="id" value="${patient.id}"/>
    </c:if>

    <div class="mb-3">
        <label for="firstName" class="form-label">Ім'я:</label>
        <input type="text" id="firstName" name="firstName" class="form-control"
               value="${not empty patient.firstName ? patient.firstName : submittedFirstName}" required minlength="2" maxlength="100">
        <c:if test="${not empty errors.firstName}"><div class="text-danger"><c:forEach var="msg" items="${errors.firstName}">${msg}<br/></c:forEach></div></c:if>
    </div>

    <div class="mb-3">
        <label for="lastName" class="form-label">Прізвище:</label>
        <input type="text" id="lastName" name="lastName" class="form-control"
               value="${not empty patient.lastName ? patient.lastName : submittedLastName}" required minlength="2" maxlength="100">
        <c:if test="${not empty errors.lastName}"><div class="text-danger"><c:forEach var="msg" items="${errors.lastName}">${msg}<br/></c:forEach></div></c:if>
    </div>

    <div class="mb-3">
        <label for="age" class="form-label">Вік:</label>
        <input type="number" id="age" name="age" class="form-control"
               value="${not empty patient.age ? patient.age : submittedAge}" required min="1" max="120">
        <c:if test="${not empty errors.age}"><div class="text-danger"><c:forEach var="msg" items="${errors.age}">${msg}<br/></c:forEach></div></c:if>
    </div>

    <div class="mb-3">
        <label for="gender" class="form-label">Стать:</label>
        <select id="gender" name="gender" class="form-select">
            <option value="" ${ (empty patient.gender && empty submittedGender) ? 'selected' : ''}>-- Оберіть стать --</option>
            <option value="Чоловік" ${ ( (not empty patient.gender && patient.gender == 'Чоловік') or (not empty submittedGender && submittedGender == 'Чоловік') ) ? 'selected' : ''}>Чоловік</option>
            <option value="Жінка" ${ ( (not empty patient.gender && patient.gender == 'Жінка') or (not empty submittedGender && submittedGender == 'Жінка') ) ? 'selected' : ''}>Жінка</option>

        </select>
        <c:if test="${not empty errors.gender}"><div class="text-danger"><c:forEach var="msg" items="${errors.gender}">${msg}<br/></c:forEach></div></c:if>
    </div>

    <button type="submit" class="btn btn-success">${not empty patient.id ? 'Оновити Пацієнта' : 'Створити Пацієнта'}</button>
    <a href="${pageContext.request.contextPath}/app?command=patients-list" class="btn btn-secondary">Скасувати</a>
</form>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>