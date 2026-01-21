package com.jp.jp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class JpApplication

fun main(args: Array<String>) {
    runApplication<JpApplication>(*args)
}
