package com.golapp.attendances.feature.attendances

data class CheckAttendance(
    val title: String,
    val value: String,
    val color: String = "#000000",
    val checked: Boolean = false
)

fun getListOfAttendance(): List<CheckAttendance> {
    return mutableListOf(
        CheckAttendance(title = "Asistencia", value = "1", color = "#008000"),
        CheckAttendance(title = "Falta", value = "2", color = "#FF0000"),
        CheckAttendance(title = "Excusa", value = "3", color = "#FFFF00"),
        CheckAttendance(title = "Retiro", value = "4", color = "#FF00FF"),
        CheckAttendance(title = "Incapacidad", value = "5", color = "#0000FF")
    )
}
