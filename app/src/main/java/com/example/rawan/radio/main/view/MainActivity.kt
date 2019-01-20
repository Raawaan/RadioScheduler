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
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.content.ComponentName
import android.support.v4.app.Fragment
import android.text.format.Time
import com.example.rawan.radio.addProgram.view.AddProgramActivity
import android.os.Build
import android.os.PersistableBundle
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.rawan.radio.*
import com.example.rawan.radio.StartService
import com.example.rawan.radio.main.model.MainModel
import com.example.rawan.radio.main.presenter.MainPresenter
import com.example.rawan.radio.radioDatabase.RadioDatabase
import com.example.rawan.radio.radioDatabase.RadioProgramEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.android.synthetic.main.preview_list.view.*
import java.util.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener




class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,MainView {

    lateinit var mainPresenter: MainPresenter
    private val time = Time()
    private lateinit var mGoogleSignInClient: GoogleSignInClient


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)
        val navUsername = headerView.findViewById(R.id.userName) as TextView
        val navEmail = headerView.findViewById(R.id.userEmail) as TextView
        val userImageIv = headerView.findViewById(R.id.userImage) as ImageView
        navUsername.text=intent.extras.get("displayName").toString()
        navEmail.text=intent.extras.get("email").toString()
        if (intent.extras.get("photoUrl")!=null)
            Picasso.get().load(intent.extras.get("photoUrl").toString()).fit().centerCrop().into(userImageIv)
        else
            userImageIv.setImageResource(R.drawable.ic_image_black_24dp)

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
        val c = Calendar.getInstance()
        time.setToNow()
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
                sendIntent.type = "text/plain"
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey all check out my new radio scheduler application!")
                startActivity(Intent.createChooser(sendIntent,"Share Radio Application"))
//                replaceFragments(ShareApplicationFragment.newInstance())
            }
            R.id.nav_about ->{
                replaceFragments(AboutFragment.newInstance())
            }
            R.id.nav_sign_out-> signOut()
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this) {
                    Intent(this,SignInActivity::class.java).apply {
                        startActivity(this)
                    }
                    finish()
                }
    }
    private fun replaceFragments(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentPlaceholder, fragment).commit()
    }
    override fun toast(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
        sendBroadcast( WidgetNotifier.notify(application,this))
    }
    override fun nextRadio(nextRadio: RadioProgramEntity){
        val bundle = PersistableBundle()
        bundle.putInt("radioId", nextRadio.radioId)
        bundle.putLong("stopService",nextRadio.toHour)
            startRadioPlayService(bundle, nextRadio)
        sendBroadcast( WidgetNotifier.notify(application,this))
    }
    private fun startRadioPlayService(bundle: PersistableBundle, nextRadio: RadioProgramEntity) {
       MyJobScheduler.radioJobScheduler(TimeInMilliSeconds.timeInMilli(nextRadio.fromHour),
              applicationContext,StartService::class.java,bundle,1 )
    }
}