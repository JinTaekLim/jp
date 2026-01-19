package com.jp.jp.domain.collect.entity

import com.jp.jp.util.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "jlpt_words")
class JlptWord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // 일본어 (히라가나/가타카나)
    @Column(name = "japanese", nullable = false, length = 100)
    val japanese: String,

    // 한자 표기 (없으면 null)
    @Column(name = "kanji", length = 100)
    val kanji: String? = null,

    // 품사 (동사, 형용사, 명사 등)
    @Column(name = "part_of_speech", nullable = false, length = 50)
    val partOfSpeech: String,

    // 의미 목록
    @ElementCollection
    @CollectionTable(name = "jlpt_word_meanings", joinColumns = [JoinColumn(name = "jlpt_word_id")])
    @Column(name = "meaning")
    val meanings: List<String>,

    // JLPT 레벨 (5, 4, 3, 2, 1)
    @Column(name = "level", nullable = false, length = 10)
    val level: String,

    // 품사 분류 (allClass, 동사, 형용사 등)
    @Column(name = "part", nullable = false, length = 50)
    val part: String,

    // 페이지 번호
    @Column(name = "page_num", nullable = false, length = 10)
    val pageNum: String
) : BaseEntity() {
    // 중복 방지를 위한 유니크 인덱스 (japanese + level + part + pageNum)
    // 같은 페이지에서 같은 단어가 중복 저장되는 것을 방지
}