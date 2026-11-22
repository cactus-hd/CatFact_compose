package com.amir.catfact.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.amir.catfact.MainActivity
import com.amir.catfact.R
import com.amir.catfact.utils.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CatFactWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.amir.catfact.ACTION_REFRESH_WIDGET"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, CatFactWidget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            // 1. Show loading text immediately
            val loadingViews = RemoteViews(context.packageName, R.layout.widget_cat_fact).apply {
                setTextViewText(R.id.widget_fact_text, "Fetching new cat fact... 🐾")
            }
            appWidgetManager.updateAppWidget(componentName, loadingViews)

            // 2. Fetch new fact in background
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitInstance.api.getRandomFact()
                    if (response.isSuccessful && response.body() != null) {
                        val factText = response.body()!!.fact
                        withContext(Dispatchers.Main) {
                            for (id in appWidgetIds) {
                                updateWidgetWithFact(context, appWidgetManager, id, factText)
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            showErrorOnWidget(context, appWidgetManager, componentName)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showErrorOnWidget(context, appWidgetManager, componentName)
                    }
                }
            }
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getRandomFact()
                val factText = if (response.isSuccessful && response.body() != null) {
                    response.body()!!.fact
                } else {
                    "Cats make life better! 🐱 Click refresh for a new fact."
                }
                withContext(Dispatchers.Main) {
                    updateWidgetWithFact(context, appWidgetManager, appWidgetId, factText)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    updateWidgetWithFact(
                        context,
                        appWidgetManager,
                        appWidgetId,
                        "Cats sleep for 70% of their lives! 🐾 Click refresh to try again."
                    )
                }
            }
        }
    }

    private fun updateWidgetWithFact(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        factText: String
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_cat_fact).apply {
            setTextViewText(R.id.widget_fact_text, factText)

            // Click container -> open MainActivity
            val appIntent = Intent(context, MainActivity::class.java)
            val pendingAppIntent = PendingIntent.getActivity(
                context,
                0,
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setOnClickPendingIntent(R.id.widget_container, pendingAppIntent)

            // Click refresh button -> send broadcast to CatFactWidget
            val refreshIntent = Intent(context, CatFactWidget::class.java).apply {
                action = ACTION_REFRESH_WIDGET
            }
            val pendingRefreshIntent = PendingIntent.getBroadcast(
                context,
                1,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setOnClickPendingIntent(R.id.widget_refresh_button, pendingRefreshIntent)
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun showErrorOnWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        componentName: ComponentName
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_cat_fact).apply {
            setTextViewText(R.id.widget_fact_text, "Failed to load fact. Click 🔄 Refresh to try again!")
        }
        appWidgetManager.updateAppWidget(componentName, views)
    }
}