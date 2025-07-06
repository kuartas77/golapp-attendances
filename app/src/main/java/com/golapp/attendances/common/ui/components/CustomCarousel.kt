package com.golapp.attendances.common.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomCarousel(
    modifier: Modifier = Modifier,
    items: List<CarouselItem> = emptyList()
) {
    var showDialog by remember { mutableStateOf(false) }
    var itemToShow by remember { mutableStateOf(CarouselItem(0, 0, 0)) }
    var animateIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { animateIn = true }

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { items.count() },
        modifier = modifier
            .fillMaxWidth()
            .height(221.dp),
        preferredItemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        val item = items[i]
        Image(
            modifier = Modifier
                .height(205.dp)
                .clickable(
                    onClick = {
                        showDialog = !showDialog
                        itemToShow = item
                    }
                )
                .maskClip(MaterialTheme.shapes.medium),
            painter = painterResource(id = item.imageResId),
            contentDescription = stringResource(item.contentDescriptionResId),
            contentScale = ContentScale.Fit
        )
    }

    AnimatedVisibility(visible = animateIn && showDialog) {
        FullScreenDialog(
            item = itemToShow,
            showDialog = showDialog,
            onClose = { showDialog = false }
        )
    }
}

data class CarouselItem(
    val id: Int,
    @param:DrawableRes val imageResId: Int,
    @param:StringRes val contentDescriptionResId: Int
)

@Composable
fun FullScreenDialog(
    item: CarouselItem,
    showDialog: Boolean,
    onClose: () -> Unit
) {
    var showAnimatedDialog by remember { mutableStateOf(false) }

    LaunchedEffect(showDialog) {
        if (showDialog) showAnimatedDialog = true
    }

    if (showAnimatedDialog) {
        Dialog(
            properties = DialogProperties(
                dismissOnBackPress = true,
                usePlatformDefaultWidth = false,
                dismissOnClickOutside = true
            ),
            onDismissRequest = onClose
        ) {
            Surface(
                modifier = Modifier
                    .size(height = 450.dp, width = 350.dp)
                    .padding(8.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        modifier = Modifier
                            .wrapContentSize()
                            .clip(RoundedCornerShape(16.dp)),
                        painter = painterResource(id = item.imageResId),
                        contentDescription = stringResource(item.contentDescriptionResId),
                        contentScale = ContentScale.Fit
                    )
                }
                DisposableEffect(Unit) {
                    onDispose {
                        showAnimatedDialog = false
                    }
                }
            }
        }
    }
}
