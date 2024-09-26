package com.manway.mylibrary

fun String.toPassword(symbol:Char='*'):String{
    val k=StringBuffer("")
    forEach {
        k.append(symbol)
    }
    return k.toString()
}

fun String.emptyReturn(value:String):String{
   return if(isEmpty()) value else this
}