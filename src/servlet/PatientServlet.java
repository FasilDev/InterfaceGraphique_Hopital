/*
 * Fichier : PatientServlet.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Contrôleur web pour les patients.
 *        Reçoit les requêtes HTTP, appelle PatientService, et transmet les données aux JSP.
 *        Ce servlet ne contient aucune logique métier — il délègue tout au service.
 *
 * Interactions : PatientService, SoinService, CsvService,
 *                JSP : patients/liste.jsp, detail.jsp, formulaire.jsp
 *
 * Architecture MVC appliquée ici :
 *   - Modèle  : Patient, PatientService (traitement des données)
 *   - Vue     : JSP (affichage uniquement)
 *   - Contrôleur : ce servlet (fait le lien entre les deux)
 *
 * Pattern PRG (Post/Redirect/Get) :
 *   Après un POST (ajout, modification, suppression), on REDIRIGE vers un GET.
 *   Cela évite le double-envoi du formulaire si l'utilisateur actualise la page.
 *
 * Actions GET  : list, detail, nouveau, editer
 * Actions POST : ajouter, modifier, supprimer, admettre, sortir
 */

package servlet;

import controller.PatientService;
import controller.PersonnelService;
import controller.SoinService;
import model.Patient;
import util.CsvService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;

@WebServlet("/patients")
public class PatientServlet extends HttpServlet {

    private PatientService patientService;
    private SoinService    soinService;

    @Override
    public void init() throws ServletException {
        patientService = PatientService.getInstance();
        soinService    = SoinService.getInstance();
    }

    // -----------------------------------------------------------------------
    // GET — affichage des pages
    // -----------------------------------------------------------------------

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "detail"  -> afficherDetail(req, resp);
            case "nouveau" -> afficherFormulaire(req, resp, null);
            case "editer"  -> afficherFormulaire(req, resp, req.getParameter("id"));
            default        -> listerPatients(req, resp);
        }
    }

    private void listerPatients(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Lecture des paramètres de filtre et de tri (tous optionnels)
        String nom           = req.getParameter("nom");
        String admisParam    = req.getParameter("admis");
        String groupeSanguin = req.getParameter("groupeSanguin");
        String triColonne    = req.getParameter("tri");
        String triOrdre      = req.getParameter("ordre");

        // "true"/"false" → Boolean, vide ou absent → null (= pas de filtre sur ce critère)
        Boolean admis = (admisParam == null || admisParam.isBlank())
                ? null : Boolean.parseBoolean(admisParam);
        boolean croissant = !"desc".equals(triOrdre);

        req.setAttribute("patients",              patientService.rechercherEtTrier(nom, admis, groupeSanguin, triColonne, croissant));
        // On repasse les critères à la JSP pour pré-remplir les champs du formulaire de recherche
        req.setAttribute("critereNom",            nom);
        req.setAttribute("critereAdmis",          admisParam);
        req.setAttribute("critereGroupeSanguin",  groupeSanguin);
        req.setAttribute("triColonne",            triColonne);
        req.setAttribute("triOrdre",              triOrdre);
        forward(req, resp, "/WEB-INF/views/patients/liste.jsp");
    }

    private void afficherDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String id = req.getParameter("id");
        Patient patient = patientService.trouverParId(id);
        if (patient == null) {
            req.setAttribute("erreur", "Patient introuvable.");
            forward(req, resp, "/WEB-INF/views/erreur.jsp");
            return;
        }
        req.setAttribute("patient", patient);
        // Historique des soins du patient (consultations + actes)
        req.setAttribute("soins", soinService.listerSoinsParPatient(patient.getNumeroPatient()));
        forward(req, resp, "/WEB-INF/views/patients/detail.jsp");
    }

    private void afficherFormulaire(HttpServletRequest req, HttpServletResponse resp, String id)
            throws ServletException, IOException {
        if (id != null) {
            req.setAttribute("patient", patientService.trouverParId(id));
        }
        forward(req, resp, "/WEB-INF/views/patients/formulaire.jsp");
    }

    // -----------------------------------------------------------------------
    // POST — traitement des formulaires
    // -----------------------------------------------------------------------

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "ajouter"           -> ajouterPatient(req, resp);
            case "modifier"          -> modifierPatient(req, resp);
            case "supprimer"         -> supprimerPatient(req, resp);
            case "admettre"          -> admettrePatient(req, resp);
            case "sortir"            -> sortirPatient(req, resp);
            case "ajouterAntecedent" -> ajouterAntecedent(req, resp);
            default                  -> resp.sendRedirect(req.getContextPath() + "/patients");
        }
    }

    private void ajouterPatient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            // Génération automatique du numéro patient (ex : "P-2025-006")
            String numero = "P-" + Year.now().getValue() + "-"
                    + String.format("%03d", patientService.getNombre() + 1);
            Patient p = new Patient(
                    req.getParameter("nom").trim(),
                    req.getParameter("prenom").trim(),
                    LocalDate.parse(req.getParameter("dateNaissance")),
                    numero
            );
            remplirChampsFacultatifs(p, req);
            patientService.ajouter(p);
            sauvegarder();
            message(req, "Patient " + p.getNomComplet() + " ajouté avec succès.", "success");
        } catch (Exception e) {
            message(req, "Erreur lors de l'ajout : " + e.getMessage(), "danger");
        }
        resp.sendRedirect(req.getContextPath() + "/patients");
    }

    private void modifierPatient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            Patient p = patientService.trouverParId(req.getParameter("id"));
            if (p == null) throw new IllegalStateException("Patient introuvable.");
            p.setNom(req.getParameter("nom").trim());
            p.setPrenom(req.getParameter("prenom").trim());
            p.setDateNaissance(LocalDate.parse(req.getParameter("dateNaissance")));
            remplirChampsFacultatifs(p, req);
            patientService.modifier(p);
            sauvegarder();
            message(req, "Dossier de " + p.getNomComplet() + " mis à jour.", "success");
        } catch (Exception e) {
            message(req, "Erreur lors de la modification : " + e.getMessage(), "danger");
        }
        resp.sendRedirect(req.getContextPath() + "/patients");
    }

    private void supprimerPatient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        patientService.supprimer(req.getParameter("id"));
        sauvegarder();
        message(req, "Patient supprimé.", "warning");
        resp.sendRedirect(req.getContextPath() + "/patients");
    }

    private void admettrePatient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String id = req.getParameter("id");
        try {
            patientService.admettre(id, req.getParameter("chambre"));
            sauvegarder();
            message(req, "Patient admis.", "success");
        } catch (Exception e) {
            message(req, "Admission impossible : " + e.getMessage(), "danger");
        }
        resp.sendRedirect(req.getContextPath() + "/patients?action=detail&id=" + id);
    }

    private void sortirPatient(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String id = req.getParameter("id");
        try {
            patientService.sortir(id);
            sauvegarder();
            message(req, "Sortie enregistrée.", "success");
        } catch (Exception e) {
            message(req, "Sortie impossible : " + e.getMessage(), "danger");
        }
        resp.sendRedirect(req.getContextPath() + "/patients?action=detail&id=" + id);
    }

    private void ajouterAntecedent(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String id         = req.getParameter("id");
        String antecedent = str(req.getParameter("antecedent"));
        try {
            Patient p = patientService.trouverParId(id);
            if (p == null) throw new IllegalStateException("Patient introuvable.");
            if (antecedent != null) {
                p.ajouterAntecedent(antecedent);
                patientService.modifier(p);
                sauvegarder();
                message(req, "Antécédent ajouté : " + antecedent, "success");
            }
        } catch (Exception e) {
            message(req, "Erreur : " + e.getMessage(), "danger");
        }
        resp.sendRedirect(req.getContextPath() + "/patients?action=detail&id=" + id);
    }

    // -----------------------------------------------------------------------
    // Méthodes utilitaires privées
    // -----------------------------------------------------------------------

    // Remplit les champs non obligatoires depuis les paramètres du formulaire
    private void remplirChampsFacultatifs(Patient p, HttpServletRequest req) {
        p.setGroupeSanguin(str(req.getParameter("groupeSanguin")));
        p.setTelephone(str(req.getParameter("telephone")));
        p.setEmail(str(req.getParameter("email")));
        p.setNotes(str(req.getParameter("notes")));
        p.setNumeroSecuriteSociale(str(req.getParameter("numeroSecuriteSociale")));
        // checkbox HTML : envoie "on" si cochée, rien si décochée
        p.setPrisEnCharge("on".equals(req.getParameter("prisEnCharge")));
    }

    // null ou vide → null ; sinon trim
    private String str(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    // Stocke un message flash en session (visible après redirect)
    private void message(HttpServletRequest req, String texte, String type) {
        req.getSession().setAttribute("message", texte);
        req.getSession().setAttribute("messageType", type);
    }

    private void sauvegarder() {
        CsvService.sauvegarderTout(
                PatientService.getInstance(),
                PersonnelService.getInstance(),
                SoinService.getInstance()
        );
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String chemin)
            throws ServletException, IOException {
        req.getRequestDispatcher(chemin).forward(req, resp);
    }
}