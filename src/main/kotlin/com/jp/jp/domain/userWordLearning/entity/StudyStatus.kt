package com.jp.jp.domain.userWordLearning.entity

enum class StudyStatus {
    LEARNING,      // 복습 대기 중 (성공/실패 무관하게 다음 복습 시간까지 대기)
    NEEDS_REVIEW,  // 복습 필요 (next_review_at 시간 도달)
}