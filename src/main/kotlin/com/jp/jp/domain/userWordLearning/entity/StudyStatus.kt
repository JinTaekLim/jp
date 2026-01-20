package com.jp.jp.domain.userWordLearning.entity

enum class StudyStatus {
    NEW,           // 새로운 단어 (첫 학습)
    LEARNING,      // 학습 중 (오답 후 재학습 대기)
    PENDING,       // 대기 중
    NEEDS_REVIEW,  // 복습 필요 (next_review_at 시간 도달)
}