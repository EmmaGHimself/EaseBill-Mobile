package com.easebill.app.network.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rating")
data class Rating(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val rating: String,
    val comment: String,
    val createdAt: String
)