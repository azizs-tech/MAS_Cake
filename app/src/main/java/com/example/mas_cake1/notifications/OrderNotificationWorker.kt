package com.example.mas_cake1.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.mas_cake1.MainActivity
import com.example.mas_cake1.R
import java.util.concurrent.TimeUnit

/**
 * Shows a local notification "Your order #1042 is being prepared!"
 * 10 seconds after the customer places an order.
 */
class OrderNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val orderId = inputData.getInt(KEY_ORDER_ID, 0)
        val english = inputData.getBoolean(KEY_ENGLISH, true)
        showNotification(applicationContext, orderId, english)
        return Result.success()
    }

    companion object {
        private const val KEY_ORDER_ID = "orderId"
        private const val KEY_ENGLISH = "english"
        private const val CHANNEL_ID = "order_updates"

        fun schedule(context: Context, orderId: Int, english: Boolean) {
            val request = OneTimeWorkRequestBuilder<OrderNotificationWorker>()
                .setInitialDelay(10, TimeUnit.SECONDS)
                .setInputData(workDataOf(KEY_ORDER_ID to orderId, KEY_ENGLISH to english))
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }

        private fun showNotification(context: Context, orderId: Int, english: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.channel_orders),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
            }

            val openApp = PendingIntent.getActivity(
                context, 0,
                Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val title = if (english) "MAS Cake" else "MAS Cake"
            val text = if (english) "Your order #$orderId is being prepared! 🎂" else "Ваш заказ №$orderId уже готовится! 🎂"

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_cake)
                .setContentTitle(title)
                .setContentText(text)
                .setColor(0xFF4A2C2A.toInt())
                .setContentIntent(openApp)
                .setAutoCancel(true)
                .build()

            try {
                NotificationManagerCompat.from(context).notify(orderId, notification)
            } catch (e: SecurityException) {
                // Permission was revoked in the meantime — nothing to show.
            }
        }
    }
}
