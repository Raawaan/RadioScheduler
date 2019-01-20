package com.example.rawan.radio

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

object WidgetNotifier {
    fun notify(application: Application, context: Context):Intent{
        val intent =  Intent(context, MyAppWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(application)
                .getAppWidgetIds(ComponentName(application,MyAppWidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        return intent
    }
}