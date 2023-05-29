package com.example.stopsmoking4me.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.stopsmoking4me.model.Reason
import com.example.stopsmoking4me.model.Messages
import com.example.stopsmoking4me.model.Quotes
import java.util.Date

@Dao
interface AppDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertMessages(msg: List<Messages>)

    @Query("SELECT * FROM messages WHERE colorType=:colorType")
    fun getMessages(colorType: String): List<Messages>

    @Query("SELECT (SELECT COUNT(*) FROM messages) == 0")
    fun isEmpty(): Boolean

    @Insert(onConflict = REPLACE)
    suspend fun insertQuotes(quotes: List<Quotes>)

    @Query("SELECT * FROM quotes")
    fun getQuotes(): LiveData<List<Quotes>>

    @Query("SELECT (SELECT COUNT(*) FROM quotes) == 0")
    fun isEmptyQuotesTable(): Boolean

    @Query("DELETE FROM quotes WHERE id = :id")
    fun deleteQuotes(id: Int)

    @Insert(onConflict = REPLACE)
    suspend fun saveReason(msgList: Reason)

    @Query("SELECT (SELECT COUNT(*) FROM reason) == 0")
    fun isEmptyReasonTable(): Boolean

    @Query("SELECT * FROM reason WHERE date >= :startDate ORDER BY date DESC")
    fun getReason(startDate : Date): LiveData<List<Reason>>

    @Query("SELECT COUNT(yesOrNo) FROM reason WHERE yesOrNo = 1")
    fun getYesCount(): LiveData<Int>

    @Query("SELECT COUNT(yesOrNo) FROM reason WHERE yesOrNo = 0")
    fun getNoCount(): LiveData<Int>

    @Query("SELECT * from reason GROUP BY yesOrNo")
    fun getTotalCount(): LiveData<Reason>

//    @Query("SELECT yesOrNo,  date, count(*) from Reason GROUP BY yesOrNo, date")
//    fun getYesNoCountDateWise()



}