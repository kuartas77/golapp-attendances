package com.golapp.attendances.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Shapes
import com.golapp.attendances.common.Constants.SHAPE_LARGE
import com.golapp.attendances.common.Constants.SHAPE_MEDIUM
import com.golapp.attendances.common.Constants.SHAPE_SMALL
import com.golapp.attendances.common.Constants.SHAPE_XLARGE


val shapes = Shapes(
    small = CutCornerShape(SHAPE_SMALL),
    medium = CutCornerShape(SHAPE_MEDIUM),
    large = CutCornerShape(SHAPE_LARGE),
    extraLarge = CutCornerShape(SHAPE_XLARGE)
)
