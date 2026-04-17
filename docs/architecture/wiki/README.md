# EWACP Architecture Wiki

This folder is the **architecture wiki** for the Enterprise Workflow Automation & Collaboration Platform (EWACP). It holds bounded-context documentation and links to diagram sources.

## Bounded contexts and data models

| Document | Description |
|----------|-------------|
| [Bounded contexts and data models](bounded-contexts.md) | Responsibilities, boundaries, and relationships between Workflow Engine, Task Scheduler, Persistence Service, and Business Rules Engine |

## Diagram sources (PlantUML)

PlantUML sources live under `docs/architecture/plantuml/` and can be rendered locally or in CI:

- `microservice-components.puml` — UML-style component diagram (service boundaries and dependencies)
- `data-models.puml` — Entity relationship / domain data model view per bounded context

## Confluence / epic linkage

Publishing to Confluence is a manual step in the target environment:

1. Export rendered PNG/SVG from PlantUML (or use a Confluence PlantUML macro).
2. Attach diagrams to the **Backend Microservice Suite Development** epic page.
3. Add a short “Architecture” subsection with links to those attachments.

Until Confluence URLs exist, this repository remains the **source of truth** for diagrams and API contracts.
