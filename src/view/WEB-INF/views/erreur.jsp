<%-- Vue : page d'erreur générique — affichée quand une entité est introuvable --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HospitApp — Erreur</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="/WEB-INF/views/commun/navbar.jsp" %>
<main class="container mt-5 text-center">
    <div class="alert alert-danger d-inline-block px-5 py-4">
        <h3>Erreur</h3>
        <p>${not empty erreur ? erreur : 'Une erreur inattendue s\'est produite.'}</p>
        <a href="${pageContext.request.contextPath}/" class="btn btn-outline-danger">Retour à l'accueil</a>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
