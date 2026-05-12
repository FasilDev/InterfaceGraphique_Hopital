/*
 * Fichier : AppListener.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Écoute le démarrage et l'arrêt du serveur Tomcat.
 *        Au démarrage : configure le chemin CSV et charge les données en mémoire.
 *        À l'arrêt    : sauvegarde toutes les données dans les fichiers CSV.
 *
 * Interactions : CsvService, DonneesTest, PatientService, PersonnelService, SoinService
 *
 * @WebListener : Tomcat détecte cette annotation automatiquement et appelle
 *   contextInitialized() quand l'application démarre,
 *   contextDestroyed() quand Tomcat s'arrête ou que le WAR est redéployé.
 *
 * Chemin des CSV :
 *   On utilise catalina.home (répertoire Tomcat) si disponible, sinon user.home.
 *   Les données sont stockées hors du WAR pour survivre aux redéploiements.
 *   Sous Windows : C:/Users/username/hospitapp-data/
 *   Sous Linux/Mac : ~/hospitapp-data/
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
        // Choix du répertoire de stockage des CSV
        String base = System.getProperty("catalina.home");
        if (base == null) {
            // En dehors de Tomcat (tests dans l'IDE), on utilise le répertoire de l'utilisateur
            base = System.getProperty("user.home");
        }
        String cheminDonnees = base + File.separator + "hospitapp-data" + File.separator;
        CsvService.setDossierCsv(cheminDonnees);

        PatientService    ps   = PatientService.getInstance();
        PersonnelService  pers = PersonnelService.getInstance();
        SoinService       ss   = SoinService.getInstance();

        // Charge les données depuis les CSV
        CsvService.chargerTout(ps, pers, ss);

        // Si les CSV étaient vides (premier lancement), on charge les données de démonstration
        if (ps.getNombre() == 0) {
            System.out.println("[HospitApp] Aucune donnée trouvée — chargement des données de démonstration.");
            DonneesTest.charger(ps, pers, ss);
        }

        System.out.println("[HospitApp] Application démarrée — "
                + ps.getNombre() + " patient(s), "
                + pers.getNombreMedecins() + " médecin(s), "
                + pers.getNombreInfirmiers() + " infirmier(s).");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Sauvegarde de toutes les données avant l'arrêt
        CsvService.sauvegarderTout(
                PatientService.getInstance(),
                PersonnelService.getInstance(),
                SoinService.getInstance()
        );
        System.out.println("[HospitApp] Application arrêtée — données sauvegardées.");
    }
}
