/*
 * Fichier : StatistiqueServlet.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Contrôleur web pour le tableau de bord statistiques.
 *        Collecte tous les indicateurs via StatistiqueService et les transmet à la JSP.
 *        Pas de POST ici — les statistiques sont en lecture seule.
 *
 * Interactions : StatistiqueService, JSP : statistiques/tableau-bord.jsp
 *
 * Capacité totale de lits : fixée à 50 pour le calcul du taux d'occupation.
 * À rendre configurable (via web.xml ou base de données) dans une version ultérieure.
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

        // Chaque attribut devient accessible dans la JSP via ${nomAttribut}
        req.setAttribute("nbPatients",              statistiqueService.getNombreTotalPatients());
        req.setAttribute("nbPatientsAdmis",         statistiqueService.getNombrePatientsAdmis());
        req.setAttribute("nbPatientsNonAdmis",      statistiqueService.getNombrePatientsNonAdmis());
        req.setAttribute("tauxOccupation",          statistiqueService.getTauxOccupation(CAPACITE_LITS));
        req.setAttribute("capaciteLits",            CAPACITE_LITS);
        req.setAttribute("ageMoyenAdmis",           statistiqueService.getAgeMoyenPatientsAdmis());

        req.setAttribute("nbMedecins",              statistiqueService.getNombreMedecins());
        req.setAttribute("nbInfirmiers",            statistiqueService.getNombreInfirmiers());
        req.setAttribute("repartitionSpecialites",  statistiqueService.getRepartitionParSpecialite());

        req.setAttribute("nbConsultations",         statistiqueService.getNombreConsultations());
        req.setAttribute("nbActes",                 statistiqueService.getNombreActesChirurgicaux());
        req.setAttribute("nbActesRealises",         statistiqueService.getNombreActesRealises());
        req.setAttribute("nbUrgencesEnAttente",     statistiqueService.getNombreUrgencesEnAttente());
        req.setAttribute("nbConsultationsAujourd",  statistiqueService.getNombreConsultationsAujourdhui());

        req.setAttribute("chiffreAffaires",         statistiqueService.getChiffreAffairesTotal());

        // DoubleSummaryStatistics : résumé financier des consultations en un seul passage stream.
        // On expose chaque valeur séparément car les objets Java ne sont pas directement
        // utilisables dans les expressions EL des JSP (pas de getters standards sur ce type).
        DoubleSummaryStatistics stats = statistiqueService.getStatistiquesFinancieresSoins();
        req.setAttribute("coutMoyenConsultation", stats.getCount() > 0 ? stats.getAverage() : 0.0);
        req.setAttribute("coutMinConsultation",   stats.getCount() > 0 ? stats.getMin()     : 0.0);
        req.setAttribute("coutMaxConsultation",   stats.getCount() > 0 ? stats.getMax()     : 0.0);

        req.getRequestDispatcher("/WEB-INF/views/statistiques/tableau-bord.jsp")
                .forward(req, resp);
    }
}
