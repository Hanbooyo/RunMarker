<template>
  <main class="min-h-screen bg-mist px-5 py-10">
    <section class="mx-auto max-w-3xl rounded border border-black/10 bg-white p-6 shadow-sm">
      <p class="text-sm font-semibold text-trail">RunMarker</p>
      <h1 class="mt-2 text-3xl font-semibold text-ink">Data Deletion</h1>
      <p class="mt-2 text-sm text-ink/55">Last updated: June 1, 2026</p>

      <div class="mt-6 space-y-5 text-sm leading-6 text-ink/70">
        <p>
          Users can revoke app access from their connected activity account settings at any time. After access is
          revoked, RunMarker can no longer refresh tokens or import new activity data.
        </p>

        <section>
          <h2 class="font-semibold text-ink">Request Deletion</h2>
          <p class="mt-1">
            To request deletion of stored profile, activity, token, and place data, contact the project owner through
            the published support channel or repository and include the account identifier or email used for support.
          </p>
        </section>

        <section>
          <h2 class="font-semibold text-ink">What We Delete</h2>
          <p class="mt-1">
            We delete stored user records, encrypted tokens, imported activity metadata, derived city/country data,
            sync logs, and aggregate marker records associated with the user.
          </p>
        </section>

        <section>
          <h2 class="font-semibold text-ink">Planned Self-Service Flow</h2>
          <p class="mt-1">
            A self-service account deletion button is planned for a future release. Until then, deletion requests are
            handled manually.
          </p>
        </section>
      </div>
    </section>
  </main>
</template>
