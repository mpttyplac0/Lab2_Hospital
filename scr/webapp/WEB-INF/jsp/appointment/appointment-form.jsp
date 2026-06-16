<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="pageTitle" value="${not empty appointment.id ? 'Редагування Запису' : 'Створення Запису'}"/>
    <jsp:param name="activePage" value="${not empty appointment.id ? 'edit_appointment' : 'new_appointment'}"/>
</jsp:include>

<h2>${not empty appointment.id ? 'Редагувати Запис на Прийом' : 'Створити Новий Запис на Прийом'}</h2>

<c:if test="${not empty errorMessageOnForm}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${errorMessageOnForm}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>


<c:if test="${not empty errors}">
    <div class="alert alert-danger" role="alert">
        Будь ласка, виправте наступні помилки:
        <ul>
            <c:forEach var="fieldErrorEntry" items="${errors}"> <%-- errors - це Map<String, List<String>> --%>
                <c:forEach var="message" items="${fieldErrorEntry.value}">
                    <li>${message}</li>
                </c:forEach>
            </c:forEach>
        </ul>
    </div>
</c:if>

<form action="${pageContext.request.contextPath}/app" method="post" class="needs-validation" novalidate>
    <input type="hidden" name="command" value="${formAction}"/>

    <c:if test="${not empty appointment.id}">
        <input type="hidden" name="id" value="${appointment.id}"/>
    </c:if>

    <div class="mb-3">
        <label for="patientId" class="form-label">Пацієнт:</label>
        <select id="patientId" name="patientId" class="form-select" required onchange="showPatientDetails(this)">
            <option value="">-- Оберіть Пацієнта --</option>
            <c:forEach var="p" items="${patients}"> <%-- 'patients' - список пацієнтів, переданий з команди --%>
                <option value="${p.id}"
                        data-firstname="${p.firstName}" data-lastname="${p.lastName}"
                        data-age="${p.age}" data-gender="${p.gender}"
                    ${(not empty appointment.patient && appointment.patient.id == p.id) or (not empty submittedPatientId && submittedPatientId == p.id) ? 'selected' : ''}>
                        ${p.firstName} ${p.lastName} (ID: ${p.id})
                </option>
            </c:forEach>
        </select>
        <c:if test="${not empty errors.patientId}">
            <div class="text-danger invalid-feedback d-block"> <%-- d-block для відображення помилки Bootstrap --%>
                <c:forEach var="msg" items="${errors.patientId}">${msg}<br/></c:forEach>
            </div>
        </c:if>
        <div id="patientDetails" class="mt-2 p-2 border rounded bg-light" style="display: none;">
            <h6>Інформація про пацієнта:</h6>
            <p><strong>Ім'я:</strong> <span id="pDetailFirstName"></span> <span id="pDetailLastName"></span></p>
            <p><strong>Вік:</strong> <span id="pDetailAge"></span>, <strong>Стать:</strong> <span id="pDetailGender"></span></p>
        </div>
    </div>

    <div class="mb-3">
        <label for="doctorId" class="form-label">Лікар:</label>
        <select id="doctorId" name="doctorId" class="form-select" required onchange="showDoctorDetails(this)">
            <option value="">-- Оберіть Лікаря --</option>
            <c:forEach var="doc" items="${doctors}"> <%-- 'doctors' - список лікарів, переданий з команди --%>
                <option value="${doc.id}"
                        data-firstname="${doc.firstName}" data-lastname="${doc.lastName}"
                        data-specialization="${doc.specialization}"
                    ${(not empty appointment.doctor && appointment.doctor.id == doc.id) or (not empty submittedDoctorId && submittedDoctorId == doc.id) ? 'selected' : ''}>
                        ${doc.firstName} ${doc.lastName} - ${doc.specialization} (ID: ${doc.id})
                </option>
            </c:forEach>
        </select>
        <c:if test="${not empty errors.doctorId}">
            <div class="text-danger invalid-feedback d-block">
                <c:forEach var="msg" items="${errors.doctorId}">${msg}<br/></c:forEach>
            </div>
        </c:if>
        <div id="doctorDetails" class="mt-2 p-2 border rounded bg-light" style="display: none;">
            <h6>Інформація про лікаря:</h6>
            <p><strong>Ім'я:</strong> <span id="dDetailFirstName"></span> <span id="dDetailLastName"></span></p>
            <p><strong>Спеціалізація:</strong> <span id="dDetailSpecialization"></span></p>
        </div>
    </div>

    <div class="mb-3">
        <label for="appointmentTime" class="form-label">Час Прийому:</label>
        <%-- Визначаємо значення для поля вводу --%>
        <c:set var="timeValueToDisplay">
            <c:choose>
                <c:when test="${not empty submittedAppointmentTime}">
                    ${submittedAppointmentTime} <%-- Якщо є значення після невдалої валідації, використовуємо його --%>
                </c:when>
                <c:when test="${not empty appointmentUtilDate}"> <%-- ВИКОРИСТОВУЄМО ЦЕЙ АТРИБУТ (java.util.Date) --%>
                    <fmt:formatDate value="${appointmentUtilDate}" pattern="yyyy-MM-dd'T'HH:mm" />
                </c:when>
                <c:otherwise>
                    ${nowDateTimeString} <%-- nowDateTimeString має бути встановлений в команді для нових записів --%>
                </c:otherwise>
            </c:choose>
        </c:set>
        <input type="datetime-local" id="appointmentTime" name="appointmentTime" class="form-control"
               value="${timeValueToDisplay}" required/>
        <c:if test="${not empty errors.appointmentTime}">
            <div class="text-danger invalid-feedback d-block">
                <c:forEach var="msg" items="${errors.appointmentTime}">${msg}<br/></c:forEach>
            </div>
        </c:if>
    </div>

    <button type="submit" class="btn btn-success">${not empty appointment.id ? 'Оновити Запис' : 'Створити Запис'}</button>
    <a href="${pageContext.request.contextPath}/app?command=appointments-list" class="btn btn-secondary">Скасувати</a>
</form>

<script>
    function showPatientDetails(selectElement) {
        const selectedOption = selectElement.options[selectElement.selectedIndex];
        const detailsDiv = document.getElementById('patientDetails');
        if (selectedOption && selectedOption.value) { // Перевіряємо, чи обрано реальний пацієнт
            document.getElementById('pDetailFirstName').textContent = selectedOption.dataset.firstname;
            document.getElementById('pDetailLastName').textContent = selectedOption.dataset.lastname;
            document.getElementById('pDetailAge').textContent = selectedOption.dataset.age;
            document.getElementById('pDetailGender').textContent = selectedOption.dataset.gender;
            detailsDiv.style.display = 'block';
        } else {
            detailsDiv.style.display = 'none';
        }
    }

    function showDoctorDetails(selectElement) {
        const selectedOption = selectElement.options[selectElement.selectedIndex];
        const detailsDiv = document.getElementById('doctorDetails');
        if (selectedOption && selectedOption.value) { // Перевіряємо, чи обрано реального лікаря
            document.getElementById('dDetailFirstName').textContent = selectedOption.dataset.firstname;
            document.getElementById('dDetailLastName').textContent = selectedOption.dataset.lastname;
            document.getElementById('dDetailSpecialization').textContent = selectedOption.dataset.specialization;
            detailsDiv.style.display = 'block';
        } else {
            detailsDiv.style.display = 'none';
        }
    }

    document.addEventListener('DOMContentLoaded', function() {
        const patientSelect = document.getElementById('patientId');
        if (patientSelect && patientSelect.value) { // Перевірка, що значення не порожнє
            showPatientDetails(patientSelect);
        }
        const doctorSelect = document.getElementById('doctorId');
        if (doctorSelect && doctorSelect.value) { // Перевірка, що значення не порожнє
            showDoctorDetails(doctorSelect);
        }
    });
</script>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>