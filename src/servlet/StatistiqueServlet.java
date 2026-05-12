/*
 * StatistiqueServlet.java - Contrôleur web pour le tableau de bord statistiques (lecture seule).
 * Collecte tous les indicateurs via StatistiqueService et les transmet à la JSP.
 * Capacité totale de lits fixée à 50 pour le calcul du taux d'occupation.
 */

package servlet;

import controller.StatistiqueService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.DoubleSummaryStatistics;

@WebServlet("/statistiques")
public class StatistiqueServlet extends HttpServlet {

    private static final int CAPACITE_LITS = 50;

    private StatistiqueService statistiqueService;

    @Override
    public void init() throws ServletException {
        statistiqueService = StatistiqueService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("nbPatients",             statistiqueService.getNombreTotalPatients());
        req.setAttribute("nbPatientsAdmis",        statistiqueService.getNombrePatientsAdmis());
        req.setAttribute("nbPatientsNonAdmis",     statistiqueService.getNombrePatientsNonAdmis());
        req.setAttribute("tauxOccupation",         statistiqueService.getTauxOccupation(CAPACITE_LITS));
        req.setAttribute("capaciteLits",           CAPACITE_LITS);
        req.setAttribute("ageMoyenAdmis",          statistiqueService.getAgeMoyenPatientsAdmis());

        req.setAttribute("nbMedecins",             statistiqueService.getNombreMedecins());
        req.setAttribute("nbInfirmiers",           statistiqueService.getNombreInfirmiers());
        req.setAttribute("repartitionSpecialites", statistiqueService.getRepartitionParSpecialite());

        req.setAttribute("nbConsultations",        statistiqueService.getNombreConsultations());
        req.setAttribute("nbActes",                statistiqueService.getNombreActesChirurgicaux());
        req.setAttribute("nbActesRealises",        statistiqueService.getNombreActesRealises());
        req.setAttribute("nbUrgencesEnAttente",    statistiqueService.getNombreUrgencesEnAttente());
        req.setAttribute("nbConsultationsAujourd", statistiqueService.getNombreConsultationsAujourdhui());

        req.setAttribute("chiffreAffaires",        statistiqueService.getChiffreAffairesTotal());

        // DoubleSummaryStatistics n'a pas de getters compatibles EL, on expose les valeurs séparément
        DoubleSummaryStatistics stats = statistiqueService.getStatistiquesFinancieresSoins();
        req.setAttribute("coutMoyenConsultation", stats.getCount() > 0 ? stats.getAverage() : 0.0);
        req.setAttribute("coutMinConsultation",   stats.getCount() > 0 ? stats.getMin()     : 0.0);
        req.setAttribute("coutMaxConsultation",   stats.getCount() > 0 ? stats.getMax()     : 0.0);

        req.getRequestDispatcher("/WEB-INF/views/statistiques/tableau-bord.jsp").forward(req, resp);
    }
}
