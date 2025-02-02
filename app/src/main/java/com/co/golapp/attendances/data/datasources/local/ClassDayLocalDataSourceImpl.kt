package com.co.golapp.attendances.data.datasources.local

import com.co.golapp.attendances.data.local.dao.ClassDayDao
import com.co.golapp.attendances.data.local.datasources.ClassDayLocalDataSource
import com.co.golapp.attendances.data.local.models.ClassDayEntity
import com.co.golapp.attendances.data.mappers.asDomain
import javax.inject.Inject

class ClassDayLocalDataSourceImpl @Inject constructor(
    private val classDayDao: ClassDayDao
) : ClassDayLocalDataSource {
    override suspend fun insertClassDay(classDay: ClassDayEntity) =
        classDayDao.insertClassDay(classDay)

    override suspend fun insertClassDayList(classDayList: List<ClassDayEntity>) =
        classDayDao.insertClassDayList(classDayList)

    override suspend fun getClassDayById(classDayId: String) =
        classDayDao.getClassDayById(classDayId).asDomain()

    override suspend fun deleteClassDays() = classDayDao.deleteClassDays()
}
