package com.golapp.attendances.ui.screens.attendances

data class CheckAttendance(
    val title: String,
    val value: String,
    val color: String = "#000000",
    val checked: Boolean = false
)

fun getListOfAttendance(): List<CheckAttendance> {
    return mutableListOf(
        CheckAttendance(title = "Asistencia", value = "as", color = "#008000"),
        CheckAttendance(title = "Falta", value = "fa", color = "#FF0000"),
        CheckAttendance(title = "Excusa", value = "ex", color = "#FFFF00"),
        CheckAttendance(title = "Retiro", value = "re", color = "#FF00FF"),
        CheckAttendance(title = "Incapacidad", value = "in", color = "#0000FF")
    )
}
