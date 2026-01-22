package com.jp.jp.domain.userWordLearning.repository

import com.jp.jp.domain.userWordLearning.entity.QUserWordLearningEntity.userWordLearningEntity
import com.jp.jp.domain.userWordLearning.entity.UserWordLearningEntity
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager

// QueryDSL을 활용한 커스텀 Repository 구현체
class UserWordLearningRepositoryImpl(
    private val entityManager: EntityManager
) : UserWordLearningCustomRepository {

    private val queryFactory = JPAQueryFactory(entityManager)

    // lastId 기반으로 사용자의 학습한 단어들을 조회함 (최근 학습 순으로 정렬)
    override fun findUserStudiedWords(
        userId: Long,
        lastId: Long?,
        size: Int
    ): List<UserWordLearningEntity> {
        return queryFactory
            .selectFrom(userWordLearningEntity)
            .where(
                userWordLearningEntity.userId.eq(userId),
                lastId?.let { userWordLearningEntity.id.lt(it) }
            )
            .orderBy(
                userWordLearningEntity.lastStudiedAt.desc().nullsLast(),
                userWordLearningEntity.id.desc()
            )
            .limit(size.toLong() + 1)
            .fetch()
    }
}