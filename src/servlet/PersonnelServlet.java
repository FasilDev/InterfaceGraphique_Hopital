/*
 * Fichier : PersonnelServlet.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Contrôleur web pour le personnel hospitalier (médecins et infirmiers).
 *        Gère les deux types dans un même servlet en distinguant via le paramètre "type"
 *        ("medecin" ou "infirmier").
 *
 * Interactions : PersonnelService, CsvService,
 *                JSP : personnel/liste.jsp, formulaire-medecin.jsp, formulaire-infirmier.jsp
 *
 * Actions GET  : list, nouveauMedecin, nouveauInfirmier, editerMedecin, editerInfirmier
 * Actions POST : ajouterMedecin, ajouterInfirmier, modifierMedecin, modifierInfirmier, supprimer
 */

package servlet;

import controller.PatientService;
import controller.PersonnelService;
import controller.SoinService;
import model.Infirmier;
import model.Medecin;
import util.CsvService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/personnel")
public class PersonnelServlet extends HttpServlet {

    private PersonnelService personnelService;

    @Override
    public void init() throws ServletException {
        personnelService = PersonnelService.getInstance();
    }

    // -----------------------------------------------------------------------
    // GET
    // -----------------------------------------------------------------------

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "nouveauMedecin"    -> afficherFormMedecin(req, resp, null);
            case "nouveauInfirmier"  -> afficherFormInfirmier(req, resp, null);
            case "editerMedecin"     -> afficherFormMedecin(req, resp, req.getParameter("id"));
            case "editerInfirmier"   -> afficherFormInfirmier(req, resp, req.getParameter("id"));
            default                  -> listerPersonnel(req, resp);
        }
    }

    private void listerPersonnel(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("medecins",   personnelService.listerMedecins());
        req.setAttribute("infirmiers", personnelService.listerInfirmiers());
        req.setAttribute("specialites", personnelService.getSpecialitesDisponibles());
        forward(req, resp, "/WEB-INF/views/personnel/liste.jsp");
    }

    private void afficherFormMedecin(HttpServletRequest req, HttpServletResponse resp, String id)
            throws ServletException, IOException {
        if (id != null) req.setAttribute("medecin", personnelService.trouverMedecinParId(id));
        forward(req, resp, "/WEB-INF/views/personnel/formulaire-medecin.jsp");
    }

    private void afficherFormInfirmier(HttpServletRequest req, HttpServletResponse resp, String id)
            throws ServletException, IOException {
        if (id != null) req.setAttribute("infirmier", personnelService.trouverInfirmierParId(id));
        forward(req, resp, "/WEB-INF/views/personnel/formulaire-infirmier.jsp");
    }

    // -----------------------------------------------------------------------
    // POST
    // -----------------------------------------------------------------------

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "ajouterMedecin"   -> ajouterMedecin(req, resp);
            case "ajouterInfirmier" -> ajouterInfirmier(req, resp);
            case "modifierMedecin"  -> modifierMedecin(req, resp);
            case "modifierInfirmier"-> modifierInfirmier(req, resp);
            case "supprimer"        -> supprimer(req, resp);
            default -> resp.sendRedirect(req.getContextPath() + "/personnel");
        }
    }

    private void ajouterMedecin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            Medecin m = new Medecin(
                    req.getParameter("nom").trim(),
                    req.getParameter("prenom").trim(),
                    LocalDate.parse(req.getParameter("dateNaissance")),
                    req.getParameter("matricule").trim(),
                    req.getParameter("specialite").trim(),
                    req.getParameter("numeroOrdre").trim()
            );
            m.setTelephone(str(req.getParameter("telephone")));
            m.setEmail(str(req.getParameter("email")));
            personnelService.ajouterMedecin(m);
            sauvegarder();
            message(req, "Dr. " + m.getNomComplet() + " ajouté.", "success");
        } catch (Exception e) {
            message(req, "Erreur : " + e.getMessage(), "danger");
        }
        resp.sendRedirect(req.getContextPath() + "/personnel");
    }

    private void ajouterInfirmier(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            Infirmier inf = new Infirmier(
                    req.getParameter("nom").trim(),
                    req.getParameter("prenom").trim(),
                    LocalDate.parse(req.getParameter("dateNaissance")),
                    req.getParameter("matricule").trim(),
                    req.getParameter("service").trim(),
                    req.getParameter("qualification").trim()
            );
            inf.setTelephone(str(req.getParameter("telephone")));
            inf.setGardeNuit("on".equals(req.getParameter("gardeNuit")));
            personnelService.ajouterInfirmier(inf);
            sauvegarder();
            message(req, inf.getNomComplet() + " ajouté(e).", "success");
        } catch (Exception e) {
            message(req, "Erreur : " + e.getMessage(), "danger");
        }
        resp.sendRedirect(req.getContextPath() + "/personnel");
    }

    private void modifierMedecin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            Medecin m = personnelService.trouverMedecinParId(req.getParameter("id"));
            if (m == null) throw new IllegalStateException("Médecin introuvable.");
            m.setNom(req.getParameter("nom").trim());
            m.setPrenom(req.getParameter("prenom").trim());
            m.setDateNaissance(LocalDate.parse(req.getParameter("dateNaissance")));
            m.setSpecialite(req.getParameter("specialite").trim());
            m.setNumeroOrdre(req.getParameter("numeroOrdre").trim());
            m.setTelephone(str(req.getParameter("telephone")));
            m.setEmail(str(req.getParameter("email")));
            m.setDisponible("on".equals(req.getParameter("disponible")));
            personnelService.modifierMedecin(m);
            sauvegarder();
            message(req, "Dossier de " + m.getNomComplet() + " mis à jour.", "success");
        } catch (Exception e) {
            message(req, "Erreur : " + e.getMessage(), "danger");
        }
        resp.sendRedirect(req.getContextPath() + "/personnel");
    }

    private void modifierInfirmier(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            Infirmier inf = personnelService.trouverInfirmierParId(req.getParameter("id"));
            if (inf == null) throw new IllegalStateException("Infirmier introuvable.");
            inf.setNom(req.getParameter("nom").trim());
            inf.setPrenom(req.getParameter("prenom").trim());
            inf.setDateNaissance(LocalDate.parse(req.getParameter("dateNaissance")));
            inf.setService(req.getParameter("service").trim());
            inf.setQualification(req.getParameter("qualification").trim());
            inf.setTelephone(str(req.getParameter("telephone")));
            inf.setGardeNuit("on".equals(req.getParameter("gardeNuit")));
            inf.setDisponible("on".equals(req.getParameter("disponible")));
            personnelService.modifierInfirmier(inf);
            sauvegarder();
            message(req, "Dossier de " + inf.getNomComplet() + " mis à jour.", "success");
        } catch (Exception e) {
            message(req, "Erreur : " + e.getMessage(), "danger");
        }
        resp.sendRedirect(req.getContextPath() + "/personnel");
    }

    private void supprimer(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String id   = req.getParameter("id");
        String type = req.getParameter("type");
        if ("infirmier".equals(type)) {
            personnelService.supprimerInfirmier(id);
        } else {
            personnelService.supprimerMedecin(id);
        }
        sauvegarder();
        message(req, "Membre du personnel supprimé.", "warning");
        resp.sendRedirect(req.getContextPath() + "/personnel");
    }

    // -----------------------------------------------------------------------
    // Utilitaires
    // -----------------------------------------------------------------------

    private String str(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }

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
