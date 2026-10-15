<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Start budgeting | 50/30/20 Budget</title>
    <link rel="stylesheet" href="<c:url value='/css/app.css'/>">
</head>
<body class="welcome-page">
<main class="welcome-layout">
    <section class="welcome-copy">
        <a class="brand" href="<c:url value='/'/>"><span class="brand-mark">B</span> Budgetwise</a>
        <p class="eyebrow">A clearer month starts here</p>
        <h1>Give every dollar a direction.</h1>
        <p class="intro">Plan your monthly income with the 50/30/20 rule, track what you spend, and see what remains in each category.</p>
        <div class="rule-card">
            <div><span class="rule-dot needs"></span><strong>50%</strong><span>Needs</span></div>
            <div><span class="rule-dot wants"></span><strong>30%</strong><span>Wants</span></div>
            <div><span class="rule-dot savings"></span><strong>20%</strong><span>Savings</span></div>
        </div>
    </section>
    <section class="panel signup-panel">
        <p class="eyebrow">Get started</p>
        <h2>Create your profile</h2>
        <p class="muted">Your profile keeps your monthly budgets together on this device.</p>
        <c:if test="${not empty pageError}">
            <div class="alert" role="alert"><c:out value="${pageError}"/></div>
        </c:if>
        <form:form modelAttribute="userForm" method="post" action="/users" cssClass="stacked-form">
            <form:errors path="*" cssClass="form-error" element="div"/>
            <label for="name">Name</label>
            <form:input path="name" id="name" maxlength="100" autocomplete="name" placeholder="Alex Example"/>
            <label for="email">Email</label>
            <form:input path="email" id="email" type="email" maxlength="254" autocomplete="email" placeholder="alex@example.com"/>
            <button class="button button-primary" type="submit">Create profile <span aria-hidden="true">→</span></button>
        </form:form>
        <p class="fine-print">This classroom project does not include sign-in or account security. Do not enter sensitive personal information.</p>
    </section>
</main>
</body>
</html>
