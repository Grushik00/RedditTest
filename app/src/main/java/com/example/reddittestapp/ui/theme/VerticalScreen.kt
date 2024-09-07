package com.example.reddittestapp.ui.theme

import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.reddittestapp.RedditViewModel
import java.time.Instant
import java.time.temporal.ChronoUnit


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VerticalScreen(viewModel: RedditViewModel) {
    val posts = viewModel.posts.collectAsState()
    val scrollState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }
    var expandedImageUrl by rememberSaveable { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LazyColumn(
        state = scrollState, modifier = Modifier
            .padding(12.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(posts.value) { post ->
            Column(
                Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .padding(12.dp)

                    .then(
                        if (isLandscape) {
                            Modifier.width(500.dp)
                        } else {
                            Modifier.fillMaxWidth()
                        }
                    )
            ) {
                Row(
                    Modifier.height(100.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        Modifier
                            .weight(1f)
                    ) {
                        Text(
                            getRelativeTime(post.created_utc),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Text(text = post.author)
                        Text(text = "Comments: ${post.num_comments}")
                    }
//                    Spacer(modifier = Modifier.width(70.dp))
                    if (!post.thumbnail.isNullOrEmpty() && post.thumbnail.contains(".jpg")) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .weight(
                                    0.9f,
                                    fill = false
                                )
                        ) {
                            GlideImage(
                                imageUrl = post.thumbnail,
                                Modifier.clickable { expandedImageUrl = post.thumbnail }
                                    .size(width = 150.dp, height = 100.dp)
                            )
                        }
                    }
                }
                Text(text = post.title)
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
        item {
            AnimatedVisibility(
                visible = viewModel.isLoading, enter = fadeIn(), exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(40.dp))
                }
            }
        }

    }
    if (expandedImageUrl != null) {
        FullScreenImage(expandedImageUrl!!, context) {
            expandedImageUrl = null
        }
    }

    LaunchedEffect(scrollState) {
        snapshotFlow {
            scrollState.firstVisibleItemIndex + scrollState.layoutInfo.visibleItemsInfo.size >= scrollState.layoutInfo.totalItemsCount - 7
        }.collect { isEndOfList ->
            if (isEndOfList && !viewModel.isLoading) {
                viewModel.loadNextPage(after = viewModel.after)
            }
        }
    }
}

//Форматирование времени
@RequiresApi(Build.VERSION_CODES.O)
fun getRelativeTime(createdUtc: Long): String {
    val postTime = Instant.ofEpochSecond(createdUtc)
    val currentTime = Instant.now()

    val hoursAgo = ChronoUnit.HOURS.between(postTime, currentTime)
    val daysAgo = ChronoUnit.DAYS.between(postTime, currentTime)

    return when {
        hoursAgo < 24 -> "$hoursAgo hours ago"
        daysAgo == 1L -> "Yesterday"
        else -> "$daysAgo days ago"
    }
}

//Отображение изображения из url
@Composable
fun GlideImage(
    imageUrl: String, modifier: Modifier = Modifier
) {
    AndroidView(factory = { context ->
        ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER
        }
    }, modifier = modifier, update = { imageView ->
        Glide.with(imageView.context).load(imageUrl).apply(RequestOptions().centerCrop())
            .into(imageView)
    })
}

// Сохранение изображения в галерею
fun saveImageToGallery(imageUrl: String, context: Context) {
    Glide.with(context).asBitmap().load(imageUrl).into(object : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            val contentValues = ContentValues().apply {
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    "image_${System.currentTimeMillis()}.jpg"
                )
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
            )

            uri?.let {
                val outputStream = context.contentResolver.openOutputStream(it)
                outputStream?.use {
                    resource.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onLoadCleared(placeholder: Drawable?) {}
    })
}

//Открытие изображения на весь экран
@Composable
fun FullScreenImage(imageUrl: String, context: Context, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false) // Полный экран
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { onDismiss() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = { saveImageToGallery(imageUrl, context) }) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = "Save Image",
                            tint = Color.White
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GlideImage(
                        imageUrl = imageUrl,
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 11f, true)
                    )
                }
            }
        }
    }
}