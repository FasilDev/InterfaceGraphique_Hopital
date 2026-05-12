/*
 * EntiteIntrouvableException.java - Exception levée quand une entité est introuvable par son id.
 * Utilisée dans les services, capturée dans les servlets pour afficher un message d'erreur.
 */

package model;

public class EntiteIntrouvableException extends RuntimeException {

    private final String typeEntite;
    private final String idRecherche;

    public EntiteIntrouvableException(String typeEntite, String idRecherche) {
        super("Aucun(e) " + typeEntite + " trouvé(e) avec l'identifiant : \"" + idRecherche + "\".");
        this.typeEntite  = typeEntite;
        this.idRecherche = idRecherche;
    }

    public String getTypeEntite()  { return typeEntite;  }
    public String getIdRecherche() { return idRecherche; }
}
