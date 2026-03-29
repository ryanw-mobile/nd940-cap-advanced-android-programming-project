# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug                        # Debug APK
./gradlew bundleRelease                        # Release AAB (requires signing env vars)
./gradlew :app:test                            # Unit tests (Robolectric)
./gradlew :app:connectedAndroidTest            # Instrumented tests (requires device/emulator)
./gradlew :app:detekt                          # Static analysis (max issues: 0)
./gradlew formatKotlin                         # Format Kotlin code (runs automatically pre-build)
./gradlew :app:jacocoTestReportDebug             # Code coverage report (JaCoCo)
```

**API key:** Debug builds require `CIVIC_API_KEY` as an environment variable or in `keystore.properties`.

## Architecture

MVVM + Clean Architecture with three layers:

- **`ui/`** — Fragments + ViewModels (Hilt-injected), XML data binding, Navigation Component. Compose dependencies are present but UI is currently XML-based.
- **`data/`** — Repository pattern with Room (local) and Retrofit/Moshi (remote). The repository interface abstracts both sources.
- **`domain/`** — Business logic, decoupled from Android framework.
- **`di/`** — Hilt modules: `DatabaseModule`, `AppModule` (DAOs), `DispatcherModule` (injects `@Main`, `@IO`, `@Default` coroutine dispatchers for testability).

Key patterns:
- Coroutines throughout; dispatchers are injected so tests can substitute `TestCoroutineDispatcher`.
- LiveData for UI-observable state from ViewModels.
- Navigation Safe Args for type-safe fragment arguments.
- Glide for image loading, Timber for logging.

## Testing

- **Unit tests** (`src/test/`): Robolectric-based, run on JVM. Use `CustomTestRunner` with `HiltTestApplication`.
- **Instrumented tests** (`src/androidTest/`): Espresso + UIAutomator. Managed device: Pixel 2 API 35 (AOSP-ATD).
- **Shared test utilities** live in `src/sharedTest/`.
- Coverage excludes DI modules, generated code, fragments, activities, and data binding classes.

## Code Quality

- **Detekt** config at `config/detekt/detekt.yml` — CI enforces zero issues.
- **Kotlinter** (ktlint) runs automatically via `preBuild`; run `./gradlew formatKotlin` to fix style issues before committing.
- `.editorconfig` uses `intellij_idea` code style; trailing commas enabled.

## CI/CD

GitHub Actions (`.github/workflows/`):
- `main_build.yml` — builds release pre-bundle, generates JaCoCo coverage, submits to Codacy on push/PR to main. Requires `CI=true` env var to activate release signing config.
- `tag_create_release.yml` — creates GitHub release with APK/AAB on `refs/tags/release/*` tags. Release notes pulled from `release_notes/` directory.
