package com.golapp.attendances.domain.models

interface IClassDay {
    val classDayId: String
    val date: Int
    val day: String
    val month: Int
    val monthName: String
    val column: String
    val groupId: Int
    val schoolId: Int
}

