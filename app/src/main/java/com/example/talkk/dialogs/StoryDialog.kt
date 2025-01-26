package com.example.talkk.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.example.talkk.AppState
import com.example.talkk.ChatViewModel
import com.example.talkk.Story
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun StoryDialog(
    appState: AppState,
    viewModel: ChatViewModel,
    story: Story,
    hideDialog: () -> Unit,
    deleteStory: (Int) -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { story.images.size }, initialPage = 0
    )

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val formatter = remember {
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    }

    Dialog(
        onDismissRequest = hideDialog,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.background.copy(
                    alpha = 0.7f
                )
            )
        ) {
            HorizontalPager(state = pagerState) {
                AsyncImage(modifier = Modifier
                    .clickable(interactionSource, indication = null) {}
                    .fillMaxSize(), model = story.images[it].imgUrl, contentDescription = null)
            }

            Column {
                Row(
                    modifier = Modifier.background(
                        Color.Black.copy(alpha = 0.6f),
                        RoundedCornerShape(16.dp)
                    )
                ) {
                    IconButton(onClick = { hideDialog.invoke() }) {
                        Icon(imageVector = Icons.Filled.ArrowBackIosNew, contentDescription = null)
                    }

                    AsyncImage(
                        model = story.ppUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(40.dp)
                    )

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = story.userName.toString() + ("You"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatter.format(story.images[pagerState.currentPage].time?.toString()),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Light),
                            color = Color.LightGray
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                    }
                }
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {

    }
}