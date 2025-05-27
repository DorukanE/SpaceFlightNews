package com.dorukaneskiceri.spaceflightnews.util

import androidx.navigation.NavBackStackEntry
import com.google.gson.Gson
import java.net.URLDecoder

inline fun <reified T> getNavigationArgument(
    navBackStackEntry: NavBackStackEntry,
    argumentKey: String
): T? {
    val argumentJson = navBackStackEntry.arguments?.getString(argumentKey)
    if (argumentJson != null) {
        try {
            val decodedJson = URLDecoder.decode(argumentJson, "UTF-8").replace("%23", "#")
            return Gson().fromJson(decodedJson, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return null
}