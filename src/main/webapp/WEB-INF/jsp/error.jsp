<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Page not found | 50/30/20 Budget</title>
    <link rel="stylesheet" href="<c:url value='/css/app.css'/>">
</head>
<body class="welcome-page">
<main class="panel error-panel">
    <p class="eyebrow">Error <c:out value="${statusCode}"/></p>
    <h1>We couldn’t find that page.</h1>
    <p class="muted"><c:out value="${pageError}"/></p>
    <a class="button button-primary" href="<c:url value='/'/>">Back to start</a>
</main>
</body>
</html>
