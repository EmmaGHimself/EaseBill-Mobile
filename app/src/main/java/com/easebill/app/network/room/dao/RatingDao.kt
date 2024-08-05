package com.easebill.app.network.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.easebill.app.network.room.entity.Rating

@Dao
interface RatingDao {
    @Insert
    suspend fun insert(rating: Rating): Long

    @Query("SELECT * FROM rating WHERE userId = :userId")
    suspend fun getRatingsByUserId(userId: String): List<Rating>

    @Query("DELETE FROM rating WHERE userId = :userId")
    suspend fun deleteRatingByUserId(userId: String)

    @Query("DELETE FROM rating")
    suspend fun deleteAllRatings()
}
