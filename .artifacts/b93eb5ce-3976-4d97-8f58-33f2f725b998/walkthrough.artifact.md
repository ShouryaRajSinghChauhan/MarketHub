# Walkthrough - Fixing API Prices to Realistic INR Values

Updated Room database version to trigger destructive migration, ensuring cached database items are cleared and re-synced with the new realistic INR pricing (`apiProduct.getPrice() * 85`).

## Changes

- **[AppDatabase.java](file:///C:/Users/Shourya/AndroidStudioProjects/MarketHub/app/src/main/java/com/example/markethub/database/AppDatabase.java)**: Incremented Room database version from `1` to `2` to force destructive migration on startup, clearing old USD-priced cached records and triggering a fresh sync with INR-scaled prices.

## Verification Results

### Automated Tests
- Ran `:app:assembleDebug` -> **BUILD SUCCESSFUL**
- Ran `:app:lintDebug` -> **BUILD SUCCESSFUL**
