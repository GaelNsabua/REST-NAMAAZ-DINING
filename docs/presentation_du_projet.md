# 🍽️ Mini-Projet : Namaaz Dining
Système de gestion des commandes et réservations  
Architecture microservices avec Jakarta EE

## 1. Contexte et objectif du projet
Le restaurant Namaaz Dining souhaite mettre en place une application interne permettant de gérer efficacement :
- le menu et les plats
- les réservations de tables
- les clients, commandes et paiements

Objectif pédagogique :
- initier les étudiants à l’architecture microservices
- programmation d’entreprise avec Jakarta EE
- communication inter-services via APIs REST
- structuration propre d’une application professionnelle

Remarque : chaque étudiant (ou groupe) est responsable d’un service métier distinct, disposant de :
- sa propre base de données
- ses entités, logique métier et beans
- ses endpoints REST

## 2. Architecture générale
### Style architectural
- Architecture microservices
- Communication via API REST (JAX-RS)
- Services indépendants et faiblement couplés

### Technologies imposées

| Domaine      | Technologie                          |
|--------------|--------------------------------------|
| Backend      | Jakarta EE                           |
| Frontend     | JSF (Jakarta Faces)                  |
| Style UI     | Tailwind CSS (via CDN)               |
| Serveur      | GlassFish                            |
| Persistance  | JPA (Jakarta Persistence)            |
| Validation   | Bean Validation (Jakarta Validation) |
| Sécurité     | Session Manager + Web Filters        |

## 3. Architecture Jakarta EE adoptée
Chaque microservice respecte la structure Jakarta EE classique.

### Couches applicatives
- **Entité (Entity)**  
    Représente les données persistées. Annotée avec `@Entity`.
- **Couche Business (Service / EJB / CDI)**  
    Contient la logique métier et gère les règles fonctionnelles.
- **Bean (JSF Managed Bean)**  
    Fait le lien entre l’interface utilisateur et la logique métier.
- **API REST (JAX-RS)**  
    Expose les fonctionnalités aux autres services.

### Composants transversaux
- Session Manager : gestion des sessions utilisateurs
- Web Filters : sécurité, contrôle d’accès, journalisation
- Bean Validator : validation automatique des données entrantes

## 4. Description des microservices

### 4.1 Service Gestion du Menu
**Rôle**  
Gérer l’ensemble des plats proposés et fournir les informations aux autres services.

**Fonctionnalités**
- Ajouter / modifier / supprimer un plat :
    - nom, description, prix, catégorie
- Organiser le menu par catégories : entrées, plats principaux, desserts, boissons
- Exposer les plats disponibles via API REST
- Répondre aux requêtes du Service Commandes et du Service Réservations
- Vérifier la disponibilité des plats

### 4.2 Service Gestion des Réservations
**Rôle**  
Gérer les réservations de tables et leur planification.

**Fonctionnalités**
- Créer / modifier / annuler une réservation :
    - client (référence du Service Clients), nombre de personnes, date et heure, description, tables associées
- Vérifier la disponibilité des tables
- Associer un client à une réservation
- Gérer l’historique : réservations passées et futures
- Associer à chaque réservation les plats choisis via le Service Menu
- Répondre aux requêtes du Service Clients et du Service Commandes

### 4.3 Service Gestion des Clients, Commandes et Paiements
**Rôle**  
Centraliser la gestion des clients, de leurs commandes et des paiements.

**Fonctionnalités**
- Gérer les clients : ajout, modification, suppression
- Suivre les commandes par client (liées à une réservation ou à une table)
- Gérer les paiements : calcul du montant total, validation du paiement
- Générer des rapports : ventes quotidiennes, rentabilité des plats
- Répondre aux requêtes du Service Réservations

## 5. Interfaces utilisateur (UI)
Approche :
- Interfaces développées avec JSF
- Utilisation de Managed Beans
- Validation JSF + Bean Validation
- Stylisation avec Tailwind CSS via CDN
- Design simple, moderne et responsive

## 6. Validation et sécurité
### Validation des données
- Bean Validation (`@NotNull`, `@Size`, `@Min`, etc.)
- Validation automatique côté serveur

### Sécurité
- Gestion des sessions utilisateurs
- Filtres web pour contrôle d’accès, redirection et sécurité minimale
- Séparation claire entre services

## 7. Objectifs pédagogiques
À la fin du projet, l’étudiant sera capable de :
- Concevoir une application microservices avec Jakarta EE
- Structurer un projet selon les bonnes pratiques Jakarta
- Implémenter des APIs REST
- Utiliser JSF + Tailwind CSS
- Comprendre la communication inter-services
- Déployer une application sur GlassFish

## 8. Livrables attendus
- Code source de chaque microservice
- Base de données propre à chaque service
- Documentation des APIs REST
- Rapport expliquant : l’architecture, les choix techniques, les difficultés rencontrées
