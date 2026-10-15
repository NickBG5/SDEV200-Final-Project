<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Monthly budgets | 50/30/20 Budget</title>
    <link rel="stylesheet" href="<c:url value='/css/app.css'/>">
</head>
<body>
<header class="site-header">
    <a class="brand" href="<c:url value='/users/${user.id}/budgets'/>"><span class="brand-mark">B</span> Budgetwise</a>
    <span class="user-label">Hi, <c:out value="${user.name}"/></span>
</header>
<main class="page-shell">
    <div class="page-heading">
        <div>
            <p class="eyebrow">Your plan</p>
            <h1>Monthly budgets</h1>
            <p class="muted">Each month gets its own 50/30/20 plan and expense list.</p>
        </div>
        <div class="avatar" aria-hidden="true"><c:out value="${user.name.substring(0, 1)}"/></div>
    </div>

    <section class="panel create-budget-panel">
        <div>
            <p class="eyebrow">New month</p>
            <h2>Create a budget</h2>
        </div>
        <form:form modelAttribute="budgetForm" method="post" action="/users/${user.id}/budgets" cssClass="budget-form">
            <form:errors path="*" cssClass="form-error" element="div"/>
            <label for="month">Month</label>
            <form:input path="month" id="month" type="month"/>
            <label for="monthlyIncome">Monthly income</label>
            <form:input path="monthlyIncome" id="monthlyIncome" type="number" min="0.01" step="0.01" placeholder="4000.00"/>
            <button class="button button-primary" type="submit">Create budget</button>
        </form:form>
    </section>

    <section class="budget-section">
        <div class="section-heading">
            <div><p class="eyebrow">Overview</p><h2>Your months</h2></div>
            <span class="muted"><c:out value="${budgets.size()}"/> total</span>
        </div>
        <c:choose>
            <c:when test="${empty budgets}">
                <div class="empty-state">
                    <span class="empty-icon" aria-hidden="true">↗</span>
                    <h3>Your first budget is one step away</h3>
                    <p>Add a month and your income above. We’ll split it into needs, wants, and savings.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="budget-grid">
                    <c:forEach items="${budgets}" var="budget">
                        <a class="budget-card" href="<c:url value='/users/${user.id}/budgets/${budget.id}'/>">
                            <div class="card-topline"><span class="month-chip"><c:out value="${budget.month}"/></span><span class="card-arrow" aria-hidden="true">↗</span></div>
                            <p class="card-label">Monthly income</p>
                            <p class="card-income">$<c:out value="${budget.monthlyIncome}"/></p>
                            <div class="mini-allocations">
                                <c:forEach items="${budget.categories}" var="category">
                                    <div class="mini-allocation">
                                        <span><c:out value="${category.label}"/></span>
                                        <strong>$<c:out value="${category.allocated}"/></strong>
                                    </div>
                                </c:forEach>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</main>
</body>
</html>
