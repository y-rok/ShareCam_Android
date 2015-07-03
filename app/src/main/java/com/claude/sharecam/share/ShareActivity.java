package com.claude.sharecam.share;

import java.util.ArrayList;
import java.util.Locale;


import android.content.Context;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.SharePerson;
import com.claude.sharecam.view.SlidingTabLayout;
import com.parse.ParseException;
import com.parse.ParseObject;

public class ShareActivity extends ActionBarActivity  {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    // Give the SlidingTabLayout the ViewPager
    SlidingTabLayout slidingTabLayout;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    Context context;

    ArrayList<IndividualItem> addedItems;//추가된 개인, 쉐어캠 친구
    public ArrayList<IndividualItem> contactItems;
    public ArrayList<IndividualItem> friendItems;
//    ArrayList<PersonItem> addedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        //init actionbar
        getSupportActionBar().hide();
        context=this;
        addedItems=new ArrayList<IndividualItem>();

        ((ImageView)findViewById(R.id.backBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //공유 대상 설정
        ((ImageView)findViewById(R.id.actionItem1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(addedItems!=null)
                {
                    ArrayList<SharePerson> scItems=new ArrayList<SharePerson>();
                    for(int i=0; i<addedItems.size(); i++)
                    {

                        //연락처 공유 설정 추가
                        if(addedItems.get(i).MODE== IndividualItem.CONTACT && !addedItems.get(i).isFriend)
                        {
                            IndividualItem tempItem=addedItems.get(i);
                            SharePerson sharePerson =new SharePerson(tempItem.personName, tempItem.phoneNumber,tempItem.contactProfile,false);
                            scItems.add(sharePerson);
                        }
                        //쉐어캠 친구 추가
                        else if(addedItems.get(i).MODE== IndividualItem.FRIEND)
                        {
                            IndividualItem tempItem=addedItems.get(i);
                            SharePerson sharePerson =new SharePerson(tempItem.objectId,tempItem.personName, tempItem.phoneNumber,tempItem.contactProfile,true);
                            scItems.add(sharePerson);
                        }
                    }

                    //공유 대상 local store에 저장
                    try {
                        ParseObject.unpinAll(ParseAPI.LABEL_SHARECONTACT);
                        ParseObject.pinAll(ParseAPI.LABEL_SHARECONTACT, scItems);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Util.setSharePersonList(context, scItems);
//                    Util.setSharePersonList(context, scItems.size(), 0);
                    Log.d("jyr","contactShare num="+scItems.size());


                    finish();
                }
                else
                    finish();
            }
        });

//        init();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Give the SlidingTabLayout the ViewPager
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        // Center the tabs in the layout
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(mViewPager);

    }

    public void setAddedItems(ArrayList<IndividualItem> addedItems)
    {
        this.addedItems.clear();
        Log.d("jyr","setAddedItem"+addedItems.size());
        for(int i=0; i<addedItems.size(); i++)
        {
            this.addedItems.add(addedItems.get(i));
        }

    }
/*
    //각 adapter에 필요한 데이터 불러옴
    private void init()
    {
        //연락처 데이터 불러옴
        personItems = Util.getContactList(this);

        //쉐어캠 친구 데이터 불러옴
        List<Friend> friendList= ParseAPI.getFriends_Local(this);
        friendItems=new ArrayList<PersonItem>();

//        for(int j=0; j<300; j++){


            for(int i=0; i<friendList.size(); i++)
            {
                User friendUser= (User) friendList.get(i).getFriendUser();

//            Log.d("jyr","friend = "+Util.convertToNationalNumber(getActivity(),friendUser.getNationalPhone()));
                friendItems.add(new PersonItem(friendUser.getUsername(),friendUser.getProfileURL(),Util.convertToNationalNumber(this, friendUser.getNationalPhone()),PersonItem.FRIEND));
            }

//        }

        //연락처 데이터 들 중 쉐어캠 친구인 것이 있는지 확인
        for(int i=0; i<personItems.size(); i++)
        {
            for(int j=0; j<friendItems.size(); j++) {
                //연락처 중 쉐어캠 친구인 경우
                if(friendItems.get(j).phoneNumber.equals(personItems.get(i).phoneNumber)) {
                    personItems.get(i).isFriend = true;
                    personItems.get(i).friendIndex=j;
//                    personItems.get(i).contactProfile=friendItems.get(j).contactProfile;
                    personItems.get(i).personName=personItems.get(i).personName+"("+friendItems.get(j).personName+")";
                    break;
                }
            }
        }
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }






    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position)
            {
                default:
                case 0:
                    return new IndividualFragment();
                case 1 :
                    return new GroupFragment();
//                case 2:
//
//                    return new GroupFragment();
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.private_tab);
                case 1:
                    return getString(R.string.group_tab);
                case 2:
                    return getString(R.string.channel_tab);
            }
            return null;
        }
    }



}
