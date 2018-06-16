package com.angryscarf.gamenews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.angryscarf.gamenews.Fragments.NewDetailsFragment;
import com.angryscarf.gamenews.Model.Data.New;

public class NewDetailsActivity extends AppCompatActivity implements NewDetailsFragment.OnNewDetFragmentInteractionListener{

    public static final String NEW_EXTRA = "new_extra_arg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_details);

        Intent intent = getIntent();
        New aNew = (New) intent.getExtras().getSerializable(NEW_EXTRA);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.new_details_activity_container, NewDetailsFragment.newInstance(aNew))
                .commit();


    }

}
