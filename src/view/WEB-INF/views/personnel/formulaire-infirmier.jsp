<%-- Vue : formulaire d'ajout / modification d'un(e) infirmier(e) --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HospitApp — <c:choose><c:when test="${not empty infirmier}">Modifier infirmier</c:when><c:otherwise>Nouvel infirmier</c:otherwise></c:choose></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/views/commun/navbar.jsp" %>

<main class="container mt-4" style="max-width: 700px;">
    <h2 class="mb-4">
        <c:choose>
            <c:when test="${not empty infirmier}">Modifier le dossier de ${infirmier.nomComplet}</c:when>
            <c:otherwise>Ajouter un(e) infirmier(e)</c:otherwise>
        </c:choose>
    </h2>

    <div class="card shadow-sm">
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/personnel">
                <input type="hidden" name="action"
                       value="${not empty infirmier ? 'modifierInfirmier' : 'ajouterInfirmier'}">
                <c:if test="${not empty infirmier}">
                    <input type="hidden" name="id" value="${infirmier.id}">
                </c:if>

                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Nom <span class="text-danger">*</span></label>
                        <input type="text" name="nom" class="form-control"
                               value="${not empty infirmier ? infirmier.nom : ''}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Prénom <span class="text-danger">*</span></label>
                        <input type="text" name="prenom" class="form-control"
                               value="${not empty infirmier ? infirmier.prenom : ''}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Date de naissance <span class="text-danger">*</span></label>
                        <input type="date" name="dateNaissance" class="form-control"
                               value="${not empty infirmier ? infirmier.dateNaissance : ''}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Matricule <span class="text-danger">*</span></label>
                        <input type="text" name="matricule" class="form-control"
                               value="${not empty infirmier ? infirmier.matricule : ''}" placeholder="INF-004" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Service <span class="text-danger">*</span></label>
                        <input type="text" name="service" class="form-control"
                               value="${not empty infirmier ? infirmier.service : ''}" placeholder="Cardiologie" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Qualification <span class="text-danger">*</span></label>
                        <select name="qualification" class="form-select" required>
                            <option value="">— Choisir —</option>
                            <c:forEach var="q" items="${'IDE,IADE,IBODE,IPDE,Auxiliaire'.split(',')}">
                                <option value="${q}"
                                    <c:if test="${q eq infirmier.qualification}">selected</c:if>>${q}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Téléphone</label>
                        <input type="tel" name="telephone" class="form-control"
                               value="${not empty infirmier ? infirmier.telephone : ''}">
                    </div>
                    <div class="col-md-6 d-flex align-items-end">
                        <div class="form-check mb-2">
                            <input type="checkbox" name="gardeNuit" class="form-check-input"
                                   id="gardeNuit" <c:if test="${infirmier.gardeNuit}">checked</c:if>>
                            <label class="form-check-label" for="gardeNuit">Garde de nuit</label>
                        </div>
                    </div>
                    <c:if test="${not empty infirmier}">
                        <div class="col-12">
                            <div class="form-check">
                                <input type="checkbox" name="disponible" class="form-check-input"
                                       id="disponible" <c:if test="${infirmier.disponible}">checked</c:if>>
                                <label class="form-check-label" for="disponible">Disponible</label>
                            </div>
                        </div>
                    </c:if>
                </div>

                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-info text-white">
                        <c:choose>
                            <c:when test="${not empty infirmier}">Enregistrer les modifications</c:when>
                            <c:otherwise>Ajouter l'infirmier(e)</c:otherwise>
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
