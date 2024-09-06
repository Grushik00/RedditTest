package com.example.reddittestapp.ui.theme

import android.widget.ImageView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.reddittestapp.RedditViewModel

@Composable
fun VerticalScreen(viewModel: RedditViewModel) {
    val posts = viewModel.posts.collectAsState()
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxWidth()
    ) {
        items(posts.value) { post ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.onPrimary)
                    .padding(8.dp)
            ) {
                Text(text = post.author)
                Text(text = post.subreddit)
                Text(text = post.title)
                if (!post.thumbnail.isNullOrEmpty() && post.thumbnail.contains(".jpg")) {
                    GlideImage(
                        imageUrl = post.thumbnail,
                        Modifier.size(width = 140.dp, height = 100.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
        item {
            AnimatedVisibility(
                visible = viewModel.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                    )
                {
                    CircularProgressIndicator(modifier = Modifier.size(40.dp))
                }
            }
        }
    }



    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size >= listState.layoutInfo.totalItemsCount-3
        }.collect { isEndOfList ->
            if (isEndOfList && !viewModel.isLoading) {
                viewModel.loadNextPage(after = viewModel.after)
            }
        }
    }
}

@Composable
fun GlideImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER
            }
        },
        modifier = modifier,
        update = { imageView ->
            Glide.with(imageView.context)
                .load(imageUrl)
                .apply(RequestOptions().centerCrop())
                .into(imageView)
        }
    )
}

