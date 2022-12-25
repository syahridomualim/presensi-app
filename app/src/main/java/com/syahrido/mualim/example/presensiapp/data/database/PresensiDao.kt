package com.syahrido.mualim.example.presensiapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PresensiDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresensi(presensiList: List<PresensiEntity>)

    @Query("SELECT * FROM presensi ORDER BY date DESC")
    fun getPresensi(): LiveData<List<PresensiEntity>>

    @Query("DELETE FROM presensi")
    fun deletePresensi()
}