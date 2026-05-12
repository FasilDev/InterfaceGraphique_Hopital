<%-- Vue : liste du personnel hospitalier (médecins et infirmiers) --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show">
            ${sessionScope.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="message" scope="session"/>
        <c:remove var="messageType" scope="session"/>
    </c:if>

    <%-- Onglets Médecins / Infirmiers --%>
    <ul class="nav nav-tabs mb-3" id="personnelTabs">
        <li class="nav-item">
            <a class="nav-link active" data-bs-toggle="tab" href="#medecins">
                Médecins <span class="badge bg-success">${medecins.size()}</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" data-bs-toggle="tab" href="#infirmiers">
                Infirmiers <span class="badge bg-info text-dark">${infirmiers.size()}</span>
            </a>
        </li>
    </ul>

    <div class="tab-content">
        <%-- Onglet Médecins --%>
        <div class="tab-pane fade show active" id="medecins">
            <div class="card shadow-sm">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="table-success">
                            <tr>
                                <th>Matricule</th><th>Nom</th><th>Spécialité</th>
                                <th>Téléphone</th><th>Disponible</th><th class="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="m" items="${medecins}">
                                <tr>
                                    <td><code>${m.matricule}</code></td>
                                    <td>Dr. ${m.nomComplet}</td>
                                    <td><span class="badge bg-light text-dark border">${m.specialite}</span></td>
                                    <td>${not empty m.telephone ? m.telephone : '—'}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${m.disponible}"><span class="text-success">Oui</span></c:when>
                                            <c:otherwise><span class="text-muted">Non</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/personnel?action=editerMedecin&id=${m.id}"
                                           class="btn btn-sm btn-outline-warning">Modifier</a>
                                        <form method="post" action="${pageContext.request.contextPath}/personnel"
                                              class="d-inline"
                                              onsubmit="return confirm('Supprimer ce médecin ?')">
                                            <input type="hidden" name="action" value="supprimer">
                                            <input type="hidden" name="id" value="${m.id}">
                                            <input type="hidden" name="type" value="medecin">
                                            <button type="submit" class="btn btn-sm btn-outline-danger">Supprimer</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty medecins}">
                                <tr><td colspan="6" class="text-center text-muted py-3">Aucun médecin enregistré.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <%-- Onglet Infirmiers --%>
        <div class="tab-pane fade" id="infirmiers">
            <div class="card shadow-sm">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="table-info">
                            <tr>
                                <th>Matricule</th><th>Nom</th><th>Service</th>
                                <th>Qualification</th><th>Garde nuit</th><th class="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="inf" items="${infirmiers}">
                                <tr>
                                    <td><code>${inf.matricule}</code></td>
                                    <td>${inf.nomComplet}</td>
                                    <td>${inf.service}</td>
                                    <td><span class="badge bg-secondary">${inf.qualification}</span></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${inf.gardeNuit}"><span class="text-primary">Oui</span></c:when>
                                            <c:otherwise><span class="text-muted">Non</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/personnel?action=editerInfirmier&id=${inf.id}"
                                           class="btn btn-sm btn-outline-warning">Modifier</a>
                                        <form method="post" action="${pageContext.request.contextPath}/personnel"
                                              class="d-inline"
                                              onsubmit="return confirm('Supprimer cet(te) infirmier(e) ?')">
                                            <input type="hidden" name="action" value="supprimer">
                                            <input type="hidden" name="id" value="${inf.id}">
                                            <input type="hidden" name="type" value="infirmier">
                                            <button type="submit" class="btn btn-sm btn-outline-danger">Supprimer</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty infirmiers}">
                                <tr><td colspan="6" class="text-center text-muted py-3">Aucun(e) infirmier(e) enregistré(e).</td></tr>
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
