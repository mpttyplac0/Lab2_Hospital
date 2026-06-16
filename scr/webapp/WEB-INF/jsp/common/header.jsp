<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Лікарня - ${not empty param.pageTitle ? param.pageTitle : 'Система Записів'}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light mb-4">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/app?command=appointments-list">Лікарня</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link ${param.activePage == 'appointments' ? 'active' : ''}" aria-current="${param.activePage == 'appointments' ? 'page' : ''}" href="${pageContext.request.contextPath}/app?command=appointments-list">Записи</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${param.activePage == 'patients' ? 'active' : ''}" href="${pageContext.request.contextPath}/app?command=patients-list">Пацієнти</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${param.activePage == 'doctors' ? 'active' : ''}" href="${pageContext.request.contextPath}/app?command=doctors-list">Лікарі</a>
                </li>
            </ul>
            <ul class="navbar-nav">
                <li class="nav-item">
                    <a class="nav-link btn btn-outline-primary ${param.activePage == 'new_appointment' ? 'active' : ''}" href="${pageContext.request.contextPath}/app?command=new-appointment-form">Створити Запис</a>
                </li>
            </ul>
        </div>
    </div>
</nav>
<div class="container mt-4"> <%-- --%>