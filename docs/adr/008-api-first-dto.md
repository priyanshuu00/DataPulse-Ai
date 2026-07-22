# ADR 008: API-First Development and DTO Patterns

## Status
Accepted

## Context
We need to design the presentation layer (REST API) for our Dataset Management module. Passing pure Domain entities directly to the API layer creates tight coupling and leaks HTTP-specific details (like JSON serialization annotations) into the business logic.

## Decision
We will adopt an API-first approach using specialized Data Transfer Objects (DTOs) located in the `data-pulse-shared` module. The `DatasetController` will strictly accept and return DTOs, mapping them to and from Domain entities before interacting with the core services. We will enforce strict `/api/v1/` versioning immediately.

## Consequences
- Protects the Core Domain from presentation layer concerns.
- Increases boilerplate (requires mappers), but ensures long-term maintainability.
- API consumers get a stable, documented contract from Day 1.
