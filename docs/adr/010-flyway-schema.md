# ADR 010: Flyway Schema Evolution and Constraint Enforcement

## Status
Accepted

## Context
In early development phases, it's common to rely on Hibernate's `ddl-auto=update` or `create-drop` to manage database schemas. However, in an enterprise data platform, this leads to unpredictable schema changes, missing constraints, and data loss in production.

## Decision
We will use Flyway for all schema migrations. All SQL files (e.g., `V1__init_schema.sql`) will live in `data-pulse-infrastructure/src/main/resources/db/migration`. Hibernate is strictly configured to `spring.jpa.hibernate.ddl-auto=validate`, ensuring it only verifies that the entity mappings match the physical schema created by Flyway.

## Consequences
- Schema changes are deterministic, versioned, and applied uniformly across all environments.
- We can enforce advanced PostgreSQL-specific constraints (e.g., partial indexes like `WHERE deleted_at IS NULL`) that Hibernate cannot reliably generate.
