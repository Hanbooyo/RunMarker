# RunMarker

RunMarker is a personal running map application that helps athletes understand where they have run. It connects to a user's own Strava account, imports running activity metadata, and turns start locations into city and country markers.

The goal is simple: show a runner's personal running history as a map, statistics, and stamp-like place collection without adding social automation or modifying activities.

RunMarker is not affiliated with or endorsed by Strava.

## What It Does

- Connects a user's own Strava account through OAuth
- Imports the user's running activities
- Filters and stores running activities only
- Uses activity start coordinates to identify cities and countries
- Shows visited countries and cities as markers, lists, and summary statistics
- Displays total distance, activity counts, first run dates, and recent run dates by place
- Supports recent sync and full-history sync with progress feedback
- Provides Privacy Policy, Terms, and Data Deletion pages

## What It Does Not Do

- Does not automate kudos, likes, comments, follows, messages, or other social actions
- Does not modify, upload, or delete activities on Strava
- Does not access other athletes' data
- Does not expose access tokens or refresh tokens to the browser
- Does not provide public leaderboards or public athlete profiles

## Core Screens

- Login
- Dashboard
- Markers
- Countries
- Cities
- Activities
- Activity detail
- Sync
- Privacy Policy
- Terms of Use
- Data Deletion

## Technology Stack

### Backend

- Java 17
- Spring Boot 3
- Spring Security
- MyBatis
- PostgreSQL
- Flyway
- Gradle

### Frontend

- Vue 3
- Vite
- Pinia
- Vue Router
- Axios
- Tailwind CSS
- Leaflet

### Infrastructure

- Cloudflare Pages for the frontend
- Render for the backend API
- Neon PostgreSQL for the hosted database
- Nominatim reverse geocoding for initial city/country lookup

## Data Model Overview

RunMarker stores only the data needed to build the user's private running markers:

- User profile reference from OAuth
- Encrypted access and refresh tokens
- Imported activity metadata
- Start latitude and longitude
- Reverse-geocoded city, region, country, and country code
- Aggregated visited place statistics
- Sync logs and progress information

Activities are deduplicated by the source activity ID per user. City and country statistics are derived from imported activity data.

## Sync Approach

RunMarker imports activities through paginated API requests. Recent sync checks the latest activities for normal updates, while full-history sync reads pages until no more activity records are returned.

The sync process:

- Tracks request, saved, geocoded, and skipped counts
- Stores sync status in the database
- Polls progress from the frontend
- Avoids duplicate activity storage
- Caches reverse-geocoding results by rounded coordinates

## Privacy and Security Principles

- OAuth tokens are stored only on the backend
- Refresh tokens are encrypted at rest
- Browser clients receive only an application session, not source API tokens
- Users can delete stored RunMarker data from the Data Deletion page
- Data is shown only to the authenticated user
- State-changing API requests are protected by origin validation and CORS controls

## Current Scope

RunMarker currently focuses on the MVP path:

- Running activities
- Start-coordinate based city/country markers
- Country and city statistics
- Map markers
- Personal activity list and detail views

Future improvements may include richer route analysis using full activity paths or streams, but the current implementation intentionally avoids detailed route processing until the base product and API review are stable.
