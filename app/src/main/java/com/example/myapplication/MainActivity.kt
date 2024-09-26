package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.manway.mylibrary.RDatePicker
import com.manway.mylibrary.RNumPadInt
import com.manway.mylibrary.RNumPadListener
import com.manway.mylibrary.dateChangeListener
import com.manway.mylibrary.rememberRDatePickerState
import com.manway.mylibrary.toPassword
import java.util.Date

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {  innerPadding ->
                    val dateState= rememberRDatePickerState(arrayListOf(), Date())
                    RDatePicker(dateState,Modifier.clip(MaterialTheme.shapes.extraLarge))
                    dateState.dateChangeListener {
                        Log.e("Update3",it.toString())
                    }
                }
            }
        }
    }
}




@Preview
@Composable
public fun password4Pad(){
        var text by remember {
            mutableStateOf("")
        }
    RNumPadInt({ Text(text.toPassword(), fontSize =35.sp, color = Color.LightGray.copy(0.5f), letterSpacing = 15.sp) },"Next",object :
        RNumPadListener {
        override fun onNumPressed(n: Int, number: Int?, string: String) {
                text=string
        }

        override fun onBackSpacePressed(number: Int?, string: String) {
                        text=string
        }

        override fun onNextPressed(number: Int?) {

        }
    }, digitsAllowed = 4, modifier = Modifier.padding(15.dp))
}

