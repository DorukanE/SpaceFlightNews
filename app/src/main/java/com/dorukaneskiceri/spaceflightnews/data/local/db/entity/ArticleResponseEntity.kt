package com.dorukaneskiceri.spaceflightnews.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_response")
data class ArticleResponseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val count: Int,
    val next: String?,
    val previous: String?
)