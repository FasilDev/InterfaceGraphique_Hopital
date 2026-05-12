/*
 * Fichier : DonneeInvalideException.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Exception levée quand une valeur saisie ne respecte pas les règles métier :
 *        champ obligatoire vide, format incorrect, valeur hors limites, objet null, etc.
 *
 * Interactions : PatientService, PersonnelService, SoinService (la lèvent),
 *                tous les Servlets (la capturent pour renvoyer un message d'erreur à la JSP)
 *
 * Deux constructeurs disponibles :
 *   - DonneeInvalideException(message) : message libre, utilisé quand l'erreur
 *     ne porte pas sur un champ précis (ex : "L'objet patient ne peut pas être null.")
 *   - DonneeInvalideException(champ, raison) : identifie le champ problématique et la raison,
 *     utile pour mettre en évidence le champ dans le formulaire HTML côté JSP.
 */

package model;

public class DonneeInvalideException extends RuntimeException {

    // Nom du champ concerné (ex : "nom", "chambre", "niveauPriorite").
    // null si l'erreur ne porte pas sur un champ spécifique.
    private final String champ;

    /**
     * Constructeur pour une erreur générale, sans champ précis.
     *
     * @param message Description de l'erreur
     */
    public DonneeInvalideException(String message) {
        super(message);
        this.champ = null;
    }

    /**
     * Constructeur ciblant un champ précis du formulaire.
     * Le message généré : Champ "nom" invalide : ne peut pas être vide.
     *
     * @param champ  Nom du champ problématique
     * @param raison Explication courte du problème
     */
    public DonneeInvalideException(String champ, String raison) {
        super("Champ \"" + champ + "\" invalide : " + raison);
        this.champ = champ;
    }

    // Retourne le nom du champ concerné, ou null si l'erreur est générale
    public String getChamp() { return champ; }

    // Indique si l'erreur est associée à un champ précis du formulaire
    public boolean porteSurUnChamp() { return champ != null; }
}
