# Instructions — Étudiant 1 (Service Menu)

Objectif
- Développer le microservice « Service Menu » en projet indépendant (Jakarta EE). Il expose les catégories et plats via une API REST consommable par les autres services.

Stack technique obligatoire
- Jakarta EE (JAX-RS, JPA)
- GlassFish
- Base : Postgres (UUID PK)
- Validations : Bean Validation
- Migration DB : Flyway ou scripts SQL soignés

Répertoire / structure (suggestion)
- service-menu/
  - src/main/java/...
  - src/main/resources/META-INF/persistence.xml
  - src/main/resources/db/migration (Flyway)
  - src/main/webapp (si UI JSF nécessaire)
  - README.md

Structure des packages (convention)
- `com.namaaz.menu.entities` : toutes les entités JPA (`Category`, `MenuItem`, etc.)
- `com.namaaz.menu.business` : services métier, EJB/CDI, logique de validation et règles métier
- `com.namaaz.menu.bean` : JSF Managed Beans / DTO pour l'UI (si utilisé) et adaptateurs REST/JSF
- `com.namaaz.menu.rest` : ressources JAX-RS (endpoints)
- `com.namaaz.menu.repository` : Interfaces DAO / Repositories (optionnel si séparé)

Tâches prioritaires
1. Implémenter les entités JPA : Category, MenuItem (voir `bd_models.md`).
2. Fournir migrations SQL (V1__create_menu_tables.sql) basées sur `bd_models.md`.
3. Exposer API REST (JAX-RS) :
   - GET /api/categories
   - GET /api/categories/{id}
   - POST /api/categories
   - PUT /api/categories/{id}
   - DELETE /api/categories/{id}

   - GET /api/menu-items
   - GET /api/menu-items/{id}
   - POST /api/menu-items
   - PUT /api/menu-items/{id}
   - DELETE /api/menu-items/{id}

   - Filtres : ?categoryId=, ?available=true
4. Validation : utiliser `@Valid` + contraintes JPA/Bean Validation pour champs obligatoires et prix >= 0.
5. Tests unitaires : tests pour repository/service et tests d’intégration pour endpoints principaux.

Contrats d'API (format attendu)
- `MenuItem` JSON (exemple)
  {
    "id": "uuid",
    "name": "Ravioles",
    "description": "...",
    "price": 12.50,
    "categoryId": "uuid",
    "available": true,
    "prepTime": 15
  }

- Retourner codes HTTP standards : 200, 201, 204, 400, 404, 422.

Intégration inter-services
- Stocker toujours UUID comme identifiant partageable.
- Fournir endpoint GET /api/menu-items/{id} utilisé par Reservation/Order services pour récupérer info produit.
- Conserver `price` dans le service Menu mais noter que consumers prendront des snapshots (`unit_price`) côté Orders/Reservations.

Enums & validations
- Utiliser `@Enumerated(EnumType.STRING)` dans JPA si nécessaire (ex: availability si transformé en enum). Actuellement `available` reste boolean.

Documentation
- Documenter les endpoints (README + postman/openapi minimal).
- Ajouter exemples de payloads et erreurs possibles.

Livrables attendus
- Projet `service-menu` compilable et déployable sur GlassFish.
- Fichier de migration SQL / Flyway dans `db/migration`.
- README avec commande build/deploy, endpoints, et exemples.
- Tests unitaires basiques.

Bonnes pratiques & communication
- Branches : `main` (stable), `feature/nom` pour travail. PR obligatoire avant merge.
- Respecter contrats JSON ; toute modification breaking doit être discutée.
- Poster changements majeurs du schéma DB dans la PR et informer l'autre étudiant.

Deadline & démonstration
- Fournir un build déployable et collection OpenAPI/Postman avant la review commune.

---

Fichiers utiles : [bd_models.md](bd_models.md)
