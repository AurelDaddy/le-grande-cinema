# Le Grande Cinema

Application web de réservation et de gestion d'un cinéma, réalisée dans le cadre d'un projet de fin d'études.

Ce dépôt contient le **backend Java / Spring Boot**. Le frontend Angular est maintenu dans un dépôt séparé.

## Liens du projet

| Ressource | Lien |
| --- | --- |
| Application web | https://le-grande-cinema-front.vercel.app |
| API backend | https://le-grande-cinema.onrender.com |
| Documentation Swagger UI | https://le-grande-cinema.onrender.com/swagger-ui/index.html |
| Dépôt backend | https://github.com/AurelDaddy/le-grande-cinema |
| Dépôt frontend | https://github.com/AurelDaddy/le-grande-cinema-front |

> Le service Render peut prendre un moment à répondre après une période d'inactivité. Le paiement est **simulé** : aucun règlement bancaire réel n'est effectué. Les identifiants de démonstration destinés à l'évaluateur figurent dans le dossier de remise, pas dans ce dépôt public.

## Objectifs et fonctionnalités

L'application permet de consulter les films et les séances, de connaître les disponibilités, de réserver des places à distance et de simuler le paiement d'une réservation. L'administration dispose de fonctionnalités de gestion de la programmation et de suivi de la fréquentation.

**Côté utilisateur**

- Consulter les films à l'affiche, leurs détails et les séances futures.
- Visualiser le nombre de places disponibles pour une séance.
- S'inscrire et s'authentifier.
- Réserver une ou plusieurs places, attribuées automatiquement.
- Effectuer un paiement simulé et confirmer la réservation lorsque le paiement est accepté.

**Côté administration**

- Ajouter des films au catalogue.
- Créer des séances et leurs places disponibles ; supprimer une séance lorsque les règles métier l'autorisent.
- Consulter les réservations d'une séance et les statistiques de remplissage.

## Technologies

| Domaine | Technologies |
| --- | --- |
| Backend | Java 17, Spring Boot 4.1.1, Spring Web MVC |
| Persistance | Spring Data JPA, Hibernate |
| Sécurité | Spring Security, HTTP Basic, BCrypt |
| Base de données | MariaDB en production (Alwaysdata), MySQL pour les tests d'intégration, H2 pour les tests Spring |
| Documentation API | Springdoc OpenAPI / Swagger UI |
| Construction et tests | Maven, JUnit 5, Mockito, MockMvc, JaCoCo, Maven Failsafe |
| Qualité et CI | GitHub Actions, SonarQube Cloud |
| Hébergement | Render (backend), Vercel (frontend) |

## Architecture

Le backend est structuré en couches, pour séparer les responsabilités :

```text
Requête HTTP
    ↓
Controller → DTO / Mapper
    ↓
Service / ServiceImpl (règles métier)
    ↓
Repository (Spring Data JPA)
    ↓
Base de données relationnelle
```

Les packages principaux sont `controller`, `dto`, `exception`, `mapper`, `model`, `repositories`, `security` et `service`. Les entités JPA ne sont pas exposées directement par l'API : les DTO et mappers contrôlent les données échangées.

Le modèle relationnel comprend notamment les entités **Utilisateur**, **Film**, **Salle**, **Séance**, **Place**, **PlaceSéance**, **Réservation** et **Paiement**.

## Règles métier importantes

### Réservations

Une réservation est créée au statut `EN_ATTENTE` après vérification de la séance et des places disponibles. Les places sont attribuées à la réservation ; un paiement accepté la fait passer à `CONFIRMEE`. Une réservation peut également être `ANNULEE`.

Pour éviter l'attribution de la même dernière place à plusieurs clients, le service utilise une transaction et un **verrou pessimiste** sur la séance (`PESSIMISTIC_WRITE`) avant de contrôler les disponibilités.

Une réservation `EN_ATTENTE` est **valable 15 minutes**. Lorsqu'une nouvelle demande de réservation arrive pour la même séance, les réservations en attente expirées sont annulées et leurs places libérées avant le contrôle des disponibilités. Le paiement d'une réservation expirée est refusé.

> La libération est déclenchée lors d'une nouvelle demande sur la séance : il n'existe **pas** de tâche planifiée qui annule automatiquement toutes les réservations dès la quinzième minute.

### Paiements

Le paiement utilise un prestataire simulé (*mock*). Son montant est calculé côté serveur :

```text
montant = prix de la séance × nombre de places réservées
```

Les statuts prévus sont `EN_ATTENTE`, `ACCEPTE` et `REFUSE`. L'enregistrement du paiement et la mise à jour de la réservation sont réalisés dans une transaction. Le champ montant utilise `BigDecimal` avec une précision SQL de **10 chiffres et 2 décimales**.

## Sécurité et gestion des erreurs

- Authentification HTTP Basic avec Spring Security ; mots de passe hachés avec BCrypt.
- Accès public à la consultation des films et des séances ainsi qu'à l'inscription ; accès aux routes `/api/admin/**` réservé au rôle `ADMIN`.
- Contrôle de l'utilisateur authentifié pour les réservations et paiements, avec vérification du propriétaire de la réservation.
- Secrets de connexion à la base externalisés dans des variables d'environnement.
- Gestion centralisée des exceptions avec `@RestControllerAdvice` ; réponses HTTP adaptées, notamment `400`, `404` et `409`.

**Limite connue :** la configuration actuelle désactive la protection CSRF et utilise HTTP Basic. Une revue de cette configuration et des tests de sécurité dédiés figurent parmi les améliorations envisageables ; le Quality Gate ne constitue pas une garantie d'absence de vulnérabilités.

## API REST — principaux endpoints

| Méthode | Endpoint | Fonction |
| --- | --- | --- |
| `GET` | `/api/films` | Liste des films |
| `GET` | `/api/films/{idFilm}/seances` | Séances futures d'un film |
| `GET` | `/api/seances/{idSeance}` | Détail d'une séance |
| `GET` | `/api/seances/{idSeance}/places-disponibles` | Nombre de places disponibles |
| `POST` | `/api/auth/inscription` | Inscription |
| `GET` | `/api/auth/moi` | Utilisateur connecté |
| `POST` | `/api/reservations` | Création d'une réservation |
| `POST` | `/api/paiements` | Paiement simulé |
| `GET` | `/api/admin/seances/{id}/reservations` | Réservations d'une séance (admin) |

Les autres opérations d'administration sont décrites dans [Swagger UI](https://le-grande-cinema.onrender.com/swagger-ui/index.html).

## Exécuter le backend en local

### Prérequis

- JDK 17 ou version compatible supérieure ; le projet est compilé pour Java 17.
- Maven (ou Maven Wrapper fourni avec le projet).
- Une base MariaDB compatible avec la configuration du backend et un schéma initialisé.

Créer, si nécessaire, la base locale :

```sql
CREATE DATABASE cinema_db;
```

La configuration se trouve dans `src/main/resources/application.properties` :

| Variable | Rôle | Valeur par défaut |
| --- | --- | --- |
| `DB_URL` | URL JDBC MariaDB | `jdbc:mariadb://localhost:3306/cinema_db` |
| `DB_USERNAME` | Utilisateur de la base | `root` |
| `DB_PASSWORD` | Mot de passe de la base | Obligatoire, sans valeur par défaut |
| `PORT` | Port HTTP | `8080` |

Les variables sensibles ne doivent pas être commitées. Dans Git Bash, après avoir configuré les variables de connexion à ta base locale :

```bash
mvn clean verify
mvn spring-boot:run
```

L'API est alors accessible sur `http://localhost:8080` et Swagger UI sur `http://localhost:8080/swagger-ui/index.html`.

> La seule création de `cinema_db` ne suffit pas à reconstituer les tables et données de démonstration. Il faut disposer d'un schéma compatible avec les entités du projet. Ne pas utiliser la base de production pour réaliser des essais destructifs.

## Tests et qualité du code

Les tests sont placés dans `src/test/java` en suivant l'arborescence des packages de `src/main/java`.

- **Tests unitaires** : JUnit 5 et Mockito pour vérifier les services et règles métier en isolant les repositories.
- **Tests de contrôleurs** : MockMvc pour vérifier les réponses HTTP et JSON.
- **Tests de persistance** : H2 en mémoire avec le profil de test.
- **Tests d'intégration MySQL** : transactions, réservations concurrentes et interactions entre paiement et expiration, sur une base de test indépendante.

Commandes :

```bash
# Tests habituels et rapport JaCoCo
mvn clean verify

# Tests habituels + tests d'intégration MySQL (base cinema_test configurée)
mvn clean verify -P integration-mysql
```

Pour les tests MySQL locaux, le profil d'intégration utilise `src/test/resources/application-integration-mysql.properties`, la base **`cinema_test`** et les variables `TEST_DB_USERNAME` / `TEST_DB_PASSWORD`. Le schéma y est créé et supprimé automatiquement (`create-drop`) : **ne jamais pointer ce profil vers une base contenant des données à conserver**.

JaCoCo génère un rapport de couverture dans `target/site/jacoco/index.html` après la phase Maven `verify`.

Le workflow `.github/workflows/build.yml` exécute les tests avec un service MySQL temporaire et lance l'analyse SonarQube Cloud pour les pushes sur `develop` et `main`, ainsi que les Pull Requests. Lors de la dernière analyse documentée pour le dossier Studi : **Quality Gate validé**, **89,2 % de couverture globale** et **0,0 % de duplication**. Un point de sécurité relatif au CSRF reste identifié.

Trois anomalies ont fait l'objet de corrections testées : montant de paiement au-delà de 999,99 €, double réservation de la dernière place et expiration des réservations en attente. Leur analyse détaillée et les preuves RED/GREEN figurent dans le dossier de certification.

## Gestion des versions avec Git

Le projet suit une organisation inspirée de Git Flow :

- `main` : versions stables et déployables ;
- `develop` : intégration des fonctionnalités validées ;
- `feature/nom-fonctionnalite` : nouvelles fonctionnalités ;
- `fix/nom-correction` : corrections d'anomalies.

Les branches dédiées sont intégrées à `develop` par Pull Request. Une Pull Request de `develop` vers `main` est créée lorsqu'une version est stable et prête à être déployée. Les branches temporaires sont supprimées après intégration.

## Déploiement

- **Backend** : Render, avec une configuration d'environnement pour accéder à la base MariaDB distante.
- **Base de données** : MariaDB hébergée chez Alwaysdata.
- **Frontend** : Angular déployé séparément sur Vercel et consommant l'API REST Render.
- **Documentation** : Swagger UI accessible sur l'instance backend déployée.

Le backend est conteneurisé pour son déploiement. Aucun secret de production ne doit figurer dans le repository.

## Auteur

**Aurélien Le Roch** — Projet de fin d'études, Bloc 3 « Développer une solution digitale ».
