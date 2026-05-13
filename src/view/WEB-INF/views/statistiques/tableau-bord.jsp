<%--
    Fichier : tableau-bord.jsp
    Projet HospitApp - application web Java de gestion hospitalière.
    Rôle : page de statistiques dynamiques (tableau de bord).
           Affiche les indicateurs calculés par StatistiqueService via StatistiqueServlet.
           Toutes les valeurs viennent des attributs de requête — aucun appel Java direct ici.
    Interactions : StatistiqueServlet (contrôleur), StatistiqueService (calculs).
    Note : vue MVC pure — zéro logique métier dans ce fichier.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HospitApp — Statistiques</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/views/commun/navbar.jsp" %>

<main class="container mt-4">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Tableau de bord</h2>
        <small class="text-muted">Données en temps réel — actualisées à chaque chargement</small>
    </div>

    <%-- =====================================================================
         SECTION 1 — PATIENTS
         Indicateurs : total, hospitalisés, non hospitalisés, taux d'occupation
         ===================================================================== --%>
    <h5 class="text-primary border-bottom pb-1 mb-3">Patients</h5>
    <div class="row g-3 mb-4">

        <div class="col-md-3">
            <div class="card text-white bg-primary shadow-sm text-center p-3 h-100">
                <div class="fs-1 fw-bold">${nbPatients}</div>
                <div>Patients enregistrés</div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card text-white bg-success shadow-sm text-center p-3 h-100">
                <div class="fs-1 fw-bold">${nbPatientsAdmis}</div>
                <div>Hospitalisés</div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card text-white bg-secondary shadow-sm text-center p-3 h-100">
                <div class="fs-1 fw-bold">${nbPatientsNonAdmis}</div>
                <div>Non hospitalisés</div>
            </div>
        </div>

        <%-- Taux d'occupation avec barre de progression visuelle --%>
        <div class="col-md-3">
            <div class="card shadow-sm p-3 h-100">
                <div class="fw-bold text-center mb-2">
                    Taux d'occupation des lits
                </div>
                <div class="fs-3 fw-bold text-center
                    ${tauxOccupation >= 90 ? 'text-danger' :
                      tauxOccupation >= 70 ? 'text-warning' : 'text-success'}">
                    <fmt:formatNumber value="${tauxOccupation}" maxFractionDigits="1"/>%
                </div>
                <div class="progress mt-2" style="height: 12px;">
                    <div class="progress-bar
                        ${tauxOccupation >= 90 ? 'bg-danger' :
                          tauxOccupation >= 70 ? 'bg-warning' : 'bg-success'}"
                         style="width: ${tauxOccupation > 100 ? 100 : tauxOccupation}%">
                    </div>
                </div>
                <div class="text-muted small text-center mt-1">
                    ${nbPatientsAdmis} / ${capaciteLits} lits occupés
                </div>
            </div>
        </div>

    </div>

    <%-- Indicateur secondaire : âge moyen --%>
    <div class="alert alert-light border mb-4">
        Age moyen des patients hospitalisés :
        <strong><fmt:formatNumber value="${ageMoyenAdmis}" maxFractionDigits="1"/> ans</strong>
        <c:if test="${nbPatientsAdmis == 0}">
            <span class="text-muted">(aucun patient hospitalisé)</span>
        </c:if>
    </div>

    <%-- =====================================================================
         SECTION 2 — PERSONNEL
         Indicateurs : nombre de médecins, infirmiers, répartition par spécialité
         ===================================================================== --%>
    <h5 class="text-success border-bottom pb-1 mb-3">Personnel médical</h5>
    <div class="row g-3 mb-4">

        <div class="col-md-4">
            <div class="card text-white bg-dark shadow-sm text-center p-3 h-100">
                <div class="fs-1 fw-bold">${nbMedecins}</div>
                <div>Médecins</div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card bg-light shadow-sm text-center p-3 h-100">
                <div class="fs-1 fw-bold text-dark">${nbInfirmiers}</div>
                <div class="text-muted">Infirmiers</div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm p-3 h-100">
                <div class="fw-bold mb-2">Répartition par spécialité</div>
                <%-- Barre de progression par spécialité --%>
                <c:choose>
                    <c:when test="${not empty repartitionSpecialites}">
                        <c:forEach var="entry" items="${repartitionSpecialites}">
                            <div class="mb-2">
                                <div class="d-flex justify-content-between small">
                                    <span>${entry.key}</span>
                                    <span class="badge bg-success">${entry.value}</span>
                                </div>
                                <%-- Largeur proportionnelle au nombre de médecins de cette spécialité --%>
                                <div class="progress" style="height: 6px;">
                                    <div class="progress-bar bg-success"
                                         style="width: ${nbMedecins > 0 ? entry.value * 100 / nbMedecins : 0}%">
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted small">Aucun médecin enregistré.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

    </div>

    <%-- =====================================================================
         SECTION 3 — SOINS ET URGENCES
         Indicateurs : consultations, actes réalisés/en attente, urgences
         ===================================================================== --%>
    <h5 class="text-danger border-bottom pb-1 mb-3">Soins et urgences</h5>
    <div class="row g-3 mb-4">

        <div class="col-md-3">
            <div class="card shadow-sm text-center p-3 h-100">
                <div class="fs-2 fw-bold text-info">${nbConsultations}</div>
                <div class="text-muted">Consultations total</div>
                <div class="text-muted small">dont ${nbConsultationsAujourd} aujourd'hui</div>
            </div>
        </div>

        <%-- Actes chirurgicaux : réalisés vs en attente --%>
        <div class="col-md-3">
            <div class="card shadow-sm p-3 h-100">
                <div class="fw-bold text-center mb-2">Actes chirurgicaux</div>
                <div class="fs-2 fw-bold text-center text-danger">${nbActes}</div>
                <div class="text-center text-muted small mb-2">au total</div>
                <div class="progress" style="height: 10px;">
                    <div class="progress-bar bg-success"
                         style="width: ${nbActes > 0 ? nbActesRealises * 100 / nbActes : 0}%"
                         title="Réalisés">
                    </div>
                    <div class="progress-bar bg-warning"
                         style="width: ${nbActes > 0 ? (nbActes - nbActesRealises) * 100 / nbActes : 0}%"
                         title="En attente">
                    </div>
                </div>
                <div class="d-flex justify-content-between small text-muted mt-1">
                    <span>${nbActesRealises} réalisés</span>
                    <span>${nbActes - nbActesRealises} en attente</span>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card text-white bg-warning shadow-sm text-center p-3 h-100">
                <div class="fs-1 fw-bold text-dark">${nbUrgencesEnAttente}</div>
                <div class="text-dark">Urgences en file d'attente</div>
                <c:if test="${nbUrgencesEnAttente > 0}">
                    <a href="${pageContext.request.contextPath}/urgences"
                       class="btn btn-sm btn-dark mt-2">Voir la file</a>
                </c:if>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card shadow-sm text-center p-3 h-100">
                <div class="fs-2 fw-bold text-danger">${nbConsultationsAujourd}</div>
                <div class="text-muted">Consultations aujourd'hui</div>
            </div>
        </div>

    </div>

    <%-- =====================================================================
         SECTION 4 — FINANCES
         Indicateurs : chiffre d'affaires, coût moyen / min / max consultations
         Calculés via DoubleSummaryStatistics dans StatistiqueService.
         ===================================================================== --%>
    <h5 class="text-warning border-bottom pb-1 mb-3">Finances</h5>
    <div class="row g-3 mb-4">

        <div class="col-md-4">
            <div class="card shadow-sm text-center p-3 h-100">
                <div class="text-muted small mb-1">Chiffre d'affaires total</div>
                <div class="fs-2 fw-bold text-success">
                    <fmt:formatNumber value="${chiffreAffaires}" type="number" maxFractionDigits="0"/> €
                </div>
                <div class="text-muted small">séjours + soins confondus</div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm p-3 h-100">
                <div class="fw-bold text-center mb-2">Coût des consultations</div>
                <table class="table table-sm table-borderless mb-0">
                    <tr>
                        <td class="text-muted">Moyenne</td>
                        <td class="text-end fw-bold">
                            <fmt:formatNumber value="${coutMoyenConsultation}" maxFractionDigits="2"/> €
                        </td>
                    </tr>
                    <tr>
                        <td class="text-muted">Minimum</td>
                        <td class="text-end text-success fw-bold">
                            <fmt:formatNumber value="${coutMinConsultation}" maxFractionDigits="2"/> €
                        </td>
                    </tr>
                    <tr>
                        <td class="text-muted">Maximum</td>
                        <td class="text-end text-danger fw-bold">
                            <fmt:formatNumber value="${coutMaxConsultation}" maxFractionDigits="2"/> €
                        </td>
                    </tr>
                </table>
                <div class="text-muted small text-center mt-1">
                    sur ${nbConsultations} consultation(s)
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm p-3 h-100">
                <div class="fw-bold text-center mb-3">Résumé global</div>
                <ul class="list-unstyled small">
                    <li class="mb-1">
                        <span class="text-muted">Total patients :</span>
                        <strong class="float-end">${nbPatients}</strong>
                    </li>
                    <li class="mb-1">
                        <span class="text-muted">Total personnel :</span>
                        <strong class="float-end">${nbMedecins + nbInfirmiers}</strong>
                    </li>
                    <li class="mb-1">
                        <span class="text-muted">Total soins :</span>
                        <strong class="float-end">${nbConsultations + nbActes}</strong>
                    </li>
                    <li>
                        <span class="text-muted">Urgences en attente :</span>
                        <strong class="float-end ${nbUrgencesEnAttente > 0 ? 'text-danger' : 'text-success'}">
                            ${nbUrgencesEnAttente}
                        </strong>
                    </li>
                </ul>
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
