<template>
  <main class="min-h-screen bg-mist px-5 py-10">
    <section class="mx-auto max-w-3xl rounded border border-black/10 bg-white p-6 shadow-sm">
      <p class="text-sm font-semibold text-trail">RunMarker</p>
      <h1 class="mt-2 text-3xl font-semibold text-ink">Privacy Policy</h1>
      <p class="mt-2 text-sm text-ink/55">Last updated: June 1, 2026</p>

      <div class="mt-6 space-y-5 text-sm leading-6 text-ink/70">
        <p>
          RunMarker helps users visualize their own running activities by city and country.
          This service is not affiliated with or endorsed by Strava.
        </p>

        <section>
          <h2 class="font-semibold text-ink">Data We Collect</h2>
          <p class="mt-1">
            We store the authenticated athlete identifier, basic profile fields, activity metadata,
            start coordinates, dates, distance, city/country information derived from reverse geocoding,
            and aggregated place statistics.
          </p>
        </section>

        <section>
          <h2 class="font-semibold text-ink">How We Use Data</h2>
          <p class="mt-1">
            Data is used only to show the connected user their own running markers, map markers,
            activity lists, and city/country statistics.
          </p>
        </section>

        <section>
          <h2 class="font-semibold text-ink">Tokens and Browser Storage</h2>
          <p class="mt-1">
            Access and refresh tokens are stored only on the backend and are not exposed to the browser.
            The browser may store a session cookie, language preference, and cached static assets.
          </p>
        </section>

        <section>
          <h2 class="font-semibold text-ink">Data Sharing</h2>
          <p class="mt-1">
            We do not sell user data. We use hosting, database, and geocoding infrastructure only to operate
            the service.
          </p>
        </section>

        <section>
          <h2 class="font-semibold text-ink">Contact</h2>
          <p class="mt-1">
            For privacy or deletion requests, contact the project owner through the published support channel
            or repository.
          </p>
        </section>
      </div>
    </section>
  </main>
</template>
