/*
 * Fichier : EntiteIntrouvableException.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Exception levée quand on cherche une entité par son id ou son numéro
 *        et qu'elle n'existe pas dans le registre.
 *
 * Interactions : PatientService, PersonnelService, SoinService (la lèvent),
 *                PatientServlet, PersonnelServlet, SoinServlet (la capturent)
 *
 * Exemples de cas d'usage :
 *   - patient introuvable lors d'une admission
 *   - médecin introuvable lors de l'ajout d'une consultation
 *   - acte chirurgical introuvable lors d'une mise à jour
 *
 * On passe le type de l'entité ("Patient", "Médecin"...) et l'identifiant recherché
 * pour construire un message d'erreur lisible sans avoir à formatter la chaîne côté servlet.
 */

package model;

public class EntiteIntrouvableException extends RuntimeException {

    // Type de l'entité recherchée (ex : "Patient", "Médecin", "Consultation")
    private final String typeEntite;

    // Identifiant utilisé pour la recherche (UUID ou numéro métier)
    private final String idRecherche;

    /**
     * @param typeEntite  Nom du type d'entité (ex : "Patient")
     * @param idRecherche Identifiant utilisé lors de la recherche
     */
    public EntiteIntrouvableException(String typeEntite, String idRecherche) {
        super("Aucun(e) " + typeEntite + " trouvé(e) avec l'identifiant : \"" + idRecherche + "\".");
        this.typeEntite  = typeEntite;
        this.idRecherche = idRecherche;
    }

    public String getTypeEntite()  { return typeEntite;  }
    public String getIdRecherche() { return idRecherche; }
}
