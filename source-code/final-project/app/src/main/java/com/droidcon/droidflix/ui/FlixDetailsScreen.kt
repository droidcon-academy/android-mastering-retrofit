package com.droidcon.droidflix.ui

import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.droidcon.droidflix.R
import com.droidcon.droidflix.data.model.Flix
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL
import kotlin.math.absoluteValue


@Composable
fun FlixDetailsScreen(
    viewModel: FlixViewModel,
    id: String,
    onFlixWatchClicked: () -> Unit,
    onFlixEditClicked: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var pendingDownloadUrl by remember { mutableStateOf<String?>(null) }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("video/*"),
        onResult = { uri ->
            val url = pendingDownloadUrl
            if (uri != null && url != null) {
                scope.launch {
                    try {
                        val outputStream = context.contentResolver.openOutputStream(uri)
                            ?: throw IOException("Cannot open output stream")

                        viewModel.downloadFile(url, outputStream)

                        Toast.makeText(context, "Download complete", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Download failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
            pendingDownloadUrl = null
        }
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.getFlix(id)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val flix by viewModel.flix.collectAsState()
    val uiState = viewModel.uiState

    val onDownloadClicked = {
        pendingDownloadUrl = flix?.video
        createDocumentLauncher.launch("flix_video.mp4")
    }

    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            FlixDetailsLandscapeLayout(flix, uiState, onFlixWatchClicked, onDownloadClicked, onFlixEditClicked)
        }
        else -> {
            FlixDetailsPortraitLayout(flix, uiState, onFlixWatchClicked, onDownloadClicked, onFlixEditClicked)
        }
    }
}

@Composable
fun FlixDetailsPortraitLayout(
    flix: Flix?,
    uiState: FlixUiState,
    onFlixWatchClicked: () -> Unit,
    onFlixDownloadClicked: () -> Unit,
    onFlixEditClicked: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (image, title, year, card, loader, info, video) = createRefs()

        if (uiState is FlixUiState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .zIndex(1f)
                    .constrainAs(loader) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            )
        } else if (uiState is FlixUiState.ApiError || uiState is FlixUiState.Error) {
            val message = if (uiState is FlixUiState.ApiError){
                uiState.error?.error
            } else {
                (uiState as FlixUiState.Error).message
            }?: ""
            Text(
                text = message,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(info) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
            )
        } else if (flix == null) {
            Text(
                text = stringResource(id = R.string.empty),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(info) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }

        flix?.let {
            val texts = arrayListOf(Pair(stringResource(id = R.string.plot), flix.plot))
            texts.add(Pair(stringResource(id = R.string.ratings), flix.ratings.joinToString("\n\n")))

            AsyncImage(
                model = flix.poster,
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                        bottom.linkTo(title.top)
                        height = Dimension.fillToConstraints
                    },
            )

            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .constrainAs(title) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start, margin = 8.dp)
                        end.linkTo(parent.end, margin = 8.dp)
                    },
                textAlign = TextAlign.Center,
                text = flix.title,
                fontSize = 24.sp
            )

            Text(
                modifier = Modifier.constrainAs(year) {
                    top.linkTo(title.bottom)
                    start.linkTo(parent.start, margin = 8.dp)
                    end.linkTo(parent.end, margin = 8.dp)
                },
                text = flix.year,
                fontSize = 16.sp
            )

            if (!flix.video.isNullOrBlank()) {
                FlixActionButtons(
                    onDownloadClicked = onFlixDownloadClicked,
                    onWatchClicked = onFlixWatchClicked,
                    onEditClicked = onFlixEditClicked,
                    modifier = Modifier
                        .constrainAs(video) {
                            top.linkTo(year.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
            }

            CardPagerWithIndicator(modifier = Modifier
                .constrainAs(card) {
                    top.linkTo(video.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                },
                texts = texts
            )
        }
    }
}

@Composable
fun FlixDetailsLandscapeLayout(
    flix: Flix?,
    uiState: FlixUiState,
    onFlixWatchClicked: () -> Unit,
    onFlixDownloadClicked: () -> Unit,
    onFlixEditClicked: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (image, title, year, card, loader, info, video) = createRefs()

        if (uiState is FlixUiState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.constrainAs(loader) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            )
        } else if (uiState is FlixUiState.ApiError || uiState is FlixUiState.Error) {
            val message = if (uiState is FlixUiState.ApiError){
                uiState.error?.error
            } else {
                (uiState as FlixUiState.Error).message
            }?: ""
            Text(
                text = message,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(info) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
            )
        } else if (flix == null) {
            Text(
                text = stringResource(id = R.string.empty),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(info) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }

        flix?.let {
            val texts = arrayListOf(Pair(stringResource(id = R.string.plot), flix.plot))
            texts.add(Pair(stringResource(id = R.string.ratings), flix.ratings.joinToString("\n\n")))

            AsyncImage(
                model = flix.poster,
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(card.start)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    },
            )

            Text(
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top, margin = 8.dp)
                    bottom.linkTo(card.top, margin = 8.dp)
                    start.linkTo(image.end, margin = 8.dp)
                    end.linkTo(parent.end, margin = 8.dp)
                },
                text = flix.title,
                fontSize = 24.sp
            )

            Text(
                modifier = Modifier.constrainAs(year) {
                    top.linkTo(parent.top, margin = 8.dp)
                    start.linkTo(title.end, margin = 8.dp)
                },
                text = flix.year,
                fontSize = 16.sp
            )

            if (!flix.video.isNullOrBlank()) {
                FlixActionButtons(
                    onDownloadClicked = onFlixDownloadClicked,
                    onWatchClicked = onFlixWatchClicked,
                    onEditClicked = onFlixEditClicked,
                    modifier = Modifier
                        .constrainAs(video) {
                            top.linkTo(title.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
            }

            CardPagerWithIndicator(modifier = Modifier
                .constrainAs(card) {
                    top.linkTo(video.bottom, margin = 8.dp)
                    start.linkTo(image.end, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                },
                texts = texts
            )
        }
    }
}

@Composable
fun FlixActionButtons(
    onDownloadClicked: () -> Unit,
    onWatchClicked: () -> Unit,
    onEditClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(8.dp)
    ) {
        IconButton(onClick = onDownloadClicked) {
            Icon(
                imageVector = Icons.Filled.Download,
                contentDescription = "Download Video"
            )
        }
        IconButton(onClick = onWatchClicked) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Watch Video"
            )
        }
        IconButton(onClick = onEditClicked) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Flix"
            )
        }
    }
}

@Composable
fun CardPagerWithIndicator(modifier: Modifier, texts: ArrayList<Pair<String, String>>) {
    Box(modifier = modifier.fillMaxSize()) {
        val pageCount = texts.size
        val pagerState = rememberPagerState(pageCount = { pageCount })
        HorizontalPager(
            beyondViewportPageCount = 2,
            state = pagerState) {
            PagerItem(
                title = texts[it].first,
                text = texts[it].second,
                modifier = Modifier
                    .pagerFadeTransition(it, pagerState = pagerState)
                    .fillMaxSize()
            )
        }
        Row(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
    }
}

@Composable
fun PagerItem(modifier: Modifier, title: String, text: String) {
    Card(modifier = modifier) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontSize = 20.sp
        )
        Text(
            text = text,
            textAlign = TextAlign.Center,
            maxLines = 10,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp)
        )
    }
}

fun PagerState.calculateCurrentOffsetForPage(page: Int): Float {
    return (currentPage - page) + currentPageOffsetFraction
}

fun Modifier.pagerFadeTransition(page: Int, pagerState: PagerState) =
    graphicsLayer {
        val pageOffset = pagerState.calculateCurrentOffsetForPage(page)
        translationX = pageOffset * size.width
        alpha = 1- pageOffset.absoluteValue
    }