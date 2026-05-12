/*
 * AppListener.java - Écoute le démarrage et l'arrêt de Tomcat.
 * Au démarrage : configure le chemin CSV et charge les données en mémoire.
 * A l'arrêt    : sauvegarde toutes les données dans les fichiers CSV.
 *
 * Les CSV sont stockés hors du WAR (dans hospitapp-data/) pour survivre aux redéploiements.
 * Chemin utilisé : catalina.home (Tomcat) ou user.home (IDE hors Tomcat).
 */

package controller;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import util.CsvService;
import util.DonneesTest;

import java.io.File;

@WebListener
public class AppListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String base = System.getProperty("catalina.home");
        if (base == null) {
            // Hors Tomcat (tests IDE), on pointe vers le répertoire de l'utilisateur
            base = System.getProperty("user.home");
        }
        CsvService.setDossierCsv(base + File.separator + "hospitapp-data" + File.separator);

        PatientService   ps   = PatientService.getInstance();
        PersonnelService pers = PersonnelService.getInstance();
        SoinService      ss   = SoinService.getInstance();

        CsvService.chargerTout(ps, pers, ss);

        if (ps.getNombre() == 0) {
            System.out.println("[HospitApp] Aucune donnée trouvée — chargement des données de démonstration.");
            DonneesTest.charger(ps, pers, ss);
        }

        System.out.println("[HospitApp] Démarré — "
                + ps.getNombre() + " patient(s), "
                + pers.getNombreMedecins() + " médecin(s), "
                + pers.getNombreInfirmiers() + " infirmier(s).");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        CsvService.sauvegarderTout(
                PatientService.getInstance(),
                PersonnelService.getInstance(),
                SoinService.getInstance()
        );
        System.out.println("[HospitApp] Arrêté — données sauvegardées.");
    }
}
