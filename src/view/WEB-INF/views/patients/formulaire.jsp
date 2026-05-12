<%-- Vue : formulaire d'ajout et de modification d'un patient.
     Si ${patient} est défini → mode édition ; sinon → mode ajout. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HospitApp — <c:choose><c:when test="${not empty patient}">Modifier</c:when><c:otherwise>Nouveau</c:otherwise></c:choose> patient</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/views/commun/navbar.jsp" %>

<main class="container mt-4" style="max-width: 700px;">
    <h2 class="mb-4">
        <c:choose>
            <c:when test="${not empty patient}">Modifier le dossier de ${patient.nomComplet}</c:when>
            <c:otherwise>Nouveau patient</c:otherwise>
        </c:choose>
    </h2>

    <c:if test="${not empty erreur}">
        <div class="alert alert-danger">${erreur}</div>
    </c:if>

    <div class="card shadow-sm">
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/patients">
                <input type="hidden" name="action"
                       value="${not empty patient ? 'modifier' : 'ajouter'}">
                <c:if test="${not empty patient}">
                    <input type="hidden" name="id" value="${patient.id}">
                </c:if>

                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Nom <span class="text-danger">*</span></label>
                        <input type="text" name="nom" class="form-control"
                               value="${not empty patient ? patient.nom : ''}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Prénom <span class="text-danger">*</span></label>
                        <input type="text" name="prenom" class="form-control"
                               value="${not empty patient ? patient.prenom : ''}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-bold">Date de naissance <span class="text-danger">*</span></label>
                        <input type="date" name="dateNaissance" class="form-control"
                               value="${not empty patient ? patient.dateNaissance : ''}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Groupe sanguin</label>
                        <select name="groupeSanguin" class="form-select">
                            <option value="">— Non renseigné —</option>
                            <c:forEach var="gs" items="${'A+,A-,B+,B-,AB+,AB-,O+,O-'.split(',')}">
                                <option value="${gs}"
                                    <c:if test="${gs eq patient.groupeSanguin}">selected</c:if>>${gs}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Téléphone</label>
                        <input type="tel" name="telephone" class="form-control"
                               value="${not empty patient ? patient.telephone : ''}">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Email</label>
                        <input type="email" name="email" class="form-control"
                               value="${not empty patient ? patient.email : ''}">
                    </div>
                    <div class="col-md-8">
                        <label class="form-label">Numéro de sécurité sociale</label>
                        <input type="text" name="numeroSecuriteSociale" class="form-control"
                               value="${not empty patient ? patient.numeroSecuriteSociale : ''}">
                    </div>
                    <div class="col-md-4 d-flex align-items-end">
                        <div class="form-check mb-2">
                            <input type="checkbox" name="prisEnCharge" class="form-check-input"
                                   id="prisEnCharge"
                                   <c:if test="${patient.prisEnCharge}">checked</c:if>>
                            <label class="form-check-label" for="prisEnCharge">Prise en charge</label>
                        </div>
                    </div>
                    <div class="col-12">
                        <label class="form-label">Notes médicales</label>
                        <textarea name="notes" class="form-control" rows="3">${not empty patient ? patient.notes : ''}</textarea>
                    </div>
                </div>

                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-primary">
                        <c:choose>
                            <c:when test="${not empty patient}">Enregistrer les modifications</c:when>
                            <c:otherwise>Ajouter le patient</c:otherwise>
                        </c:choose>
                    </button>
                    <a href="${pageContext.request.contextPath}/patients" class="btn btn-outline-secondary">Annuler</a>
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
