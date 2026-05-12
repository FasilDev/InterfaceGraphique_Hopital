<%-- Vue : tableau de bord des statistiques dynamiques --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
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
    <h2 class="mb-4">Tableau de bord</h2>

    <%-- Ligne 1 : Patients --%>
    <div class="row g-3 mb-3">
        <div class="col-md-3">
            <div class="card text-white bg-primary shadow-sm text-center p-3">
                <div class="fs-1 fw-bold">${nbPatients}</div>
                <div>Patients total</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-white bg-success shadow-sm text-center p-3">
                <div class="fs-1 fw-bold">${nbPatientsAdmis}</div>
                <div>Hospitalisés</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-white bg-secondary shadow-sm text-center p-3">
                <div class="fs-1 fw-bold">${nbPatientsNonAdmis}</div>
                <div>Non hospitalisés</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-white bg-info shadow-sm text-center p-3">
                <div class="fs-1 fw-bold"><fmt:formatNumber value="${tauxOccupation}" maxFractionDigits="1"/>%</div>
                <div>Taux d'occupation (/${capaciteLits} lits)</div>
            </div>
        </div>
    </div>

    <%-- Ligne 2 : Personnel --%>
    <div class="row g-3 mb-3">
        <div class="col-md-3">
            <div class="card text-white bg-dark shadow-sm text-center p-3">
                <div class="fs-1 fw-bold">${nbMedecins}</div>
                <div>Médecins</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card bg-light shadow-sm text-center p-3">
                <div class="fs-1 fw-bold text-dark">${nbInfirmiers}</div>
                <div class="text-muted">Infirmiers</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-white bg-warning shadow-sm text-center p-3">
                <div class="fs-1 fw-bold text-dark">${nbUrgencesEnAttente}</div>
                <div class="text-dark">Urgences en attente</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-white bg-danger shadow-sm text-center p-3">
                <div class="fs-1 fw-bold">${nbConsultationsAujourd}</div>
                <div>Consultations aujourd'hui</div>
            </div>
        </div>
    </div>

    <%-- Ligne 3 : Soins et finances --%>
    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="card shadow-sm text-center p-3">
                <div class="fs-2 fw-bold text-info">${nbConsultations}</div>
                <div class="text-muted">Consultations total</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card shadow-sm text-center p-3">
                <div class="fs-2 fw-bold text-danger">${nbActes}</div>
                <div class="text-muted">Actes chirurgicaux</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card shadow-sm text-center p-3">
                <div class="fs-2 fw-bold text-success">
                    <fmt:formatNumber value="${chiffreAffaires}" type="number" maxFractionDigits="0"/> €
                </div>
                <div class="text-muted">Chiffre d'affaires total</div>
            </div>
        </div>
    </div>

    <%-- Répartition par spécialité --%>
    <div class="card shadow-sm">
        <div class="card-header fw-bold">Répartition des médecins par spécialité</div>
        <div class="card-body">
            <c:forEach var="entry" items="${repartitionSpecialites}">
                <div class="mb-2">
                    <div class="d-flex justify-content-between">
                        <span>${entry.key}</span>
                        <span class="badge bg-success">${entry.value} médecin(s)</span>
                    </div>
                    <div class="progress" style="height: 8px;">
                        <div class="progress-bar bg-success" style="width: ${entry.value * 100 / nbMedecins}%"></div>
                    </div>
                </div>
            </c:forEach>
            <c:if test="${empty repartitionSpecialites}">
                <p class="text-muted">Aucun médecin enregistré.</p>
            </c:if>
        </div>
    </div>

    <p class="text-muted mt-3 small">
        Age moyen des patients hospitalisés : <fmt:formatNumber value="${ageMoyenAdmis}" maxFractionDigits="1"/> ans
    </p>
</main>

<footer class="mt-5 py-3 bg-light border-top text-center text-muted small">
    HospitApp — Projet POO Avancée Bachelor 3
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
