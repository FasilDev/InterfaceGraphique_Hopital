<%-- Vue : liste de tous les patients avec actions CRUD --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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

    <%-- Message flash (succès / erreur) --%>
    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show">
            ${sessionScope.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="message" scope="session"/>
        <c:remove var="messageType" scope="session"/>
    </c:if>

    <div class="card shadow-sm">
        <div class="table-responsive">
            <table class="table table-hover mb-0">
                <thead class="table-primary">
                    <tr>
                        <th>Numéro</th>
                        <th>Nom complet</th>
                        <th>Âge</th>
                        <th>Groupe sanguin</th>
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
                                      onsubmit="return confirm('Supprimer ce patient ?')">
                                    <input type="hidden" name="action" value="supprimer">
                                    <input type="hidden" name="id" value="${p.id}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">Supprimer</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty patients}">
                        <tr><td colspan="7" class="text-center text-muted py-3">Aucun patient enregistré.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</main>

<footer class="mt-5 py-3 bg-light border-top text-center text-muted small">
    HospitApp — Projet POO Avancée Bachelor 3
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
