/*
 * Fichier : Facturable.java
 * Projet : HospitApp - Gestion hospitalière
 *
 * Rôle : Interface pour toute entité qui peut générer une facturation.
 *        Implémentée uniquement par Patient dans ce projet.
 *
 * Interactions : Patient, StatistiqueService
 *
 * Dans une vraie application, on aurait une classe Facture séparée.
 * Ici, on simplifie : la facturation est directement calculée depuis le patient.
 */

package model;

public interface Facturable {

    // Calcule le montant total à facturer pour ce patient.
    // Basé sur le nombre de jours d'hospitalisation * tarif journalier.
    double calculerMontantTotal();

    // Numéro de sécurité sociale — identifiant pour la prise en charge
    String getNumeroSecuriteSociale();

    void setNumeroSecuriteSociale(String numero);

    // Indique si les soins sont pris en charge (mutuelle, CMU, etc.)
    boolean estPrisEnCharge();

    void setPrisEnCharge(boolean prise);
}