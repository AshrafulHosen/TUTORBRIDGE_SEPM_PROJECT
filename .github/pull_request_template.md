## What
<!-- Summarize the changes introduced by this pull request. -->

## Why
<!-- Explain the problem solved, rubric item covered, or task from the 7-day plan. -->

## How tested
<!-- List unit tests (*Test), integration tests (*IT), and manual verification steps. -->
- [ ] `./mvnw verify` passes locally (including JaCoCo 70% coverage check)

## Screenshots
<!-- Attach UI screenshots, Swagger outputs, or CI/configuration screenshots if applicable. -->

## Checklist
- [ ] Code follows package-by-feature layout and thin-controller conventions
- [ ] New endpoints include `@PreAuthorize` and ownership checks (404 for non-owners)
- [ ] No entities returned directly from controllers (DTO records only)
- [ ] No hardcoded secrets, passwords, or credentials
- [ ] New service behavior has Mockito unit tests / endpoints have MockMvc integration tests
