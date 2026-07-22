# ADR 002: Maven as Build Tool

## Status
Accepted

## Context
A multi-module project requires a strict dependency management and build lifecycle tool.

## Decision
We will use Apache Maven.

## Consequences
- Provides a strict convention-over-configuration lifecycle.
- Universal support in CI/CD environments and IDEs.
- XML configuration is more verbose than Gradle's Groovy/Kotlin DSL, potentially leading to larger configuration files.
