package com.golapp.attendances.domain.models

interface IPlayer {
    val playerId: Int
    val groupId: Int
    val uniqueCode: String
    val names: String
    val lastNames: String
    val category: String
    val fullNames: String
    val photoUrl: String
    val inscriptionId: Int
}
