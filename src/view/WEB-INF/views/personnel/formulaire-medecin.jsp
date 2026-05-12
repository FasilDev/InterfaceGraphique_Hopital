<%-- Vue : formulaire d'ajout / modification d'un médecin --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HospitApp — <c:choose><c:when test="${not empty medecin}">Modifier médecin</c:when><c:otherwise>Nouveau médecin</c:otherwise></c:choose></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/views/commun/navbar.jsp" %>

<main class="container mt-4" style="max-width: 700px;">
    <h2 class="mb-4">
        <c:choose>
            <c:when test="${not empty medecin}">Modifier le dossier de Dr. ${medecin.nomComplet}</c:when>
            <c:otherwise>Ajouter un médecin</c:otherwise>
        </c:choose>
    </h2>

    <div class="card shadow-sm">
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/personnel">
                <input type="hidden" name="action"
                       value="${not empty medecin ? 'modifierMedecin' : 'ajouterMedecin'}">
                <c:if test="${not empty medecin}">
                    <input type="hidden" name="id" value="${medecin.id}">
                </c:if>

                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Nom <span class="text-danger">*</span></label>
                        <input type="text" name="nom" class="form-control"
                               value="${not empty medecin ? medecin.nom : ''}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Prénom <span class="text-danger">*</span></label>
                        <input type="text" name="prenom" class="form-control"
                               value="${not empty medecin ? medecin.prenom : ''}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Date de naissance <span class="text-danger">*</span></label>
                        <input type="date" name="dateNaissance" class="form-control"
                               value="${not empty medecin ? medecin.dateNaissance : ''}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Matricule <span class="text-danger">*</span></label>
                        <input type="text" name="matricule" class="form-control"
                               value="${not empty medecin ? medecin.matricule : ''}" placeholder="MED-005" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Spécialité <span class="text-danger">*</span></label>
                        <input type="text" name="specialite" class="form-control"
                               value="${not empty medecin ? medecin.specialite : ''}" placeholder="Cardiologie" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold">N° Ordre <span class="text-danger">*</span></label>
                        <input type="text" name="numeroOrdre" class="form-control"
                               value="${not empty medecin ? medecin.numeroOrdre : ''}" placeholder="ORD-12345678" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Téléphone</label>
                        <input type="tel" name="telephone" class="form-control"
                               value="${not empty medecin ? medecin.telephone : ''}">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Email</label>
                        <input type="email" name="email" class="form-control"
                               value="${not empty medecin ? medecin.email : ''}">
                    </div>
                    <c:if test="${not empty medecin}">
                        <div class="col-12">
                            <div class="form-check">
                                <input type="checkbox" name="disponible" class="form-check-input"
                                       id="disponible" <c:if test="${medecin.disponible}">checked</c:if>>
                                <label class="form-check-label" for="disponible">Disponible</label>
                            </div>
                        </div>
                    </c:if>
                </div>

                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-success">
                        <c:choose>
                            <c:when test="${not empty medecin}">Enregistrer les modifications</c:when>
                            <c:otherwise>Ajouter le médecin</c:otherwise>
                        </c:choose>
                    </button>
                    <a href="${pageContext.request.contextPath}/personnel" class="btn btn-outline-secondary">Annuler</a>
                </div>
            </form>
        </div>
    </div>
</main>

<footer class="mt-5 py-3 bg-light border-top text-center text-muted small">
    HospitApp — Projet POO Avancée Bachelor 3
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
