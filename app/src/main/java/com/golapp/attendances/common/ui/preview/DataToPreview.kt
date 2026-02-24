package com.golapp.attendances.common.ui.preview

import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.models.Group
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.models.Player


fun groupClassPreview(): Group {
    return Group(
        id = 32,
        name = "Grupo de prueba",
        days = "Miércoles,Viernes",
        explodeSchedules = "10:05AM - 11:00AM, 11:05AM - 12:00M, 12:05AM - 01:00PM",
        fullScheduleGroup = "Grupo de prueba Marte 1 (2008) Miércoles,Viernes 10:05AM - 11:00AM, 11:05AM - 12:00M, 12:05AM - 01:00PM",
        fullGroup = "Grupo de prueba Marte 1 (2008)",
        playerCount = 1,
    )
}

fun groupWithClassPreview(): GroupWithClassDays {
    return GroupWithClassDays(
        group = Group(
            id = 32,
            name = "Grupo de prueba",
            days = "Miércoles,Viernes",
            explodeSchedules = "10:05AM - 11:00AM, 11:05AM - 12:00M, 12:05AM - 01:00PM",
            fullScheduleGroup = "Grupo de prueba Marte 1 (2008) Miércoles,Viernes 10:05AM - 11:00AM, 11:05AM - 12:00M, 12:05AM - 01:00PM",
            fullGroup = "Grupo de prueba Marte 1 (2008)",
            playerCount = 1,
        ),
        classDays = listOf(
            ClassDay(
                classDayId = "3294",
                date = 4,
                day = "Miércoles",
                month = 9,
                monthName = "Septiembre",
                column = "assistance_one",
                groupId = 32,
                schoolId = 2
            ),
            ClassDay(
                classDayId = "3296",
                date = 6,
                day = "Viernes",
                month = 9,
                monthName = "Septiembre",
                column = "assistance_two",
                groupId = 32,
                schoolId = 2
            ),
            ClassDay(
                classDayId = "32911",
                date = 11,
                day = "Miércoles",
                month = 9,
                monthName = "Septiembre",
                column = "assistance_three",
                groupId = 32,
                schoolId = 2
            ),
            ClassDay(
                classDayId = "32913",
                date = 13,
                day = "Viernes",
                month = 9,
                monthName = "Septiembre",
                column = "assistance_four",
                groupId = 32,
                schoolId = 2
            ),
            ClassDay(
                classDayId = "32918",
                date = 18,
                day = "Miércoles",
                month = 9,
                monthName = "Septiembre",
                column = "assistance_five",
                groupId = 32,
                schoolId = 2
            ),
            ClassDay(
                classDayId = "32920",
                date = 20,
                day = "Viernes",
                month = 9,
                monthName = "Septiembre",
                column = "assistance_six",
                groupId = 32,
                schoolId = 2
            ),
            ClassDay(
                classDayId = "32925",
                date = 25,
                day = "Miércoles",
                month = 9,
                monthName = "Septiembre",
                column = "assistance_seven",
                groupId = 32,
                schoolId = 2
            ),
            ClassDay(
                classDayId = "32927",
                date = 27,
                day = "Viernes",
                month = 9,
                monthName = "Septiembre",
                column = "assistance_eight",
                groupId = 32,
                schoolId = 2
            )
        )
    )
}

fun attendanceWithPlayerPreview(): AttendanceWithPlayer {
    return AttendanceWithPlayer(
        id = 1,
        attendanceId = null,
        schoolId = 2,
        trainingGroupId = 30,
        inscriptionId = 23,
        year = 2024,
        month = 10,
        column = "assistance_one",
        value = null,
        playerId = 102,
        player = Player(
            playerId = 102,
            groupId = 11,
            uniqueCode = "1098023456",
            names = "JOSE ALEJANDRO",
            lastNames = "CARDONA VANEGAS",
            category = "SUB-16",
            fullNames = "JOSE ALEJANDRO CARDONA VANEGAS",
            photoUrl = "http://10.0.2.2/dinamyc/user.png",
            inscriptionId = 23
        )
    )
}