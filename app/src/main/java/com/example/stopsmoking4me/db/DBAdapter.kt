package com.example.stopsmoking4me.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.stopsmoking4me.model.DayWiseSmokeCount
import com.example.stopsmoking4me.model.HourlyAnalyticData
import com.example.stopsmoking4me.model.OneDayAnalyticsData
import com.example.stopsmoking4me.model.SmokeCountAndPercentage


private const val DB_NAME = "TabQuitSmokingApp"
private const val TABLE_NAME = "stopSmoking"
private const val DB_VERSION = 1
private const val SRNO = "SNo"
private const val DATE = "Date"
private const val HOUR = "Hour"
private const val DAY = "Day"
private const val REASON = "Reason"
private const val SMOKING = "Smoking"

class DBAdapter(private var context: Context) {
    private val SQLQUERY = buildString {
        append("CREATE TABLE")
        append(TABLE_NAME)
        append("(")
        append(SRNO)
        append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
        append(DATE)
        append(" DATE DEFAULT CURRENT_DATE NOT NULL, ")
        append(HOUR)
        append(" INTEGER, ")
        append(DAY)
        append(" TEXT, ")
        append(REASON)
        append(" TEXT, ")
        append(SMOKING)
        append(" TEXT)")
    }

    val createTableQuery = "CREATE TABLE TabQuitSmokingApp (" +
            "SrNo INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "Date DATE DEFAULT CURRENT_DATE NOT NULL, " +
            "Hour INTEGER, " +
            "Day VARCHAR(10), " +
            "Reason VARCHAR(35), " +
            "Smoking CHAR(3)" +
            ")"

    private var dbHelper: DBHelper = DBHelper(context = context)
    var sqliteDatabase: SQLiteDatabase = dbHelper.writableDatabase

    fun saveData(reasonInput: String, smokingInput: String) {
        val query = "INSERT INTO TabQuitSmokingApp (Date, Hour, Day, Reason, Smoking) " +
                "VALUES (" +
                "DATE('now', 'localtime'), " +
                "CASE " +
                "WHEN round(strftime('%M', 'now', 'localtime')) > 30 THEN round(strftime('%H', 'now', 'localtime')) + 1 " +
                "ELSE round(strftime('%H', 'now', 'localtime')) " +
                "END, " +
                "CASE " +
                "WHEN cast (strftime('%w', 'now', 'localtime') as integer) = 0 THEN 'Sunday' " +
                "WHEN cast (strftime('%w', 'now', 'localtime') as integer) = 1 THEN 'Monday' " +
                "WHEN cast (strftime('%w', 'now', 'localtime') as integer) = 2 THEN 'Tuesday' " +
                "WHEN cast (strftime('%w', 'now', 'localtime') as integer) = 3 THEN 'Wednesday' " +
                "WHEN cast (strftime('%w', 'now', 'localtime') as integer) = 4 THEN 'Thursday' " +
                "WHEN cast (strftime('%w', 'now', 'localtime') as integer) = 5 THEN 'Friday' " +
                "ELSE 'Saturday' " +
                "END, " +
                "'$reasonInput', " +
                "'$smokingInput' " +
                ")"

        sqliteDatabase.execSQL(query)

    }

    fun getOneDayAnalytics(): ArrayList<OneDayAnalyticsData> {
        var arrayList = ArrayList<OneDayAnalyticsData>()
        val query = """
    SELECT a.Date, a.Day, a.Reason, a.SmokedCount,
    round(((a.SmokedCount * 100.0) / b.SmokedSumEntireDay), 2) || '%' AS ContributedReasonPercentage
    FROM (
        SELECT Date, Day, Reason, count(Smoking) AS SmokedCount
        FROM TabQuitSmokingApp tqsaa
        WHERE Smoking = 'Yes' AND Date >= date('now', '-1 day', 'localtime')
        GROUP BY Date, Day, Reason
    ) AS a,
    (
        SELECT date, count(*) AS SmokedSumEntireDay
        FROM TabQuitSmokingApp tqsab
        WHERE Smoking = 'Yes' AND Date >= date('now', '-1 day', 'localtime')
        GROUP BY Date
    ) AS b
    WHERE a.Date = b.Date
    ORDER BY 1 DESC, 4 DESC;
""".trimIndent()

        val cursor = sqliteDatabase.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(0)
                val day = cursor.getString(1)
                val reason = cursor.getString(2)
                val smokedCount = cursor.getInt(3)
                val contributedReasonPercentage = cursor.getString(4)
                arrayList.add(
                    OneDayAnalyticsData(
                        date,
                        day,
                        reason,
                        smokedCount.toString(),
                        contributedReasonPercentage
                    )
                )
            } while (cursor.moveToNext())
        }
        return arrayList
    }

    fun getOneDaySmokedPercentageAnalytic(): ArrayList<SmokeCountAndPercentage>{
        var arrayList = ArrayList<SmokeCountAndPercentage>()
        val query = """
    SELECT (a.SmokedCount + b.NoSmokedCount) AS TotalAttempts,
    a.SmokedCount,
    b.NoSmokedCount,
    round((a.SmokedCount * 100.0) /(a.SmokedCount + b.NoSmokedCount), 2) || '%' AS SmokedPercentage,
    round((b.NoSmokedCount * 100.0) /(a.SmokedCount + b.NoSmokedCount), 2) || '%' AS NoSmokedPercentage
    FROM (
        SELECT 1 AS DummyCol,
        count(*) AS SmokedCount
        FROM TabQuitSmokingApp tqsab
        WHERE Smoking = 'Yes' AND Date >= date('now', '-1 day', 'localtime')
    ) AS a,
    (
        SELECT 1 AS DummyCol,
        count(*) AS NoSmokedCount
        FROM TabQuitSmokingApp tqsab
        WHERE Smoking = 'No' AND Date >= date('now', '-1 day', 'localtime')
    ) AS b
    WHERE a.DummyCol = b.DummyCol;
""".trimIndent()

        val cursor = sqliteDatabase.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val totalAttempts = cursor.getInt(0)
                val smokedCount = cursor.getInt(1)
                val noSmokedCount = cursor.getInt(2)
                val smokedPercentage = cursor.getString(3)
                val noSmokedPercentage = cursor.getString(4)
                if (smokedPercentage != null) {
                    arrayList.add(
                        SmokeCountAndPercentage(
                            totalAttempts.toString(),
                            smokedCount.toString(),
                            noSmokedCount.toString(),
                            smokedPercentage,
                            noSmokedPercentage
                        )
                    )

                }
            } while (cursor.moveToNext())
        }
        return arrayList;

    }

    fun getSevenDaysAnalytics(): ArrayList<OneDayAnalyticsData>{
        var arrayList = ArrayList<OneDayAnalyticsData>()
        val query = """
    SELECT a.Date, a.Day, a.Reason, a.SmokedCount,
    round(((a.SmokedCount * 100.0) / b.SmokedSumEntireDay), 2) || '%' AS ContributedReasonPercentage
    FROM (
        SELECT Date, Day, Reason, count(Smoking) AS SmokedCount
        FROM TabQuitSmokingApp tqsaa
        WHERE Smoking = 'Yes' AND Date >= date('now', '-7 day', 'localtime')
        GROUP BY Date, Day, Reason
    ) AS a,
    (
        SELECT date, count(*) AS SmokedSumEntireDay
        FROM TabQuitSmokingApp tqsab
        WHERE Smoking = 'Yes' AND Date >= date('now', '-7 day', 'localtime')
        GROUP BY Date
    ) AS b
    WHERE a.Date = b.Date
    ORDER BY 1 DESC, 4 DESC;
""".trimIndent()

        val cursor = sqliteDatabase.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(0)
                val day = cursor.getString(1)
                val reason = cursor.getString(2)
                val smokedCount = cursor.getInt(3)
                val contributedReasonPercentage = cursor.getString(4)

                arrayList.add(OneDayAnalyticsData(date, day, reason, smokedCount.toString(), contributedReasonPercentage))

            } while (cursor.moveToNext())
        }
        return arrayList
    }

    fun getSevenDaysDayWiseAnalytics(): ArrayList<DayWiseSmokeCount>{
        var arrayList = ArrayList<DayWiseSmokeCount>()
        val query = """
    SELECT
        CASE
            WHEN a.Day = 'Sunday' THEN 0
            WHEN a.Day = 'Monday' THEN 1
            WHEN a.Day = 'Tuesday' THEN 2
            WHEN a.Day = 'Wednesday' THEN 3
            WHEN a.Day = 'Thursday' THEN 4
            WHEN a.Day = 'Friday' THEN 5
            ELSE 6
        END AS SrNoDay,
        a.Day,
        a.Reason,
        a.SmokedCount,
        round(((a.SmokedCount * 100.0) / b.SmokedSumEntireDay), 2) || '%' AS ContributedReasonPercentage
    FROM (
        SELECT Day, Reason, count(Smoking) AS SmokedCount
        FROM TabQuitSmokingApp tqsaa
        WHERE Smoking = 'Yes' AND Date >= date('now', '-7 day', 'localtime')
        GROUP BY Day, Reason
    ) AS a,
    (
        SELECT Day, count(*) AS SmokedSumEntireDay
        FROM TabQuitSmokingApp tqsab
        WHERE Smoking = 'Yes' AND Date >= date('now', '-7 day', 'localtime')
        GROUP BY Day
    ) AS b
    WHERE a.Day = b.Day
    ORDER BY 1 ASC, 4 DESC;
""".trimIndent()

        val cursor = sqliteDatabase.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val srNoDay = cursor.getInt(0)
                val day = cursor.getString(1)
                val reason = cursor.getString(2)
                val smokedCount = cursor.getInt(3)
                val contributedReasonPercentage = cursor.getString(4)
                arrayList.add(DayWiseSmokeCount(day, reason, smokedCount.toString(), contributedReasonPercentage))
            } while (cursor.moveToNext())
        }
        return arrayList

    }

    fun getSevenDaysSmokePercentageAnalytic(): ArrayList<SmokeCountAndPercentage>{
        var arrayList = ArrayList<SmokeCountAndPercentage>()
        val db = dbHelper.readableDatabase
        val query = """
    SELECT
        (a.SmokedCount + b.NoSmokedCount) AS TotalAttempts,
        a.SmokedCount,
        b.NoSmokedCount,
        round((a.SmokedCount * 100.0) / (a.SmokedCount + b.NoSmokedCount), 2) || '%' AS SmokedPercentage,
        round((b.NoSmokedCount * 100.0) / (a.SmokedCount + b.NoSmokedCount), 2) || '%' AS NoSmokedPercentage
    FROM (
        SELECT 1 AS DummyCol, count(*) AS SmokedCount
        FROM TabQuitSmokingApp tqsab
        WHERE Smoking = 'Yes' AND Date >= date('now', '-7 day', 'localtime')
    ) AS a,
    (
        SELECT 1 AS DummyCol, count(*) AS NoSmokedCount
        FROM TabQuitSmokingApp tqsab
        WHERE Smoking = 'No' AND Date >= date('now', '-7 day', 'localtime')
    ) AS b
    WHERE a.DummyCol = b.DummyCol;
""".trimIndent()

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val totalAttempts = cursor.getInt(0)
                val smokedCount = cursor.getInt(1)
                val noSmokedCount = cursor.getInt(2)
                val smokedPercentage = cursor.getString(3)
                val noSmokedPercentage = cursor.getString(4)
                if (smokedPercentage != null && noSmokedPercentage != null) {
                    arrayList.add(
                        SmokeCountAndPercentage(
                            totalAttempts.toString(),
                            smokedCount.toString(),
                            noSmokedCount.toString(),
                            smokedPercentage,
                            noSmokedPercentage
                        )
                    )
                }
            } while (cursor.moveToNext())
        }
        return arrayList
    }

    fun get30DaysAnalytics(): ArrayList<OneDayAnalyticsData>{
        var arrayList = ArrayList<OneDayAnalyticsData>()
        val query = """
    SELECT a.Date, a.Day, a.Reason, a.SmokedCount,
    round(((a.SmokedCount * 100.0) / b.SmokedSumEntireDay), 2) || '%' AS ContributedReasonPercentage
    FROM (
        SELECT Date, Day, Reason, count(Smoking) AS SmokedCount
        FROM TabQuitSmokingApp tqsaa
        WHERE Smoking = 'Yes' AND Date >= date('now', '-30 day', 'localtime')
        GROUP BY Date, Day, Reason
    ) AS a,
    (
        SELECT date, count(*) AS SmokedSumEntireDay
        FROM TabQuitSmokingApp tqsab
        WHERE Smoking = 'Yes' AND Date >= date('now', '-30 day', 'localtime')
        GROUP BY Date
    ) AS b
    WHERE a.Date = b.Date
    ORDER BY 1 DESC, 4 DESC;
""".trimIndent()

        val cursor = sqliteDatabase.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(0)
                val day = cursor.getString(1)
                val reason = cursor.getString(2)
                val smokedCount = cursor.getInt(3)
                val contributedReasonPercentage = cursor.getString(4)

                arrayList.add(OneDayAnalyticsData(date, day, reason, smokedCount.toString(), contributedReasonPercentage))

            } while (cursor.moveToNext())
        }
        return arrayList
    }

    fun get30DaysDayWiseAnalytics(): ArrayList<DayWiseSmokeCount>{
        var arrayList = ArrayList<DayWiseSmokeCount>()
        val query = """
    SELECT
        CASE
            WHEN a.Day = 'Sunday' THEN 0
            WHEN a.Day = 'Monday' THEN 1
            WHEN a.Day = 'Tuesday' THEN 2
            WHEN a.Day = 'Wednesday' THEN 3
            WHEN a.Day = 'Thursday' THEN 4
            WHEN a.Day = 'Friday' THEN 5
            ELSE 6
        END AS SrNoDay,
        a.Day,
        a.Reason,
        a.SmokedCount,
        round(((a.SmokedCount * 100.0) / b.SmokedSumEntireDay), 2) || '%' AS ContributedReasonPercentage
    FROM (
        SELECT Day, Reason, count(Smoking) AS SmokedCount
        FROM TabQuitSmokingApp tqsaa
        WHERE Smoking = 'Yes' AND Date >= date('now', '-30 day', 'localtime')
        GROUP BY Day, Reason
    ) AS a,
    (
        SELECT Day, count(*) AS SmokedSumEntireDay
        FROM TabQuitSmokingApp tqsab
        WHERE Smoking = 'Yes' AND Date >= date('now', '-30 day', 'localtime')
        GROUP BY Day
    ) AS b
    WHERE a.Day = b.Day
    ORDER BY 1 ASC, 4 DESC;
""".trimIndent()

        val cursor = sqliteDatabase.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val srNoDay = cursor.getInt(0)
                val day = cursor.getString(1)
                val reason = cursor.getString(2)
                val smokedCount = cursor.getInt(3)
                val contributedReasonPercentage = cursor.getString(4)
                arrayList.add(DayWiseSmokeCount(day, reason, smokedCount.toString(), contributedReasonPercentage))
            } while (cursor.moveToNext())
        }
        return arrayList

    }

    fun get30DaysSmokePercentageAnalytic(): ArrayList<SmokeCountAndPercentage>{
        var arrayList = ArrayList<SmokeCountAndPercentage>()
        val db = dbHelper.readableDatabase
        val query = """
    SELECT
        (a.SmokedCount + b.NoSmokedCount) AS TotalAttempts,
        a.SmokedCount,
        b.NoSmokedCount,
        round((a.SmokedCount * 100.0) / (a.SmokedCount + b.NoSmokedCount), 2) || '%' AS SmokedPercentage,
        round((b.NoSmokedCount * 100.0) / (a.SmokedCount + b.NoSmokedCount), 2) || '%' AS NoSmokedPercentage
    FROM (
        SELECT 1 AS DummyCol, count(*) AS SmokedCount
        FROM TabQuitSmokingApp tqsab
        WHERE Smoking = 'Yes' AND Date >= date('now', '-30 day', 'localtime')
    ) AS a,
    (
        SELECT 1 AS DummyCol, count(*) AS NoSmokedCount
        FROM TabQuitSmokingApp tqsab
        WHERE Smoking = 'No' AND Date >= date('now', '-30 day', 'localtime')
    ) AS b
    WHERE a.DummyCol = b.DummyCol;
""".trimIndent()

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val totalAttempts = cursor.getInt(0)
                val smokedCount = cursor.getInt(1)
                val noSmokedCount = cursor.getInt(2)
                val smokedPercentage = cursor.getString(3)
                val noSmokedPercentage = cursor.getString(4)
                if (smokedPercentage != null && noSmokedPercentage != null) {
                    arrayList.add(
                        SmokeCountAndPercentage(
                            totalAttempts.toString(),
                            smokedCount.toString(),
                            noSmokedCount.toString(),
                            smokedPercentage,
                            noSmokedPercentage
                        )
                    )
                }
            } while (cursor.moveToNext())
        }
        return arrayList
    }

    fun getLifeTimeAnalytics(): ArrayList<OneDayAnalyticsData>{
        var arrayList = ArrayList<OneDayAnalyticsData>()
        val db = dbHelper.readableDatabase
        val query = """
    SELECT a.Date, a.Day, a.Reason, a.SmokedCount,
    round(((a.SmokedCount * 100.0) / b.SmokedSumEntireDay), 2) || '%' AS ContributedReasonPercentage
    FROM (
        SELECT Date, Day, Reason, count(Smoking) AS SmokedCount
        FROM TabQuitSmokingApp tqsaa
        WHERE Smoking = 'Yes'
        GROUP BY Date, Day, Reason
    ) AS a,
    (
        SELECT date, count(*) AS SmokedSumEntireDay
        FROM TabQuitSmokingApp tqsab
        WHERE Smoking = 'Yes'
        GROUP BY Date
    ) AS b
    WHERE a.Date = b.Date
    ORDER BY 1 DESC, 4 DESC;
""".trimIndent()

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(0)
                val day = cursor.getString(1)
                val reason = cursor.getString(2)
                val smokedCount = cursor.getInt(3)
                val contributedReasonPercentage = cursor.getString(4)
                arrayList.add(OneDayAnalyticsData(date, day, reason, smokedCount.toString(), contributedReasonPercentage))
            } while (cursor.moveToNext())
        }
        return arrayList
    }

    fun getLifetimeAnalyticsDayWise(): ArrayList<DayWiseSmokeCount>{
        var arrayList = ArrayList<DayWiseSmokeCount>()
        val db = dbHelper.readableDatabase
        val query = """
    SELECT 
        CASE 
            WHEN a.Day = 'Sunday' THEN 0
            WHEN a.Day = 'Monday' THEN 1
            WHEN a.Day = 'Tuesday' THEN 2
            WHEN a.Day = 'Wednesday' THEN 3
            WHEN a.Day = 'Thursday' THEN 4
            WHEN a.Day = 'Friday' THEN 5
            ELSE 6
        END AS SrNoDay,
        a.Day,
        a.Reason,
        a.SmokedCount,
        round(((a.SmokedCount * 100.0) / b.SmokedSumEntireDay), 2) || '%' AS ContributedReasonPercentage
    FROM (
        SELECT Day, Reason, COUNT(Smoking) AS SmokedCount
        FROM TabQuitSmokingApp tqsaa
        where Smoking = 'Yes'
        GROUP BY Day, Reason
    ) AS a,
    (
        SELECT Day, COUNT(*) AS SmokedSumEntireDay
        FROM TabQuitSmokingApp tqsab
        where Smoking = 'Yes'
        GROUP BY Day
    ) AS b
    WHERE a.Day = b.Day
    ORDER BY 1 ASC, 4 DESC;
""".trimIndent()

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val srNoDay = cursor.getInt(0)
                val day = cursor.getString(1)
                val reason = cursor.getString(2)
                val smokedCount = cursor.getInt(3)
                val contributedReasonPercentage = cursor.getString(4)
                arrayList.add(DayWiseSmokeCount(day, reason, smokedCount.toString(), contributedReasonPercentage))

            } while (cursor.moveToNext())
        }
        return arrayList
    }

    fun getLifetimeAnalyticsCountPercentage(): ArrayList<SmokeCountAndPercentage>{
        var arrayList = ArrayList<SmokeCountAndPercentage>()
        val db = dbHelper.readableDatabase
        val query = """
    SELECT 
        (a.SmokedCount + b.NoSmokedCount) AS TotalAttempts,
        a.SmokedCount,
        b.NoSmokedCount,
        ROUND((a.SmokedCount * 100.0) / (a.SmokedCount + b.NoSmokedCount), 2) || '%' AS SmokedPercentage,
        ROUND((b.NoSmokedCount * 100.0) / (a.SmokedCount + b.NoSmokedCount), 2) || '%' AS NoSmokedPercentage
    FROM (
        SELECT 1 AS DummyCol, COUNT(*) AS SmokedCount
        FROM TabQuitSmokingApp tqsab
        WHERE Smoking = 'Yes'
    ) AS a,
    (
        SELECT 1 AS DummyCol, COUNT(*) AS NoSmokedCount
        FROM TabQuitSmokingApp tqsab
        WHERE Smoking = 'No'
    ) AS b
    WHERE a.DummyCol = b.DummyCol;
""".trimIndent()

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            val totalAttempts = cursor.getInt(0)
            val smokedCount = cursor.getInt(1)
            val noSmokedCount = cursor.getInt(2)
            val smokedPercentage = cursor.getString(3)
            val noSmokedPercentage = cursor.getString(4)
            if (smokedPercentage != null && noSmokedPercentage != null) {
                arrayList.add(
                    SmokeCountAndPercentage(
                        totalAttempts.toString(),
                        smokedCount.toString(),
                        noSmokedCount.toString(),
                        smokedPercentage,
                        noSmokedPercentage
                    )
                )
            }
        }
        return arrayList
    }

    fun getTotalNoOfDays(): Int{
        var lifetimeDays = 0
        var db = dbHelper.readableDatabase
        val query = """
    SELECT COUNT(DISTINCT Date) AS LifetimeDays
    FROM TabQuitSmokingApp;
""".trimIndent()
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()){
            lifetimeDays = cursor.getInt(0)
        }
        return lifetimeDays
    }

    fun getOneDayHourlyAnalytics(): ArrayList<HourlyAnalyticData>{
        var list = ArrayList<HourlyAnalyticData>()
        val db = dbHelper.readableDatabase
        val query = """
    SELECT a.Hour AS Seqno,
    CASE
        WHEN a.hour BETWEEN 1 AND 11 THEN a.hour || ' a.m.'
        WHEN a.hour = 24 THEN 'midnight'
        WHEN a.hour = 12 THEN '12 p.m.'
        WHEN a.hour = 13 THEN '1 p.m.'
        WHEN a.hour = 14 THEN '2 p.m.'
        WHEN a.hour = 15 THEN '3 p.m.'
        WHEN a.hour = 16 THEN '4 p.m.'
        WHEN a.hour = 17 THEN '5 p.m.'
        WHEN a.hour = 18 THEN '6 p.m.'
        WHEN a.hour = 19 THEN '7 p.m.'
        WHEN a.hour = 20 THEN '8 p.m.'
        WHEN a.hour = 21 THEN '9 p.m.'
        WHEN a.hour = 22 THEN '10 p.m.'
        WHEN a.hour = 23 THEN '11 p.m.'
    END AS Hour,
    a.Reason,
    a.SmokedCount,
    ROUND(((a.SmokedCount * 100.0) / b.SmokedSumHourly), 2) || '%' AS ContributedReasonPercentage
    FROM (
            SELECT hour,
                Reason,
                COUNT(Smoking) AS SmokedCount
            FROM TabQuitSmokingApp tqsaa
            WHERE Smoking = 'Yes'
                AND Date >= date('now', '-1 day', 'localtime')
            GROUP BY hour, Reason
        ) AS a,
        (
            SELECT hour,
                COUNT(*) AS SmokedSumHourly
            FROM TabQuitSmokingApp tqsab
            WHERE Smoking = 'Yes'
                AND Date >= date('now', '-1 day', 'localtime')
            GROUP BY hour
        ) AS b
    WHERE a.hour = b.hour
    ORDER BY 1 DESC, 4 DESC;
""".trimIndent()

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val seqNo = cursor.getInt(0)
                val hour = cursor.getString(1)
                val reason = cursor.getString(2)
                val smokedCount = cursor.getInt(3)
                val contributedReasonPercentage = cursor.getString(4)
                list.add(HourlyAnalyticData(seqNo, hour, reason, smokedCount, contributedReasonPercentage))
            } while (cursor.moveToNext())
        }
        return list
    }

    fun getSevenDaysHourlyAnalytics(): ArrayList<HourlyAnalyticData>{
        var list = ArrayList<HourlyAnalyticData>()
        val db = dbHelper.readableDatabase
        val query = """
    SELECT a.Hour AS Seqno,
    CASE
        WHEN a.hour BETWEEN 1 AND 11 THEN a.hour || ' a.m.'
        WHEN a.hour = 24 THEN 'midnight'
        WHEN a.hour = 12 THEN '12 p.m.'
        WHEN a.hour = 13 THEN '1 p.m.'
        WHEN a.hour = 14 THEN '2 p.m.'
        WHEN a.hour = 15 THEN '3 p.m.'
        WHEN a.hour = 16 THEN '4 p.m.'
        WHEN a.hour = 17 THEN '5 p.m.'
        WHEN a.hour = 18 THEN '6 p.m.'
        WHEN a.hour = 19 THEN '7 p.m.'
        WHEN a.hour = 20 THEN '8 p.m.'
        WHEN a.hour = 21 THEN '9 p.m.'
        WHEN a.hour = 22 THEN '10 p.m.'
        WHEN a.hour = 23 THEN '11 p.m.'
    END AS Hour,
    a.Reason,
    a.SmokedCount,
    ROUND(((a.SmokedCount * 100.0) / b.SmokedSumHourly), 2) || '%' AS ContributedReasonPercentage
    FROM (
            SELECT hour,
                Reason,
                COUNT(Smoking) AS SmokedCount
            FROM TabQuitSmokingApp tqsaa
            WHERE Smoking = 'Yes'
                AND Date >= date('now', '-7 day', 'localtime')
            GROUP BY hour, Reason
        ) AS a,
        (
            SELECT hour,
                COUNT(*) AS SmokedSumHourly
            FROM TabQuitSmokingApp tqsab
            WHERE Smoking = 'Yes'
                AND Date >= date('now', '-7 day', 'localtime')
            GROUP BY hour
        ) AS b
    WHERE a.hour = b.hour
    ORDER BY 1 DESC, 4 DESC;
""".trimIndent()

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val seqNo = cursor.getInt(0)
                val hour = cursor.getString(1)
                val reason = cursor.getString(2)
                val smokedCount = cursor.getInt(3)
                val contributedReasonPercentage = cursor.getString(4)
                list.add(HourlyAnalyticData(seqNo, hour, reason, smokedCount, contributedReasonPercentage))

            } while (cursor.moveToNext())
        }
        return list
    }

    fun get31DaysHourlyAnalytics(): ArrayList<HourlyAnalyticData>{
        var list = ArrayList<HourlyAnalyticData>()
        val db = dbHelper.readableDatabase
        val query = """
    SELECT a.Hour AS Seqno,
    CASE
        WHEN a.hour BETWEEN 1 AND 11 THEN a.hour || ' a.m.'
        WHEN a.hour = 24 THEN 'midnight'
        WHEN a.hour = 12 THEN '12 p.m.'
        WHEN a.hour = 13 THEN '1 p.m.'
        WHEN a.hour = 14 THEN '2 p.m.'
        WHEN a.hour = 15 THEN '3 p.m.'
        WHEN a.hour = 16 THEN '4 p.m.'
        WHEN a.hour = 17 THEN '5 p.m.'
        WHEN a.hour = 18 THEN '6 p.m.'
        WHEN a.hour = 19 THEN '7 p.m.'
        WHEN a.hour = 20 THEN '8 p.m.'
        WHEN a.hour = 21 THEN '9 p.m.'
        WHEN a.hour = 22 THEN '10 p.m.'
        WHEN a.hour = 23 THEN '11 p.m.'
    END AS Hour,
    a.Reason,
    a.SmokedCount,
    ROUND(((a.SmokedCount * 100.0) / b.SmokedSumHourly), 2) || '%' AS ContributedReasonPercentage
    FROM (
            SELECT hour,
                Reason,
                COUNT(Smoking) AS SmokedCount
            FROM TabQuitSmokingApp tqsaa
            WHERE Smoking = 'Yes'
                AND Date >= date('now', '-30 day', 'localtime')
            GROUP BY hour, Reason
        ) AS a,
        (
            SELECT hour,
                COUNT(*) AS SmokedSumHourly
            FROM TabQuitSmokingApp tqsab
            WHERE Smoking = 'Yes'
                AND Date >= date('now', '-30 day', 'localtime')
            GROUP BY hour
        ) AS b
    WHERE a.hour = b.hour
    ORDER BY 1 DESC, 4 DESC;
""".trimIndent()

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val seqNo = cursor.getInt(0)
                val hour = cursor.getString(1)
                val reason = cursor.getString(2)
                val smokedCount = cursor.getInt(3)
                val contributedReasonPercentage = cursor.getString(4)
                list.add(HourlyAnalyticData(seqNo, hour, reason, smokedCount, contributedReasonPercentage))
            } while (cursor.moveToNext())
        }
        return list
    }

    fun getAllDaysHourlyAnalytics(): ArrayList<HourlyAnalyticData>{
        var list = ArrayList<HourlyAnalyticData>()
        val db = dbHelper.readableDatabase
        val query = """
    SELECT a.Hour as Seqno,
    CASE
        WHEN a.hour BETWEEN 1 AND 11 THEN a.hour || ' a.m.'
        WHEN a.hour = 24 THEN 'midnight'
        WHEN a.hour = 12 THEN '12 p.m.'
        WHEN a.hour = 13 THEN '1 p.m.'
        WHEN a.hour = 14 THEN '2 p.m.'
        WHEN a.hour = 15 THEN '3 p.m.'
        WHEN a.hour = 16 THEN '4 p.m.'
        WHEN a.hour = 17 THEN '5 p.m.'
        WHEN a.hour = 18 THEN '6 p.m.'
        WHEN a.hour = 19 THEN '7 p.m.'
        WHEN a.hour = 20 THEN '8 p.m.'
        WHEN a.hour = 21 THEN '9 p.m.'
        WHEN a.hour = 22 THEN '10 p.m.'
        WHEN a.hour = 23 THEN '11 p.m.'
    END AS Hour,
    a.Reason,
    a.SmokedCount,
    ROUND(((a.SmokedCount * 100.0) / b.SmokedSumHourly), 2) || '%' AS ContributedReasonPercentage
    FROM (
            SELECT hour,
                Reason,
                COUNT(Smoking) AS SmokedCount
            FROM TabQuitSmokingApp tqsaa
            WHERE Smoking = 'Yes'
            GROUP BY hour, Reason
        ) AS a,
        (
            SELECT hour,
                COUNT(*) AS SmokedSumHourly
            FROM TabQuitSmokingApp tqsab
            WHERE Smoking = 'Yes'
            GROUP BY hour
        ) AS b
    WHERE a.hour = b.hour
    ORDER BY 1 DESC, 4 DESC;
""".trimIndent()

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val seqNo = cursor.getInt(0)
                val hour = cursor.getString(1)
                val reason = cursor.getString(2)
                val smokedCount = cursor.getInt(3)
                val contributedReasonPercentage = cursor.getString(4)
                list.add(HourlyAnalyticData(seqNo, hour, reason, smokedCount, contributedReasonPercentage))
            } while (cursor.moveToNext())
        }
        return list
    }




    inner class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL(createTableQuery)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            TODO("Not yet implemented")
        }

    }
}