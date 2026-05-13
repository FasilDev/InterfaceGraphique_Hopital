<%--
    Fichier : navbar.jsp
    Projet HospitApp - application web Java de gestion hospitalière.
    Rôle : fragment de navigation réutilisé dans toutes les pages.
           Inclure avec : <%@ include file="/WEB-INF/views/commun/navbar.jsp" %>
    Note : le lien de la section courante est mis en surbrillance automatiquement
           grâce au script JS en bas de ce fragment (lecture de l'URL courante).
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
    <div class="container">

        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">
            HospitApp
        </a>

        <button class="navbar-toggler" type="button"
                data-bs-toggle="collapse" data-bs-target="#navMenu"
                aria-controls="navMenu" aria-expanded="false" aria-label="Menu">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navMenu">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/patients"
                       data-section="patients">Patients</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/personnel"
                       data-section="personnel">Personnel</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/soins"
                       data-section="soins">Soins</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/urgences"
                       data-section="urgences">Urgences</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/statistiques"
                       data-section="statistiques">Statistiques</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<script>
    // Mise en surbrillance du lien de la section courante.
    // On lit le chemin de l'URL (ex: "/HospitApp/patients") et on cherche quel lien correspond.
    // Cette approche client-side évite de passer un attribut "page courante" dans chaque servlet.
    document.addEventListener("DOMContentLoaded", function () {
        var chemin = window.location.pathname;
        document.querySelectorAll(".navbar-nav .nav-link[data-section]").forEach(function (lien) {
            var section = lien.getAttribute("data-section");
            if (chemin.indexOf("/" + section) !== -1) {
                lien.classList.add("active");
                lien.setAttribute("aria-current", "page");
            }
        });

        // Auto-masquage des messages de succès et d'information après 4 secondes.
        // Les alertes de type "danger" et "warning" restent affichées (l'utilisateur doit les lire).
        document.querySelectorAll(".alert-success, .alert-info").forEach(function (alerte) {
            setTimeout(function () {
                // Bootstrap 5 : on déclenche la fermeture via l'API JS de Bootstrap
                var instance = bootstrap.Alert.getOrCreateInstance(alerte);
                if (instance) {
                    instance.close();
                }
            }, 4000); // 4000ms = 4 secondes
        });
    });
</script>
