package com.example.carlog.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Валюта приложения — при необходимости поменяйте здесь. */
const val CURRENCY = "₽"

// DatePicker работает в UTC-полуночи, поэтому форматируем тоже в UTC,
// чтобы дата на экране совпадала с выбранной в календаре.
private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

fun fmtDate(millis: Long): String = dateFormat.format(Date(millis))

fun fmtMoney(v: Double): String = "%.2f $CURRENCY".format(v)

fun fmtLiters(v: Double): String = "%.2f л".format(v)

fun fmtConsumption(v: Double): String = "%.1f л/100км".format(v)
