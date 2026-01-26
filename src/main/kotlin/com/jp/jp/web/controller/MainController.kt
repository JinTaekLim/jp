package com.jp.jp.web.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/page")
class MainController {

    // 메인 페이지 (Landing Page)
    @GetMapping("/")
    fun home(): String {
        return "index"
    }

    // 로그인 페이지
    @GetMapping("/login")
    fun login(): String {
        return "auth/login"
    }

    // 회원가입 페이지
    @GetMapping("/signup")
    fun signup(): String {
        return "auth/signup"
    }

    // 학습 모드 선택 페이지
    @GetMapping("/study/mode")
    fun studyModeSelect(): String {
        return "study/study-mode-select"
    }

    // 레벨 선택 페이지
    @GetMapping("/study/level")
    fun levelSelect(): String {
        return "study/level-select"
    }

    // 학습 기록 페이지
    @GetMapping("/study/record")
    fun studyRecord(): String {
        return "study/record"
    }

    // 단어 학습 페이지
    @GetMapping("/study/words/{level}")
    fun wordStudy(@PathVariable level: String, model: Model): String {
        model.addAttribute("level", level)
        return "study/word-study"
    }

    // 랜덤 단어 학습 페이지
    @GetMapping("/study/words/{level}/random")
    fun randomWordStudy(@PathVariable level: String, model: Model): String {
        model.addAttribute("level", level)
        return "study/word-study"
    }

    // 균등 학습 페이지
    @GetMapping("/study/words/{level}/balanced")
    fun balancedWordStudy(@PathVariable level: String, model: Model): String {
        model.addAttribute("level", level)
        return "study/word-study"
    }
}