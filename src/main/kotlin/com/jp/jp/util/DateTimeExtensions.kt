package com.jp.jp.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// LocalDateTime을 "yyyy-MM-dd HH:mm:ss" 형식으로 변환함
fun LocalDateTime.toFormattedString(): String {
    return this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}