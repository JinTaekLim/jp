package com.jp.jp.domain.collect.manager.business

import com.jp.jp.domain.collect.dto.ParsedJlptData
import com.jp.jp.domain.collect.dto.ParsedJlptWord
import org.jsoup.Jsoup
import org.springframework.stereotype.Component

@Component
class CrawlingManagerParser {

    // HTML을 파싱하여 JLPT 단어 목록을 추출함
    fun parseJlptHtml(html: String): ParsedJlptData {
        val document = Jsoup.parse(html)

        // 전체 단어 수 추출 (예: "5급, 전체, 744건")
        val totalWordCount = extractTotalWordCount(document)

        val wordElements = document.select("li.row")
        val words = mutableListOf<ParsedJlptWord>()

        for (element in wordElements) {
            try {
                // 일본어 추출
                val japanese = element.select("div.origin a").text().trim()
                if (japanese.isEmpty()) continue

                // 한자 추출 (대괄호 제거)
                val pronunciationElement = element.select("span.pronunciation").text()
                val kanji = if (pronunciationElement.isNotEmpty()) {
                    pronunciationElement.replace("[", "").replace("]", "").trim().takeIf { it.isNotEmpty() }
                } else null

                // 품사 추출
                val partOfSpeechElement = element.select("span.word_class").text().trim()
                if (partOfSpeechElement.isEmpty()) continue

                // 의미 추출
                val meanElement = element.select("p.mean").text().trim()
                val meanings = extractMeanings(meanElement, partOfSpeechElement)

                words.add(
                    ParsedJlptWord(
                        japanese = japanese,
                        kanji = kanji,
                        partOfSpeech = partOfSpeechElement,
                        meanings = meanings
                    )
                )
            } catch (_: Exception) {
                // 개별 단어 파싱 실패 시 건너뛰기
                continue
            }
        }

        return ParsedJlptData(words = words, totalWordCount = totalWordCount)
    }

    // 의미 문자열에서 개별 의미들을 추출함
    private fun extractMeanings(fullText: String, partOfSpeech: String): List<String> {
        // 품사 제거
        val meaningText = fullText.replace(partOfSpeech, "").trim()

        // 숫자로 시작하는 의미들 추출
        val meanings = mutableListOf<String>()
        val regex = Regex("""(\d+)\.(.*?)(?=\s*\d+\.|$)""")
        val matches = regex.findAll(meaningText)

        for (match in matches) {
            val number = match.groupValues[1]
            val meaning = match.groupValues[2].trim()
            if (meaning.isNotEmpty()) {
                meanings.add("$number.$meaning")
            }
        }

        // 숫자 패턴이 없으면 전체를 하나의 의미로 처리
        if (meanings.isEmpty() && meaningText.isNotEmpty()) {
            meanings.add(meaningText)
        }

        return meanings
    }

    // HTML에서 전체 단어 수를 추출함 (예: "5급, 전체, 744건" -> 744)
    private fun extractTotalWordCount(document: org.jsoup.nodes.Document): Int {
        return try {
            // 여러 선택자를 시도해서 전체 단어 수를 찾음
            val selectors = listOf(
                ".result .text .amount",  // <div class="result"><p class="text"><span class="amount">744건</span></p></div>
                ".amount",                // 직접 amount 클래스
                ".text .amount",         // text 하위의 amount
                "span.amount"            // span 태그의 amount 클래스
            )

            for (selector in selectors) {
                val resultText = document.select(selector).text()
                if (resultText.isNotEmpty()) {
                    // 숫자 추출 (예: "744건" -> 744)
                    val regex = Regex("""(\d+)건""")
                    val matchResult = regex.find(resultText)
                    val count = matchResult?.groupValues?.get(1)?.toIntOrNull()
                    if (count != null && count > 0) {
                        return count
                    }
                }
            }

            0
        } catch (_: Exception) {
            0
        }
    }
}