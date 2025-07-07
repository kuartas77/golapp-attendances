package com.golapp.attendances.data.repository

import com.golapp.attendances.data.local.datasources.ClassDayLocalDataSource
import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.repository.ClassDayRepository
import javax.inject.Inject

class ClassDayRepositoryImpl @Inject constructor(
    private val classDayLocalDataSource: ClassDayLocalDataSource
) : ClassDayRepository {
    override suspend fun getClassDayById(id: String): ClassDay =
        classDayLocalDataSource.getClassDayById(id)
}
