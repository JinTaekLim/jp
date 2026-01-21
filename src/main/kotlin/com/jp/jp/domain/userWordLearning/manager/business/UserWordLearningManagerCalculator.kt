package com.jp.jp.domain.userWordLearning.manager.business

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UserWordLearningManagerCalculator {

    // Anki/SuperMemo 연구 기반 간격 반복 알고리즘으로 다음 복습 시간을 계산함 (에빙하우스 망각곡선 + SM-2 알고리즘 적용)
    fun calculateNextReviewTime(count: Int, isSuccess: Boolean, hadFailures: Boolean = false): LocalDateTime {
        val now = LocalDateTime.now()

        return if (isSuccess) {
            if (hadFailures) {
                // 이전에 실패 기록이 있던 단어는 성공해도 1일 후 재확인 (확실한 기억 정착 확인)
                adjustToOptimalReviewTime(now.plusDays(1))
            } else {
                // 일반적인 성공 시: 연구 검증된 과학적 간격 적용
                val baseTime = calculateSuccessInterval(count, now)
                adjustToOptimalReviewTime(baseTime)
            }
        } else {
            // 실패 시: 진정한 기억 테스트를 위한 현실적 간격 적용
            val baseTime = calculateFailureInterval(count, now)
            adjustToOptimalReviewTime(baseTime)
        }
    }

    // 성공 시 간격 계산 (Anki/SuperMemo 연구 기반 최적화된 망각곡선 적용)
    private fun calculateSuccessInterval(count: Int, now: LocalDateTime): LocalDateTime {
        return when (count) {
            1 -> now.plusDays(2)    // 1회 성공: 2일 후 (에빙하우스 망각곡선 - 단기기억 정착 시간 고려)
            2 -> now.plusDays(6)    // 2회 성공: 6일 후 (SuperMemo SM-2 알고리즘 기반)
            3 -> now.plusDays(15)   // 3회 성공: 15일 후 (장기기억 전환점 - 연구 검증됨)
            4 -> now.plusDays(30)   // 4회 성공: 30일 후 (Anki 기본 설정 기반)
            5 -> now.plusDays(60)   // 5회 성공: 60일 후 (장기 기억 안정화)
            else -> now.plusDays(120) // 6+ 연속 정답: 120일 후 (완전 습득 단계)
        }
    }

    // 실패 시 간격 계산 (진정한 기억 테스트를 위한 현실적 간격 적용)
    private fun calculateFailureInterval(count: Int, now: LocalDateTime): LocalDateTime {
        return when (count) {
            1 -> now.plusDays(1)    // 1회 실패: 1일 후 (단기기억이 아닌 진짜 기억 테스트)
            2 -> now.plusDays(3)    // 2회 연속 실패: 3일 후 (충분한 휴식 후 재도전)
            else -> now.plusDays(7) // 3+ 연속 실패: 7일 후 (근본적 재학습 필요)
        }
    }

    // 최적의 복습 시간대로 조정 (오전 9시-오후 9시 사이로 분산)
    private fun adjustToOptimalReviewTime(targetDateTime: LocalDateTime): LocalDateTime {
        val hour = targetDateTime.hour

        return when {
            // 새벽/밤 시간(오후 9시 ~ 오전 9시)이면 다음날 오전 9시로 조정
            hour < 9 || hour >= 21 -> {
                if (hour < 9) {
                    targetDateTime.withHour(9).withMinute(0).withSecond(0)
                } else {
                    targetDateTime.plusDays(1).withHour(9).withMinute(0).withSecond(0)
                }
            }
            // 적절한 시간대(오전 9시 ~ 오후 9시)는 그대로 유지
            else -> targetDateTime
        }
    }
}