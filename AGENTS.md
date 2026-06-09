# AGENTS.md

## Project

Build **CardioLog**, an Android app for logging and visualizing blood pressure measurements.

The app must support:

* Logging blood pressure measurements.
* Automatically saving the current date and time for each measurement.
* Allowing the user to manually change the measurement date and time.
* Nice daily and monthly blood pressure visualizations.
* Viewing measurements as a list, not only on charts.

## Tech stack

Use modern native Android:

* Kotlin
* Jetpack Compose
* Material 3
* Room for local persistence
* ViewModel + Kotlin Flow
* Navigation Compose
* Gradle Kotlin DSL if the project is new

Keep health data local on device. Do not add cloud sync or analytics unless explicitly requested.

## App name

Use the app name:

```text
CardioLog
```

Suggested package name:

```text
com.cardiolog.app
```

## Main screens

### 1. Add Measurement Screen

The user should be able to enter:

* Systolic pressure
* Diastolic pressure
* Pulse, optional but recommended
* Date and time
* Optional note

Behavior:

* Date and time should default to the current local date and time.
* The user must be able to edit the date and time manually.
* Validate input before saving:

  * systolic: reasonable numeric range, for example 50–300
  * diastolic: reasonable numeric range, for example 30–200
  * pulse: optional, but if entered, reasonable range, for example 30–250
* Show friendly validation errors.
* Save the record locally using Room.

### 2. Charts Screen

Show blood pressure visualization with two modes:

* Daily view
* Monthly view

The chart should display at least:

* Systolic pressure
* Diastolic pressure

Pulse can be displayed if it does not clutter the UI.

Requirements:

* The chart should be readable and visually pleasant.
* Use different visual styles for systolic and diastolic values.
* Daily view should show measurements grouped by day or within a selected day.
* Monthly view should show trends across days in a selected month.
* Allow switching between daily and monthly modes.
* Empty states should be handled gracefully.

Preferred implementation:

* Use a Compose-friendly chart library such as Vico, or implement a clean custom Compose Canvas chart if adding a dependency is not desirable.
* Keep chart logic separate from persistence logic.

### 3. Measurement List Screen

Show all measurements in a list.

Each row should show:

* Date and time
* Systolic / diastolic pressure
* Pulse if available
* Optional note preview

Requirements:

* Sort newest first by default.
* Allow deleting a measurement.
* Allow editing an existing measurement.
* The list must be accessible independently from the chart screen.

## Data model

Create a Room entity similar to:

```kotlin
@Entity(tableName = "blood_pressure_measurements")
data class BloodPressureMeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int?,
    val measuredAtMillis: Long,
    val note: String?,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)
```

Use `measuredAtMillis` for the actual measurement time.
Use `createdAtMillis` only for record creation metadata.

## Architecture

Use a simple clean structure:

```text
data/
  BloodPressureMeasurementEntity.kt
  BloodPressureDao.kt
  CardioLogDatabase.kt
  BloodPressureRepository.kt

ui/
  add/
  charts/
  list/
  components/

domain/
  BloodPressureMeasurement.kt
  ChartRange.kt
  Validation.kt
```

Use ViewModels for UI state.

Do not put database calls directly inside Composables.

## Navigation

Provide bottom navigation or another simple navigation pattern with:

* Add
* Charts
* List

The user should be able to move between these screens easily.

## UI expectations

Use Material 3.

The app should feel calm, medical, and trustworthy.

Use:

* Clear typography
* Large readable blood pressure values
* Good spacing
* Friendly empty states
* Proper light and dark theme support

Avoid clutter.

## Date and time

When creating a new measurement:

```kotlin
val defaultMeasuredAt = System.currentTimeMillis()
```

But expose date and time pickers so the user can change it.

Use local timezone formatting for display.

## Tests

Add tests where practical:

* Validation tests
* Repository or DAO tests
* ViewModel tests for adding and listing measurements

At minimum, make sure the project builds successfully.

## Commands

Before finishing, run the relevant checks:

```bash
./gradlew assembleDebug
./gradlew test
```

If instrumented tests exist:

```bash
./gradlew connectedAndroidTest
```

## Acceptance criteria

The implementation is complete when:

* A user can add a blood pressure measurement.
* The measurement time is automatically filled with the current date/time.
* The user can manually change the measurement date/time.
* Measurements are persisted locally.
* The user can see measurements on a daily chart.
* The user can see measurements on a monthly chart.
* The user can view measurements as a list.
* The user can edit and delete existing measurements.
* Empty states and validation errors are handled cleanly.
* The app builds successfully.

## Coding guidelines for Codex

* Inspect the existing project before making changes.
* Preserve existing architecture unless it conflicts with these requirements.
* Prefer small, focused commits/patches.
* Avoid unnecessary dependencies.
* Keep UI code readable and idiomatic Compose.
* Do not hardcode user-visible strings deep inside logic; prefer resources where appropriate.
* Do not add network permissions.
* Do not upload or share health data.
* Keep the implementation simple, stable, and maintainable.
