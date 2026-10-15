# Le Grande Cinema

Application web de réservation et de gestion d'un cinéma.

Ce projet est réalisé dans le cadre du Bloc 3 « Développer une solution digitale ».

## Objectifs

L'application permet aux utilisateurs de consulter les films et les séances,
de vérifier le nombre de places disponibles, de réserver des places et
d'effectuer un paiement simulé.

Une interface d'administration permet également de gérer la programmation,
de suivre les réservations et d'analyser la fréquentation du cinéma.

## Fonctionnalités

### Utilisateur

- Consultation des films à l'affiche
- Consultation du détail d'un film
- Consultation des séances
- Consultation du nombre de places disponibles
- Inscription d'un utilisateur
- Authentification
- Réservation d'une ou plusieurs places
- Attribution automatique des places
- Paiement simulé
- Confirmation de la réservation après paiement

### Administration

- Création d'un film
- Création d'une séance
- Création automatique des places disponibles d'une séance
- Suppression d'une séance lorsqu'elle ne possède pas de réservation
- Consultation des réservations d'une séance
- Consultation du taux de remplissage d'une séance
- Analyse du taux de remplissage sur une période

## Technologies

### Backend

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Hibernate
- Maven
- Lombok

### Base de données

- MySQL

### Tests

- JUnit 5
- Mockito
- MockMvc

## Architecture

Le backend suit une architecture en couches :

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Base de données MySQL
```

Les entités JPA ne sont pas directement exposées par l'API.

Des DTO et des mappers sont utilisés afin de contrôler les données reçues
et retournées par l'application.

La structure principale du projet repose notamment sur les packages suivants :

```text
controller
dto
exception
mapper
model
repositories
security
service
```

## Sécurité

L'application utilise Spring Security.

Les règles principales sont les suivantes :

- Les films et les séances sont accessibles publiquement.
- L'inscription est accessible sans authentification.
- Les réservations et les paiements nécessitent une authentification.
- Les routes `/api/admin/**` nécessitent le rôle `ADMIN`.
- Les mots de passe sont encodés avec BCrypt.
- Un utilisateur ne peut payer que ses propres réservations.
- Le montant d'un paiement est calculé côté backend afin de ne pas faire confiance à une valeur transmise par le client.

Pour la première version de l'application, l'authentification utilise HTTP Basic.

## Gestion des erreurs

Une gestion centralisée des erreurs est mise en place avec
`@RestControllerAdvice`.

L'API peut notamment retourner les codes HTTP suivants :

- `400 Bad Request` : requête invalide
- `404 Not Found` : ressource introuvable
- `409 Conflict` : conflit avec une règle métier

Cette gestion permet d'éviter de retourner systématiquement des erreurs serveur
génériques lorsqu'une erreur fonctionnelle est identifiée.

## Gestion des réservations

Lorsqu'un utilisateur effectue une réservation :

1. L'utilisateur authentifié est identifié.
2. La séance demandée est recherchée.
3. L'application vérifie que la séance n'est pas déjà passée.
4. Le nombre de places disponibles est contrôlé.
5. Une réservation au statut `EN_ATTENTE` est créée.
6. Les places disponibles sont attribuées automatiquement à la réservation.
7. Un paiement peut ensuite être effectué.

Une réservation peut notamment avoir les statuts suivants :

```text
EN_ATTENTE
CONFIRMEE
ANNULEE
```

## Gestion des paiements

Le paiement est simulé à l'aide d'un système de paiement mock.

Les statuts possibles sont :

```text
EN_ATTENTE
ACCEPTE
REFUSE
```

Le montant du paiement n'est pas envoyé par le frontend.

Il est calculé côté serveur à partir :

```text
prix de la séance × nombre de places réservées
```

Si le paiement est accepté, la réservation passe au statut :

```text
CONFIRMEE
```

Si le paiement est refusé, elle reste :

```text
EN_ATTENTE
```

## API REST

Quelques routes principales de l'application :

### Films

```text
GET /api/films
```

Consultation des films.

### Séances

```text
GET /api/films/{idFilm}/seances
```

Consultation des séances associées à un film.

```text
GET /api/seances/{idSeance}
```

Consultation d'une séance.

```text
GET /api/seances/{idSeance}/places-disponibles
```

Consultation du nombre de places disponibles.

### Authentification

```text
POST /api/auth/inscription
```

Création d'un compte utilisateur.

```text
GET /api/auth/moi
```

Récupération des informations de l'utilisateur authentifié.

### Réservations

```text
POST /api/reservations
```

Création d'une réservation.

### Paiements

```text
POST /api/paiements
```

Paiement d'une réservation.

### Administration

Les routes d'administration sont protégées par Spring Security :

```text
/api/admin/**
```

Elles permettent notamment :

- la création de films ;
- la création et la suppression de séances ;
- la consultation des réservations d'une séance ;
- la consultation des statistiques de fréquentation.

## Git

Le projet utilise une organisation Git basée sur plusieurs types de branches.

### `main`

Contient la version stable du projet.

### `develop`

Branche d'intégration utilisée pour regrouper les fonctionnalités terminées.

### `feature/...`

Chaque nouvelle fonctionnalité est développée dans une branche dédiée.

Exemples :

```text
feature/creation-entites
feature/creation-services
feature/controllers-rest
feature/security-authentification
feature/reservation-api
feature/admin-api
feature/user-registration
feature/payment-security
feature/gestion-erreurs
```

Le workflow utilisé est le suivant :

```text
feature/...
    ↓
Pull Request
    ↓
develop
    ↓
Pull Request
    ↓
main
```

Une branche `feature` est supprimée lorsqu'elle a été fusionnée et que son
développement est terminé.

## Lancer le projet

### Prérequis

Pour exécuter le backend localement :

- Java 17 ou supérieur
- MySQL
- Maven ou Maven Wrapper
- Git

### Base de données

Créer une base MySQL :

```sql
CREATE DATABASE cinema_db;
```

La configuration de connexion à la base de données est définie dans :

```text
src/main/resources/application.properties
```

L'application utilise la base :

```text
cinema_db
```

Le mot de passe MySQL est fourni à l'application via la variable
d'environnement :

```text
DB_PASSWORD
```

Cela permet d'éviter de stocker directement le mot de passe de la base de
données dans le repository Git.

### Démarrage

Depuis la racine du projet :

```bash
./mvnw spring-boot:run
```

Sous Windows avec Git Bash, la même commande peut être utilisée.

L'application Spring Boot démarre ensuite sur le serveur local configuré.

## Tests

Les tests unitaires sont réalisés avec :

- JUnit 5
- Mockito
- MockMvc

Pour exécuter l'ensemble des tests :

```bash
./mvnw test
```

Un build réussi doit se terminer par :

```text
BUILD SUCCESS
```

Les tests couvrent notamment :

- les services ;
- les contrôleurs REST ;
- la création des séances ;
- les réservations ;
- les paiements ;
- l'inscription utilisateur ;
- les fonctionnalités administrateur ;
- les statistiques de fréquentation.

## Frontend

Le frontend de l'application est développé avec Angular.

Conformément à l'organisation retenue pour le projet, le frontend est placé
dans un repository Git public séparé du backend.

### Backend

```text
Le-Grande-Cinema
```

### Frontend

```text
Le-Grande-Cinema-Front
```

Le repository frontend suivra également le workflow :

```text
main
develop
feature/...
```

Le lien Git du frontend sera ajouté ici une fois le repository créé.

## État du projet

### Backend

Le backend principal est opérationnel avec :

- API REST
- accès MySQL
- gestion des films
- gestion des séances
- gestion des utilisateurs
- authentification
- réservation
- attribution automatique des places
- paiement simulé
- administration
- statistiques de fréquentation
- gestion centralisée des erreurs
- tests unitaires

### Frontend

En cours de développement avec Angular.

### Déploiement

À venir.

## Auteur

Projet réalisé dans le cadre d'une formation de développement d'application.

Développement :

**Aurélien Le Roch**
