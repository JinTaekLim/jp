package com.jp.jp.domain.jlptWord.service.business

import com.jp.jp.domain.userJlptLevel.entity.JlptLevel
import org.springframework.stereotype.Component

@Component
class JlptWordServiceCalculate {

    // 각 레벨별 할당 개수를 계산함 (나머지는 낮은 레벨부터 제외)
    fun calculateLevelCounts(levels: List<JlptLevel>, totalCount: Int): Map<JlptLevel, Int> {
        val baseCount = totalCount / levels.size
        val remainder = totalCount % levels.size

        return levels.mapIndexed { index, level ->
            // 나머지는 높은 레벨(뒤쪽)부터 할당 (낮은 레벨이 적게 가져옴)
            val extraCount = if (index >= levels.size - remainder) 1 else 0
            level to (baseCount + extraCount)
        }.toMap()
    }
}