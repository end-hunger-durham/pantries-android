package org.endhungerdurham.pantries.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import org.endhungerdurham.pantries.backend.Pantry

fun startGoogleMapsIntent(pantry: Pantry, context: Context) {
    // Create URI + intent that searches for the pantry's address
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(StringBuilder()
            .append("geo:")
            .append(pantry.latitude)
            .append(',')
            .append(pantry.longitude)
            .append("?q=")
            .append(Uri.encode(pantry.address))
            .append(", ")
            .append(Uri.encode(pantry.city)).toString()
    ))

    // explicitly set Google Maps package
    intent.setPackage("com.google.android.apps.maps")

    // ensure we can run the Google Maps activity
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "", Toast.LENGTH_LONG).show()
    }
}