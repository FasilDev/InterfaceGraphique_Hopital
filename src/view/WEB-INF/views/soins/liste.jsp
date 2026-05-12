<%-- Vue : liste des consultations et actes chirurgicaux --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show">
            ${sessionScope.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="message" scope="session"/>
        <c:remove var="messageType" scope="session"/>
    </c:if>

    <%-- Onglets --%>
    <ul class="nav nav-tabs mb-3">
        <li class="nav-item">
            <a class="nav-link active" data-bs-toggle="tab" href="#consultations">
                Consultations <span class="badge bg-info text-dark">${consultations.size()}</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" data-bs-toggle="tab" href="#actes">
                Actes chirurgicaux <span class="badge bg-danger">${actes.size()}</span>
            </a>
        </li>
    </ul>

    <div class="tab-content">
        <%-- Consultations --%>
        <div class="tab-pane fade show active" id="consultations">
            <div class="card shadow-sm">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="table-info">
                            <tr>
                                <th>Date</th><th>Motif</th><th>Patient</th><th>Médecin</th>
                                <th>Diagnostic</th><th>Ordonnance</th><th>Coût</th><th class="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${consultations}">
                                <tr>
                                    <td>${c.dateSoin}</td>
                                    <td>${c.motif}</td>
                                    <td><code>${c.numeroPatient}</code></td>
                                    <td><code>${c.matriculeMedecin}</code></td>
                                    <td>${not empty c.diagnostic ? c.diagnostic : '—'}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${c.uneOrdonnance}">
                                                <span class="badge bg-success">Oui</span>
                                            </c:when>
                                            <c:otherwise><span class="text-muted">Non</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${c.cout} €</td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/soins?action=editerCons&id=${c.id}"
                                           class="btn btn-sm btn-outline-warning">Modifier</a>
                                        <form method="post" action="${pageContext.request.contextPath}/soins"
                                              class="d-inline"
                                              onsubmit="return confirm('Supprimer cette consultation ?')">
                                            <input type="hidden" name="action" value="supprimerCons">
                                            <input type="hidden" name="id" value="${c.id}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger">Supprimer</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty consultations}">
                                <tr><td colspan="8" class="text-center text-muted py-3">Aucune consultation enregistrée.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <%-- Actes chirurgicaux --%>
        <div class="tab-pane fade" id="actes">
            <div class="card shadow-sm">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="table-danger">
                            <tr>
                                <th>Date</th><th>Acte</th><th>Priorité</th><th>Patient</th>
                                <th>Médecin</th><th>Salle</th><th>Statut</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="a" items="${actes}">
                                <tr>
                                    <td>${a.dateSoin}</td>
                                    <td>${a.typeActe}</td>
                                    <td><span class="badge bg-danger">P${a.niveauPriorite} — ${a.labelPriorite}</span></td>
                                    <td><code>${a.numeroPatient}</code></td>
                                    <td><code>${a.matriculeMedecin}</code></td>
                                    <td>${not empty a.salle ? a.salle : '—'}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${a.realise}"><span class="badge bg-success">Réalisé</span></c:when>
                                            <c:otherwise><span class="badge bg-warning text-dark">En attente</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty actes}">
                                <tr><td colspan="7" class="text-center text-muted py-3">Aucun acte chirurgical enregistré.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</main>

<footer class="mt-5 py-3 bg-light border-top text-center text-muted small">
    HospitApp — Projet POO Avancée Bachelor 3
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
