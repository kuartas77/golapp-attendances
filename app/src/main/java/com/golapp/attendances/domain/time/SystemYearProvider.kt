package com.golapp.attendances.domain.time

import javax.inject.Inject

class SystemYearProvider @Inject constructor() : YearProvider {
    override fun currentYear(): Int = java.time.LocalDate.now().year
}