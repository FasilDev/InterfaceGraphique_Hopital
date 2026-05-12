/*
 * UrgenceServlet.java - Contrôleur web pour la file d'urgences.
 * "Traiter" retire l'acte le plus prioritaire de la PriorityQueue et le marque comme réalisé.
 *
 * GET  : list, nouvelleUrgence
 * POST : ajouterUrgence, traiter
 */

package servlet;

import controller.PatientService;
import controller.PersonnelService;
import controller.SoinService;
import model.ActeChirurgical;
import util.CsvService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/urgences")
public class UrgenceServlet extends HttpServlet {

    private SoinService      soinService;
    private PatientService   patientService;
    private PersonnelService personnelService;

    @Override
    public void init() throws ServletException {
        soinService      = SoinService.getInstance();
        patientService   = PatientService.getInstance();
        personnelService = PersonnelService.getInstance();
    }

    // -------------------------------------------------------------------
    // GET
    // -------------------------------------------------------------------

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "nouvelleUrgence" -> afficherFormulaire(req, resp);
            default                -> listerUrgences(req, resp);
        }
    }

    private void listerUrgences(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("urgences",         soinService.listerUrgencesEnAttente());
        req.setAttribute("prochaineUrgence", soinService.voirProchaineUrgence());
        req.setAttribute("nbUrgences",       soinService.getNombreUrgencesEnAttente());
        forward(req, resp, "/WEB-INF/views/urgences/liste.jsp");
    }

    private void afficherFormulaire(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("patients", patientService.listerTous());
        req.setAttribute("medecins", personnelService.listerMedecins());
        forward(req, resp, "/WEB-INF/views/urgences/formulaire-urgence.jsp");
    }

    // -------------------------------------------------------------------
    // POST
    // -------------------------------------------------------------------

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "ajouterUrgence" -> ajouterUrgence(req, resp);
            case "traiter"        -> traiterUrgence(req, resp);
            default -> resp.sendRedirect(req.getContextPath() + "/urgences");
        }
    }

    private void ajouterUrgence(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            int niveau = Integer.parseInt(req.getParameter("niveauPriorite"));
            ActeChirurgical acte = new ActeChirurgical(
                    req.getParameter("typeActe").trim(),
                    niveau,
                    req.getParameter("descriptionUrgence").trim(),
                    req.getParameter("numeroPatient"),
                    req.getParameter("matriculeMedecin")
            );
            acte.setSalle(str(req.getParameter("salle")));
            soinService.ajouterActeChirurgical(acte);
            sauvegarder();
            message(req, "Urgence ajoutée à la file (priorité : " + acte.getLabelPriorite() + ").", "warning");
        } catch (Exception e) {
            message(req, "Erreur : " + e.getMessage(), "danger");
        }
        resp.sendRedirect(req.getContextPath() + "/urgences");
    }

    private void traiterUrgence(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        ActeChirurgical traite = soinService.traiterProchaineUrgence();
        if (traite != null) {
            sauvegarder();
            message(req, "Urgence traitée : " + traite.getTypeActe()
                    + " (patient " + traite.getNumeroPatient() + ").", "success");
        } else {
            message(req, "La file d'urgences est vide.", "info");
        }
        resp.sendRedirect(req.getContextPath() + "/urgences");
    }

    // -------------------------------------------------------------------
    // Utilitaires
    // -------------------------------------------------------------------

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
