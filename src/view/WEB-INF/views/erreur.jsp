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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/views/commun/navbar.jsp" %>
<main class="container mt-5 text-center">
    <div class="card shadow-sm d-inline-block px-5 py-4 border-danger">
        <h3 class="text-danger mb-3">Une erreur s'est produite</h3>
        <p class="text-muted">
            ${not empty erreur ? erreur : 'Une erreur inattendue s\'est produite.'}
        </p>
        <a href="${pageContext.request.contextPath}/" class="btn btn-outline-danger me-2">
            Retour à l'accueil
        </a>
        <a href="javascript:history.back()" class="btn btn-outline-secondary">
            Page précédente
        </a>
    </div>
</main>
<footer class="mt-5 py-3 bg-light border-top text-center text-muted small">
    HospitApp — Projet POO Avancée Bachelor 3
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
