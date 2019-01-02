package com.example.rawan.radio.main.view

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.rawan.radio.home.view.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.app.Activity
import android.support.v4.app.Fragment
import android.text.format.Time
import com.example.rawan.radio.addProgram.view.AddProgramActivity
import android.app.job.JobScheduler
import android.app.job.JobInfo
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.PersistableBundle
import android.support.annotation.RequiresApi
import android.widget.Toast
import com.example.rawan.radio.*
import com.example.rawan.radio.StartService
import com.example.rawan.radio.main.model.MainModel
import com.example.rawan.radio.main.presenter.MainPresenter
import com.example.rawan.radio.radioDatabase.RadioDatabase
import com.example.rawan.radio.radioDatabase.RadioProgramEntity
import java.util.*
import kotlin.math.abs


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,MainView {

    private var listener: FragmentClickListener? = null

    fun setOnClickListener(listener: FragmentClickListener) {
        this.listener = listener
    }
    private val time = Time()
    lateinit var mainPresenter: MainPresenter
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportFragmentManager.beginTransaction().add(R.id.fragmentPlaceholder, HomeFragment.newInstance(), "a").commit()

        fab.setOnClickListener { view ->
     val intent = Intent(this, AddProgramActivity::class.java)
            startActivity(intent)
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()
        time.setToNow()
        val c = Calendar.getInstance()

        mainPresenter= MainPresenter(this, MainModel(RadioDatabase.getInstance(this)))
        mainPresenter.selectNextRadio((time.hour*60000*60+time.minute*60000-6000).toLong(),
                c.get(Calendar.DAY_OF_WEEK))


    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.favorite -> {
                listener?.toFavorite()
                return true
            }
            R.id.alphabetical -> {
                listener?.toAlphabetical()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==1){
            if (resultCode == Activity.RESULT_OK) {
                val result = data?.getStringExtra("countryCode")
                result?.length
            }
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_program -> {
                replaceFragments(HomeFragment.newInstance())
            }
            R.id.nav_setting ->{
                replaceFragments(SettingFragment.newInstance())
            }
            R.id.nav_share_app ->{
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey all check out my new radio scheduler application!")
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
//                replaceFragments(ShareApplicationFragment.newInstance())
            }
            R.id.nav_about ->{
                replaceFragments(AboutFragment.newInstance())
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun replaceFragments(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentPlaceholder, fragment).commit()
    }
    override fun toast(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }
    override fun nextRadio(nextRadio: RadioProgramEntity) {
        val bundle = PersistableBundle()
        bundle.putInt("radioId", nextRadio.radioId)
        val jobScheduler = applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        startRadioPlayService(bundle, nextRadio, jobScheduler)
        stopRadioService(nextRadio, jobScheduler)
    }

    private fun stopRadioService(nextRadio: RadioProgramEntity, jobScheduler: JobScheduler) {
        val componentName1 = ComponentName(applicationContext, StopService::class.java)
        val jobInfo1 = JobInfo.Builder(2, componentName1)
                .setMinimumLatency(abs(nextRadio.toHour
                        .minus((time.hour * 60000 * 60).plus(time.minute * 60000))))
                .setOverrideDeadline(abs(nextRadio.toHour
                        .minus((time.hour * 60000 * 60).plus(time.minute * 60000))))
                .build()
        jobScheduler.schedule(jobInfo1)
    }

    private fun startRadioPlayService(bundle: PersistableBundle, nextRadio: RadioProgramEntity, jobScheduler: JobScheduler) {
        val componentName = ComponentName(applicationContext, StartService::class.java)
        val jobInfo = JobInfo.Builder(1, componentName)
                .setExtras(bundle)
                .setMinimumLatency(abs((nextRadio.fromHour.minus((time.hour * 60000 * 60)
                        .plus(time.minute * 60000)))))
                .setOverrideDeadline(abs((nextRadio.fromHour.minus((time.hour * 60000 * 60)
                        .plus(time.minute * 60000)))))
                .build()
        jobScheduler.schedule(jobInfo)
    }


}
interface FragmentClickListener {
    fun toAlphabetical()
    fun toFavorite()
}
