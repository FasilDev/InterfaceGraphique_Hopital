<%-- Vue : liste des soins avec filtre par patient, médecin, type de soin, et tri par date --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HospitApp — Soins</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/views/commun/navbar.jsp" %>

<main class="container mt-4">

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>Soins médicaux</h2>
        <a href="${pageContext.request.contextPath}/soins?action=nouvelleCons"
           class="btn btn-info text-white">+ Nouvelle consultation</a>
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
         FORMULAIRE DE FILTRAGE ET TRI
         Critère 1 : type  |  Critère 2 : patient  |  Critère 3 : médecin  |  Tri : date
         ===================================================================== --%>
    <div class="card shadow-sm mb-3">
        <div class="card-header bg-light d-flex justify-content-between align-items-center">
            <span class="fw-bold">Filtres et tri</span>
            <c:if test="${not empty criterePatient or not empty critereMedecin or not empty critereType or not empty triOrdre}">
                <a href="${pageContext.request.contextPath}/soins"
                   class="btn btn-sm btn-outline-secondary">Effacer les filtres</a>
            </c:if>
        </div>
        <div class="card-body">
            <form method="get" action="${pageContext.request.contextPath}/soins">
                <div class="row g-2 align-items-end">

                    <%-- Critère 1 : type de soin --%>
                    <div class="col-md-2">
                        <label class="form-label small">Type de soin</label>
                        <select name="type" class="form-select form-select-sm">
                            <option value="">Tous</option>
                            <option value="consultation" ${critereType == 'consultation' ? 'selected' : ''}>
                                Consultations
                            </option>
                            <option value="acte" ${critereType == 'acte' ? 'selected' : ''}>
                                Actes chirurgicaux
                            </option>
                        </select>
                    </div>

                    <%-- Critère 2 : numéro de patient --%>
                    <div class="col-md-3">
                        <label class="form-label small">N° patient</label>
                        <input type="text" name="numeroPatient" class="form-control form-control-sm"
                               placeholder="P-2024-001"
                               value="${not empty criterePatient ? criterePatient : ''}">
                    </div>

                    <%-- Critère 3 : matricule du médecin --%>
                    <div class="col-md-3">
                        <label class="form-label small">Matricule médecin</label>
                        <input type="text" name="matriculeMedecin" class="form-control form-control-sm"
                               placeholder="MED-001"
                               value="${not empty critereMedecin ? critereMedecin : ''}">
                    </div>

                    <%-- Tri : ordre de date --%>
                    <div class="col-md-2">
                        <label class="form-label small">Ordre des dates</label>
                        <select name="ordre" class="form-select form-select-sm">
                            <option value="desc" ${triOrdre == 'desc' or empty triOrdre ? 'selected' : ''}>
                                Plus récent ↓
                            </option>
                            <option value="asc" ${triOrdre == 'asc' ? 'selected' : ''}>
                                Plus ancien ↑
                            </option>
                        </select>
                    </div>

                    <div class="col-md-2">
                        <button type="submit" class="btn btn-info text-white btn-sm w-100">OK</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <%-- =====================================================================
         ONGLETS CONSULTATIONS / ACTES CHIRURGICAUX
         ===================================================================== --%>
    <ul class="nav nav-tabs mb-3">
        <li class="nav-item">
            <a class="nav-link ${critereType != 'acte' ? 'active' : ''}"
               data-bs-toggle="tab" href="#consultations">
                Consultations
                <span class="badge bg-info text-dark">${consultations.size()}</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${critereType == 'acte' ? 'active' : ''}"
               data-bs-toggle="tab" href="#actes">
                Actes chirurgicaux
                <span class="badge bg-danger">${actes.size()}</span>
            </a>
        </li>
    </ul>

    <div class="tab-content">

        <%-- Consultations --%>
        <div class="tab-pane fade ${critereType != 'acte' ? 'show active' : ''}" id="consultations">
            <div class="card shadow-sm">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="table-info">
                            <tr>
                                <th>
                                    Date
                                    <span class="text-warning">${triOrdre == 'asc' ? '↑' : '↓'}</span>
                                </th>
                                <th>Motif</th>
                                <th>Patient</th>
                                <th>Médecin</th>
                                <th>Diagnostic</th>
                                <th>Ordonnance</th>
                                <th class="text-end">Coût</th>
                                <th class="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${consultations}">
                                <tr>
                                    <td>${c.dateSoin}</td>
                                    <td>${c.motif}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/patients?action=detail&id="
                                           class="text-decoration-none">
                                            <code>${c.numeroPatient}</code>
                                        </a>
                                    </td>
                                    <td><code>${c.matriculeMedecin}</code></td>
                                    <td>
                                        <small>
                                            <c:choose>
                                                <c:when test="${not empty c.diagnostic}">${c.diagnostic}</c:when>
                                                <c:otherwise class="text-muted">—</c:otherwise>
                                            </c:choose>
                                        </small>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${c.uneOrdonnance}">
                                                <span class="badge bg-success">Oui</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Non</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end">${c.cout} €</td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/soins?action=editerCons&id=${c.id}"
                                           class="btn btn-sm btn-outline-warning">Modifier</a>
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/soins"
                                              class="d-inline"
                                              onsubmit="return confirm('Supprimer cette consultation ?')">
                                            <input type="hidden" name="action" value="supprimerCons">
                                            <input type="hidden" name="id" value="${c.id}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger">
                                                Supprimer
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty consultations}">
                                <tr>
                                    <td colspan="8" class="text-center text-muted py-3">
                                        <c:choose>
                                            <c:when test="${not empty criterePatient or not empty critereMedecin}">
                                                Aucune consultation ne correspond aux critères.
                                            </c:when>
                                            <c:when test="${critereType == 'acte'}">
                                                Filtre actif : actes chirurgicaux uniquement.
                                            </c:when>
                                            <c:otherwise>Aucune consultation enregistrée.</c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <%-- Actes chirurgicaux --%>
        <div class="tab-pane fade ${critereType == 'acte' ? 'show active' : ''}" id="actes">
            <div class="card shadow-sm">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="table-danger">
                            <tr>
                                <th>
                                    Date
                                    <span class="text-warning">${triOrdre == 'asc' ? '↑' : '↓'}</span>
                                </th>
                                <th>Acte</th>
                                <th>Priorité</th>
                                <th>Patient</th>
                                <th>Médecin</th>
                                <th>Salle</th>
                                <th>Statut</th>
                                <th class="text-end">Coût</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="a" items="${actes}">
                                <tr>
                                    <td>${a.dateSoin}</td>
                                    <td>${a.typeActe}</td>
                                    <td>
                                        <span class="badge ${a.niveauPriorite <= 2 ? 'bg-danger' : a.niveauPriorite == 3 ? 'bg-warning text-dark' : 'bg-secondary'}">
                                            P${a.niveauPriorite} — ${a.labelPriorite}
                                        </span>
                                    </td>
                                    <td><code>${a.numeroPatient}</code></td>
                                    <td><code>${a.matriculeMedecin}</code></td>
                                    <td>${not empty a.salle ? a.salle : '—'}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${a.realise}">
                                                <span class="badge bg-success">Réalisé</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-warning text-dark">En attente</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end">${a.cout} €</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty actes}">
                                <tr>
                                    <td colspan="8" class="text-center text-muted py-3">
                                        <c:choose>
                                            <c:when test="${not empty criterePatient or not empty critereMedecin}">
                                                Aucun acte ne correspond aux critères.
                                            </c:when>
                                            <c:when test="${critereType == 'consultation'}">
                                                Filtre actif : consultations uniquement.
                                            </c:when>
                                            <c:otherwise>Aucun acte chirurgical enregistré.</c:otherwise>
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

    <p class="text-muted small mt-2">
        ${consultations.size()} consultation(s) — ${actes.size()} acte(s) chirurgical/aux
        <c:if test="${not empty criterePatient or not empty critereMedecin or not empty critereType}">
            — filtre actif
        </c:if>
        — trié par date ${triOrdre == 'asc' ? '(plus ancien en premier)' : '(plus récent en premier)'}
    </p>

</main>

<footer class="mt-5 py-3 bg-light border-top text-center text-muted small">
    HospitApp — Projet POO Avancée Bachelor 3
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
