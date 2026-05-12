/*
 * Facturable.java - Interface pour toute entité générant une facturation.
 * Implémentée uniquement par Patient dans ce projet.
 * Le montant est calculé directement depuis le patient (nombre de jours * tarif journalier).
 */

package model;

public interface Facturable {

    double calculerMontantTotal();

    String getNumeroSecuriteSociale();
    void setNumeroSecuriteSociale(String numero);

    boolean isPrisEnCharge();
    void setPrisEnCharge(boolean prise);
}
