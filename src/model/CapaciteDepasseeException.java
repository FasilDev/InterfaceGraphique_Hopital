/*
 * CapaciteDepasseeException.java - Exception levée quand un service a atteint sa capacité maximale.
 * extends RuntimeException (unchecked) : remonte naturellement jusqu'au servlet sans polluer les signatures.
 */

package model;

public class CapaciteDepasseeException extends RuntimeException {

    private final String nomService;
    private final int capaciteMax;

    public CapaciteDepasseeException(String nomService, int capaciteMax) {
        super("Capacité maximale atteinte pour le service \"" + nomService
                + "\" (" + capaciteMax + " lit(s) au maximum).");
        this.nomService  = nomService;
        this.capaciteMax = capaciteMax;
    }

    public String getNomService()  { return nomService;  }
    public int    getCapaciteMax() { return capaciteMax; }
}
