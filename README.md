# HospitApp — Application Web de Gestion Hospitalière

## Groupe

| Prénom Nom | Rôle principal |
|---|---|
| John DINH | Modèle métier & Services |
| Fasil MOUGAMADOU | Servlets & Architecture MVC |
| Akash ROUBERT | Interface JSP & CSS |

---

## Description

**HospitApp** est une application web Java de gestion hospitalière, développée dans le cadre du projet POO Avancée en Bachelor 3 Informatique.

L'application couvre la gestion des **patients** (admission, dossier médical, antécédents, sortie), du **personnel médical** (médecins avec spécialités, infirmiers, plannings, disponibilité), des **soins** (consultations, prescriptions, actes chirurgicaux), et d'une **file d'attente des urgences** triée par priorité médicale (PriorityQueue). Les données sont persistées en CSV. L'interface est responsive grâce à Bootstrap 5.

---

## Technologie choisie : Option B — Servlet/JSP (Tomcat 10.1+)

Nous avons choisi Servlet/JSP pour avoir un MVC côté serveur clair et explicite :
- Les **Servlets** reçoivent les requêtes HTTP, appellent les services et transmettent les données aux vues.
- Les **JSP** affichent les données via JSTL — elles ne contiennent aucune logique métier.
- Le **modèle Java** est totalement indépendant du web.

Ce choix offre une compréhension concrète du fonctionnement des frameworks MVC comme Spring.

---

## Prérequis

| Outil | Version minimale |
|---|---|
| Java JDK | 17+ |
| Apache Maven | 3.8+ |
| Apache Tomcat | **10.1+** (pas 9.x ni 10.0) |
| Navigateur | Tout navigateur moderne |

> Tomcat 10+ utilise `jakarta.*` (Jakarta EE 10). Tomcat 9 utilisait `javax.*` (Java EE). Les deux sont incompatibles — vérifier la version avant de déployer.

---

## Compilation et déploiement

### 1. Cloner le projet

```bash
git clone <url-du-repo>
cd HospitApp
```

### 2. Compiler et packager

```bash
mvn clean package
```

Génère `target/HospitApp.war`.

### 3a. Déploiement manuel sur Tomcat

```bash
# Copier le WAR dans Tomcat
cp target/HospitApp.war /chemin/vers/apache-tomcat-10.1.x/webapps/

# Démarrer Tomcat (Linux/Mac)
/chemin/vers/apache-tomcat-10.1.x/bin/startup.sh

# Démarrer Tomcat (Windows)
/chemin/vers/apache-tomcat-10.1.x/bin/startup.bat
```

### 3b. Déploiement via IntelliJ IDEA

1. **Run > Edit Configurations**
2. **+** → **Tomcat Server > Local**
3. Onglet **Server** : indiquer le chemin vers Tomcat 10.1
4. Onglet **Deployment** : **+** → **Artifact** → `HospitApp:war exploded`
5. **Application context** : `/HospitApp`
6. Lancer avec **Run**

### 4. Accéder à l'application

```
http://localhost:8080/HospitApp/
```

Les données de test sont chargées automatiquement au démarrage (patients, médecins, infirmiers, consultations, actes chirurgicaux).

---

## Structure du projet

```
HospitApp/
├── src/
│   ├── model/              Entités métier, classes abstraites, interfaces, exceptions
│   │   ├── Entite.java
│   │   ├── Personne.java
│   │   ├── Patient.java
│   │   ├── Personnel.java
│   │   ├── Medecin.java
│   │   ├── Infirmier.java
│   │   ├── Soin.java
│   │   ├── Consultation.java
│   │   ├── ActeChirurgical.java
│   │   ├── FileUrgences.java
│   │   ├── Soignable.java
│   │   ├── Planifiable.java
│   │   ├── Facturable.java
│   │   ├── Urgence.java
│   │   ├── CapaciteDepasseeException.java
│   │   ├── EntiteIntrouvableException.java
│   │   └── DonneeInvalideException.java
│   ├── controller/         Services métier (logique, jamais de code HTTP)
│   │   ├── PatientService.java
│   │   ├── PersonnelService.java
│   │   ├── SoinService.java
│   │   ├── StatistiqueService.java
│   │   └── AppListener.java
│   ├── servlet/            Contrôleurs web (HTTP, jamais de logique métier)
│   │   ├── PatientServlet.java
│   │   ├── PersonnelServlet.java
│   │   ├── SoinServlet.java
│   │   ├── UrgenceServlet.java
│   │   └── StatistiqueServlet.java
│   ├── util/               Utilitaires génériques
│   │   ├── Registre.java
│   │   ├── CsvService.java
│   │   └── DonneesTest.java
│   └── view/               JSP + ressources web
│       ├── index.jsp
│       ├── css/style.css
│       └── WEB-INF/
│           ├── web.xml
│           └── views/
│               ├── commun/navbar.jsp
│               ├── erreur.jsp
│               ├── patients/
│               ├── personnel/
│               ├── soins/
│               ├── urgences/
│               └── statistiques/
├── resources/              Données CSV de test
│   ├── patients.csv
│   ├── medecins.csv
│   ├── infirmiers.csv
│   ├── consultations.csv
│   └── actes_chirurgicaux.csv
├── pom.xml
├── README.md
└── rapport_conception.md
```

---

## Architecture POO

### Classes abstraites

| Classe | Rôle |
|---|---|
| `Entite` | Racine du modèle : id UUID + date de création |
| `Personne` | Attributs communs à tout individu (nom, prénom, date de naissance…) |
| `Personnel` | Étend Personne : matricule, disponibilité, date d'embauche |
| `Soin` | Base commune aux actes médicaux : date, coût, patient, médecin |

### Interfaces métier

| Interface | Rôle | Implémenté par |
|---|---|---|
| `Soignable` | Peut recevoir des soins | `Patient`, `Medecin` |
| `Planifiable` | Possède un planning gérable | `Medecin`, `Infirmier` |
| `Facturable` | Génère une facturation | `Patient` |
| `Urgence` | A une priorité médicale (1 = critique, 5 = non urgent) | `ActeChirurgical` |

### Héritage

```
Entite (abstract)
└── Personne (abstract)
    ├── Personnel (abstract)
    │   ├── Medecin     implements Planifiable, Soignable
    │   └── Infirmier   implements Planifiable
    └── Patient         implements Soignable, Facturable

Entite (abstract)
└── Soin (abstract)
    ├── Consultation
    └── ActeChirurgical  implements Urgence
```

### Generics bornés

- `Registre<T extends Entite>` — collection universelle typée pour toute entité
- Méthode `filtrer(Predicate<T>)` — lambda passé en paramètre
- Méthode `trierPar(Comparator<? super T>)` — wildcard `? super T`
- Méthode statique `compterEntites(List<? extends Entite>)` — wildcard `? extends`

### Collections utilisées

| Collection | Usage justifié |
|---|---|
| `ArrayList<T>` | Liste ordonnée dans Registre (ordre d'insertion) |
| `HashMap<String, T>` | Index par id — accès en O(1) dans Registre |
| `HashSet<String>` | Vérification d'unicité des ids en O(1) dans Registre |
| `PriorityQueue<ActeChirurgical>` | File d'urgences triée par niveau de priorité médicale |
| `TreeMap<String, T>` | Index trié alphabétiquement — méthode `getIndexTrie()` dans Registre |
| `LinkedHashMap<String, Long>` | Répartition des spécialités triée et ordonnée (statistiques) |

### Streams et lambdas

Utilisés systématiquement dans les services pour :
- `filter()` — recherche multicritères (nom, statut, groupe sanguin, spécialité…)
- `sorted()` avec `Comparator` — tri ASC/DESC sur nom ou date
- `mapToDouble().sum()` — calcul du chiffre d'affaires
- `mapToInt().average()` — âge moyen des patients hospitalisés
- `collect(Collectors.groupingBy(..., Collectors.counting()))` — répartition par spécialité
- `summaryStatistics()` — min/max/moyenne des coûts de consultations
- `Stream.concat()` — fusion de deux flux (médecins + infirmiers, consultations + actes)
- `distinct().sorted()` — liste dédoublonnée des spécialités pour les menus déroulants

---

## Fonctionnalités implémentées

- [x] Initialisation du projet Maven web Jakarta EE (pom.xml, web.xml, index.jsp)
- [x] Modèle métier de base (Entite, Personne, Patient, Personnel, Medecin, Infirmier)
- [x] Interfaces métier (Soignable, Planifiable, Facturable, Urgence)
- [x] Soins (Consultation, ActeChirurgical, file d'urgences PriorityQueue)
- [x] Classe générique Registre et collections avancées
- [x] Services métier (PatientService, PersonnelService, SoinService, StatistiqueService)
- [x] Exceptions personnalisées (CapaciteDepasseeException, EntiteIntrouvableException, DonneeInvalideException)
- [x] Persistance CSV (sauvegarde et chargement automatiques)
- [x] Servlets (PatientServlet, PersonnelServlet, SoinServlet, UrgenceServlet, StatistiqueServlet)
- [x] Pages JSP patients (liste avec filtre/tri, détail, formulaire ajout/modification)
- [x] Pages JSP personnel, soins, urgences (liste, formulaires)
- [x] Recherche multicritères (3 critères combinables) et tri dynamique (ASC/DESC)
- [x] Tableau de bord statistiques (10+ indicateurs dynamiques, barres de progression)
- [x] Interface Bootstrap 5 finalisée (navbar active, alertes auto-masquées, responsive)

---

## Répartition des tâches

| Membre | Commits | Responsabilités |
|---|---|---|
| John DINH | 2, 3, 4, 5 | Modèle POO, interfaces, generics, exceptions |
| Fasil MOUGAMADOU | 6, 7, 8, 9 | Services métier, persistance CSV, Servlets |
| Akash ROUBERT | 10, 11, 12, 13, 14 | Pages JSP, CSS Bootstrap, filtres, statistiques |
| Commun | 1, 15 | Initialisation projet, README, rapport de conception |
