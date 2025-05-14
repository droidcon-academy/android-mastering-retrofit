package com.droidcon.droidflix.data.model

import androidx.annotation.StringRes
import com.droidcon.droidflix.R

enum class FlixNav(@StringRes val title: Int, val route: String) {
    Login(title = R.string.app_name, route = "Login"),
    FlixList(title = R.string.app_name, route = "FlixList"),
    FlixDetail(title = R.string.details, route = "FlixDetail/{id}"),
    FlixWatch(title = R.string.watch, route = "FlixWatch"),
    FlixEdit(title = R.string.edit, route = "FlixEdit");

    companion object {
        fun getScreenByRoute(route: String): FlixNav {
            return when (route) {
                Login.route -> Login
                FlixList.route -> FlixList
                FlixDetail.route -> FlixDetail
                FlixWatch.route -> FlixWatch
                FlixEdit.route -> FlixEdit
                else -> throw IllegalArgumentException("Unknown DroidFlix route: $route")
            }
        }
    }
}