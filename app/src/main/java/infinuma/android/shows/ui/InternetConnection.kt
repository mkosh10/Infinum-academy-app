package infinuma.android.shows.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

object InternetConnection {
     fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.e("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    Log.e("Internet", "WIFI TRUE")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.e("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    Log.e("Internet", "WIFI TRUE")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.e("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    Log.e("Internet", "WIFI TRUE")
                    return true
                }
            }
        }
        Log.e("Internet", "WIFI FALSE")
        return false
    }
}