package com.example.testeappmoove.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liked_movies")
data class LikedMovie(
    @PrimaryKey()
    val id: Int
)