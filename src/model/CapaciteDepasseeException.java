/*
 * Fichier : CapaciteDepasseeException.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Exception levée quand on tente d'admettre un patient dans un service
 *        ou une salle qui a atteint sa capacité maximale de lits.
 *
 * Interactions : PatientService (la lève), PatientServlet (la capture et affiche un message)
 *
 * Pourquoi extends RuntimeException et pas Exception ?
 *   - Exception (checked) : le compilateur oblige chaque méthode appelante à déclarer
 *     "throws CapaciteDepasseeException" ou à mettre un try/catch. C'est verbeux.
 *   - RuntimeException (unchecked) : on peut laisser l'exception remonter jusqu'au servlet
 *     sans polluer toutes les signatures de méthodes intermédiaires.
 *   Pour une appli web où les erreurs remontent naturellement jusqu'au contrôleur,
 *   RuntimeException est le choix le plus pratique.
 *
 * On conserve les infos utiles (nomService, capaciteMax) en champs pour que le servlet
 * puisse afficher un message précis à l'utilisateur, plutôt qu'un message générique.
 */

package model;

public class CapaciteDepasseeException extends RuntimeException {

    // Le service ou la salle concernée (ex : "Réanimation", "Bloc A")
    private final String nomService;

    // Nombre maximum de lits dans ce service
    private final int capaciteMax;

    /**
     * @param nomService  Nom du service ou de la salle surchargée
     * @param capaciteMax Capacité maximale qui a été atteinte
     */
    public CapaciteDepasseeException(String nomService, int capaciteMax) {
        // super() appelle le constructeur de RuntimeException avec le message d'erreur
        super("Capacité maximale atteinte pour le service \"" + nomService
                + "\" (" + capaciteMax + " lit(s) au maximum).");
        this.nomService  = nomService;
        this.capaciteMax = capaciteMax;
    }

    public String getNomService()  { return nomService;  }
    public int    getCapaciteMax() { return capaciteMax; }
}
