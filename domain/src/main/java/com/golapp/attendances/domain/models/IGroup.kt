package com.golapp.attendances.domain.models

interface IGroup {
    val id: Int
    val name: String
    val days: String
    val explodeSchedules: String
    val fullScheduleGroup: String
    val fullGroup: String
    val playerCount: Int
}
