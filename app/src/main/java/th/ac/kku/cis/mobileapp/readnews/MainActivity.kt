package th.ac.kku.cis.mobileapp.readnews

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var auth: FirebaseAuth
    lateinit var googleClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        var gso = GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleClient = GoogleSignIn.getClient(this,gso)
        auth = FirebaseAuth.getInstance()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navView.getHeaderView(0)
        val navUsername:TextView = headerView.findViewById(R.id.UserName) as TextView
        if(auth.getCurrentUser() ==null){
            updateUI(null)

        }
        else{
            updateUI(auth.getCurrentUser())
        }
        navUsername.setOnClickListener( {v->log()} )






        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send,R.id.nav_login,R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    private fun log(){
        if(auth.getCurrentUser() ==null){
            updateUI(null)
            singIn()
        }
        else{
            updateUI(auth.getCurrentUser())
            singOut()
        }
    }
    private fun singIn() {
        var signInInent = googleClient.signInIntent
        startActivityForResult(signInInent,101)
    }
    private fun singOut() {
        auth.signOut()
        googleClient.signOut().addOnCompleteListener(this) {
            updateUI(null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==101){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                firebaseAuth(account!!)
                //FirebaseAuth(account)
            }catch (e:ApiException){
                updateUI(null)
            }
        }
    }
    private fun firebaseAuth(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){task->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    updateUI(user)
                }
                else{
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        val navigationView: NavigationView = findViewById(R.id.nav_view) as NavigationView

        val headerView: View = navigationView.getHeaderView(0)
        val navUsername:TextView = headerView.findViewById(R.id.UserName) as TextView
        val navEmail: TextView = headerView.findViewById(R.id.Email) as TextView
        val nav_Menu: Menu = navigationView.getMenu()
        if(user==null){
            navUsername.text = "Not Found"

            navEmail.text = ""
            nav_Menu.findItem(R.id.nav_login ).setVisible(true)

            nav_Menu.findItem(R.id.nav_logout ).setVisible(false)

            nav_Menu.findItem(R.id.nav_gallery ).setVisible(false)
            nav_Menu.findItem(R.id.nav_slideshow ).setVisible(false)
        }
        else{
            nav_Menu.findItem(R.id.nav_login ).setVisible(!true)
            nav_Menu.findItem(R.id.nav_logout ).setVisible(!false)
            navEmail.text = user.email.toString()
            navUsername.text = user.displayName.toString()
            nav_Menu.findItem(R.id.nav_gallery ).setVisible(true)
            nav_Menu.findItem(R.id.nav_slideshow ).setVisible(true)
        }
    }
}
