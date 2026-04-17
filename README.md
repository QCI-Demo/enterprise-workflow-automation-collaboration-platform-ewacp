# EWACP — Service boundaries and API contracts

This repository holds **architecture documentation** and **contract-first API artifacts** for the Enterprise Workflow Automation & Collaboration Platform (EWACP) backend microservices.

## Layout

| Path | Contents |
|------|----------|
| `docs/architecture/wiki/` | Bounded-context narrative and wiki index |
| `docs/architecture/plantuml/` | PlantUML sources for component and data-model diagrams |
| `openapi/` | OpenAPI 3.0 specifications (one file per service) + shared `common.yaml` |
| `proto/` | gRPC `.proto` files mirroring the same operations |

## Validate OpenAPI

```bash
npm install
npm run validate:openapi
```

Validation uses `@apidevtools/swagger-cli` (`swagger-cli validate`) against each service spec.

## Confluence

Diagrams are authored in PlantUML here; publishing rendered assets to Confluence and linking from the epic is a manual step in your Atlassian environment (see `docs/architecture/wiki/README.md`).
