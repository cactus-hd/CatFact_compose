package com.amir.catfact

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amir.catfact.model.CatFacts
import com.amir.catfact.ui.theme.CatFactTheme
import com.amir.catfact.utils.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MainActivity : ComponentActivity() {
    private var fact=mutableStateOf(CatFacts())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //https://catfact.ninja/fact
            CatFactTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                        .clickable{
                            sendRequest()
                        },
                    color = MaterialTheme.colorScheme.background
                ) {
                    sendRequest()

//                    val context = LocalContext.current
//
//                    var fact by remember {
//                        mutableStateOf(CatFacts())
//                    }
//
//                    val scope = rememberCoroutineScope()
//                    LaunchedEffect(key1 = true) {
//                        scope.launch(Dispatchers.IO) {
//

//                    }

                    myui(fact =fact)

                }
            }
        }
    }

    private fun sendRequest() {
        GlobalScope.launch(Dispatchers.IO) {

            val response=try {

                RetrofitInstance.api.getRandomFact()

            }catch (e: HttpException){

                Toast.makeText(applicationContext,"Http error ${e.message}", Toast.LENGTH_SHORT).show()
                return@launch
            }catch (e: IOException){

                Toast.makeText(applicationContext,"IO error ${e.message}", Toast.LENGTH_SHORT).show()
                return@launch
            }
            if (response.isSuccessful && response.body() != null){
                withContext(Dispatchers.Main){
                    fact.value=response.body()!!
                }
            }

        }

        }
    }


@Composable
fun myui(modifier: Modifier = Modifier, fact: MutableState<CatFacts>) {

    Column(
        modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Cat Facts:", fontSize = 26.sp)

        Spacer(modifier.height(25.dp))

        Text(fact.value.fact, fontSize = 26.sp, fontWeight = FontWeight.Bold, lineHeight = 40.sp)


    }

}
