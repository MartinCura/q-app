package ar.uba.fi.remy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ar.uba.fi.remy.model.ClickListener
import ar.uba.fi.remy.model.NavigationItemModel
import ar.uba.fi.remy.model.NavigationRVAdapter
import ar.uba.fi.remy.model.RecyclerTouchListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: NavigationRVAdapter
    private var items = arrayListOf(
        NavigationItemModel(R.drawable.ic_baseline_group_24, "Contactos"),
        NavigationItemModel(R.drawable.ic_event, "Eventos"),
        NavigationItemModel(R.drawable.ic_history, "Historial"),
        NavigationItemModel(R.drawable.ic_cancel, "Cerrar Sesión")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*this.supportActionBar?.hide()*/
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        // Side Navigation Menu
        // Set the toolbar
        setSupportActionBar(toolbar)

        // Setup Recyclerview's Layout
        navigation_rv.layoutManager = LinearLayoutManager(this)
        navigation_rv.setHasFixedSize(true)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Icon click
        toolbar_icon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Add Item Touch Listener
        navigation_rv.addOnItemTouchListener(RecyclerTouchListener(this, object : ClickListener {
            override fun onClick(view: View, position: Int) {
                when (position) {
                    0 -> {
                        goContacts()
                    }
                    1 -> {
                        goEvents()
                    }
                    2 -> {
                        goRecipesCooked()
                    }
                    3 -> {
                        cerrarSesion()
                    }
                }
                Handler().postDelayed({
                    drawerLayout.closeDrawer(GravityCompat.START)
                }, 200)
            }
        }))

        updateAdapter(0)

        // Bottom Navigation Menu
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,
                R.id.navigation_chango, R.id.navigation_inventory
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun updateAdapter(highlightItemPos: Int) {
        adapter = NavigationRVAdapter(items, highlightItemPos)
        navigation_rv.adapter = adapter
        adapter.notifyDataSetChanged()
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Checking for fragment count on back stack
            if (supportFragmentManager.backStackEntryCount > 0) {
                // Go to the previous fragment
                supportFragmentManager.popBackStack()
            } else {
                // Exit the app
                super.onBackPressed()
            }
        }
    }

    private fun goLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        this.finish()
    }

    private fun goEvents() {
        val intent = Intent(this, EventsActivity::class.java)
        startActivity(intent)
    }

    private fun goContacts() {
        val intent = Intent(this, ContactsActivity::class.java)
        startActivity(intent)
    }

    private fun goRecipesCooked() {
        val intent = Intent(this, RecipesCookedActivity::class.java)
        startActivity(intent)
    }

    private fun cerrarSesion() {
        //Access sharedPreferences
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()
        editor?.clear()
        editor?.apply()
        goLogin()
    }
}
