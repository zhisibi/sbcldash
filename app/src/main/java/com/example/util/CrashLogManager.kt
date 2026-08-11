package com.example.util

import android.content.Context
import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CrashLogEntry(
    val id: Long = System.currentTimeMillis(),
    val timestamp: String,
    val threadName: String,
    val exceptionType: String,
    val message: String,
    val stackTrace: String
)

object CrashLogManager {
    private const val PREF_NAME = "zashboard_crash_logs"
    private const val KEY_CRASH_HISTORY = "crash_history_raw"
    private const val KEY_HAS_UNREAD_CRASH = "has_unread_crash"
    private const val MAX_LOGS = 30

    private var originalHandler: Thread.UncaughtExceptionHandler? = null
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        isInitialized = true

        val appContext = context.applicationContext
        originalHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                recordCrash(appContext, thread.name, throwable)
            } catch (e: Exception) {
                Log.e("CrashLogManager", "Failed to record crash", e)
            } finally {
                originalHandler?.uncaughtException(thread, throwable)
            }
        }
    }

    fun recordCrash(context: Context, threadName: String, throwable: Throwable) {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        val stackTrace = sw.toString()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())

        val exceptionType = throwable.javaClass.simpleName
        val message = throwable.localizedMessage ?: throwable.message ?: "Unknown Exception"

        val newEntry = "$timestamp|$threadName|$exceptionType|${message.replace("\n", " ")}|${stackTrace.replace("\n", "___BR___")}"

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val existingLogs = prefs.getString(KEY_CRASH_HISTORY, "") ?: ""
        
        val updatedLogs = if (existingLogs.isEmpty()) {
            newEntry
        } else {
            val lines = existingLogs.split("___LOG_SEP___").take(MAX_LOGS - 1)
            (listOf(newEntry) + lines).joinToString("___LOG_SEP___")
        }

        prefs.edit()
            .putString(KEY_CRASH_HISTORY, updatedLogs)
            .putBoolean(KEY_HAS_UNREAD_CRASH, true)
            .commit()
    }

    fun logCustomError(context: Context, tag: String, message: String, throwable: Throwable? = null) {
        val sw = StringWriter()
        if (throwable != null) {
            val pw = PrintWriter(sw)
            throwable.printStackTrace(pw)
        }
        val stackTrace = sw.toString().ifEmpty { "No stacktrace" }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())

        val newEntry = "$timestamp|AppTag:$tag|CustomError|${message.replace("\n", " ")}|${stackTrace.replace("\n", "___BR___")}"

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val existingLogs = prefs.getString(KEY_CRASH_HISTORY, "") ?: ""
        val updatedLogs = if (existingLogs.isEmpty()) {
            newEntry
        } else {
            val lines = existingLogs.split("___LOG_SEP___").take(MAX_LOGS - 1)
            (listOf(newEntry) + lines).joinToString("___LOG_SEP___")
        }

        prefs.edit()
            .putString(KEY_CRASH_HISTORY, updatedLogs)
            .putBoolean(KEY_HAS_UNREAD_CRASH, true)
            .apply()
    }

    fun getCrashLogs(context: Context): List<CrashLogEntry> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_CRASH_HISTORY, "") ?: ""
        if (raw.isEmpty()) return emptyList()

        return raw.split("___LOG_SEP___").mapNotNull { line ->
            val parts = line.split("|")
            if (parts.size >= 5) {
                CrashLogEntry(
                    timestamp = parts[0],
                    threadName = parts[1],
                    exceptionType = parts[2],
                    message = parts[3],
                    stackTrace = parts[4].replace("___BR___", "\n")
                )
            } else null
        }
    }

    fun hasUnreadCrash(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_HAS_UNREAD_CRASH, false)
    }

    fun markCrashRead(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_HAS_UNREAD_CRASH, false).apply()
    }

    fun clearLogs(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .remove(KEY_CRASH_HISTORY)
            .remove(KEY_HAS_UNREAD_CRASH)
            .apply()
    }
}
