<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${budget.month}"/> budget | 50/30/20 Budget</title>
    <link rel="stylesheet" href="<c:url value='/css/app.css'/>">
</head>
<body>
<header class="site-header">
    <a class="brand" href="<c:url value='/users/${user.id}/budgets'/>"><span class="brand-mark">B</span> Budgetwise</a>
    <a class="back-link" href="<c:url value='/users/${user.id}/budgets'/>">← All budgets</a>
</header>
<main class="page-shell">
    <div class="page-heading detail-heading">
        <div>
            <p class="eyebrow">Monthly budget</p>
            <h1><c:out value="${budget.month}"/></h1>
            <p class="muted">Your income of <strong>$<c:out value="${budget.monthlyIncome}"/></strong> is split across three spending goals.</p>
        </div>
        <span class="month-chip large-chip">50 / 30 / 20</span>
    </div>

    <section class="category-grid" aria-label="Budget category summaries">
        <c:forEach items="${budget.categories}" var="category">
            <article class="category-card ${category.name}">
                <div class="category-title">
                    <span class="category-symbol" aria-hidden="true">
                        <c:choose><c:when test="${category.name eq 'NEEDS'}">⌂</c:when><c:when test="${category.name eq 'WANTS'}">✦</c:when><c:otherwise>◎</c:otherwise></c:choose>
                    </span>
                    <div><h2><c:out value="${category.label}"/></h2><p>50/30/20 allocation</p></div>
                </div>
                <div class="amount-line"><span>Allocated</span><strong>$<c:out value="${category.allocated}"/></strong></div>
                <div class="amount-line"><span>Spent</span><strong>$<c:out value="${category.spent}"/></strong></div>
                <div class="progress-track" role="progressbar" aria-label="${category.label} budget used" aria-valuenow="${category.spent}" aria-valuemin="0" aria-valuemax="${category.allocated}">
                    <c:choose>
                        <c:when test="${category.allocated gt 0}">
                            <c:set var="percent" value="${category.spent * 100 / category.allocated}"/>
                            <c:choose><c:when test="${percent gt 100}"><c:set var="percent" value="100"/></c:when></c:choose>
                            <span style="width: ${percent}%"></span>
                        </c:when>
                        <c:otherwise><span style="width: 0%"></span></c:otherwise>
                    </c:choose>
                </div>
                <div class="remaining-line"><span>Remaining</span><strong>$<c:out value="${category.remaining}"/></strong></div>
            </article>
        </c:forEach>
    </section>

    <div class="detail-columns">
        <section class="panel expense-panel">
            <div class="section-heading">
                <div><p class="eyebrow">Keep it current</p><h2>Add an expense</h2></div>
            </div>
            <form:form modelAttribute="expenseForm" method="post" action="/users/${user.id}/budgets/${budget.id}/expenses" cssClass="stacked-form">
                <form:errors path="*" cssClass="form-error" element="div"/>
                <label for="description">Description</label>
                <form:input path="description" id="description" maxlength="200" placeholder="Groceries"/>
                <div class="form-row">
                    <div><label for="amount">Amount</label><form:input path="amount" id="amount" type="number" min="0.01" step="0.01" placeholder="42.50"/></div>
                    <div><label for="category">Category</label><select name="category" id="category">
                        <option value="">Choose</option>
                        <c:forEach items="${categories}" var="categoryOption">
                            <option value="<c:out value='${categoryOption.value}'/>"<c:if test="${not empty expenseForm.category and expenseForm.category.name eq categoryOption.value}"> selected</c:if>><c:out value="${categoryOption.label}"/></option>
                        </c:forEach>
                    </select></div>
                </div>
                <button class="button button-primary" type="submit">Add expense</button>
            </form:form>
        </section>

        <section class="panel history-panel">
            <div class="section-heading">
                <div><p class="eyebrow">Activity</p><h2>Recent expenses</h2></div>
                <span class="count-pill"><c:out value="${expenses.size()}"/></span>
            </div>
            <c:choose>
                <c:when test="${empty expenses}">
                    <div class="empty-history"><p>No expenses yet.</p><span>Add one to see your category totals update.</span></div>
                </c:when>
                <c:otherwise>
                    <div class="expense-list">
                        <c:forEach items="${expenses}" var="expense">
                            <div class="expense-row">
                                <span class="expense-icon" aria-hidden="true">↘</span>
                                <div class="expense-description"><strong><c:out value="${expense.description}"/></strong><span><c:out value="${expense.category}"/> · <c:out value="${expense.createdAt}"/></span></div>
                                <strong class="expense-amount">−$<c:out value="${expense.amount}"/></strong>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</main>
</body>
</html>
