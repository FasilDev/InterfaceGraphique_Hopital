/*
 * DonneesTest.java - Données de démonstration pour le premier lancement.
 * Injecte un jeu de données cohérent dans les services quand les CSV sont absents.
 * Les IDs sont fixes (non aléatoires) pour que les CSV générés soient reproductibles.
 */

package util;

import controller.PatientService;
import controller.PersonnelService;
import controller.SoinService;
import model.*;

import java.time.LocalDate;

public class DonneesTest {

    private static final String ID_PAT_1  = "00000000-0000-0000-0000-000000000001";
    private static final String ID_PAT_2  = "00000000-0000-0000-0000-000000000002";
    private static final String ID_PAT_3  = "00000000-0000-0000-0000-000000000003";
    private static final String ID_PAT_4  = "00000000-0000-0000-0000-000000000004";
    private static final String ID_PAT_5  = "00000000-0000-0000-0000-000000000005";

    private static final String ID_MED_1  = "00000000-0000-0000-0000-000000000011";
    private static final String ID_MED_2  = "00000000-0000-0000-0000-000000000012";
    private static final String ID_MED_3  = "00000000-0000-0000-0000-000000000013";
    private static final String ID_MED_4  = "00000000-0000-0000-0000-000000000014";

    private static final String ID_INF_1  = "00000000-0000-0000-0000-000000000021";
    private static final String ID_INF_2  = "00000000-0000-0000-0000-000000000022";
    private static final String ID_INF_3  = "00000000-0000-0000-0000-000000000023";

    private static final String ID_CONS_1 = "00000000-0000-0000-0000-000000000031";
    private static final String ID_CONS_2 = "00000000-0000-0000-0000-000000000032";
    private static final String ID_CONS_3 = "00000000-0000-0000-0000-000000000033";

    private static final String ID_ACTE_1 = "00000000-0000-0000-0000-000000000041";
    private static final String ID_ACTE_2 = "00000000-0000-0000-0000-000000000042";

    private DonneesTest() {}

    public static void charger(PatientService ps, PersonnelService pers, SoinService ss) {
        chargerMedecins(pers);
        chargerInfirmiers(pers);
        chargerPatients(ps);
        chargerConsultations(ss);
        chargerActesChirurgicaux(ss);
        CsvService.sauvegarderTout(ps, pers, ss);
    }

    // -------------------------------------------------------------------
    // Personnel
    // -------------------------------------------------------------------

    private static void chargerMedecins(PersonnelService pers) {
        Medecin moreau = new Medecin(ID_MED_1, "Moreau", "Thomas",
                LocalDate.of(1975, 3, 12), "MED-001", "Cardiologie", "ORD-12345678");
        moreau.setTelephone("06 12 34 56 78");
        moreau.setEmail("t.moreau@hospitapp.fr");
        pers.ajouterMedecin(moreau);

        Medecin fontaine = new Medecin(ID_MED_2, "Fontaine", "Julie",
                LocalDate.of(1980, 7, 20), "MED-002", "Chirurgie", "ORD-23456789");
        fontaine.setTelephone("06 23 45 67 89");
        fontaine.setEmail("j.fontaine@hospitapp.fr");
        pers.ajouterMedecin(fontaine);

        Medecin durand = new Medecin(ID_MED_3, "Durand", "Marc",
                LocalDate.of(1968, 11, 5), "MED-003", "Médecine générale", "ORD-34567890");
        durand.setTelephone("06 34 56 78 90");
        durand.setEmail("m.durand@hospitapp.fr");
        pers.ajouterMedecin(durand);

        Medecin lambert = new Medecin(ID_MED_4, "Lambert", "Sophie",
                LocalDate.of(1983, 2, 28), "MED-004", "Neurologie", "ORD-45678901");
        lambert.setTelephone("06 45 67 89 01");
        lambert.setEmail("s.lambert@hospitapp.fr");
        pers.ajouterMedecin(lambert);
    }

    private static void chargerInfirmiers(PersonnelService pers) {
        Infirmier blanc = new Infirmier(ID_INF_1, "Blanc", "Amélie",
                LocalDate.of(1990, 6, 15), "INF-001", "Cardiologie", "IDE");
        blanc.setTelephone("06 56 78 90 12");
        pers.ajouterInfirmier(blanc);

        Infirmier garnier = new Infirmier(ID_INF_2, "Garnier", "Nicolas",
                LocalDate.of(1987, 9, 3), "INF-002", "Urgences", "IADE");
        garnier.setGardeNuit(true);
        pers.ajouterInfirmier(garnier);

        Infirmier petit = new Infirmier(ID_INF_3, "Petit", "Claire",
                LocalDate.of(1993, 1, 22), "INF-003", "Chirurgie", "IBODE");
        pers.ajouterInfirmier(petit);
    }

    // -------------------------------------------------------------------
    // Patients
    // -------------------------------------------------------------------

    private static void chargerPatients(PatientService ps) {
        Patient dupont = new Patient(ID_PAT_1, "Dupont", "Marie",
                LocalDate.of(1979, 4, 10), "P-2024-001");
        dupont.setGroupeSanguin("A+");
        dupont.setNumeroSecuriteSociale("279047512345678");
        dupont.setPrisEnCharge(true);
        dupont.ajouterAntecedent("Hypertension artérielle");
        dupont.ajouterAntecedent("Diabète type 2");
        dupont.setTelephone("06 11 22 33 44");
        ps.ajouter(dupont);
        ps.admettre(ID_PAT_1, "201");

        Patient martin = new Patient(ID_PAT_2, "Martin", "Jean",
                LocalDate.of(1962, 8, 23), "P-2024-002");
        martin.setGroupeSanguin("B-");
        martin.setNumeroSecuriteSociale("162087534567890");
        martin.ajouterAntecedent("Allergie pénicilline");
        martin.setTelephone("07 22 33 44 55");
        ps.ajouter(martin);

        Patient bernard = new Patient(ID_PAT_3, "Bernard", "Sophie",
                LocalDate.of(1996, 12, 5), "P-2024-003");
        bernard.setGroupeSanguin("O+");
        bernard.setTelephone("06 33 44 55 66");
        ps.ajouter(bernard);
        ps.admettre(ID_PAT_3, "305");

        Patient lefevre = new Patient(ID_PAT_4, "Lefèvre", "Pierre",
                LocalDate.of(1951, 3, 17), "P-2024-004");
        lefevre.setGroupeSanguin("AB+");
        lefevre.ajouterAntecedent("Insuffisance cardiaque congestive");
        lefevre.ajouterAntecedent("Cholestérol élevé");
        lefevre.ajouterAntecedent("Arthrose lombaire");
        ps.ajouter(lefevre);

        Patient rousseau = new Patient(ID_PAT_5, "Rousseau", "Camille",
                LocalDate.of(1990, 9, 8), "P-2024-005");
        rousseau.setGroupeSanguin("A-");
        rousseau.setTelephone("06 55 66 77 88");
        ps.ajouter(rousseau);
        ps.admettre(ID_PAT_5, "102");
    }

    // -------------------------------------------------------------------
    // Soins
    // -------------------------------------------------------------------

    private static void chargerConsultations(SoinService ss) {
        ss.ajouterConsultation(new Consultation(ID_CONS_1,
                "Douleurs thoraciques", "Angine de poitrine stable",
                "Trinitrine sublinguale 0.5mg au besoin + aspirine 75mg/j",
                LocalDate.of(2024, 10, 10), "P-2024-001", "MED-001", 25.0));

        ss.ajouterConsultation(new Consultation(ID_CONS_2,
                "Fatigue chronique et vertiges", "Anémie ferriprive",
                "Sulfate de fer 80mg/j pendant 3 mois - Contrôle NFS dans 6 semaines",
                LocalDate.of(2024, 10, 14), "P-2024-002", "MED-003", 25.0));

        ss.ajouterConsultation(new Consultation(ID_CONS_3,
                "Céphalées récurrentes", "Migraines sans aura - IRM recommandée",
                null, LocalDate.of(2024, 10, 15), "P-2024-004", "MED-004", 25.0));
    }

    private static void chargerActesChirurgicaux(SoinService ss) {
        // Actes non réalisés -> entrent automatiquement dans la file d'urgences
        ss.ajouterActeChirurgical(new ActeChirurgical(ID_ACTE_1,
                "Appendicectomie", 2,
                "Douleurs abdominales aiguës en fosse iliaque droite - appendicite probable",
                "Bloc 2", false, LocalDate.now(), "P-2024-003", "MED-002", 500.0));

        ss.ajouterActeChirurgical(new ActeChirurgical(ID_ACTE_2,
                "Réduction fracture du poignet", 3,
                "Fracture du radius distal suite à une chute - déplacement modéré",
                "Salle B", false, LocalDate.now(), "P-2024-005", "MED-002", 500.0));
    }
}
