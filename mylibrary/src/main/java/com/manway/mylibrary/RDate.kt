package com.manway.mylibrary

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import kotlin.math.max
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.toDate():Date{
    return Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
}

/**Date**/

fun Date.setRDate(date:Int): Date {
    return Date(year,month,date,hours,minutes,seconds)
}

fun Date.setRMonth(month:Int): Date {
    return Date(year,month,date,hours,minutes,seconds)
}

fun Date.setRYear(year:Int): Date {
    return Date(year,month,date,hours,minutes,seconds)
}

@RequiresApi(Build.VERSION_CODES.O)
fun Date.convertLocalDate():LocalDate=toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()

fun Date.withoutTime():Date{
    return Date(year,month, date,0,0,0)
}

fun Date.setTime(hour:Int,minute:Int,seconds:Int=0):Date{
    return Date(year,month,date,hour,minute,seconds)
}

fun Date.addSecs(i: Long):Date{
    return   Date(time+(1000*i))
}

fun Date.addMinute(i: Long):Date{
    return   Date(time+(60*1000*i))
}

fun Date.addHours(i: Long):Date{
    return   Date(time+(3600*1000*i))
}

fun Date.addDate(i: Long):Date{
    return   Date(time+(3600*1000*24*i))
}

fun Date.addWeek(i: Long):Date{
    return   Date(time+(3600*1000*24*i*7))
}

fun Date.addMillSecs(i:Long):Date{
    return Date(time+i)
}

/** in this function date is today get the date now other wise it is take time 12.00AM   **/
fun Date.currentTime():Date{
    return  if(this.setTime(0,0)==Date().setTime(0,0)){
        this.setTime(Date().hours,Date().minutes)
    }
    else{
        this
    }
}

infix fun Date.differance(date:Date):Int{
    return ((date.time-this.time)/(24*60*60*1000)).toInt()
}


data class RDateRange constructor(var start:Date, var end:Date, var selectionMode: SelectionMode?, var taskId:Int?=null){
    var repeat=0
    var step=1
    var repeatId=0

    init {
        if(start.time>=end.time){
            val k=start
            start=end
            end=k
        }
    }

    fun difMillSeconds():Long{
        return end.time-start.time
    }

    fun diffSeconds():Long{
        return difMillSeconds()/1000
    }

    fun diffMinutes():Long{
        return diffSeconds()/60
    }

    fun diffHours():Long{
        return diffMinutes()/60
    }

    fun diffDays():Long{
        return diffHours()/24
    }



    constructor(start:Date, end:Date, repeat: Int,step: Int=1):this(start,end, SelectionMode.column){
        this.repeat=repeat
        this.step=step
    }

    fun getTimeRange(): TimeRange {
        return TimeRange(start.hours,start.minutes,end.hours,end.minutes,start.seconds,end.seconds)
    }

    fun setTimeRange(timeRange: TimeRange): RDateRange {
        start=start.setTime(timeRange.startHour,timeRange.startMinute,timeRange.startSeconds)
        end=end.setTime(timeRange.endHour,timeRange.endMinute,timeRange.endSeconds)
        return this
    }

    fun repeatList():ArrayList<RDateRange>{
        val repeatList=ArrayList<RDateRange>()
        if(repeat==0) throw Exception("Zero repeat on DataRange Class")
        if(step==0) throw Exception("Zero step on DataRange Class")
        for(i in 0..repeat){
            repeatList.add(RDateRange(start.addDate(i.toLong()*step),end.addDate(i.toLong()*step),selectionMode).apply {
                repeatId=i
            })

        }
        return repeatList
    }


    fun inRange( date:Date):Boolean{
        return start.time<=date.time&&date.time<=end.time
    }

    companion object{
        public const val dayMillisecs=86400000
        val initialRDateRange= RDateRange(Date(1L),Date(1L),null)
    }
}


data class TimeRange(var startHour:Int,var startMinute:Int,var endHour:Int,var endMinute:Int,var startSeconds:Int=0,var endSeconds:Int=0)



fun List<RDateRange>.toArrayList():ArrayList<RDateRange>{
    val l= arrayListOf<RDateRange>()
    l.addAll(this)
    return l
}

enum class SelectionMode{
    single,multible,range,row,column
}

fun ArrayList<*>.selectionData(selectionMode: SelectionMode, data:Any, steps:Long, repeatMode:Boolean=false):ArrayList<Any>{
    var l = ArrayList<Any>()
    l.addAll(this)
    when (selectionMode) {
        SelectionMode.multible ->{
            if(!repeatMode)   if(l.contains(data)) l.remove(data) else l.add(data)
            else l.add(data)
        }
        SelectionMode.range ->{
            if(l.size<2)  l.add(data) else{ l.clear();l.add(data)}
            if(l.size==2){
                try {
                    val min= min(l.first().toString().toLong() , l.last().toString().toLong())
                    val max= max(l.first().toString().toLong() , l.last().toString().toLong())
                    l.clear()
                    l.addAll ( (min..max step steps ).map { it })
                }
                catch (_:Exception){
                    val min= l.indexOf(l.first())
                    val max= l.indexOf(l.last())
                    l.clear()
                    l.addAll ( (min..max step steps.toInt() ).map { it })

                }
            }
        }
        SelectionMode.row, SelectionMode.column ->{
            //no action inputted now time
        }
        else -> {
            if(!repeatMode) { l.clear();l.addAll(listOf(data)) }else  l.add(data)
            Log.e("Update",l.toString())
        }
    }
    return l
}

data class RTask(val taskId:Int,var taskName:String,var selectMode: SelectionMode?=null)