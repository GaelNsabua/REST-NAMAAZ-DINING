# Instructions — Étudiant 2 (Services Réservations + Clients/Commandes/Paiements)

Objectif
- Développer deux microservices séparés, chacun dans son propre projet :
  1) `service-reservations` (réservations, tables)
  2) `service-clients-orders` (clients, commandes, paiements)

Stack technique obligatoire
- Jakarta EE (JAX-RS, JPA)
- GlassFish
- Base : Postgres (UUID PK)
- Validation : Bean Validation
- Migration DB : Flyway ou SQL scripts

Répertoire / structure (suggestion)
- service-reservations/
  - src/... , db/migration, README
- service-clients-orders/
  - src/... , db/migration, README

Structure des packages (convention)
Pour garantir cohérence entre les deux projets, respecter la convention suivante pour chaque service :

- `com.namaaz.<service>.entities` : entités JPA (ex: `Reservation`, `RestaurantTable`, `Client`, `Order`, ...)
- `com.namaaz.<service>.business` : logique métier (EJB / CDI), validations complexes et règles de gestion
- `com.namaaz.<service>.bean` : Managed Beans JSF / DTOs pour l'UI et adaptateurs pour REST
- `com.namaaz.<service>.rest` : ressources JAX-RS (endpoints)
- `com.namaaz.<service>.repository` : interfaces DAO / Repositories (optionnel)

Remplacez `<service>` par `reservations` ou `clientsorders` selon le projet.

Tâches prioritaires (ordre recommandé)
1. `service-clients-orders` : implémenter l'entité `Client` et endpoints basiques (CRUD) — ceci permet de tester `client_id` pour les réservations.
2. `service-reservations` : implémenter entités `RestaurantTable`, `Reservation`, `ReservationItem` (voir `bd_models.md`).
3. `service-clients-orders` : implémenter `Order`, `OrderItem`, `Payment` avec enums (voir `bd_models.md`).
4. Migrations SQL pour chaque service (V1__create_tables.sql).
5. Endpoints REST principaux :

   service-reservations :
   - GET /api/reservations
   - GET /api/reservations/{id}
   - POST /api/reservations  (payload inclut `clientId`, `numPeople`, `startTime`, `endTime`, `items` avec menu_item_id & quantity)
   - PUT /api/reservations/{id}
   - DELETE /api/reservations/{id}
   - GET /api/tables
   - POST /api/tables

   service-clients-orders :
   - Clients CRUD -> /api/clients
   - Orders : POST /api/orders (refs: `clientId`, optional `reservationId`, items list) / GET /api/orders/{id}
   - Payments : POST /api/payments (orderId, amount, method)

Contrats importants & règles d'intégration
- Identifiants inter-services : utiliser UUID.
- Reservation POST doit valider l'existence du `clientId` via `service-clients-orders` (HTTP call). Le service-reservations peut refuser la création si client inconnu.
- ReservationItem / OrderItem utilisent `menu_item_id` (UUID) — le service doit appeler `service-menu` pour obtenir nom/prix si nécessaire ou stocker price snapshot reçu par le client qui crée la commande.
- Pour la cohérence historique, sauvegarder `price_snapshot`/`unit_price` dans les tables d'items.
- Toutes les appels inter-services doivent être idempotents en re-try safe.

Enums (à utiliser)
- Reservation status : `PENDING`, `CONFIRMED`, `CANCELLED`.
- Table status : `FREE`, `RESERVED`, `OCCUPIED`, `OUT_OF_SERVICE`.
- Order status : `NEW`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`.
- Payment method : `CASH`, `CARD`, `ONLINE`.
- Payment status : `PENDING`, `OK`, `FAILED`.

Validation & erreurs
- Utiliser Bean Validation pour payloads.
- Répondre avec codes HTTP standards et corps d'erreur structuré {"code":..., "message":...}.

Tests & QA
- Tests unitaires pour logique métier (ex: vérification disponibilité tables, calcul totals).
- Tests d’intégration pour endpoints critiques (création de réservation, création de commande + paiement).

Sécurité minimale
- Protéger endpoints modificateurs avec un filtre web (auth simple si temps limité) ; sinon documenter clairement la limitation.

Livrables attendus
- Deux projets distincts : `service-reservations` et `service-clients-orders` compilables et déployables.
- Fichiers de migration SQL dans `db/migration` pour chaque projet.
- README par projet expliquant build/deploy et endpoints essentiels.
- Collection Postman/OpenAPI minimale pour tester les flows : créer client → créer réservation → créer commande → paiement.

Bonnes pratiques de collaboration
- Branches : `main`, `feature/xxx` ; PR + code review avant merge.
- Contract-first : tout changement breaking sur un endpoint ou schéma DB doit être communiqué et validé en PR.
- Mocking local : fournir stubs pour `service-menu` (ex: endpoint /api/menu-items/{id}) pour tests locaux si l'étudiant 1 n'a pas encore livré.

Checklist rapide avant review commune
- [ ] Migrations SQL ajoutées
- [ ] Endpoints CRUD opérationnels
- [ ] Tests automatisés (unit + integration)
- [ ] README et collection API fournis

Fichiers utiles : [bd_models.md](bd_models.md)
