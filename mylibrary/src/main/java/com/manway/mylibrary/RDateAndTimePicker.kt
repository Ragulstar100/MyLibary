package com.manway.mylibrary


import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date
import java.util.regex.Pattern


@SuppressLint("SimpleDateFormat")
@Composable
private fun DateSelector(datePickerState: MutableState<RDatePickerState>, selectDateListener:(Date)->Unit){

    val dayNow = Date()

    val startDate = datePickerState.value.goDate.setRDate(1).currentTime()


    val daysList=listOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")
    val d = (0..41).map {
        if(startDate.currentTime()!=dayNow.currentTime())  startDate.addDate(it.toLong()) else dayNow.addDate(it.toLong())
    }

    Column {
        LazyVerticalGrid(GridCells.Fixed(7), Modifier.fillMaxWidth(0.87f)) {
            items(7) {
                Row {
                    Spacer(Modifier.width(10.dp))
                    if (datePickerState.value.selectMode != SelectionMode.column) Text(daysList.get(it).substring(0, 3), fontSize = 13.sp, fontWeight = FontWeight.W300)
                    else {
                        Text(daysList.get(it).substring(0, 3), fontSize = 13.sp, textAlign = TextAlign.Center , fontWeight = FontWeight.W300,
                            modifier = Modifier.size(35.dp).clickable {
                                val offset=if(it<dayNow.day)7+(it-dayNow.day) else it-dayNow.day
                                datePickerState.value = datePickerState select RDateRange(dayNow.addDate(offset.toLong()).currentTime(), dayNow.addDate(offset.toLong()).addMinute(1).currentTime(), 180)
                            }.border(1.dp, Color.LightGray, MaterialTheme.shapes.small).background(if(datePickerState.value.outputDates.filter { it.selectionMode== SelectionMode.column }.map { it.start.day }.contains(it)) Color.Blue else Color.Unspecified, MaterialTheme.shapes.small).padding(top=5.dp),
                        )
                    }
                }
            }

            items(d.count()) {
                val date = d[it].currentTime()
                val RDateRange= RDateRange(date, date.setTime(23,59), null)
                val bol=datePickerState.value.outputDates.filter { it.inRange(RDateRange.start)||it.inRange(RDateRange.end) }.isEmpty()
                Box(Modifier.padding(5.dp).clip(MaterialTheme.shapes.small).background(if (bol) Color.Transparent else MaterialTheme.colorScheme.secondaryContainer.copy(0.85f), MaterialTheme.shapes.small).border(1.25.dp, Color.Transparent, MaterialTheme.shapes.small).size(50.dp)
                    .clickable(enabled = datePickerState.value.selectMode != SelectionMode.column) {
                        datePickerState.value = datePickerState select RDateRange
                        selectDateListener(date)
                    }, contentAlignment = Alignment.Center
                ) {
                    Column(Modifier) {
                        Text(date.date.toString(), Modifier.fillMaxWidth().fillMaxHeight(0.65f).padding(top = 13.dp), textAlign = TextAlign.Center, fontWeight = if (date.currentTime() != Date().currentTime()) FontWeight.W300 else FontWeight.W500, color = if (date.currentTime() == Date().currentTime()) Color.Blue else if (date.month == datePickerState.value.goDate.month) Color.Unspecified else Color.LightGray)
                        val c = datePickerState.value.outputDates.filter { it.inRange(RDateRange.start)||it.inRange(RDateRange.end) }.size
                        Text(if (c <= 1) "" else c.toString(), fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

        }

    }
    Spacer(Modifier.height(15.dp))

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RDatePicker(datePickerState: MutableState<RDatePickerState>, modifier: Modifier=Modifier, onDatePickListener:(Date)->Unit={}){

        var selectionUnit by remember{ mutableStateOf(0) }

        //Options

        val (date, month, year) = listOf(0, 1, 2)

    val months = (0..11).map { index -> index to java.time.Month.of(index + 1).name }

        Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {

            Spacer(Modifier.height(15.dp))

            Text(datePickerState.value.goDate.year.plus(1900).toString(),Modifier.clickable { selectionUnit=if(selectionUnit==year) date else year })

            Row {
                AssistChip({ selectionUnit=if(selectionUnit==month) date else month }, modifier = Modifier.fillMaxWidth(0.50f), border = AssistChipDefaults.assistChipBorder(Color.DarkGray.copy(0.25f),Color.DarkGray.copy(0.25f),1.dp), shape = MaterialTheme.shapes.small, label = { Text(months.find { it.first == datePickerState.value.goDate.month }?.second ?: "", Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }, leadingIcon = {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "back",
                        modifier = Modifier.clickable {
                            datePickerState.value= datePickerState.value.copy(goDate = datePickerState.value.goDate.setRMonth(datePickerState.value.goDate.month-1));
                            if (datePickerState.value.goDate.month < 0) {
                                datePickerState.value= datePickerState.value.copy(goDate = datePickerState.value.goDate.setRMonth(12));
                            }
                        })
                }, trailingIcon = {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "front", modifier = Modifier.clickable {
                        datePickerState.value= datePickerState.value.copy(goDate = datePickerState.value.goDate.setRMonth(datePickerState.value.goDate.month+1)); if (datePickerState.value.goDate.month > 11) {
                        datePickerState.value= datePickerState.value.copy(goDate = datePickerState.value.goDate.setRMonth(1))
                    } })
                })
            }

            Spacer(Modifier.height(25.dp))

            when (selectionUnit) {
                month -> {
                    LazyVerticalGrid(GridCells.Fixed(3),Modifier.fillMaxWidth()) {
                        items(12){
                            Text(months[it].second,Modifier.padding(10.dp).clickable { datePickerState.value=datePickerState.value.copy( goDate = datePickerState.value.goDate.setRMonth(it) );Log.e("Update",datePickerState.value.goDate.month.toString()); selectionUnit=date }, color = if(datePickerState.value.goDate.month==it) Color.Blue else Color.Unspecified , fontWeight = FontWeight.W300)
                        }
                    }
                }
                year->{
                    LazyVerticalGrid(GridCells.Fixed(3), state = rememberLazyGridState(datePickerState.value.goDate.year+1900), modifier = Modifier.fillMaxHeight(0.90f).fillMaxWidth()) {
                        items(200){
                            Text((it+1924).toString(), color = if(datePickerState.value.goDate.year.plus(1900)==(it+1924)) Color.Blue else Color.Unspecified , modifier = Modifier.padding(10.dp).clickable { datePickerState.value=datePickerState.value.copy(goDate = datePickerState.value.goDate.setRYear(it.plus(24)));
                                selectionUnit=month }, fontWeight = FontWeight.W300)
                        }
                    }
                }

                else -> DateSelector(datePickerState) {
                    onDatePickListener(it);
                    datePickerState.value.dateChangeListener(it)
                    datePickerState.value=datePickerState.value.copy(goDate = it)
                }
            }

            if(datePickerState.value.datePickerOptions.enableGoto)  RDateInput(datePickerState.value.goDate,{ datePickerState.value=datePickerState.value.copy(goDate =it ) },Modifier.fillMaxWidth(0.58f))

        }


}

@Composable
fun rememberRDatePickerState(outputDates: ArrayList<RDateRange>, date: Date, selectMode: SelectionMode = SelectionMode.single, datePickerOptions: RDatePickerOptions = RDatePickerOptions()):MutableState<RDatePickerState>{
    return remember {
        mutableStateOf(RDatePickerState(outputDates,date,selectMode,datePickerOptions))
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun RDateInput(date: MutableState<Date>,modifier: Modifier=Modifier){
    var dateText by remember { mutableStateOf("") }
    var error by remember {
        mutableStateOf(false)
    }
    OutlinedTextField(value = if(error) dateText else SimpleDateFormat("dd/MM/yyyy").format(date.value), isError = error,
        modifier = modifier,
        onValueChange = {
            dateText=it
            val pattern= Pattern.compile("(\\d{2})/(\\d{2})/(\\d{4})")
            val matcher=pattern.matcher(it)
            if (it.matches(pattern.toRegex())) {
                while (matcher.find()) {
                    val mdate=matcher.group(1).toInt()
                    val month=matcher.group(2).toInt()
                    val year=matcher.group(3).toInt()
                    val maxDays=when(month){
                        1,3,5,7,8,10,12->31
                        2->if(year%4==0) 29 else 28
                        else->30
                    }
                    if(mdate in 1..maxDays){
                        if(month in 1..12) {
                            if(year in 1924..2124){
                                error=false
                                date.value=Date(year-1900,month-1,mdate)
                            }else{
                                error=true
                            }
                        }else{
                            error=true
                        }

                    }else{
                        error=true
                    }

                }

            }else error=true
        },
        label = { Text("Enter Date (DD/MM/YYYY)") },
    )
}

@SuppressLint("SimpleDateFormat")
@Composable
fun RDateInput(date: Date, dateChangeListener:(Date)->Unit, modifier: Modifier=Modifier){
    var dateText by remember { mutableStateOf("") }
    var error by remember {
        mutableStateOf(false)
    }
    OutlinedTextField(value = if(error) dateText else SimpleDateFormat("dd/MM/yyyy").format(date), isError = error, modifier = modifier,
        onValueChange = {
            dateText=it
            val pattern= Pattern.compile("(\\d{2})/(\\d{2})/(\\d{4})")
            val matcher=pattern.matcher(it)
            if (it.matches(pattern.toRegex())) {
                while (matcher.find()) {
                    val mdate=matcher.group(1).toInt()
                    val month=matcher.group(2).toInt()
                    val year=matcher.group(3).toInt()
                    val maxDays=when(month){
                        1,3,5,7,8,10,12->31
                        2->if(year%4==0) 29 else 28
                        else->30
                    }
                    if(mdate in 1..maxDays){
                        if(month in 1..12) {
                            if(year in 1924..2124){
                                error=false
                                dateChangeListener(Date(year-1900,month-1,mdate))
                            }else{
                                error=true
                            }
                        }else{
                            error=true
                        }

                    }else{
                        error=true
                    }

                }

            }else error=true
        },
        label = { Text("Enter Date (DD/MM/YYYY)") },
    )
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RTimePicker(timePickerState:MutableState<TimePickerState>, modifier: Modifier=Modifier){


    var modifier1 by remember {
        mutableStateOf(Modifier)
    }

    val k= LocalDensity.current.density
    Column ( modifier =modifier.offset { IntOffset(0,-70*this.density.toInt()) }, horizontalAlignment = Alignment.CenterHorizontally ) {
        TimePicker( timePickerState.value, layoutType = TimePickerLayoutType.Vertical, modifier = modifier1)

        val timePickerAction:(Int,Int)->Unit={h,m->
            modifier1=Modifier.apply {
                timePickerState.value= TimePickerState(h,m,false)
            }
        }

        Row(Modifier.fillMaxWidth().padding(25.dp).background(Color.White, MaterialTheme.shapes.small), horizontalArrangement = Arrangement.SpaceEvenly) {
            var (hour,minute)= listOf( timePickerState.value.hour,timePickerState.value.minute)
            Text("0",
                Modifier
                    .padding(10.dp)
                    .clickable {
                        minute = 0;
                        timePickerAction(hour, minute)
                    })
            Text("15",
                Modifier
                    .padding(10.dp)
                    .clickable {
                        minute = 15;
                        timePickerAction(hour, minute)
                    })
            Text("30",
                Modifier
                    .padding(10.dp)
                    .clickable {
                        minute = 30;
                        timePickerAction(hour, minute)
                    })
            Text("45",
                Modifier
                    .padding(10.dp)
                    .clickable {
                        minute = 45;
                        timePickerAction(hour, minute)
                    })

        }


    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun timePickerDlg(dlgOpen:MutableState<Boolean>, taskTimeList: MutableState<ArrayList<Int>>, datePickerState:MutableState<RDatePickerState>){


    var timeRange by remember {
        mutableStateOf(TimeRange(0,0,0,0))
    }
    var (timeList, start, end) = listOf(0, 1, 2)
    var timeAction by remember {
        mutableStateOf(start )
    }

    var timePickerState= remember { mutableStateOf(if(start==timeAction) TimePickerState(timeRange.startHour,timeRange.startMinute,false) else TimePickerState(timeRange.startHour,timeRange.endHour,false) ) }
    if(dlgOpen.value) Dialog({ timeRange== TimeRange(0,0,0,0) }) {
        if(timeRange== TimeRange(0,0,0,0)) timeRange=datePickerState.value.outputDates[0].getTimeRange()

        var timerAction: (MutableState<TimePickerState>) -> Unit = {
            if (timeAction == start)   timeRange=timeRange.copy(startHour = timePickerState.value.hour, startMinute = timePickerState.value.minute )
            else   timeRange=timeRange.copy(endHour = timePickerState.value.hour, endMinute = timePickerState.value.minute )
        }

        var rangeChangerAction:(Int)->Unit={
            timeAction=it;
            if(timeAction==start) timePickerState.value= TimePickerState(timeRange.startHour,timeRange.startMinute,false) else timePickerState.value=TimePickerState(timeRange.endHour,timeRange.endMinute,false)
        }

        timerAction(timePickerState)

        Column(Modifier.background(Color.White).fillMaxWidth().offset { IntOffset(0,-100) }.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {



            Spacer(Modifier.height(30.dp))
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AssistChip({ rangeChangerAction(start) }, { Text(SimpleDateFormat("hh:mm a").format(Date().setTime(timeRange.startHour,timeRange.startMinute))) }, border = AssistChipDefaults.assistChipBorder(if(timeAction != start) Color.LightGray else Color.Blue.copy(0.75f),if(timeAction != start) Color.LightGray else Color.Blue.copy(0.75f),1.dp))
                AssistChip({ rangeChangerAction(end) }, { Text(SimpleDateFormat("hh:mm a").format(Date().setTime(timeRange.endHour,timeRange.endMinute))) }, border = AssistChipDefaults.assistChipBorder(if(timeAction != end) Color.LightGray else Color.Blue.copy(0.75f),if(timeAction != end) Color.LightGray else Color.Blue.copy(0.75f),1.dp))
            }
            Row {
                Text(taskTimeList.toString())
            }
            Spacer(Modifier.height(30.dp))

            RTimePicker(timePickerState, Modifier.scale(0.8f));
            Row {
                TextButton({


                    datePickerState.value = datePickerState.value.copy(outputDates = arrayListOf<RDateRange>().apply {
                        addAll(datePickerState.value.outputDates)
                        taskTimeList.value.forEach {
                            val t = datePickerState.value.outputDates[it]
                            set(it, t.copy(t.start.setTime(timeRange.startHour,timeRange.startMinute),t.end.setTime(timeRange.endHour,timeRange.endMinute)))
                        }
                    })
                    Log.e("Update",datePickerState.value.outputDates.toString())

                    timeRange== TimeRange(0,0,0,0)
                    taskTimeList.value= arrayListOf()
                    dlgOpen.value=false
                }) {
                    Text("Ok")
                }
                TextButton({
                    timeRange== TimeRange(0,0,0,0)
                    taskTimeList.value= arrayListOf()
                    dlgOpen.value=false
                }) {
                    Text("Cancel")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun SelectionMenu1(selectMode: SelectionMode, outputDate: ArrayList<Any>){

    //                    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

//                    LaunchedEffect(Unit) {
//                        focusRequester.requestFocus()
//
//                    }

    if (selectMode == SelectionMode.range) Column(Modifier.fillMaxWidth().offset((-30).dp,60.dp)) {
        var dif by remember {
            mutableStateOf(0L)
        }
        Spacer(Modifier.height(100.dp))

        dif = (Date(outputDate.first().toString().toLong()) differance Date(outputDate.last().toString().toLong())).toLong()
        OutlinedTextField(
            SimpleDateFormat("dd/MM/yyyy").format(Date(outputDate.first().toString().toLong())),
            {},
            readOnly = true,
            label = { Text("start") },
            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Blue),
            modifier = Modifier.fillMaxWidth(0.35f),
            textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 14.sp),
            keyboardActions = KeyboardActions({ keyboardController })
        )

        BasicTextField(
            dif.toString(),{
//                it.numberOnly {
//                    dif = it
//                    val min = min(outputDate.first().toString().toLong(), Date(outputDate.first().toString().toLong()).addDate(dif).time)
//                    val max = max(outputDate.first().toString().toLong(), Date(outputDate.first().toString().toLong()).addDate(dif).time)
//                    outputDate.apply {
//                        outputDate.clear()
//                        addAll((min..max step (24 * 60 * 60 * 1000).toLong()).map { it })
//                    }
//                }

            },
            modifier = Modifier.fillMaxWidth(0.20f).padding(top = 20.dp),
            textStyle = TextStyle(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(SimpleDateFormat("dd/MM/yyyy").format(outputDate.last()), {}, readOnly = true,
            label = { Text("end") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Red
            ),
            modifier = Modifier.fillMaxWidth(0.35f),
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
        )
    }
    else if (selectMode == SelectionMode.multible) {
        Row(Modifier.fillMaxWidth(.85f).horizontalScroll(rememberScrollState())) {
            outputDate.map { (Date(it.toString().toLong()).year+1900).toLong() }.toSet().forEach { _year->
                Text(_year.toString(),Modifier.padding(10.dp), color = Color(0xFF00BCD4), fontWeight = FontWeight.W300)
                outputDate.filter { Date(it.toString().toLong()).year==_year.toInt()-1900  }.map { Date(it.toString().toLong()).month }.toSet().forEach { _month->
                    Text(java.time.Month.of(_month+1).name.substring(0,3),Modifier.padding(10.dp), color = Color(0xFFE91E63), fontWeight = FontWeight.W300)
                    outputDate.filter { Date(it.toString().toLong()).year==_year.toInt()-1900&&Date(it.toString().toLong()).month==_month }.forEach {
                        Text(Date(it.toString().toLong()).date.toString(),Modifier.padding(10.dp))
                    }
                }
            }
        }

    }
}

private infix fun MutableState<RDatePickerState>.select(data: RDateRange): RDatePickerState {
    var l = ArrayList<RDateRange>()
    l.addAll(value.outputDates)
    if(Date().currentTime().time<=data.start.time&& Date().currentTime().time<=data.end.time||value.datePickerOptions.enableFreeSelect)  when (value.selectMode) {
        SelectionMode.multible ->{
            if(l.contains(data.copy(selectionMode = value.selectMode))) {
                l.remove(data.copy(selectionMode = value.selectMode))
            }
            else{ l.add(data.copy(selectionMode = value.selectMode));}

        }
        SelectionMode.range ->{
            if(l.filter { it.selectionMode==null }.size<2)  l.add(data) else{ l.add(data)}
            if(l.filter { it.selectionMode==null }.size==2){
                val RDateRange= RDateRange(l[l.size-2].start,l[l.size-1].start.setTime(l[l.size-2].start.hours,l[l.size-2].start.minutes),value.selectMode)
                l.removeIf { it.selectionMode==null }
                l.removeIf { it.selectionMode==null }
                l.add(RDateRange)
            }
        }
        SelectionMode.row ->{
            if(l.isNotEmpty()&&l.last().selectionMode== SelectionMode.row)  l.set(l.size-1,data.copy(end =data.start.addDate(7) , selectionMode = value.selectMode))
            else l.add(data.copy(end =data.start.addDate(7) ,selectionMode = value.selectMode))

        }
        SelectionMode.column ->{
            l.add(RDateRange(data.start,data.end,180))
        }
        else -> {
            if(l.isNotEmpty()&&l.last().selectionMode== SelectionMode.single)  l.set(l.size-1,data.copy(selectionMode = value.selectMode))
            else l.add(data.copy(selectionMode = value.selectMode))
        }
    }
    return value.copy(l)
}

data class RDatePickerState(var outputDates: ArrayList<RDateRange>, var goDate: Date, var selectMode: SelectionMode = SelectionMode.single, var datePickerOptions: RDatePickerOptions = RDatePickerOptions(), var dateChangeListener: (Date) -> Unit={}){


    companion object{
        @SuppressLint("NewApi") var dateNow= Date(LocalDate.now().year-1901, LocalDate.now().dayOfMonth, LocalDate.now().dayOfMonth, LocalTime.now().hour, LocalTime.now().minute, LocalTime.now().second)
    }

}

data class RDatePickerOptions(var enableGoto:Boolean=false,var enableFreeSelect:Boolean=false){

}

/*MutableState<RDatePickerState>*/
fun MutableState<RDatePickerState>.add(data: RDateRange){
    val list= arrayListOf<RDateRange>()
    list.addAll(value.outputDates)
    list.add(data)
    value=value.copy(list)
}

fun MutableState<RDatePickerState>.update(i:Int, data: RDateRange){
    val list= arrayListOf<RDateRange>()
    list.addAll(value.outputDates)
    list.set(i,data)
    value=value.copy(list)
}

fun MutableState<RDatePickerState>.delete(i:Int){
    val list= arrayListOf<RDateRange>()
    list.addAll(value.outputDates)
    list.removeAt(i)
    value=value.copy(list)
}

fun MutableState<RDatePickerState>.get(i:Int): RDateRange {
    return value.outputDates[i]
}

fun MutableState<RDatePickerState>.singleSelection(){
   value=value.copy(selectMode = SelectionMode.single)
}

fun MutableState<RDatePickerState>.multipleSelection(){
    value=value.copy(selectMode = SelectionMode.multible)
}

fun MutableState<RDatePickerState>.rangeSelection(){
    value=value.copy(selectMode = SelectionMode.range)
}

fun MutableState<RDatePickerState>.weekSelection(){
    value=value.copy(selectMode = SelectionMode.row)
}

fun MutableState<RDatePickerState>.daySelection(){
    value=value.copy(selectMode = SelectionMode.row)
}

fun MutableState<RDatePickerState>.resetSelection(){ value=value.copy(arrayListOf())
}

fun MutableState<RDatePickerState>.dateChangeListener(dateChangeListener:(Date)->Unit){
    value=value.copy(dateChangeListener = dateChangeListener)
    }


//                    val focusRequester = remember { FocusRequester() }
//val keyboardController = LocalSoftwareKeyboardController.current

//                    LaunchedEffect(Unit) {
//                        focusRequester.requestFocus()
//
//                    }
