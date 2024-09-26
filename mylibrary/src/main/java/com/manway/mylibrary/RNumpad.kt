package com.manway.mylibrary
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**In this numpad numbers Only 10 Digits Available.
 * Reason Integer allows 10 digit Only extend 10 digit that will become Exception
 * **/
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RNumPadInt(titleContent:@Composable ()->Unit, nextButtonText:String, numpadListener: RNumPadListener, numPadColor: Color=Color(0xFF1DE9B6), digitsAllowed:Int=10, modifier: Modifier=Modifier){
    var number by remember {
        mutableStateOf<StringBuffer>(StringBuffer(""))
    }

    if(digitsAllowed>10) throw NumberFormatException("Only Till 10 digits Allowed")

    val numClick:(Int)->Unit ={n->

         if(number.length<digitsAllowed) {
             number = number.apply {
                 append(n)
             }
             numpadListener.onNumPressed(n, number.toString().toInt(), number.toString())
         }
    }

    @Composable
    fun numButton(n:Int){
        val shape: Shape = MaterialTheme.shapes.medium
        Button(onClick = {
                    numClick(n)
        }, modifier = Modifier.width(75.dp).fillMaxHeight().absoluteOffset(x = 10.dp).border(0.7.dp, if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray, shape), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = if(isSystemInDarkTheme()) darkColorScheme().primary else lightColorScheme().primary), shape = shape) {
            Text(text ="$n", fontWeight = FontWeight.W300)
        }

    }

    Spacer(modifier = Modifier.height(25.dp))
    var off=50

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        titleContent()
        Spacer(modifier = Modifier.fillMaxWidth().height(25.dp))
        Row(modifier = Modifier.width(350.dp).height(100.dp).background(color = Color.Transparent)) {

            numButton(n = 1)
            Spacer(modifier = Modifier.fillMaxHeight().width(off.dp))
            numButton(n = 2)
            Spacer(modifier = Modifier.fillMaxHeight().width(off.dp))
            numButton(n = 3)

        }
        Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

        Row(modifier = Modifier.width(350.dp).height(100.dp).background(color = Color.Transparent)) {
            numButton(n = 4)
            Spacer(modifier = Modifier.fillMaxHeight().width(off.dp))
            numButton(n = 5)
            Spacer(modifier = Modifier.fillMaxHeight().width(off.dp))
            numButton(n = 6)

        }

        Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

        Row(modifier = Modifier.width(350.dp).height(100.dp).background(color = Color.Transparent)) {
            numButton(n = 7)
            Spacer(modifier = Modifier.fillMaxHeight().width(off.dp))
            numButton(n = 8)
            Spacer(modifier = Modifier.fillMaxHeight().width(off.dp))
            numButton(n = 9)

        }

        Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

        Row (modifier = Modifier.absoluteOffset(x=-12.dp).height(100.dp).background(color = Color.Transparent), horizontalArrangement = Arrangement.Center){

            val shape: Shape = RoundedCornerShape(10.dp)
            TextButton (onClick = {
                try { number=number.apply { deleteCharAt(number.length-1) } }catch (e:Exception){ }
                if(number.isEmpty()) number=StringBuffer("")
                numpadListener.onBackSpacePressed(number.toString().emptyReturn("0").toInt(),number.toString())
            }, modifier = Modifier.clip(RoundedCornerShape(10.dp)).width(120.dp).fillMaxHeight().absoluteOffset(x = 18.dp), colors = ButtonDefaults.buttonColors(containerColor =Color(0xFF2979FF).copy(0.70f), contentColor =if(isSystemInDarkTheme()) darkColorScheme().primary else lightColorScheme().primary), shape = shape) {
                Text(text ="BackSpace", color = Color(0xFF304FFE), fontSize =11.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.W500,modifier=Modifier.absoluteOffset(x=-5.dp).combinedClickable( onLongClick = {
                    number=StringBuffer("")
                    numpadListener.onBackSpacePressed(number.toString().emptyReturn("0").toInt(),number.toString())
                }) {
                    try { number=number.apply { deleteCharAt(number.length-1) } }catch (e:Exception){ }
                    if(number.isEmpty()) number=StringBuffer("")
                    numpadListener.onBackSpacePressed(number.toString().emptyReturn("0").toInt(),number.toString())
                })
            }


            Spacer(modifier = Modifier.fillMaxHeight().width(15.dp))
            numButton(n = 0)
            Spacer(modifier = Modifier.fillMaxHeight().width(20.dp))


           TextButton (onClick = {
                numpadListener.onNextPressed(number.toString().emptyReturn("0").toInt())
            }, modifier = Modifier.clip(RoundedCornerShape(10.dp)).width(110.dp).fillMaxHeight().absoluteOffset(x = 10.dp), colors = ButtonDefaults.buttonColors(containerColor =numPadColor, contentColor =if(isSystemInDarkTheme()) darkColorScheme().primary else lightColorScheme().primary), shape = shape) { Text(text =nextButtonText, color = Color(0xFF2979FF), fontSize =11.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.W500)
            }

        }

    }
    Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
}

interface RNumPadListener{
    fun onNumPressed(n:Int,number:Int?,string: String)
    fun onBackSpacePressed(number: Int?,string: String)
    fun onNextPressed(number: Int?)
}