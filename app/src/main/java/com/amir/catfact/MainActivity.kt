package com.amir.catfact

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.amir.catfact.model.CatFacts
import com.amir.catfact.ui.theme.CatFactTheme
import com.amir.catfact.utils.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MainActivity : ComponentActivity() {
    private var fact = mutableStateOf(CatFacts())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CatFactTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            sendRequest()
                        },
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
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getRandomFact()
                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        fact.value = response.body()!!
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Http error ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "IO error ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Composable
    fun myui(modifier: Modifier = Modifier, fact: MutableState<CatFacts>) {
        val context = LocalContext.current
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
                modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4B397E)
                )
            ) {
                Column(modifier.padding(16.dp)) {
                    Text(
                        "Did you know ?",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.pixel))
                    )

                    Spacer(modifier.height(20.dp))

                    Text(
                        fact.value.fact,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 35.sp,
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.pixel))
                    )
                }
            }

            Button(
                { sendRequest() },
                shape = RoundedCornerShape(100.dp),
                modifier = modifier.fillMaxWidth().padding(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEEA2B8)
                )
            ) {
                Text(
                    "refresh",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 35.sp,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.pixel))
                )
            }

            Column(
                    modifier.fillMaxSize(),
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
