/*
 * DonneeInvalideException.java - Exception levée pour une valeur invalide (champ vide, format incorrect...).
 * Deux constructeurs : un pour une erreur générale, un pour cibler un champ précis du formulaire.
 */

package model;

public class DonneeInvalideException extends RuntimeException {

    // null si l'erreur ne porte pas sur un champ spécifique
    private final String champ;

    public DonneeInvalideException(String message) {
        super(message);
        this.champ = null;
    }

    // Exemple de message généré : Champ "chambre" invalide : le numéro est obligatoire.
    public DonneeInvalideException(String champ, String raison) {
        super("Champ \"" + champ + "\" invalide : " + raison);
        this.champ = champ;
    }

    public String getChamp() { return champ; }

    public boolean porteSurUnChamp() { return champ != null; }
}
