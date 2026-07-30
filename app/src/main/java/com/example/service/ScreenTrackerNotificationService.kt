package com.example.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.data.local.AppDatabase
import com.example.data.model.NotificationLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScreenTrackerNotificationService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        val packageName = sbn.packageName ?: return
        if (packageName == "android" || packageName == applicationContext.packageName) return

        val extras = sbn.notification?.extras ?: return
        val title = extras.getCharSequence("android.title")?.toString() ?: "Notification"
        val text = extras.getCharSequence("android.text")?.toString() ?: ""

        val pm = applicationContext.packageManager
        val appName = try {
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }

        serviceScope.launch {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                db.notificationLogDao().insertLog(
                    NotificationLog(
                        packageName = packageName,
                        appName = appName,
                        title = title,
                        text = text,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
