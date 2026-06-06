package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Resume
import kotlinx.coroutines.flow.Flow

@Dao
interface ResumeDao {
    @Query("SELECT * FROM resume_table WHERE id = 1 LIMIT 1")
    fun getResumeFlow(): Flow<Resume?>

    @Query("SELECT * FROM resume_table WHERE id = 1 LIMIT 1")
    suspend fun getResumeDirect(): Resume?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateResume(resume: Resume)

    @Query("DELETE FROM resume_table")
    suspend fun clearResume()
}
