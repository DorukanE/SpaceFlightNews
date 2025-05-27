package com.dorukaneskiceri.spaceflightnews.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "articles",
    foreignKeys = [
        ForeignKey(
            entity = ArticleResponseEntity::class,
            parentColumns = ["id"],
            childColumns = ["articleResponseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("articleResponseId")]
)
data class ArticleEntity(
    @PrimaryKey val id: Int,
    val articleResponseId: Long,
    val title: String,
    val url: String,
    val imageUrl: String,
    val newsSite: String,
    val summary: String,
    val publishedAt: String,
    val updatedAt: String,
    val featured: Boolean,
    val isFavorite: Boolean = false
)