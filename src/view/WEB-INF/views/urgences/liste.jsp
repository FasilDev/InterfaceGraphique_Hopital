<%-- Vue : file d'urgences triée par priorité médicale --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HospitApp — Urgences</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/views/commun/navbar.jsp" %>

<main class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>File des urgences
            <span class="badge bg-danger">${nbUrgences}</span>
        </h2>
        <a href="${pageContext.request.contextPath}/urgences?action=nouvelleUrgence"
           class="btn btn-danger">+ Nouvelle urgence</a>
    </div>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show">
            ${sessionScope.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="message" scope="session"/>
        <c:remove var="messageType" scope="session"/>
    </c:if>

    <%-- Prochaine urgence en vedette --%>
    <c:if test="${not empty prochaineUrgence}">
        <div class="alert alert-danger d-flex justify-content-between align-items-center">
            <div>
                <strong>Prochaine urgence :</strong>
                [P${prochaineUrgence.niveauPriorite} — ${prochaineUrgence.labelPriorite}]
                ${prochaineUrgence.typeActe} — Patient ${prochaineUrgence.numeroPatient}
                — <em>${prochaineUrgence.descriptionUrgence}</em>
            </div>
            <form method="post" action="${pageContext.request.contextPath}/urgences"
                  onsubmit="return confirm('Marquer cette urgence comme traitée ?')">
                <input type="hidden" name="action" value="traiter">
                <button type="submit" class="btn btn-success btn-sm">Traiter</button>
            </form>
        </div>
    </c:if>

    <%-- Table des urgences en attente --%>
    <div class="card shadow-sm">
        <div class="card-header fw-bold">Urgences en attente — triées par priorité médicale</div>
        <div class="table-responsive">
            <table class="table table-hover mb-0">
                <thead class="table-danger">
                    <tr>
                        <th>Priorité</th><th>Acte</th><th>Description</th>
                        <th>Patient</th><th>Médecin</th><th>Salle</th><th>Date</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="u" items="${urgences}">
                        <tr class="${u.niveauPriorite == 1 ? 'table-danger' : u.niveauPriorite == 2 ? 'table-warning' : ''}">
                            <td>
                                <span class="badge fs-6
                                    ${u.niveauPriorite == 1 ? 'bg-danger' :
                                      u.niveauPriorite == 2 ? 'bg-warning text-dark' :
                                      u.niveauPriorite == 3 ? 'bg-info text-dark' : 'bg-secondary'}">
                                    P${u.niveauPriorite} — ${u.labelPriorite}
                                </span>
                            </td>
                            <td>${u.typeActe}</td>
                            <td><small>${u.descriptionUrgence}</small></td>
                            <td><code>${u.numeroPatient}</code></td>
                            <td><code>${u.matriculeMedecin}</code></td>
                            <td>${not empty u.salle ? u.salle : '—'}</td>
                            <td>${u.dateSoin}</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty urgences}">
                        <tr><td colspan="7" class="text-center text-muted py-3">
                            Aucune urgence en attente. La file est vide.
                        </td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <p class="text-muted mt-2 small">
        La file est une PriorityQueue : niveau 1 (critique) est toujours traité en premier,
        indépendamment de l'ordre d'arrivée.
    </p>
</main>

<footer class="mt-5 py-3 bg-light border-top text-center text-muted small">
    HospitApp — Projet POO Avancée Bachelor 3
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
