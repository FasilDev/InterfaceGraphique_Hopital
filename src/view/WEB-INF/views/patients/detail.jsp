<%-- Vue : fiche détaillée d'un patient avec actions d'admission et historique des soins --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HospitApp — ${patient.nomComplet}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/views/commun/navbar.jsp" %>

<main class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>${patient.nomComplet}
            <c:choose>
                <c:when test="${patient.admis}">
                    <span class="badge bg-success ms-2">Hospitalisé</span>
                </c:when>
                <c:otherwise>
                    <span class="badge bg-secondary ms-2">Non hospitalisé</span>
                </c:otherwise>
            </c:choose>
        </h2>
        <a href="${pageContext.request.contextPath}/patients" class="btn btn-outline-secondary">
            &larr; Retour à la liste
        </a>
    </div>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show">
            ${sessionScope.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="message" scope="session"/>
        <c:remove var="messageType" scope="session"/>
    </c:if>

    <div class="row g-3">
        <%-- Informations personnelles --%>
        <div class="col-md-6">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-primary text-white fw-bold">Informations personnelles</div>
                <div class="card-body">
                    <dl class="row mb-0">
                        <dt class="col-sm-5">Numéro patient</dt>
                        <dd class="col-sm-7"><code>${patient.numeroPatient}</code></dd>

                        <dt class="col-sm-5">Date de naissance</dt>
                        <dd class="col-sm-7">${patient.dateNaissance} (${patient.age} ans)</dd>

                        <dt class="col-sm-5">Groupe sanguin</dt>
                        <dd class="col-sm-7">${not empty patient.groupeSanguin ? patient.groupeSanguin : '—'}</dd>

                        <dt class="col-sm-5">Téléphone</dt>
                        <dd class="col-sm-7">${not empty patient.telephone ? patient.telephone : '—'}</dd>

                        <dt class="col-sm-5">N° Sécu</dt>
                        <dd class="col-sm-7">${not empty patient.numeroSecuriteSociale ? patient.numeroSecuriteSociale : '—'}</dd>

                        <dt class="col-sm-5">Prise en charge</dt>
                        <dd class="col-sm-7">
                            <c:choose>
                                <c:when test="${patient.prisEnCharge}"><span class="text-success">Oui</span></c:when>
                                <c:otherwise><span class="text-muted">Non</span></c:otherwise>
                            </c:choose>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>

        <%-- Hospitalisation --%>
        <div class="col-md-6">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-info text-white fw-bold">Hospitalisation</div>
                <div class="card-body">
                    <dl class="row">
                        <dt class="col-sm-5">Chambre</dt>
                        <dd class="col-sm-7">${not empty patient.chambre ? patient.chambre : '—'}</dd>

                        <dt class="col-sm-5">Admission</dt>
                        <dd class="col-sm-7">${not empty patient.dateAdmission ? patient.dateAdmission : '—'}</dd>

                        <dt class="col-sm-5">Sortie</dt>
                        <dd class="col-sm-7">${not empty patient.dateSortie ? patient.dateSortie : '—'}</dd>

                        <dt class="col-sm-5">Antécédents</dt>
                        <dd class="col-sm-7">${patient.antecedentsFormates}</dd>

                        <dt class="col-sm-5">Notes</dt>
                        <dd class="col-sm-7">${not empty patient.notes ? patient.notes : '—'}</dd>
                    </dl>

                    <%-- Actions d'admission / sortie --%>
                    <c:choose>
                        <c:when test="${!patient.admis}">
                            <form method="post" action="${pageContext.request.contextPath}/patients"
                                  class="d-flex gap-2 mt-2">
                                <input type="hidden" name="action" value="admettre">
                                <input type="hidden" name="id" value="${patient.id}">
                                <input type="text" name="chambre" class="form-control form-control-sm"
                                       placeholder="N° chambre" required>
                                <button type="submit" class="btn btn-success btn-sm">Admettre</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <form method="post" action="${pageContext.request.contextPath}/patients"
                                  onsubmit="return confirm('Confirmer la sortie ?')">
                                <input type="hidden" name="action" value="sortir">
                                <input type="hidden" name="id" value="${patient.id}">
                                <button type="submit" class="btn btn-outline-warning btn-sm mt-2">
                                    Enregistrer la sortie
                                </button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>

    <%-- Historique des soins --%>
    <div class="card shadow-sm mt-3">
        <div class="card-header bg-light fw-bold">Historique des soins</div>
        <div class="table-responsive">
            <table class="table table-sm mb-0">
                <thead class="table-light">
                    <tr><th>Date</th><th>Type</th><th>Description</th><th>Coût</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="s" items="${soins}">
                        <tr>
                            <td>${s.dateSoin}</td>
                            <td><span class="badge bg-info text-dark">${s.typeSoin}</span></td>
                            <td>${s.description}</td>
                            <td>${s.cout} €</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty soins}">
                        <tr><td colspan="4" class="text-center text-muted">Aucun soin enregistré.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/patients?action=editer&id=${patient.id}"
           class="btn btn-warning">Modifier le dossier</a>
    </div>
</main>

<footer class="mt-5 py-3 bg-light border-top text-center text-muted small">
    HospitApp — Projet POO Avancée Bachelor 3
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
