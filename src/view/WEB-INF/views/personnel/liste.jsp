<%-- Vue : liste du personnel avec filtrage multicritères et tri par nom --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HospitApp — Personnel</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/views/commun/navbar.jsp" %>

<main class="container mt-4">

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>Personnel hospitalier</h2>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/personnel?action=nouveauMedecin"
               class="btn btn-success btn-sm">+ Médecin</a>
            <a href="${pageContext.request.contextPath}/personnel?action=nouveauInfirmier"
               class="btn btn-outline-success btn-sm">+ Infirmier</a>
        </div>
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
         FORMULAIRE DE RECHERCHE
         Critère 1 : nom/prénom  |  Critère 2 : type  |  Critère 3 : spécialité/service
         ===================================================================== --%>
    <div class="card shadow-sm mb-3">
        <div class="card-header bg-light d-flex justify-content-between align-items-center">
            <span class="fw-bold">Recherche et tri</span>
            <c:if test="${not empty critereNom or not empty critereType or not empty critereSpecialite}">
                <a href="${pageContext.request.contextPath}/personnel"
                   class="btn btn-sm btn-outline-secondary">Effacer les filtres</a>
            </c:if>
        </div>
        <div class="card-body">
            <form method="get" action="${pageContext.request.contextPath}/personnel">
                <div class="row g-2 align-items-end">

                    <%-- Critère 1 : nom --%>
                    <div class="col-md-3">
                        <label class="form-label small">Nom ou prénom</label>
                        <input type="text" name="nom" class="form-control form-control-sm"
                               placeholder="Martin..."
                               value="${not empty critereNom ? critereNom : ''}">
                    </div>

                    <%-- Critère 2 : type de personnel --%>
                    <div class="col-md-2">
                        <label class="form-label small">Type</label>
                        <select name="type" class="form-select form-select-sm">
                            <option value="">Tous</option>
                            <option value="medecin"   ${critereType == 'medecin'   ? 'selected' : ''}>Médecins</option>
                            <option value="infirmier" ${critereType == 'infirmier' ? 'selected' : ''}>Infirmiers</option>
                        </select>
                    </div>

                    <%-- Critère 3 : spécialité ou service --%>
                    <div class="col-md-3">
                        <label class="form-label small">Spécialité / Service</label>
                        <input type="text" name="specialite" class="form-control form-select-sm"
                               placeholder="Cardiologie, Urgences..."
                               value="${not empty critereSpecialite ? critereSpecialite : ''}">
                    </div>

                    <%-- Tri par nom --%>
                    <div class="col-md-2">
                        <label class="form-label small">Ordre du nom</label>
                        <select name="ordre" class="form-select form-select-sm">
                            <option value="asc"  ${triOrdre == 'asc'  or empty triOrdre ? 'selected' : ''}>A → Z ↑</option>
                            <option value="desc" ${triOrdre == 'desc' ? 'selected' : ''}>Z → A ↓</option>
                        </select>
                    </div>

                    <div class="col-md-2">
                        <button type="submit" class="btn btn-success btn-sm w-100">OK</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <%-- =====================================================================
         ONGLETS MÉDECINS / INFIRMIERS
         ===================================================================== --%>
    <ul class="nav nav-tabs mb-3" id="personnelTabs">
        <li class="nav-item">
            <a class="nav-link ${empty critereType or critereType != 'infirmier' ? 'active' : ''}"
               data-bs-toggle="tab" href="#medecins">
                Médecins
                <span class="badge bg-success">${medecins.size()}</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${critereType == 'infirmier' ? 'active' : ''}"
               data-bs-toggle="tab" href="#infirmiers">
                Infirmiers
                <span class="badge bg-info text-dark">${infirmiers.size()}</span>
            </a>
        </li>
    </ul>

    <div class="tab-content">

        <%-- Onglet Médecins --%>
        <div class="tab-pane fade ${empty critereType or critereType != 'infirmier' ? 'show active' : ''}" id="medecins">
            <div class="card shadow-sm">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="table-success">
                            <tr>
                                <th>Matricule</th>
                                <th>
                                    Nom
                                    <span class="text-warning">${triOrdre == 'desc' ? '↓' : '↑'}</span>
                                </th>
                                <th>Spécialité</th>
                                <th>N° Ordre</th>
                                <th>Téléphone</th>
                                <th>Disponible</th>
                                <th class="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="m" items="${medecins}">
                                <tr>
                                    <td><code>${m.matricule}</code></td>
                                    <td><strong>Dr. ${m.nomComplet}</strong></td>
                                    <td>
                                        <span class="badge bg-light text-dark border">${m.specialite}</span>
                                    </td>
                                    <td><small>${m.numeroOrdre}</small></td>
                                    <td>${not empty m.telephone ? m.telephone : '—'}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${m.disponible}">
                                                <span class="badge bg-success">Disponible</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">Indisponible</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/personnel?action=editerMedecin&id=${m.id}"
                                           class="btn btn-sm btn-outline-warning">Modifier</a>
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/personnel"
                                              class="d-inline"
                                              onsubmit="return confirm('Supprimer Dr. ${m.nomComplet} ?')">
                                            <input type="hidden" name="action" value="supprimer">
                                            <input type="hidden" name="id" value="${m.id}">
                                            <input type="hidden" name="type" value="medecin">
                                            <button type="submit" class="btn btn-sm btn-outline-danger">
                                                Supprimer
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty medecins}">
                                <tr>
                                    <td colspan="7" class="text-center text-muted py-3">
                                        <c:choose>
                                            <c:when test="${not empty critereNom or not empty critereSpecialite}">
                                                Aucun médecin ne correspond aux critères.
                                            </c:when>
                                            <c:otherwise>Aucun médecin enregistré.</c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <%-- Onglet Infirmiers --%>
        <div class="tab-pane fade ${critereType == 'infirmier' ? 'show active' : ''}" id="infirmiers">
            <div class="card shadow-sm">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="table-info">
                            <tr>
                                <th>Matricule</th>
                                <th>
                                    Nom
                                    <span class="text-warning">${triOrdre == 'desc' ? '↓' : '↑'}</span>
                                </th>
                                <th>Service</th>
                                <th>Qualification</th>
                                <th>Garde nuit</th>
                                <th>Disponible</th>
                                <th class="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="inf" items="${infirmiers}">
                                <tr>
                                    <td><code>${inf.matricule}</code></td>
                                    <td><strong>${inf.nomComplet}</strong></td>
                                    <td>${inf.service}</td>
                                    <td><span class="badge bg-secondary">${inf.qualification}</span></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${inf.gardeNuit}">
                                                <span class="badge bg-primary">Oui</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Non</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${inf.disponible}">
                                                <span class="badge bg-success">Disponible</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">Indisponible</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/personnel?action=editerInfirmier&id=${inf.id}"
                                           class="btn btn-sm btn-outline-warning">Modifier</a>
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/personnel"
                                              class="d-inline"
                                              onsubmit="return confirm('Supprimer ${inf.nomComplet} ?')">
                                            <input type="hidden" name="action" value="supprimer">
                                            <input type="hidden" name="id" value="${inf.id}">
                                            <input type="hidden" name="type" value="infirmier">
                                            <button type="submit" class="btn btn-sm btn-outline-danger">
                                                Supprimer
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty infirmiers}">
                                <tr>
                                    <td colspan="7" class="text-center text-muted py-3">
                                        <c:choose>
                                            <c:when test="${not empty critereNom or not empty critereSpecialite}">
                                                Aucun(e) infirmier(e) ne correspond aux critères.
                                            </c:when>
                                            <c:otherwise>Aucun(e) infirmier(e) enregistré(e).</c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

    </div>

    <%-- Résumé des spécialités disponibles --%>
    <c:if test="${not empty specialites}">
        <p class="text-muted small mt-2">
            Spécialités présentes :
            <c:forEach var="s" items="${specialites}" varStatus="st">
                <span class="badge bg-light text-dark border me-1">${s}</span>
            </c:forEach>
        </p>
    </c:if>

</main>

<footer class="mt-5 py-3 bg-light border-top text-center text-muted small">
    HospitApp — Projet POO Avancée Bachelor 3
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
