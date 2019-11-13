package com.nva.tpcell.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nva.tpcell.R
import com.nva.tpcell.fragments.DriveDetailsFragment
import com.nva.tpcell.fragments.DrivesFragment
import com.nva.tpcell.fragments.StudentDetailsFragment
import com.nva.tpcell.fragments.StudentsFragment
import com.nva.tpcell.models.Drive
import com.nva.tpcell.models.Student
import com.nva.tpcell.utils.Database

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    StudentDetailsFragment.OnFragmentInteractionListener,
    DriveDetailsFragment.OnFragmentInteractionListener,
    StudentsFragment.OnListFragmentInteractionListener,
    DrivesFragment.OnListFragmentInteractionListener {
    override fun onListFragmentInteraction(item: Drive?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onListFragmentInteraction(item: Student?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    private lateinit var studentsFragment: StudentsFragment
    private lateinit var drivesFragment: DrivesFragment
    private lateinit var studentDetailsFragment: StudentDetailsFragment

    private var dbDatabase: Database = Database()
    private var user: FirebaseUser? = null

    private var isUserAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Getting if user is admin from Login Activity
        val bundle: Bundle? = intent.extras
        val isUserAdminARG = bundle?.getBoolean("is-user-admin")
        if (isUserAdminARG != null) {
            isUserAdmin = isUserAdminARG
        }

        // NavDrawer Initialization
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        // Identifying User
        user = FirebaseAuth.getInstance().currentUser


        // Getting Nav Header TextViews
        val navHeaderView = navView.getHeaderView(0)

        // Putting Email in Nav Header
        val navHeaderUserEmail: TextView = navHeaderView.findViewById(R.id.nav_user_email)
        val navHeaderUserName: TextView = navHeaderView.findViewById(R.id.nav_user_name)

        navHeaderUserEmail.text = user?.email
        navHeaderUserName.text = user?.displayName

        // Getting Nav Menu Items - Profile, Students, Drives
        val profileMenuItem = navView.menu.findItem(R.id.nav_profile)
        val studentsMenuItem = navView.menu.findItem(R.id.nav_students)
        val drivesMenuItem = navView.menu.findItem(R.id.nav_drives)


        // If user is an Admin, change Nav Menu Items
        // else get current user data and change Nav Menu Items
        if (isUserAdmin) {
            studentsMenuItem.isVisible = true
            profileMenuItem.isVisible = false

        } else {
            studentsMenuItem.isVisible = false
            profileMenuItem.isVisible = true

            dbDatabase.getStudent(this, user?.email)
        }

        // Default Menu Item is Drives, if activity just launched, select drives menu and load its fragment
        drivesFragment = DrivesFragment.newInstance(isUserAdmin)

        if (savedInstanceState == null) {
            drivesMenuItem.isChecked = true
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, drivesFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            //R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_drives -> {

                // Loading drivesFragment
                drivesFragment = DrivesFragment.newInstance(isUserAdmin)

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, drivesFragment)
//                    .addToBackStack(drivesFragment.toString())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()

            }
            R.id.nav_students -> {

                // Loading studentsFragment
                studentsFragment = StudentsFragment.newInstance(isUserAdmin, null)

                if (isUserAdmin) {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, studentsFragment)
//                    .addToBackStack(studentsFragment.toString())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

            }
            R.id.nav_profile -> {

                if ((!isUserAdmin) && (user?.email != null)) {

                    val studentDataArg = if (dbDatabase.studentData == null) {
                        Student(user?.email!!)
                    } else {
                        dbDatabase.studentData
                    }

                    // Loading studentDetailsFragment
                    studentDetailsFragment =
                        StudentDetailsFragment.newInstance(
                            isUserAdmin,
                            studentDataArg!!
                        )

                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, studentDetailsFragment)
                        // This can be used to go back to Drive fragment after adding details by student
                        //.addToBackStack(studentDetailsFragment.toString())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

            }
            R.id.nav_settings -> {

                // Not implemented, hidden in Nav Drawer

            }
            R.id.nav_logout -> {

                // Sign out current user
                val auth = FirebaseAuth.getInstance()
                auth.signOut()

                // Start Login Activity
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)

                finish()

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}
