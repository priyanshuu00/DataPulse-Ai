# ADR 005: Clean Architecture

## Status
Accepted

## Context
The platform needs to be highly maintainable, testable, and independent of specific frameworks (like Spring Boot) or databases.

## Decision
We will adopt Clean Architecture principles, structuring the application into concentric layers where dependencies only point inward towards the Domain.

## Consequences
- Business logic is completely decoupled from UI, databases, and external agencies.
- High testability of the core domain.
- Requires strict discipline to map entities across boundaries (e.g., Domain Model <-> Persistence Entity <-> API DTO), increasing the number of classes and initial boilerplate.
