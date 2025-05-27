package com.dorukaneskiceri.spaceflightnews.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "authors",
    foreignKeys = [
        ForeignKey(
            entity = ArticleEntity::class,
            parentColumns = ["id"],
            childColumns = ["articleListId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("articleListId")]
)
data class AuthorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val articleListId: Int,
    val name: String
)