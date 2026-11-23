
# AGENTS.md

## 1. Project Overview

This project is an Android application developed using Kotlin and Jetpack Compose.

The project follows a feature-based architecture with separated layers:
- UI layer
- Domain layer
- Data layer
- Dependency Injection (DI)

The base package currently used is `com.androidproject`.

## 2. Technology Stack

- Language: Kotlin
- UI: Jetpack Compose
- IDE: Android Studio
- Build system: Gradle (Kotlin DSL)
- CI/CD: GitHub Actions
- Version control: Git and GitHub

## 3. Project Architecture

All application source code should be organized under:

`app/src/main/java/com/androidproject/`

Expected structure:

    com/androidproject/
    ├── ui/
    │   ├── auth/
    │   ├── groups/
    │   ├── expenses/
    │   └── notifications/
    ├── domain/
    ├── data/
    │   ├── local/
    │   └── remote/
    └── di/

### Architecture Rules

- Keep business logic independent of Android UI components.
- ViewModels should handle UI state and presentation logic.
- Composables should focus on displaying UI.
- Keep data access separate from UI code.
- Do not place business logic directly inside Composable functions.
- Create directories when they do not already exist.

## 4. Coding Guidelines

- Follow Kotlin coding conventions.
- Use meaningful names for classes, functions and variables.
- Prefer small, reusable functions and Composable components.
- Avoid unnecessary code duplication.
- Handle loading, empty, error and offline states where applicable.
- Add unit tests for business logic.
- Validate user input before processing it.
- Never commit passwords, API keys, tokens or private credentials.

## 5. Git Workflow

### Golden Rules

- Never work directly on the main branch.
- Every task must be implemented on a separate feature branch.
- Use descriptive branch names.

Branch naming examples:

- `feature/auth`
- `feature/groups-main`
- `feature/groups-details`
- `feature/expenses-logic`
- `feature/notifications`
- `feature/offline-sync`

### Commit Messages

Use clear commit messages describing the changes.

Examples:

- `feat: add notifications screen`
- `fix: correct expense validation`
- `test: add debt calculation tests`
- `docs: update project documentation`
- `ci: configure Android build workflow`

### Pull Requests

- All changes must be merged into main through Pull Requests.
- Every PR must pass automated CI checks.
- Every PR requires approval from another team member who did not write or AI-generate the submitted code.
- Do not merge your own PR without the required independent review.
- Resolve review comments before merging.

## 6. AI Usage Policy

AI tools such as ChatGPT, Claude and GitHub Copilot are permitted.

However:

- Every significant AI contribution must be documented in the AI Dossier.
- This includes generated code, logic, architecture suggestions and meaningful code completions.
- AI-generated code must be reviewed and understood by the team member submitting it.
- Never accept AI-generated code without checking its correctness.
- Document AI mistakes in the Failure Log.
- The project must contain at least three real, documented AI failure cases.
- Never invent AI failure cases. Record actual mistakes encountered during development.

## 7. Testing and CI

Before submitting a Pull Request:

1. Build the project locally.
2. Run relevant unit tests.
3. Check that the application compiles.
4. Review your changes.
5. Push your branch and create a Pull Request.

The project uses GitHub Actions for automated build verification.

Local build command:

    ./gradlew build

## 8. Feature-Specific Requirements

### Notifications

- Implement the notification center and notification settings.
- Implement the repayment reminder screen.
- Sensitive financial information must not be displayed on the device lock screen.
- Review notification privacy before submitting changes.

### Offline Support

- Respect the local database and offline synchronization architecture.
- Avoid introducing network dependencies into UI components.

## 9. General Team Rules

- Keep changes within your assigned task whenever possible.
- Communicate before modifying shared architecture or configuration.
- Avoid modifying another person's feature branch.
- Keep the main branch stable.
- Document important architectural decisions.
