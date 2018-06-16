package com.angryscarf.gamenews;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.angryscarf.gamenews.Fragments.GameFragment;
import com.angryscarf.gamenews.Fragments.NewDetailsFragment;
import com.angryscarf.gamenews.Fragments.NewsFragment;
import com.angryscarf.gamenews.Fragments.PlayerDetailsFragment;
import com.angryscarf.gamenews.Fragments.PlayersFragment;
import com.angryscarf.gamenews.Model.GameNewsViewModel;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GameFragment.OnGameFragmentInteractionListener,
        NewsFragment.OnNewsFragmentInteractionListener,
        PlayersFragment.OnPlayersFragmentInteractionListener,
        NewDetailsFragment.OnNewDetFragmentInteractionListener,
        PlayerDetailsFragment.OnPlayerDetFragmentInteractionListener
{

    private GameNewsViewModel gameNewsViewModel;
    private FrameLayout fragContainer;

    private GameFragment gameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {
            gameFragment = GameFragment.newInstance(null, false);
        }
        else {
            gameFragment = (GameFragment) getSupportFragmentManager().getFragments().get(0);
        }

        fragContainer = findViewById(R.id.main_container_content);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(fragContainer.getId(), gameFragment)
                .commit();



        gameNewsViewModel = ViewModelProviders.of(this).get(GameNewsViewModel.class);

        gameNewsViewModel.loggedInStatus().subscribe(aBoolean -> {
           if (!aBoolean) {
               onLogOut();
           }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        //FAB button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_all);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            }
            else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.getItem(0).setIcon(gameFragment.isFavorites()? R.drawable.ic_favorite: R.drawable.ic_favorite_border);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorites) {
            if(gameFragment.isFavorites()) {
                item.setIcon(R.drawable.ic_favorite_border);
                gameFragment.filterFavorites(false);
            }
            else {
                item.setIcon(R.drawable.ic_favorite);
                gameFragment.filterFavorites(true);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all) {
            // Handle the camera action
            gameFragment.filterByGame(null);
        } else if (id == R.id.nav_lol) {
            gameFragment.filterByGame("lol");
        } else if (id == R.id.nav_csgo) {
            gameFragment.filterByGame("csgo");
        } else if (id == R.id.nav_overwatch) {
            gameFragment.filterByGame("overwatch");
        }
        else if (id == R.id.nav_logout) {
            onLogOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onLogOut() {
        gameNewsViewModel.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
