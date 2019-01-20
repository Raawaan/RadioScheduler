package com.example.rawan.radio

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.text.format.Time
import com.example.rawan.radio.main.model.MainModel
import com.example.rawan.radio.main.presenter.MainPresenter
import com.example.rawan.radio.main.view.MainView
import com.example.rawan.radio.radioDatabase.RadioDatabase
import com.example.rawan.radio.radioDatabase.RadioProgramEntity
import java.util.*
import android.widget.RemoteViews
import java.util.concurrent.TimeUnit


class MyAppWidgetProvider:AppWidgetProvider(),MainView {
    lateinit var mainPresenter: MainPresenter
    private val time = Time()
    lateinit var views:RemoteViews
    var appWidgetManager: AppWidgetManager?=null
    var appWidgetIds:IntArray?=null
    override fun toast(message: String) {

    }

    override fun nextRadio(nextRadio: RadioProgramEntity) {
        views.setTextViewText(R.id.widgetTextView,"Next Radio starts at "+TimeUnit.MILLISECONDS.toMinutes(nextRadio.fromHour)
                .div(60).toString()+":"+
                (TimeUnit.MILLISECONDS.toMinutes(nextRadio.fromHour)%60).toString()+" and ends at "+
                TimeUnit.MILLISECONDS.toMinutes(nextRadio.toHour)
                        .div(60).toString()+":"+
                (TimeUnit.MILLISECONDS.toMinutes(nextRadio.toHour)%60).toString())
        appWidgetIds?.forEach {
            appWidgetManager?.updateAppWidget(it,views)
        }
    }
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val c = Calendar.getInstance()
        time.setToNow()
        this.appWidgetIds=appWidgetIds
        this.appWidgetManager=appWidgetManager
        mainPresenter= MainPresenter(this, MainModel(RadioDatabase.getInstance(context!!)))
        mainPresenter.selectNextRadio((time.hour*60000*60+time.minute*60000-6000).toLong(),
                c.get(Calendar.DAY_OF_WEEK))
       views= RemoteViews(context.packageName,R.layout.widget_layout )
        views.setTextViewText(R.id.widgetTextView,"No upcoming radios")
        appWidgetIds?.forEach {
            appWidgetManager?.updateAppWidget(it,views)
        }
    }
}