import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.amir.catfact.R
import com.amir.catfact.model.CatFacts
import com.amir.catfact.ui.theme.CatFactTheme
import com.amir.catfact.utils.RetrofitInstance
import com.amir.catfact.utils.StreakManager
import com.amir.catfact.widget.CatFactWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MainActivity : ComponentActivity() {
    private var fact = mutableStateOf(CatFacts())
    private var isLoading = mutableStateOf(false)
    private var isError = mutableStateOf(false)
    private var errorMessage = mutableStateOf("")
    private var streakState = mutableStateOf(StreakManager.StreakState(0, isWarningState = false, isExtinguished = false))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        streakState.value = StreakManager.getStreakState(applicationContext)

        setContent {
            CatFactTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF6650a4)
                ) {
                    LaunchedEffect(Unit) {
                        sendRequest()
                    }

                    myui(fact = fact)
                }
            }
        }
    }

    private fun sendRequest() {
        if (isLoading.value) return
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                isLoading.value = true
                isError.value = false
            }
            try {
                val response = RetrofitInstance.api.getRandomFact()
                if (response.isSuccessful && response.body() != null) {
                    val updatedStreak = StreakManager.recordFactRead(applicationContext)
                    withContext(Dispatchers.Main) {
                        fact.value = response.body()!!
                        isError.value = false
                        streakState.value = updatedStreak
                        notifyWidgetUpdate()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isError.value = true
                        errorMessage.value = "Error ${response.code()}: ${response.message()}"
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    isError.value = true
                    errorMessage.value = "HTTP error: ${e.message}"
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    isError.value = true
                    errorMessage.value = "Connection error. Check your internet."
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                }
            }
        }
    }

    private fun notifyWidgetUpdate() {
        val widgetIntent = Intent(applicationContext, CatFactWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val ids = AppWidgetManager.getInstance(applicationContext).getAppWidgetIds(
            ComponentName(applicationContext, CatFactWidget::class.java)
        )
        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(widgetIntent)
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun myui(modifier: Modifier = Modifier, fact: MutableState<CatFacts>) {
        val context = LocalContext.current
        val clipboardManager = LocalClipboardManager.current
        val imageLoader = remember {
            ImageLoader.Builder(context)
                .components {
                    add(ImageDecoderDecoder.Factory())
                }
                .build()
        }

        Column(
            modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {

            // Streak Badge
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = Color(0xFF4B397E),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(R.drawable.streak)
                            .build(),
                        imageLoader = imageLoader,
                        contentDescription = "Streak GIF",
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val streakText = if (streakState.value.isExtinguished) {
                        "STREAK OFF (0 DAYS)"
                    } else {
                        "STREAK: ${streakState.value.count} DAYS"
                    }
                    Text(
                        text = streakText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (streakState.value.isExtinguished) Color(0xFFEEA2B8) else Color.White,
                        fontFamily = FontFamily(Font(R.font.pixel))
                    )
                }
            }

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(R.drawable.cargif)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "Cute animated cat",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4B397E)
                )
            ) {
                if (isError.value) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Oops! 😿",
                            fontSize = 20.sp,
                            color = Color(0xFFEEA2B8),
                            fontFamily = FontFamily(Font(R.font.pixel))
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            errorMessage.value,
                            fontSize = 11.sp,
                            color = Color.White,
                            lineHeight = 22.sp,
                            fontFamily = FontFamily(Font(R.font.pixel))
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { sendRequest() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEA2B8)),
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Text(
                                "Try Again 🔄",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontFamily = FontFamily(Font(R.font.pixel))
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onLongClick = {
                                    if (fact.value.fact.isNotEmpty()) {
                                        clipboardManager.setText(AnnotatedString(fact.value.fact))
                                        Toast.makeText(context, "Copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onClick = { }
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            "Did you know ?",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.pixel))
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        AnimatedContent(
                            targetState = fact.value.fact,
                            transitionSpec = {
                                (fadeIn() + slideInVertically { it / 2 }) togetherWith
                                        (fadeOut() + slideOutVertically { -it / 2 })
                            },
                            label = "CatFactAnimation"
                        ) { targetFact ->
                            Text(
                                text = targetFact,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 35.sp,
                                color = Color.White,
                                fontFamily = FontFamily(Font(R.font.pixel))
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            "📋 Long press to copy",
                            fontSize = 8.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            fontFamily = FontFamily(Font(R.font.pixel))
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { sendRequest() },
                    enabled = !isLoading.value,
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEEA2B8)
                    )
                ) {
                    if (isLoading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "refresh",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.pixel))
                        )
                    }
                }

                Button(
                    onClick = {
                        if (fact.value.fact.isNotEmpty()) {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "🐱 Cat Fact:\n\n${fact.value.fact}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Cat Fact"))
                        }
                    },
                    enabled = fact.value.fact.isNotEmpty() && !isLoading.value,
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier.height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4B397E)
                    )
                ) {
                    Text(
                        "share ↗",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.pixel))
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                CatWithSpeechBubble()
            }
        }
    }

    @Composable
    fun CatWithSpeechBubble(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        var isBubbleVisible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            while (true) {
                delay(5000)
                isBubbleVisible = true
                delay(4000)
                isBubbleVisible = false
            }
        }

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            AnimatedVisibility(
                visible = isBubbleVisible,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 2.dp
                    ),
                    color = Color(0xFF4B397E),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/cactus-hd/CatFact_compose")
                            )
                            context.startActivity(intent)
                        }
                ) {
                    Text(
                        text = "If you liked it, give us a star on GitHub! ⭐",
                        fontSize = 10.sp,
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.pixel)),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            Image(
                painter = painterResource(R.drawable.catbox),
                contentDescription = "catbox",
                modifier = Modifier
                    .height(80.dp)
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/cactus-hd/CatFact_compose")
                        )
                        context.startActivity(intent)
                    }
            )
        }
    }
}
