<%-- Vue : formulaire d'ajout / modification d'une consultation --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HospitApp — Consultation</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/views/commun/navbar.jsp" %>

<main class="container mt-4" style="max-width: 700px;">
    <h2 class="mb-4">
        <c:choose>
            <c:when test="${not empty consultation}">Modifier la consultation</c:when>
            <c:otherwise>Nouvelle consultation</c:otherwise>
        </c:choose>
    </h2>

    <div class="card shadow-sm">
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/soins">
                <input type="hidden" name="action"
                       value="${not empty consultation ? 'modifierCons' : 'ajouterCons'}">
                <c:if test="${not empty consultation}">
                    <input type="hidden" name="id" value="${consultation.id}">
                </c:if>

                <div class="row g-3">
                    <div class="col-12">
                        <label class="form-label fw-bold">Patient <span class="text-danger">*</span></label>
                        <select name="numeroPatient" class="form-select" required>
                            <option value="">— Sélectionner un patient —</option>
                            <c:forEach var="p" items="${patients}">
                                <option value="${p.numeroPatient}"
                                    <c:if test="${p.numeroPatient eq consultation.numeroPatient}">selected</c:if>>
                                    ${p.nomComplet} (${p.numeroPatient})
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-12">
                        <label class="form-label fw-bold">Médecin <span class="text-danger">*</span></label>
                        <select name="matriculeMedecin" class="form-select" required>
                            <option value="">— Sélectionner un médecin —</option>
                            <c:forEach var="m" items="${medecins}">
                                <option value="${m.matricule}"
                                    <c:if test="${m.matricule eq consultation.matriculeMedecin}">selected</c:if>>
                                    Dr. ${m.nomComplet} — ${m.specialite}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-12">
                        <label class="form-label fw-bold">Motif <span class="text-danger">*</span></label>
                        <input type="text" name="motif" class="form-control"
                               value="${not empty consultation ? consultation.motif : ''}"
                               placeholder="Ex : Douleurs thoraciques" required>
                    </div>
                    <div class="col-12">
                        <label class="form-label">Diagnostic</label>
                        <input type="text" name="diagnostic" class="form-control"
                               value="${not empty consultation ? consultation.diagnostic : ''}"
                               placeholder="Diagnostic établi">
                    </div>
                    <div class="col-12">
                        <label class="form-label">Ordonnance</label>
                        <textarea name="ordonnance" class="form-control" rows="3"
                                  placeholder="Médicaments prescrits, posologie...">${not empty consultation ? consultation.ordonnance : ''}</textarea>
                    </div>
                </div>

                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-info text-white">
                        <c:choose>
                            <c:when test="${not empty consultation}">Enregistrer</c:when>
                            <c:otherwise>Ajouter la consultation</c:otherwise>
                        </c:choose>
                    </button>
                    <a href="${pageContext.request.contextPath}/soins" class="btn btn-outline-secondary">Annuler</a>
                </div>
            </form>
        </div>
    </div>
</main>

<footer class="mt-5 py-3 bg-light border-top text-center text-muted small">
    HospitApp — Projet POO Avancée Bachelor 3
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
