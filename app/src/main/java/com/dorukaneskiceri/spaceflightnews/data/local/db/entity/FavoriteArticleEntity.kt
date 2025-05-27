package com.dorukaneskiceri.spaceflightnews.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_articles")
data class FavoriteArticleEntity(
    @PrimaryKey val articleId: Int
)