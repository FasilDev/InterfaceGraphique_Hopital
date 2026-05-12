<%-- Vue : formulaire pour déclarer un nouvel acte chirurgical urgent --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HospitApp — Nouvelle urgence</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/views/commun/navbar.jsp" %>

<main class="container mt-4" style="max-width: 700px;">
    <h2 class="mb-4 text-danger">Déclarer une urgence chirurgicale</h2>

    <div class="card shadow-sm border-danger">
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/urgences">
                <input type="hidden" name="action" value="ajouterUrgence">

                <div class="row g-3">
                    <div class="col-12">
                        <label class="form-label fw-bold">Niveau de priorité <span class="text-danger">*</span></label>
                        <select name="niveauPriorite" class="form-select" required>
                            <option value="1">1 — Critique (pronostic vital engagé)</option>
                            <option value="2" selected>2 — Urgent (risque fort dans l'heure)</option>
                            <option value="3">3 — Semi-urgent</option>
                            <option value="4">4 — Peu urgent</option>
                            <option value="5">5 — Non urgent</option>
                        </select>
                    </div>
                    <div class="col-12">
                        <label class="form-label fw-bold">Patient <span class="text-danger">*</span></label>
                        <select name="numeroPatient" class="form-select" required>
                            <option value="">— Sélectionner un patient —</option>
                            <c:forEach var="p" items="${patients}">
                                <option value="${p.numeroPatient}">${p.nomComplet} (${p.numeroPatient})</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-12">
                        <label class="form-label fw-bold">Chirurgien <span class="text-danger">*</span></label>
                        <select name="matriculeMedecin" class="form-select" required>
                            <option value="">— Sélectionner un médecin —</option>
                            <c:forEach var="m" items="${medecins}">
                                <option value="${m.matricule}">Dr. ${m.nomComplet} — ${m.specialite}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-12">
                        <label class="form-label fw-bold">Type d'acte <span class="text-danger">*</span></label>
                        <input type="text" name="typeActe" class="form-control"
                               placeholder="Ex : Appendicectomie, Suture, Réduction fracture..." required>
                    </div>
                    <div class="col-12">
                        <label class="form-label fw-bold">Description de l'urgence <span class="text-danger">*</span></label>
                        <textarea name="descriptionUrgence" class="form-control" rows="2"
                                  placeholder="Décrivez brièvement la situation clinique..." required></textarea>
                    </div>
                    <div class="col-12">
                        <label class="form-label">Salle assignée</label>
                        <input type="text" name="salle" class="form-control"
                               placeholder="Ex : Bloc 1, Salle A">
                    </div>
                </div>

                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-danger">Ajouter à la file d'urgences</button>
                    <a href="${pageContext.request.contextPath}/urgences" class="btn btn-outline-secondary">Annuler</a>
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
