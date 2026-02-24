package com.golapp.attendances.data.repositories

import com.golapp.attendances.data.local.database.daos.ClassDayDao
import com.golapp.attendances.data.mappers.toDomain
import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.repositories.ClassDayRepository
import javax.inject.Inject

class ClassDayRepositoryImpl @Inject constructor(
    private val classDayDao: ClassDayDao
) : ClassDayRepository {
    override suspend fun getClassDayById(id: String): ClassDay =
        classDayDao.getClassDayById(id).toDomain()
}