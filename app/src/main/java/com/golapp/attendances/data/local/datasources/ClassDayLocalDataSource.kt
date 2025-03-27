package com.golapp.attendances.data.local.datasources

import com.golapp.attendances.data.local.models.ClassDayEntity
import com.golapp.attendances.domain.models.ClassDay

interface ClassDayLocalDataSource {
    suspend fun insertClassDay(classDay: ClassDayEntity)
    suspend fun insertClassDayList(classDayList: List<ClassDayEntity>)
    suspend fun getClassDayById(classDayId: String): ClassDay
    suspend fun deleteClassDays()
}
