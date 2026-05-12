<%-- Vue : fiche détaillée d'un patient — dossier médical, antécédents et facturation --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
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

    <%-- En-tête avec statut --%>
    <div class="d-flex justify-content-between align-items-start mb-3">
        <div>
            <h2 class="mb-0">${patient.nomComplet}
                <c:choose>
                    <c:when test="${patient.admis}">
                        <span class="badge bg-success">Hospitalisé — Chambre ${patient.chambre}</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge bg-secondary">Non hospitalisé</span>
                    </c:otherwise>
                </c:choose>
            </h2>
            <small class="text-muted">Dossier ${patient.numeroPatient}</small>
        </div>
        <a href="${pageContext.request.contextPath}/patients" class="btn btn-outline-secondary">
            &larr; Retour à la liste
        </a>
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

    <div class="row g-3">

        <%-- Colonne gauche : infos personnelles + hospitalisation --%>
        <div class="col-md-6">

            <%-- Informations personnelles --%>
            <div class="card shadow-sm mb-3">
                <div class="card-header bg-primary text-white fw-bold">Informations personnelles</div>
                <div class="card-body">
                    <dl class="row mb-0">
                        <dt class="col-sm-5">Nom complet</dt>
                        <dd class="col-sm-7">${patient.nomComplet}</dd>

                        <dt class="col-sm-5">Date de naissance</dt>
                        <dd class="col-sm-7">${patient.dateNaissance} (${patient.age} ans)</dd>

                        <dt class="col-sm-5">Groupe sanguin</dt>
                        <dd class="col-sm-7">
                            <c:choose>
                                <c:when test="${not empty patient.groupeSanguin}">
                                    <span class="badge bg-danger fs-6">${patient.groupeSanguin}</span>
                                </c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </dd>

                        <dt class="col-sm-5">Téléphone</dt>
                        <dd class="col-sm-7">${not empty patient.telephone ? patient.telephone : '—'}</dd>

                        <dt class="col-sm-5">Email</dt>
                        <dd class="col-sm-7">${not empty patient.email ? patient.email : '—'}</dd>

                        <dt class="col-sm-5">N° Sécu</dt>
                        <dd class="col-sm-7">${not empty patient.numeroSecuriteSociale ? patient.numeroSecuriteSociale : '—'}</dd>

                        <dt class="col-sm-5">Prise en charge</dt>
                        <dd class="col-sm-7">
                            <c:choose>
                                <c:when test="${patient.prisEnCharge}">
                                    <span class="text-success fw-bold">Oui</span>
                                </c:when>
                                <c:otherwise><span class="text-muted">Non</span></c:otherwise>
                            </c:choose>
                        </dd>

                        <c:if test="${not empty patient.notes}">
                            <dt class="col-sm-5">Notes</dt>
                            <dd class="col-sm-7"><em>${patient.notes}</em></dd>
                        </c:if>
                    </dl>
                </div>
            </div>

            <%-- Hospitalisation : admission / sortie --%>
            <div class="card shadow-sm mb-3">
                <div class="card-header bg-info text-white fw-bold">Hospitalisation</div>
                <div class="card-body">
                    <dl class="row mb-2">
                        <dt class="col-sm-5">Date d'admission</dt>
                        <dd class="col-sm-7">${not empty patient.dateAdmission ? patient.dateAdmission : '—'}</dd>

                        <dt class="col-sm-5">Date de sortie</dt>
                        <dd class="col-sm-7">${not empty patient.dateSortie ? patient.dateSortie : '—'}</dd>
                    </dl>

                    <c:choose>
                        <c:when test="${!patient.admis}">
                            <%-- Formulaire d'admission --%>
                            <form method="post" action="${pageContext.request.contextPath}/patients"
                                  class="d-flex gap-2">
                                <input type="hidden" name="action" value="admettre">
                                <input type="hidden" name="id" value="${patient.id}">
                                <input type="text" name="chambre" class="form-control form-control-sm"
                                       placeholder="N° chambre" required maxlength="20">
                                <button type="submit" class="btn btn-success btn-sm">Admettre</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <%-- Bouton de sortie --%>
                            <form method="post" action="${pageContext.request.contextPath}/patients"
                                  onsubmit="return confirm('Confirmer la sortie du patient ?')">
                                <input type="hidden" name="action" value="sortir">
                                <input type="hidden" name="id" value="${patient.id}">
                                <button type="submit" class="btn btn-outline-warning btn-sm">
                                    Enregistrer la sortie
                                </button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <%-- Facturation --%>
            <div class="card shadow-sm border-success">
                <div class="card-header bg-success text-white fw-bold">Facturation</div>
                <div class="card-body text-center">
                    <p class="display-6 fw-bold text-success">
                        <fmt:formatNumber value="${patient.calculerMontantTotal()}" type="number" maxFractionDigits="2"/> €
                    </p>
                    <p class="text-muted small mb-0">
                        Séjour hospitalier × 350 €/jour
                        <c:if test="${patient.admis}"> (en cours)</c:if>
                    </p>
                    <c:if test="${patient.prisEnCharge}">
                        <span class="badge bg-success mt-1">Pris en charge par l'assurance</span>
                    </c:if>
                </div>
            </div>

        </div>

        <%-- Colonne droite : antécédents + historique soins --%>
        <div class="col-md-6">

            <%-- Antécédents médicaux --%>
            <div class="card shadow-sm mb-3">
                <div class="card-header bg-warning text-dark fw-bold">Antécédents médicaux</div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty patient.antecedents}">
                            <div class="d-flex flex-wrap gap-2 mb-3">
                                <c:forEach var="ant" items="${patient.antecedents}">
                                    <span class="badge bg-warning text-dark fs-6 px-3 py-2">${ant}</span>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted mb-3">Aucun antécédent connu.</p>
                        </c:otherwise>
                    </c:choose>

                    <%-- Formulaire d'ajout d'un antécédent --%>
                    <form method="post" action="${pageContext.request.contextPath}/patients"
                          class="d-flex gap-2">
                        <input type="hidden" name="action" value="ajouterAntecedent">
                        <input type="hidden" name="id" value="${patient.id}">
                        <input type="text" name="antecedent" class="form-control form-control-sm"
                               placeholder="Nouvel antécédent (ex : Diabète type 2)" required>
                        <button type="submit" class="btn btn-warning btn-sm text-dark">Ajouter</button>
                    </form>
                </div>
            </div>

            <%-- Historique des soins --%>
            <div class="card shadow-sm">
                <div class="card-header bg-light fw-bold">
                    Historique des soins
                    <span class="badge bg-secondary ms-1">${soins.size()}</span>
                </div>
                <div class="table-responsive">
                    <table class="table table-sm mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Date</th>
                                <th>Type</th>
                                <th>Description</th>
                                <th class="text-end">Coût</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="s" items="${soins}">
                                <tr>
                                    <td>${s.dateSoin}</td>
                                    <td>
                                        <span class="badge ${s.typeSoin == 'Consultation' ? 'bg-info text-dark' : 'bg-danger'}">
                                            ${s.typeSoin}
                                        </span>
                                    </td>
                                    <td><small>${s.description}</small></td>
                                    <td class="text-end">${s.cout} €</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty soins}">
                                <tr>
                                    <td colspan="4" class="text-center text-muted py-2">
                                        Aucun soin enregistré.
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </div>

    <div class="mt-3 d-flex gap-2">
        <a href="${pageContext.request.contextPath}/patients?action=editer&id=${patient.id}"
           class="btn btn-warning">Modifier le dossier</a>
        <a href="${pageContext.request.contextPath}/soins?action=nouvelleCons"
           class="btn btn-outline-info">+ Ajouter une consultation</a>
        <a href="${pageContext.request.contextPath}/urgences?action=nouvelleUrgence"
           class="btn btn-outline-danger">+ Déclarer une urgence</a>
    </div>

</main>

<footer class="mt-5 py-3 bg-light border-top text-center text-muted small">
    HospitApp — Projet POO Avancée Bachelor 3
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>