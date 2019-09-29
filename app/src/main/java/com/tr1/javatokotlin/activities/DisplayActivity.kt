package com.tr1.javatokotlin.activities


import android.content.Context
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast

import com.tr1.javatokotlin.R
import com.tr1.javatokotlin.adapters.DisplayAdapter
import com.tr1.javatokotlin.app.Constants
import com.tr1.javatokotlin.extensions.showErrorMessage
import com.tr1.javatokotlin.extensions.toast
import com.tr1.javatokotlin.models.Repository
import com.tr1.javatokotlin.models.SearchResponse
import com.tr1.javatokotlin.retrofit.GithubAPIService
import com.tr1.javatokotlin.retrofit.RetrofitClient
import io.realm.Realm

import java.util.HashMap

import kotlinx.android.synthetic.main.activity_display.*
import kotlinx.android.synthetic.main.header.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DisplayActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit  var displayAdapter: DisplayAdapter
    private var browsedRepositories: List<Repository> = mutableListOf()
    private val githubAPIService: GithubAPIService by lazy {
        RetrofitClient.githubAPIService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Showing Browsed Results"

        setAppUserName()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        navigationView.setNavigationItemSelectedListener(this)

        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        val intent = intent
        if (intent.getIntExtra(Constants.KEY_QUERY_TYPE, -1) == Constants.SEARCH_BY_REPO) {
            val queryRepo = intent.getStringExtra(Constants.KEY_REPO_SEARCH)
            val repoLanguage = intent.getStringExtra(Constants.KEY_LANGUAGE)
            fetchRepositories(queryRepo, repoLanguage)
        } else {
            val githubUser = intent.getStringExtra(Constants.KEY_GITHUB_USER)
            fetchUserRepositories(githubUser)
        }
    }

    private fun setAppUserName() {

        val sp = getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val personName = sp.getString(Constants.KEY_PERSON_NAME, "User")

        val headView = navigationView.getHeaderView(0)
        headView.txvName.text = personName
    }

    private fun fetchUserRepositories(githubUser: String ) {

        githubAPIService.searchRepositoriesByUser(githubUser).enqueue(object : Callback<List<Repository>> {
            override fun onResponse(call: Call<List<Repository>>, response: Response<List<Repository>>) {
                if (response.isSuccessful) {
                    Log.i(TAG, "posts loaded from API $response")

                   response.body()?.let {
                       browsedRepositories = it
                   }

                    if (browsedRepositories.isNotEmpty())
                        setupRecyclerView(browsedRepositories)
                    else
                        toast("No Items Found")

                } else {
                    Log.i(TAG, "Error $response")
                    showErrorMessage(response.errorBody()!!)
                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                toast("Error fetching Result")
            }
        })
    }

    private fun fetchRepositories(queryRepo: String, repoLanguage: String) {
        var queryRepo = queryRepo

        val query = HashMap<String, String>()

        if (repoLanguage.isNotEmpty())
            queryRepo += " language:$repoLanguage"

            query["q"] = queryRepo

        githubAPIService.searchRepositories(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    Log.i(TAG, "posts loaded from API $response")

                    response.body()?.items?.let{
                        browsedRepositories = it
                    }

                    if (browsedRepositories.isEmpty())
                        setupRecyclerView(browsedRepositories)
                    else
                        toast("No Items Found")

                } else {
                    Log.i(TAG, "error $response")
                    showErrorMessage(response.errorBody()!!)
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                toast(t.toString(),Toast.LENGTH_LONG)

            }
        })
    }

    private fun setupRecyclerView(items: List<Repository>) {
        displayAdapter = DisplayAdapter(this, items)
        recyclerView.adapter = displayAdapter
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        menuItem.isChecked = true
        when (menuItem.itemId) {

            R.id.item_bookmark -> {
                consumeMenuEvent({showBookmarks()} ,"Showing Bookmarks")
            }

            R.id.item_browsed_results -> {
                consumeMenuEvent({ showBrowsedResults()} , "Showing Browsed Results")
            }
        }

        return true
    }

    private inline fun consumeMenuEvent(myFunc: () -> Unit, title:String){
        myFunc()
        closeDrawer()
        supportActionBar!!.title = title
    }

    private fun showBrowsedResults() {
        displayAdapter.swap(browsedRepositories!!)
    }

    private fun showBookmarks() {
        var realm = Realm.getDefaultInstance()

        realm.executeTransaction { realm: Realm ->
            var bookMarkRepoList = realm.where(Repository::class.java).findAll()
            displayAdapter.swap(bookMarkRepoList)
        }
    }

    private fun closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            closeDrawer()
        else {
            super.onBackPressed()
        }
    }

    companion object {

        private val TAG = DisplayActivity::class.java.simpleName
    }
}
