<%-- Vue : liste des patients avec recherche multicritères et tri dynamique --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HospitApp — Patients</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/views/commun/navbar.jsp" %>

<main class="container mt-4">

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>Patients <span class="badge bg-secondary">${patients.size()}</span></h2>
        <a href="${pageContext.request.contextPath}/patients?action=nouveau"
           class="btn btn-primary">+ Nouveau patient</a>
    </div>

    <%-- Message flash --%>
    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show">
            ${sessionScope.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="message" scope="session"/>
        <c:remove var="messageType" scope="session"/>
    </c:if>

    <%-- =====================================================================
         FORMULAIRE DE RECHERCHE ET TRI
         Soumis en GET pour que les critères soient visibles dans l'URL.
         Commit 10 : filtrage multicritères (3 critères) + tri sur 2 colonnes.
         ===================================================================== --%>
    <div class="card shadow-sm mb-3">
        <div class="card-header bg-light d-flex justify-content-between align-items-center">
            <span class="fw-bold">Recherche et tri</span>
            <%-- Lien "Effacer" visible seulement si un filtre est actif --%>
            <c:if test="${not empty critereNom or not empty critereAdmis or not empty critereGroupeSanguin or not empty triColonne}">
                <a href="${pageContext.request.contextPath}/patients" class="btn btn-sm btn-outline-secondary">
                    Effacer les filtres
                </a>
            </c:if>
        </div>
        <div class="card-body">
            <form method="get" action="${pageContext.request.contextPath}/patients">
                <div class="row g-2 align-items-end">

                    <%-- Critère 1 : nom --%>
                    <div class="col-md-3">
                        <label class="form-label small">Nom du patient</label>
                        <input type="text" name="nom" class="form-control form-control-sm"
                               placeholder="Dupont..."
                               value="${not empty critereNom ? critereNom : ''}">
                    </div>

                    <%-- Critère 2 : statut d'admission --%>
                    <div class="col-md-2">
                        <label class="form-label small">Statut</label>
                        <select name="admis" class="form-select form-select-sm">
                            <option value="">Tous</option>
                            <option value="true"  ${critereAdmis == 'true'  ? 'selected' : ''}>Hospitalisés</option>
                            <option value="false" ${critereAdmis == 'false' ? 'selected' : ''}>Non hospitalisés</option>
                        </select>
                    </div>

                    <%-- Critère 3 : groupe sanguin --%>
                    <div class="col-md-2">
                        <label class="form-label small">Groupe sanguin</label>
                        <select name="groupeSanguin" class="form-select form-select-sm">
                            <option value="">Tous</option>
                            <c:forEach var="gs" items="${'A+,A-,B+,B-,AB+,AB-,O+,O-'.split(',')}">
                                <option value="${gs}" ${gs == critereGroupeSanguin ? 'selected' : ''}>${gs}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <%-- Tri : colonne --%>
                    <div class="col-md-2">
                        <label class="form-label small">Trier par</label>
                        <select name="tri" class="form-select form-select-sm">
                            <option value="">— Ordre d'ajout —</option>
                            <option value="nom"  ${triColonne == 'nom'  ? 'selected' : ''}>Nom</option>
                            <option value="date" ${triColonne == 'date' ? 'selected' : ''}>Date d'admission</option>
                        </select>
                    </div>

                    <%-- Tri : ordre --%>
                    <div class="col-md-2">
                        <label class="form-label small">Ordre</label>
                        <select name="ordre" class="form-select form-select-sm">
                            <option value="asc"  ${triOrdre == 'asc'  or empty triOrdre ? 'selected' : ''}>Croissant ↑</option>
                            <option value="desc" ${triOrdre == 'desc' ? 'selected' : ''}>Décroissant ↓</option>
                        </select>
                    </div>

                    <div class="col-md-1">
                        <button type="submit" class="btn btn-primary btn-sm w-100">OK</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <%-- =====================================================================
         TABLEAU DES PATIENTS
         ===================================================================== --%>
    <div class="card shadow-sm">
        <div class="table-responsive">
            <table class="table table-hover mb-0">
                <thead class="table-primary">
                    <tr>
                        <th>Numéro</th>
                        <th>
                            Nom complet
                            <%-- Indicateur visuel si le tri est actif sur cette colonne --%>
                            <c:if test="${triColonne == 'nom'}">
                                <span class="text-warning">${triOrdre == 'desc' ? '↓' : '↑'}</span>
                            </c:if>
                        </th>
                        <th>Âge</th>
                        <th>Groupe sanguin</th>
                        <th>
                            Date d'admission
                            <c:if test="${triColonne == 'date'}">
                                <span class="text-warning">${triOrdre == 'desc' ? '↓' : '↑'}</span>
                            </c:if>
                        </th>
                        <th>Statut</th>
                        <th>Chambre</th>
                        <th class="text-center">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${patients}">
                        <tr>
                            <td><code>${p.numeroPatient}</code></td>
                            <td><strong>${p.nomComplet}</strong></td>
                            <td>${p.age} ans</td>
                            <td>${not empty p.groupeSanguin ? p.groupeSanguin : '—'}</td>
                            <td>${not empty p.dateAdmission ? p.dateAdmission : '—'}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${p.admis}">
                                        <span class="badge bg-success">Hospitalisé</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">Non hospitalisé</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${not empty p.chambre ? p.chambre : '—'}</td>
                            <td class="text-center">
                                <a href="${pageContext.request.contextPath}/patients?action=detail&id=${p.id}"
                                   class="btn btn-sm btn-outline-info">Détail</a>
                                <a href="${pageContext.request.contextPath}/patients?action=editer&id=${p.id}"
                                   class="btn btn-sm btn-outline-warning">Modifier</a>
                                <form method="post" action="${pageContext.request.contextPath}/patients"
                                      class="d-inline"
                                      onsubmit="return confirm('Supprimer définitivement ce patient ?')">
                                    <input type="hidden" name="action" value="supprimer">
                                    <input type="hidden" name="id" value="${p.id}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">Supprimer</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty patients}">
                        <tr>
                            <td colspan="8" class="text-center text-muted py-4">
                                <c:choose>
                                    <c:when test="${not empty critereNom or not empty critereAdmis or not empty critereGroupeSanguin}">
                                        Aucun patient ne correspond aux critères de recherche.
                                    </c:when>
                                    <c:otherwise>
                                        Aucun patient enregistré.
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <p class="text-muted small mt-2">
        ${patients.size()} résultat(s)
        <c:if test="${not empty critereNom or not empty critereAdmis or not empty critereGroupeSanguin}">
            — filtre actif
        </c:if>
    </p>

</main>

<footer class="mt-5 py-3 bg-light border-top text-center text-muted small">
    HospitApp — Projet POO Avancée Bachelor 3
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
