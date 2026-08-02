package com.golapp.attendances.core.common

import androidx.compose.ui.unit.dp

object Constants {
    const val BD_NAME = "golapp_database"
    const val PREFERENCES_NAME = "golapp_preference"

    const val TOKEN = "access_token"
    const val REFRESH_TOKEN = "refresh_token"
    const val TYPE = "token_type"
    const val EXPIRATION = "expiration"
    const val USERNAME = "username"
    const val SCHOOL_ID = "school_id"
    const val SCHOOL_NAME = "school_name"
    const val SCHOOL_SLUG = "school_slug"
    const val SCHOOL_LOGO = "school_logo"

    const val AUTH = "login"
    const val REFRESH = "refresh-token"
    const val CHECK = "check"
    const val GROUPS = "instructor/training_groups"
    const val ATTENDANCES = "instructor/attendances"
    const val UPDATE_ATTENDANCE = "instructor/attendances/upsert"
    const val STATISTICS = "instructor/statistics/groups"

    val SHAPE_SMALL = 4.dp
    val SHAPE_MEDIUM = 8.dp
    val SHAPE_LARGE = 16.dp
    val SHAPE_XLARGE = 32.dp

    val SPACER_SMALL = 2.dp
    val SPACER_MEDIUM = 4.dp
    val SPACER_LARGE = 8.dp
    val SPACER_MEDIUM_LARGE = 12.dp
    val SPACER_XLARGE = 16.dp
}