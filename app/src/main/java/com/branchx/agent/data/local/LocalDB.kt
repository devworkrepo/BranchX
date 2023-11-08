package com.branchx.agent.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.branchx.agent.helper.util.AppUtil
import com.branchx.agent.helper.util.DateUtil
import javax.inject.Inject


object DBContract{
    const val DB_NAME = "sr_plus_local_db"
    const val DB_VERSION = 1

    class DoubleCheck{
        companion object {
            const val TABLE_NAME = "double_check_transaction"
            const val ID = "_id"
            const val AMOUNT = "amount"
            const val NUMBER = ""
            const val INSERTED_TIME = "inserted_time"
        }
    }
}

class LocalDB @Inject constructor(context: Context) : SQLiteOpenHelper(
    context, DBContract.DB_NAME,
    null,
    DBContract.DB_VERSION){


    override fun onCreate(db: SQLiteDatabase?) {
        createDoubleCheckTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    private fun createDoubleCheckTable(db: SQLiteDatabase?) {
        val q = "CREATE TABLE ${DBContract.DoubleCheck.TABLE_NAME} (" +
                "${DBContract.DoubleCheck.ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${DBContract.DoubleCheck.AMOUNT} REAL," +
                "${DBContract.DoubleCheck.NUMBER} INTEGER," +
                "${DBContract.DoubleCheck.INSERTED_TIME} LONG" +
                ")"
       db?.execSQL(q)
        AppUtil.logger("table created : $q")
    }

    fun canTransactionProceed(number: String, amount: String,waitingMinute:Int =2): Boolean {
        AppUtil.logger("DB : canTransactionProceed")
        var canTransaction = false
        val db = writableDatabase
        val selectString =
            "SELECT * FROM " + DBContract.DoubleCheck.TABLE_NAME + " WHERE " + DBContract.DoubleCheck.NUMBER + " =? AND "+DBContract.DoubleCheck.AMOUNT+" =?"
        val cursor = db.rawQuery(selectString, arrayOf(number,amount))

        if (cursor.moveToFirst()) {

            val mNumber = cursor.getString(cursor.getColumnIndex(DBContract.DoubleCheck.NUMBER))
            val mAmount = cursor.getString(cursor.getColumnIndex(DBContract.DoubleCheck.AMOUNT))
            val mDelayTime = cursor.getString(cursor.getColumnIndex(DBContract.DoubleCheck.INSERTED_TIME))
            AppUtil.logger("DB : canTransactionProceed - moveToFirst")

            AppUtil.logger("DB : number $mNumber")
            AppUtil.logger("DB : amount $mAmount")
            AppUtil.logger("DB : current time : ${DateUtil.getTimeInMilliSeconds()}")
            AppUtil.logger("DB : trans time : $mDelayTime")


            canTransaction = if (DateUtil.getTimeInMilliSeconds() > mDelayTime.toLong()) {
                if (updateTransaction(mNumber, amount,waitingMinute)) {
                    AppUtil.logger("DB : canTransactionProceed - current time is greater than store time")
                    true
                } else false
            } else {
                AppUtil.logger("DB : canTransactionProceed - current time is less than store time")
                false
            }
        } else {
            AppUtil.logger("DB : canTransactionProceed - not moveToFirst")
            if (insertTransaction(number, amount,waitingMinute) > 0)
                canTransaction = true
        }
        cursor.close() // Dont forget to close your cursor
        db.close() //AND your Database!
        return canTransaction
    }

    private fun insertTransaction(number: String, amount: String,waitingMinute: Int): Long {
        AppUtil.logger("begin insert transaction")
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DBContract.DoubleCheck.NUMBER, number)
        contentValues.put(DBContract.DoubleCheck.AMOUNT, amount)
        val timeDelay = DateUtil.getTimeAfterMinute(waitingMinute)
        contentValues.put(DBContract.DoubleCheck.INSERTED_TIME, timeDelay)
        val success = db.insert(DBContract.DoubleCheck.TABLE_NAME, null, contentValues)
        db.close()
        if (success > 0)
            AppUtil.logger("transaction inserted successfully")
        else AppUtil.logger("failed to insert transaction")
        return success
    }

    private fun updateTransaction(number: String, amount: String, waitingMinute: Int, withCurrentTime: Boolean = false): Boolean {
        AppUtil.logger("begin update transaction")
        val db = writableDatabase
        val contentValues = ContentValues()
        if (withCurrentTime)
            contentValues.put(DBContract.DoubleCheck.INSERTED_TIME, DateUtil.getTimeInMilliSeconds())
        else contentValues.put(DBContract.DoubleCheck.INSERTED_TIME, DateUtil.getTimeAfterMinute(waitingMinute))
        contentValues.put(DBContract.DoubleCheck.AMOUNT,amount)

        val whereClause = DBContract.DoubleCheck.NUMBER + "=? And "+DBContract.DoubleCheck.AMOUNT+" =?"
        val whereArgs = arrayOf(number,amount)
        val isUpdate = db.update(DBContract.DoubleCheck.TABLE_NAME, contentValues, whereClause, whereArgs)

        if (isUpdate > 0)
            AppUtil.logger("transaction update successfully")
        else AppUtil.logger("failed to update transaction")
        return isUpdate > 0
    }

    fun deleteAllData() {
        AppUtil.logger("begin delete transactiona")
        val db = this.writableDatabase
        db.execSQL("DELETE FROM ${DBContract.DoubleCheck.TABLE_NAME}")
        db.close()
    }

}