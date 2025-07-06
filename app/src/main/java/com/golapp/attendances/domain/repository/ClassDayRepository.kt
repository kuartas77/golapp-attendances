package com.golapp.attendances.domain.repository

import com.golapp.attendances.domain.models.ClassDay

interface ClassDayRepository {
    suspend fun getClassDayById(id: String): ClassDay
}
