/*
 * SoinServlet.java - Contrôleur web pour les consultations médicales.
 * Les actes chirurgicaux urgents sont gérés séparément par UrgenceServlet.
 *
 * GET  : list, nouvelleCons, editerCons
 * POST : ajouterCons, modifierCons, supprimerCons
 */

package servlet;

import controller.PatientService;
import controller.PersonnelService;
import controller.SoinService;
import model.ActeChirurgical;
import model.Consultation;
import util.CsvService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/soins")
public class SoinServlet extends HttpServlet {

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
            case "nouvelleCons" -> afficherFormConsultation(req, resp, null);
            case "editerCons"   -> afficherFormConsultation(req, resp, req.getParameter("id"));
            default             -> listerSoins(req, resp);
        }
    }

    private void listerSoins(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String numeroPatient    = req.getParameter("numeroPatient");
        String matriculeMedecin = req.getParameter("matriculeMedecin");
        String type             = req.getParameter("type");  // "consultation", "acte", ou null = tous
        String ordre            = req.getParameter("ordre");
        boolean croissant       = !"desc".equals(ordre);

        List<Consultation> consultations = "acte".equals(type)
                ? java.util.Collections.emptyList()
                : soinService.rechercherConsultations(numeroPatient, matriculeMedecin, croissant);

        List<ActeChirurgical> actes = "consultation".equals(type)
                ? java.util.Collections.emptyList()
                : soinService.rechercherActes(numeroPatient, matriculeMedecin, croissant);

        req.setAttribute("consultations",  consultations);
        req.setAttribute("actes",          actes);
        req.setAttribute("criterePatient", numeroPatient);
        req.setAttribute("critereMedecin", matriculeMedecin);
        req.setAttribute("critereType",    type);
        req.setAttribute("triOrdre",       ordre);
        forward(req, resp, "/WEB-INF/views/soins/liste.jsp");
    }

    private void afficherFormConsultation(HttpServletRequest req, HttpServletResponse resp, String id)
            throws ServletException, IOException {
        if (id != null) req.setAttribute("consultation", soinService.trouverConsultationParId(id));
        // Listes nécessaires pour remplir les menus déroulants du formulaire
        req.setAttribute("patients", patientService.listerTous());
        req.setAttribute("medecins", personnelService.listerMedecins());
        forward(req, resp, "/WEB-INF/views/soins/formulaire-consultation.jsp");
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
            case "ajouterCons"   -> ajouterConsultation(req, resp);
            case "modifierCons"  -> modifierConsultation(req, resp);
            case "supprimerCons" -> supprimerConsultation(req, resp);
            default -> resp.sendRedirect(req.getContextPath() + "/soins");
        }
    }

    private void ajouterConsultation(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            Consultation c = new Consultation(
                    req.getParameter("motif").trim(),
                    req.getParameter("numeroPatient"),
                    req.getParameter("matriculeMedecin")
            );
            c.setDiagnostic(str(req.getParameter("diagnostic")));
            c.setOrdonnance(str(req.getParameter("ordonnance")));
            soinService.ajouterConsultation(c);
            sauvegarder();
            message(req, "Consultation ajoutée.", "success");
        } catch (Exception e) {
            message(req, "Erreur : " + e.getMessage(), "danger");
        }
        resp.sendRedirect(req.getContextPath() + "/soins");
    }

    private void modifierConsultation(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            Consultation c = soinService.trouverConsultationParId(req.getParameter("id"));
            if (c == null) throw new IllegalStateException("Consultation introuvable.");
            c.setMotif(req.getParameter("motif").trim());
            c.setDiagnostic(str(req.getParameter("diagnostic")));
            c.setOrdonnance(str(req.getParameter("ordonnance")));
            c.setNumeroPatient(req.getParameter("numeroPatient"));
            c.setMatriculeMedecin(req.getParameter("matriculeMedecin"));
            soinService.modifierConsultation(c);
            sauvegarder();
            message(req, "Consultation mise à jour.", "success");
        } catch (Exception e) {
            message(req, "Erreur : " + e.getMessage(), "danger");
        }
        resp.sendRedirect(req.getContextPath() + "/soins");
    }

    private void supprimerConsultation(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        soinService.supprimerConsultation(req.getParameter("id"));
        sauvegarder();
        message(req, "Consultation supprimée.", "warning");
        resp.sendRedirect(req.getContextPath() + "/soins");
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
