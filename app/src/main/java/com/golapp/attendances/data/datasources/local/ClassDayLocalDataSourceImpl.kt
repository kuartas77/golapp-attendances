package com.golapp.attendances.data.datasources.local

import com.golapp.attendances.data.local.dao.ClassDayDao
import com.golapp.attendances.data.local.datasources.ClassDayLocalDataSource
import com.golapp.attendances.data.local.models.ClassDayEntity
import com.golapp.attendances.data.mappers.asDomain
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
    override suspend fun deleteClassDaysByGroupId(groupId: Int) = classDayDao.deleteClassDaysByGroupId(groupId)
}
