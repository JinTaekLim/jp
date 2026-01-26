package com.jp.jp.domain.userJlptLevel.entity

enum class JlptLevel {
    N5, // 가장 쉬운 레벨
    N4,
    N3,
    N2,
    N1; // 가장 어려운 레벨

    companion object {
        // DB에 저장된 숫자 값을 enum으로 변환
        fun fromNumber(number: String): JlptLevel {
            return when (number) {
                "5" -> N5
                "4" -> N4
                "3" -> N3
                "2" -> N2
                "1" -> N1
                else -> throw IllegalArgumentException("Invalid JLPT level: $number")
            }
        }

        // enum을 DB 숫자 값으로 변환
        fun toNumber(level: JlptLevel): String {
            return when (level) {
                N5 -> "5"
                N4 -> "4"
                N3 -> "3"
                N2 -> "2"
                N1 -> "1"
            }
        }

        // 목표 레벨에 포함되는 모든 레벨을 반환함 (N2면 [N5, N4, N3, N2])
        fun getIncludedLevels(targetLevel: JlptLevel): List<JlptLevel> {
            return values().filter { level ->
                // ordinal 값 비교: N5(0) <= N4(1) <= N3(2) <= N2(3) <= N1(4)
                level.ordinal <= targetLevel.ordinal
            }.sortedBy { it.ordinal } // N5부터 목표레벨까지 순서대로 정렬
        }
    }
}