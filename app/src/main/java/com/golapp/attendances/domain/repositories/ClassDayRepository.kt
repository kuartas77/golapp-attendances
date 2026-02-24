package com.golapp.attendances.domain.repositories

import com.golapp.attendances.domain.models.ClassDay

interface ClassDayRepository {
    suspend fun getClassDayById(id: String): ClassDay
}