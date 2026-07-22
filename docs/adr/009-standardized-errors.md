# ADR 009: Standardized API Error Handling

## Status
Accepted

## Context
When APIs throw exceptions (e.g., entity not found, validation failures), the default framework behavior often leaks stack traces or returns inconsistent JSON structures, leading to poor developer experience for API consumers.

## Decision
We will use Spring's `@RestControllerAdvice` to implement a `GlobalExceptionHandler`. Every error will be caught and mapped to a standardized `ApiErrorResponse` DTO containing `timestamp`, `status`, `error`, `path`, and a unique `traceId`.

## Consequences
- Guaranteed consistent error structure across all API endpoints.
- The `traceId` provides a foundation for distributed tracing and centralized logging in future phases.
- Hides internal application implementation details from the client.
