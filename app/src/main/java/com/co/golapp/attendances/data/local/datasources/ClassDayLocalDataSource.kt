package com.co.golapp.attendances.data.local.datasources

import com.co.golapp.attendances.data.local.models.ClassDayEntity
import com.co.golapp.attendances.domain.models.ClassDay

interface ClassDayLocalDataSource {
    suspend fun insertClassDay(classDay: ClassDayEntity)
    suspend fun insertClassDayList(classDayList: List<ClassDayEntity>)
    suspend fun getClassDayById(classDayId: String): ClassDay
    suspend fun deleteClassDays()
}
