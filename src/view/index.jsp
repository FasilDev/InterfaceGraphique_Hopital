<%--
    📄 Fichier : index.jsp
    📌 Ce fichier fait partie du projet HospitApp, une application web Java de gestion hospitalière.
    🧩 Rôle de ce fichier : Page d'accueil de l'application.
                            C'est la première page que l'utilisateur voit en arrivant sur le site.
                            Elle présente les différentes sections et permet la navigation.
    🔄 Il interagit avec : les Servlets via les liens href (PatientServlet, PersonnelServlet, etc.)
    👶 Niveau débutant : JSP = Java Server Pages.
                         Mélange de HTML et de balises spéciales.
                         Le serveur Tomcat exécute ce fichier et envoie du HTML pur au navigateur.
                         Cette page ne contient PAS de logique métier — juste de l'affichage (vue MVC).
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <!-- Responsive : l'application s'adapte aux mobiles, tablettes et ordinateurs -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HospitApp - Gestion Hospitalière</title>

    <!--
        Bootstrap 5 : framework CSS open-source.
        Chargé depuis un CDN (Content Delivery Network) pour éviter de le stocker localement.
        Bootstrap fournit la grille responsive, les boutons, les cartes, la navbar, etc.
    -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <!--
        ${pageContext.request.contextPath} = chemin racine de l'application dans Tomcat.
        Exemple : si l'app est déployée sur /HospitApp, ça retourne "/HospitApp".
        TOUJOURS utiliser ça pour les URLs internes — ça évite les bugs si on change le chemin.
    -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <%-- ============================================================
         BARRE DE NAVIGATION — Vue MVC
         Présente sur toutes les pages (sera déplacée dans un fragment JSP plus tard).
         Liens vers les 5 grandes sections de l'application.
         ============================================================ --%>
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
        <div class="container">

            <!-- Logo / Nom de l'application — clique ramène à l'accueil -->
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">
                &#x1F3E5; HospitApp
            </a>

            <!--
                Bouton "hamburger" visible sur mobile.
                data-bs-toggle="collapse" et data-bs-target="#navbarNav"
                sont des attributs Bootstrap qui gèrent l'affichage/masquage du menu.
            -->
            <button class="navbar-toggler" type="button"
                    data-bs-toggle="collapse" data-bs-target="#navbarNav"
                    aria-controls="navbarNav" aria-expanded="false"
                    aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <!-- Liens de navigation — masqués sur mobile, visibles au clic sur hamburger -->
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <%-- Chaque lien pointe vers un Servlet (défini dans les commits suivants) --%>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/patients">
                            Patients
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/personnel">
                            Personnel
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/soins">
                            Soins
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/urgences">
                            Urgences
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/statistiques">
                            Statistiques
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
    <%-- Fin navbar --%>

    <%-- ============================================================
         CONTENU PRINCIPAL
         ============================================================ --%>
    <main class="container mt-5">

        <%-- En-tête de bienvenue --%>
        <div class="text-center mb-5">
            <h1 class="display-4 fw-bold text-primary">Bienvenue sur HospitApp</h1>
            <p class="lead text-muted">
                Application web de gestion hospitalière &mdash; Projet POO Avancée Bachelor 3
            </p>
            <hr class="my-4">
            <p class="text-muted">
                Gérez patients, personnel médical, soins et urgences depuis une interface simple et intuitive.
            </p>
        </div>

        <%-- ============================================================
             CARTES DE NAVIGATION RAPIDE
             Bootstrap grid system : "row g-4" = rangée avec espacement 4 entre les colonnes.
             "col-md-4" = 1/3 de la largeur sur écrans medium (768px+), 100% sur mobile.
             ============================================================ --%>
        <div class="row g-4 justify-content-center">

            <%-- Carte Patients --%>
            <div class="col-md-4">
                <div class="card h-100 shadow-sm border-primary">
                    <div class="card-body text-center p-4">
                        <div class="fs-1 mb-3">&#x1F9D1;</div>
                        <h5 class="card-title text-primary fw-bold">Patients</h5>
                        <p class="card-text text-muted">
                            Admission, dossiers médicaux, antécédents et sorties.
                        </p>
                        <a href="${pageContext.request.contextPath}/patients"
                           class="btn btn-primary mt-2">
                            G&eacute;rer les patients
                        </a>
                    </div>
                </div>
            </div>

            <%-- Carte Personnel --%>
            <div class="col-md-4">
                <div class="card h-100 shadow-sm border-success">
                    <div class="card-body text-center p-4">
                        <div class="fs-1 mb-3">&#x1F468;&#x200D;&#x2695;&#xFE0F;</div>
                        <h5 class="card-title text-success fw-bold">Personnel</h5>
                        <p class="card-text text-muted">
                            M&eacute;decins, infirmiers, sp&eacute;cialit&eacute;s et plannings.
                        </p>
                        <a href="${pageContext.request.contextPath}/personnel"
                           class="btn btn-success mt-2">
                            G&eacute;rer le personnel
                        </a>
                    </div>
                </div>
            </div>

            <%-- Carte Soins --%>
            <div class="col-md-4">
                <div class="card h-100 shadow-sm border-info">
                    <div class="card-body text-center p-4">
                        <div class="fs-1 mb-3">&#x1F489;</div>
                        <h5 class="card-title text-info fw-bold">Soins</h5>
                        <p class="card-text text-muted">
                            Consultations, prescriptions et actes m&eacute;dicaux.
                        </p>
                        <a href="${pageContext.request.contextPath}/soins"
                           class="btn btn-info text-white mt-2">
                            G&eacute;rer les soins
                        </a>
                    </div>
                </div>
            </div>

            <%-- Carte Urgences --%>
            <div class="col-md-4">
                <div class="card h-100 shadow-sm border-danger">
                    <div class="card-body text-center p-4">
                        <div class="fs-1 mb-3">&#x1F6A8;</div>
                        <h5 class="card-title text-danger fw-bold">Urgences</h5>
                        <p class="card-text text-muted">
                            File d'attente avec priorit&eacute; m&eacute;dicale en temps r&eacute;el.
                        </p>
                        <a href="${pageContext.request.contextPath}/urgences"
                           class="btn btn-danger mt-2">
                            File des urgences
                        </a>
                    </div>
                </div>
            </div>

            <%-- Carte Statistiques --%>
            <div class="col-md-4">
                <div class="card h-100 shadow-sm border-warning">
                    <div class="card-body text-center p-4">
                        <div class="fs-1 mb-3">&#x1F4CA;</div>
                        <h5 class="card-title text-warning fw-bold">Statistiques</h5>
                        <p class="card-text text-muted">
                            Indicateurs dynamiques : lits, urgences, sp&eacute;cialit&eacute;s.
                        </p>
                        <a href="${pageContext.request.contextPath}/statistiques"
                           class="btn btn-warning mt-2">
                            Voir les stats
                        </a>
                    </div>
                </div>
            </div>

        </div>
        <%-- Fin des cartes --%>

    </main>
    <%-- Fin du contenu principal --%>

    <%-- ============================================================
         PIED DE PAGE
         ============================================================ --%>
    <footer class="footer mt-5 py-3 bg-light border-top">
        <div class="container text-center">
            <span class="text-muted small">
                HospitApp &mdash; Projet POO Avanc&eacute;e &mdash; Bachelor 3 Informatique
            </span>
        </div>
    </footer>

    <!--
        Bootstrap JS — doit être en bas du body, après le HTML.
        "bundle" inclut Popper.js (nécessaire pour les menus déroulants, tooltips, etc.)
    -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js">
    </script>

</body>
</html>
