package com.jp.jp.domain.userWordLearning.manager.business

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UserWordLearningManagerCalculator {

    // 간격 반복 알고리즘에 따른 다음 복습 시간을 계산함
    fun calculateNextReviewTime(count: Int, isSuccess: Boolean, hadFailures: Boolean = false): LocalDateTime {
        val now = LocalDateTime.now()

        return if (isSuccess) {
            // 이전에 실패 기록이 있었던 단어에서 성공한 경우 1시간 후로 설정
            if (hadFailures) {
                now.plusHours(1)
            } else {
                // 일반적인 성공 시: 1→3일, 2→6일, 3→12일, 4→24일, 5→48일, 6+→60일
                calculateSuccessInterval(count, now)
            }
        } else {
            // 실패 시: 1→1일, 2+→30분
            calculateFailureInterval(count, now)
        }
    }

    // 성공 시 간격 계산
    private fun calculateSuccessInterval(count: Int, now: LocalDateTime): LocalDateTime {
        return when (count) {
            1 -> now.plusDays(3)
            2 -> now.plusDays(6)
            3 -> now.plusDays(12)
            4 -> now.plusDays(24)
            5 -> now.plusDays(48)
            else -> now.plusDays(60) // 6+ 연속 정답
        }
    }

    // 실패 시 간격 계산
    private fun calculateFailureInterval(count: Int, now: LocalDateTime): LocalDateTime {
        return when (count) {
            1 -> now.plusDays(1)
            else -> now.plusMinutes(30) // 2+ 연속 오답
        }
    }
}