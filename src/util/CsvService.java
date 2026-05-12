/*
 * CsvService.java - Gère toute la persistance des données via des fichiers CSV.
 * Un fichier par type : patients.csv, medecins.csv, infirmiers.csv, consultations.csv, actes_chirurgicaux.csv.
 *
 * Format : séparateur ";" entre les colonnes, "|" pour les listes dans une cellule, encodage UTF-8.
 * Limitation : si un champ contient ";" ou "|", le parsing sera incorrect (acceptable en projet étudiant).
 * Chemin par défaut : "resources/" relatif au répertoire de travail.
 * En déploiement Tomcat, AppListener appelle setDossierCsv() avec le bon chemin absolu.
 */

package util;

import controller.PatientService;
import controller.PersonnelService;
import controller.SoinService;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CsvService {

    private static final String SEP             = ";";
    private static final String SEP_LISTE       = "\\|"; // pour split()
    private static final String SEP_LISTE_WRITE = "|";   // pour join()

    private static String dossierCsv = "resources" + File.separator;

    private static final String F_PATIENTS      = "patients.csv";
    private static final String F_MEDECINS      = "medecins.csv";
    private static final String F_INFIRMIERS    = "infirmiers.csv";
    private static final String F_CONSULTATIONS = "consultations.csv";
    private static final String F_ACTES         = "actes_chirurgicaux.csv";

    private static final String HEADER_PATIENTS =
            "id;nom;prenom;dateNaissance;telephone;email;numeroPatient;groupeSanguin;" +
            "antecedents;dateAdmission;dateSortie;admis;notes;chambre;numeroSecuriteSociale;prisEnCharge";

    private static final String HEADER_MEDECINS =
            "id;nom;prenom;dateNaissance;telephone;email;matricule;specialite;numeroOrdre;disponible;dateEmbauche";

    private static final String HEADER_INFIRMIERS =
            "id;nom;prenom;dateNaissance;telephone;email;matricule;service;qualification;gardeNuit;disponible;dateEmbauche";

    private static final String HEADER_CONSULTATIONS =
            "id;motif;diagnostic;ordonnance;dateSoin;numeroPatient;matriculeMedecin;cout";

    private static final String HEADER_ACTES =
            "id;typeActe;niveauPriorite;descriptionUrgence;salle;realise;dateSoin;numeroPatient;matriculeMedecin;cout";

    private CsvService() {}

    // Permet à AppListener de pointer vers un dossier persistant hors du WAR
    public static void setDossierCsv(String chemin) {
        dossierCsv = chemin.endsWith(File.separator) ? chemin : chemin + File.separator;
    }

    // -------------------------------------------------------------------
    // Utilitaires privés
    // -------------------------------------------------------------------

    private static void creerDossierSiNecessaire() {
        File dossier = new File(dossierCsv);
        if (!dossier.exists()) dossier.mkdirs();
    }

    private static String str(Object o)       { return o == null ? "" : o.toString(); }
    private static String nullSiVide(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }

    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.trim()); } catch (Exception e) { return null; }
    }

    private static double parseDouble(String s, double defaut) {
        if (s == null || s.isBlank()) return defaut;
        try { return Double.parseDouble(s.trim()); } catch (NumberFormatException e) { return defaut; }
    }

    private static int parseInt(String s, int defaut) {
        if (s == null || s.isBlank()) return defaut;
        try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return defaut; }
    }

    // List<String> -> "elem1|elem2|elem3"
    private static String joinListe(List<String> liste) {
        if (liste == null || liste.isEmpty()) return "";
        return liste.stream()
                .map(s -> s.replace(SEP_LISTE_WRITE, " "))
                .collect(Collectors.joining(SEP_LISTE_WRITE));
    }

    // "elem1|elem2|elem3" -> List<String>
    private static List<String> splitListe(String s) {
        if (s == null || s.isBlank()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(s.split(SEP_LISTE)));
    }

    // -------------------------------------------------------------------
    // Patients
    // -------------------------------------------------------------------

    public static void sauvegarderPatients(List<Patient> patients) {
        creerDossierSiNecessaire();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(dossierCsv + F_PATIENTS), StandardCharsets.UTF_8))) {

            bw.write(HEADER_PATIENTS);
            bw.newLine();
            for (Patient p : patients) {
                bw.write(String.join(SEP,
                        str(p.getId()), str(p.getNom()), str(p.getPrenom()),
                        str(p.getDateNaissance()), str(p.getTelephone()), str(p.getEmail()),
                        str(p.getNumeroPatient()), str(p.getGroupeSanguin()),
                        joinListe(p.getAntecedents()),
                        str(p.getDateAdmission()), str(p.getDateSortie()), str(p.isAdmis()),
                        str(p.getNotes()), str(p.getChambre()),
                        str(p.getNumeroSecuriteSociale()), str(p.isPrisEnCharge())
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[CsvService] Erreur sauvegarde patients : " + e.getMessage());
        }
    }

    // Retourne une liste vide si le fichier n'existe pas ; ignore les lignes corrompues
    public static List<Patient> chargerPatients() {
        List<Patient> patients = new ArrayList<>();
        File fichier = new File(dossierCsv + F_PATIENTS);
        if (!fichier.exists()) return patients;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fichier), StandardCharsets.UTF_8))) {

            br.readLine(); // ignore l'en-tête
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.isBlank()) continue;
                // -1 dans split() conserve les champs vides en fin de ligne
                String[] c = ligne.split(SEP, -1);
                if (c.length < 16) { System.err.println("[CsvService] Ligne patient ignorée : " + ligne); continue; }
                try {
                    Patient p = new Patient(c[0], c[1], c[2], LocalDate.parse(c[3].trim()), c[6]);
                    p.setTelephone(nullSiVide(c[4]));
                    p.setEmail(nullSiVide(c[5]));
                    p.setGroupeSanguin(nullSiVide(c[7]));
                    p.setAntecedents(splitListe(c[8]));
                    p.setDateAdmission(parseDate(c[9]));
                    p.setDateSortie(parseDate(c[10]));
                    p.setAdmis(Boolean.parseBoolean(c[11]));
                    p.setNotes(nullSiVide(c[12]));
                    p.setChambre(nullSiVide(c[13]));
                    p.setNumeroSecuriteSociale(nullSiVide(c[14]));
                    p.setPrisEnCharge(Boolean.parseBoolean(c[15]));
                    patients.add(p);
                } catch (Exception e) {
                    System.err.println("[CsvService] Ligne patient ignorée (format invalide) : " + ligne);
                }
            }
        } catch (IOException e) {
            System.err.println("[CsvService] Erreur chargement patients : " + e.getMessage());
        }
        return patients;
    }

    // -------------------------------------------------------------------
    // Médecins
    // -------------------------------------------------------------------

    public static void sauvegarderMedecins(List<Medecin> medecins) {
        creerDossierSiNecessaire();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(dossierCsv + F_MEDECINS), StandardCharsets.UTF_8))) {

            bw.write(HEADER_MEDECINS);
            bw.newLine();
            for (Medecin m : medecins) {
                bw.write(String.join(SEP,
                        str(m.getId()), str(m.getNom()), str(m.getPrenom()),
                        str(m.getDateNaissance()), str(m.getTelephone()), str(m.getEmail()),
                        str(m.getMatricule()), str(m.getSpecialite()), str(m.getNumeroOrdre()),
                        str(m.isDisponible()), str(m.getDateEmbauche())
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[CsvService] Erreur sauvegarde médecins : " + e.getMessage());
        }
    }

    public static List<Medecin> chargerMedecins() {
        List<Medecin> medecins = new ArrayList<>();
        File fichier = new File(dossierCsv + F_MEDECINS);
        if (!fichier.exists()) return medecins;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fichier), StandardCharsets.UTF_8))) {

            br.readLine();
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.isBlank()) continue;
                String[] c = ligne.split(SEP, -1);
                if (c.length < 9) continue;
                try {
                    Medecin m = new Medecin(c[0], c[1], c[2], LocalDate.parse(c[3].trim()), c[6], c[7], c[8]);
                    m.setTelephone(nullSiVide(c[4]));
                    m.setEmail(nullSiVide(c[5]));
                    if (c.length > 9)  m.setDisponible(Boolean.parseBoolean(c[9]));
                    if (c.length > 10 && !c[10].isBlank()) m.setDateEmbauche(LocalDate.parse(c[10].trim()));
                    medecins.add(m);
                } catch (Exception e) {
                    System.err.println("[CsvService] Ligne médecin ignorée : " + ligne);
                }
            }
        } catch (IOException e) {
            System.err.println("[CsvService] Erreur chargement médecins : " + e.getMessage());
        }
        return medecins;
    }

    // -------------------------------------------------------------------
    // Infirmiers
    // -------------------------------------------------------------------

    public static void sauvegarderInfirmiers(List<Infirmier> infirmiers) {
        creerDossierSiNecessaire();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(dossierCsv + F_INFIRMIERS), StandardCharsets.UTF_8))) {

            bw.write(HEADER_INFIRMIERS);
            bw.newLine();
            for (Infirmier inf : infirmiers) {
                bw.write(String.join(SEP,
                        str(inf.getId()), str(inf.getNom()), str(inf.getPrenom()),
                        str(inf.getDateNaissance()), str(inf.getTelephone()), str(inf.getEmail()),
                        str(inf.getMatricule()), str(inf.getService()), str(inf.getQualification()),
                        str(inf.isGardeNuit()), str(inf.isDisponible()), str(inf.getDateEmbauche())
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[CsvService] Erreur sauvegarde infirmiers : " + e.getMessage());
        }
    }

    public static List<Infirmier> chargerInfirmiers() {
        List<Infirmier> infirmiers = new ArrayList<>();
        File fichier = new File(dossierCsv + F_INFIRMIERS);
        if (!fichier.exists()) return infirmiers;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fichier), StandardCharsets.UTF_8))) {

            br.readLine();
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.isBlank()) continue;
                String[] c = ligne.split(SEP, -1);
                if (c.length < 9) continue;
                try {
                    Infirmier inf = new Infirmier(c[0], c[1], c[2], LocalDate.parse(c[3].trim()), c[6], c[7], c[8]);
                    inf.setTelephone(nullSiVide(c[4]));
                    inf.setEmail(nullSiVide(c[5]));
                    if (c.length > 9)  inf.setGardeNuit(Boolean.parseBoolean(c[9]));
                    if (c.length > 10) inf.setDisponible(Boolean.parseBoolean(c[10]));
                    if (c.length > 11 && !c[11].isBlank()) inf.setDateEmbauche(LocalDate.parse(c[11].trim()));
                    infirmiers.add(inf);
                } catch (Exception e) {
                    System.err.println("[CsvService] Ligne infirmier ignorée : " + ligne);
                }
            }
        } catch (IOException e) {
            System.err.println("[CsvService] Erreur chargement infirmiers : " + e.getMessage());
        }
        return infirmiers;
    }

    // -------------------------------------------------------------------
    // Consultations
    // -------------------------------------------------------------------

    public static void sauvegarderConsultations(List<Consultation> consultations) {
        creerDossierSiNecessaire();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(dossierCsv + F_CONSULTATIONS), StandardCharsets.UTF_8))) {

            bw.write(HEADER_CONSULTATIONS);
            bw.newLine();
            for (Consultation c : consultations) {
                bw.write(String.join(SEP,
                        str(c.getId()), str(c.getMotif()), str(c.getDiagnostic()),
                        str(c.getOrdonnance()), str(c.getDateSoin()),
                        str(c.getNumeroPatient()), str(c.getMatriculeMedecin()), str(c.getCout())
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[CsvService] Erreur sauvegarde consultations : " + e.getMessage());
        }
    }

    public static List<Consultation> chargerConsultations() {
        List<Consultation> consultations = new ArrayList<>();
        File fichier = new File(dossierCsv + F_CONSULTATIONS);
        if (!fichier.exists()) return consultations;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fichier), StandardCharsets.UTF_8))) {

            br.readLine();
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.isBlank()) continue;
                String[] c = ligne.split(SEP, -1);
                if (c.length < 8) continue;
                try {
                    consultations.add(new Consultation(
                            c[0], c[1], nullSiVide(c[2]), nullSiVide(c[3]),
                            parseDate(c[4]), c[5], c[6], parseDouble(c[7], 25.0)
                    ));
                } catch (Exception e) {
                    System.err.println("[CsvService] Ligne consultation ignorée : " + ligne);
                }
            }
        } catch (IOException e) {
            System.err.println("[CsvService] Erreur chargement consultations : " + e.getMessage());
        }
        return consultations;
    }

    // -------------------------------------------------------------------
    // Actes chirurgicaux
    // -------------------------------------------------------------------

    public static void sauvegarderActes(List<ActeChirurgical> actes) {
        creerDossierSiNecessaire();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(dossierCsv + F_ACTES), StandardCharsets.UTF_8))) {

            bw.write(HEADER_ACTES);
            bw.newLine();
            for (ActeChirurgical a : actes) {
                bw.write(String.join(SEP,
                        str(a.getId()), str(a.getTypeActe()), str(a.getNiveauPriorite()),
                        str(a.getDescriptionUrgence()), str(a.getSalle()), str(a.isRealise()),
                        str(a.getDateSoin()), str(a.getNumeroPatient()),
                        str(a.getMatriculeMedecin()), str(a.getCout())
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[CsvService] Erreur sauvegarde actes chirurgicaux : " + e.getMessage());
        }
    }

    public static List<ActeChirurgical> chargerActes() {
        List<ActeChirurgical> actes = new ArrayList<>();
        File fichier = new File(dossierCsv + F_ACTES);
        if (!fichier.exists()) return actes;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fichier), StandardCharsets.UTF_8))) {

            br.readLine();
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.isBlank()) continue;
                String[] c = ligne.split(SEP, -1);
                if (c.length < 10) continue;
                try {
                    actes.add(new ActeChirurgical(
                            c[0], c[1], parseInt(c[2], 3), nullSiVide(c[3]), nullSiVide(c[4]),
                            Boolean.parseBoolean(c[5]), parseDate(c[6]),
                            c[7], c[8], parseDouble(c[9], 500.0)
                    ));
                } catch (Exception e) {
                    System.err.println("[CsvService] Ligne acte chirurgical ignorée : " + ligne);
                }
            }
        } catch (IOException e) {
            System.err.println("[CsvService] Erreur chargement actes chirurgicaux : " + e.getMessage());
        }
        return actes;
    }

    // -------------------------------------------------------------------
    // Méthodes globales
    // -------------------------------------------------------------------

    // Sauvegarde tout en une seule fois (appelée par AppListener et les servlets)
    public static void sauvegarderTout(PatientService ps, PersonnelService pers, SoinService ss) {
        sauvegarderPatients(new ArrayList<>(ps.listerTous()));
        sauvegarderMedecins(new ArrayList<>(pers.listerMedecins()));
        sauvegarderInfirmiers(new ArrayList<>(pers.listerInfirmiers()));
        sauvegarderConsultations(new ArrayList<>(ss.listerConsultations()));
        sauvegarderActes(new ArrayList<>(ss.listerActes()));
    }

    // Charge tout au démarrage du serveur ; liste vide si le CSV est absent
    public static void chargerTout(PatientService ps, PersonnelService pers, SoinService ss) {
        chargerPatients().forEach(ps::ajouter);
        chargerMedecins().forEach(pers::ajouterMedecin);
        chargerInfirmiers().forEach(pers::ajouterInfirmier);
        chargerConsultations().forEach(ss::ajouterConsultation);
        // ajouterActeChirurgical() ajoute automatiquement les actes non réalisés à la file d'urgences
        chargerActes().forEach(ss::ajouterActeChirurgical);
    }
}
