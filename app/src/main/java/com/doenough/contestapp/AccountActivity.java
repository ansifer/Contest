package com.doenough.contestapp;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doenough.contestapp.account_fragments.MyEarnFragment;
import com.doenough.contestapp.account_fragments.MyEventFragment;
import com.doenough.contestapp.account_fragments.MyVoteFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.subinkrishna.widget.CircularImageView;

public class AccountActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;


    private ViewPager mViewPager;
    private ImageButton settingButton;

    private String userName;
    private Uri userPicture;
    private TabLayout tabLayout;

    private FirebaseUser user;
    private DatabaseReference eventReference;
    private DatabaseReference voteReference;
    private DatabaseReference earnReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        CircularImageView profilePicture = findViewById(R.id.profile_picture);
        TextView profileName = findViewById(R.id.profile_name);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile");
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        eventReference = firebaseDatabase.getReference("users").child(user.getUid()).child("myevents");
        voteReference = firebaseDatabase.getReference("users").child(user.getUid()).child("voted");
        earnReference = firebaseDatabase.getReference("users").child(user.getUid()).child("earned");
        if(user == null) {
            startActivity(new Intent(AccountActivity.this, LoginActivity.class));
            finish();
        }
        else {
            userName = user.getDisplayName();
            if(user.getPhotoUrl() != null) {
                userPicture = user.getPhotoUrl();
            }
        }
        profileName.setText(userName);
        String[] placeholderTextArray = userName.split(" ");
        String placeholder = "";
        for (int i = 0; i < placeholderTextArray.length; i++) {
            String temp = placeholderTextArray[i];
            placeholder = placeholder + temp.charAt(0);
        }
        profilePicture.setPlaceholder(placeholder);
        Log.e("tag", "Image"+userPicture);
        if(userPicture != null) {
            Glide.with(getApplicationContext()).load(userPicture).dontAnimate().into(profilePicture);
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        settingButton = findViewById(R.id.settings_button);


        LinearLayout tabOne = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.tab_custom_view, null);
        TextView number1 = tabOne.findViewById(R.id.number_tab);
        number1.setText("0");
        TextView name1 = tabOne.findViewById(R.id.name_tab);
        name1.setText("My Event");
        tabLayout.getTabAt(0).setCustomView(tabOne);

        LinearLayout tabTwo = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.tab_custom_view, null);
        TextView number2 = tabTwo.findViewById(R.id.number_tab);
        number2.setText("0");
        TextView name2 = tabTwo.findViewById(R.id.name_tab);
        name2.setText("Voted");
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        LinearLayout tabThree = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.tab_custom_view, null);
        TextView number3 = tabThree.findViewById(R.id.number_tab);
        number3.setText("0");
        TextView name3 = tabThree.findViewById(R.id.name_tab);
        name3.setText("Earned");
        tabLayout.getTabAt(2).setCustomView(tabThree);


        //MyVote Listener
        eventReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setupTabs0(Long.toString(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Vote Listener
        voteReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setupTabs1(Long.toString(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Earn Listener
        earnReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setupTabs2(Long.toString(dataSnapshot.getValue(Long.class)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, EditAccountActivity.class));
            }
        });


    }

    private void setupTabs0(String numberEvent) {
        if(tabLayout.getTabAt(0).getCustomView() != null) {
            LinearLayout tabOne = (LinearLayout) tabLayout.getTabAt(0).getCustomView();
            TextView number1 = tabOne.findViewById(R.id.number_tab);
            number1.setText(numberEvent);
            TextView name1 = tabOne.findViewById(R.id.name_tab);
            name1.setText("My Event");
        }

    }

    private void setupTabs1(String numberVote) {
        LinearLayout tabTwo;
        if(tabLayout.getTabAt(1).getCustomView() != null) {
            tabTwo = (LinearLayout) tabLayout.getTabAt(1).getCustomView();
            TextView number2 = tabTwo.findViewById(R.id.number_tab);
            number2.setText(numberVote);
            TextView name2 = tabTwo.findViewById(R.id.name_tab);
            name2.setText("Voted");
        }

    }

    private void setupTabs2(String earned) {
        LinearLayout tabThree;
        if(tabLayout.getTabAt(2).getCustomView() != null) {
            tabThree = (LinearLayout) tabLayout.getTabAt(2).getCustomView();
            TextView number3 = tabThree.findViewById(R.id.number_tab);
            number3.setText(earned);
            TextView name3 = tabThree.findViewById(R.id.name_tab);
            name3.setText("Earned");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_account, container, false);
            TextView textView = rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment;
            if(position == 0) {
                fragment = new MyEventFragment();
            }
            else if (position == 1) {
                fragment = new MyVoteFragment();
            }
            else if(position == 2) {
                fragment = new MyEarnFragment();
            }
            else {
                fragment = new MyEventFragment();
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
