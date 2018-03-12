package com.example.mayingnan.project301.ActivityTest;


/**
 * Created by wdong2 on 3/8/18.
 */

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.example.mayingnan.project301.R;
import com.example.mayingnan.project301.allUserActivity.UserCharacterActivity;
import com.example.mayingnan.project301.providerActivity.ProviderBidHistoryActivity;
import com.example.mayingnan.project301.providerActivity.ProviderEditInfoActivity;
import com.example.mayingnan.project301.providerActivity.ProviderMainActivity;
import com.example.mayingnan.project301.providerActivity.ProviderMapActivity;
import com.example.mayingnan.project301.requesterActivity.RequesterMainActivity;
import com.robotium.solo.Solo;

public class ProviderActivityTest extends ActivityInstrumentationTestCase2 {

    private Solo solo;

    public ProviderActivityTest() {
        super(ProviderMainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }


    public void testProviderMainActivity() {

        ProviderMainActivity activity = (ProviderMainActivity)solo.getCurrentActivity();

        solo.assertCurrentActivity("Wrong Activity", ProviderMainActivity.class);

        //solo.goBack();

        solo.enterText((EditText) solo.getView(R.id.search_info),"some test");

        solo.clickOnButton("Search");

        solo.assertCurrentActivity("Wrong Activity", ProviderMainActivity.class);

        //solo.goBack();

        //solo.assertCurrentActivity("Wrong Activity", UserCharacterActivity.class);

         solo.clickOnButton("View On Map");

         solo.assertCurrentActivity("Wrong Activity", ProviderMapActivity.class);

         solo.goBack();

         solo.assertCurrentActivity("Wrong Activity", ProviderMainActivity.class);

         solo.clickOnButton("Bid History");

         solo.assertCurrentActivity("Wrong Activity", ProviderBidHistoryActivity.class);

         solo.goBack();

         solo.assertCurrentActivity("Wrong Activity", ProviderMainActivity.class);

        solo.clickOnButton("edit profile");

        solo.assertCurrentActivity("Wrong Activity", ProviderEditInfoActivity.class);

        solo.enterText((EditText) solo.getView(R.id.edit_name),"wdong2");

        solo.enterText((EditText) solo.getView(R.id.edit_email),"wdong2@ualberta.ca");

        solo.enterText((EditText) solo.getView(R.id.edit_phone),"1234567890");

        solo.enterText((EditText) solo.getView(R.id.edit_passward),"abc");

        solo.goBack();

        solo.assertCurrentActivity("Wrong Activity", ProviderMainActivity.class);
    }

    public void testProviderEditProfileActivity() {
        solo.assertCurrentActivity("Wrong Activity", ProviderMainActivity.class);

        //solo.goBack();

        //solo.assertCurrentActivity("Wrong Activity", UserCharacterActivity.class);

        solo.clickOnButton("edit profile");

        solo.assertCurrentActivity("Wrong Activity", ProviderEditInfoActivity.class);

        solo.enterText((EditText) solo.getView(R.id.edit_name),"wdong2");

        solo.enterText((EditText) solo.getView(R.id.edit_email),"wdong2@ualberta.ca");

        solo.enterText((EditText) solo.getView(R.id.edit_phone),"1234567890");

        solo.enterText((EditText) solo.getView(R.id.edit_passward),"abc");

        solo.clickOnButton("Save");

        solo.assertCurrentActivity("Wrong Activity", ProviderMainActivity.class);

        solo.enterText((EditText) solo.getView(R.id.search_info),"some test");

        solo.goBack();

        solo.enterText((EditText) solo.getView(R.id.edit_name),"wdong2");

        solo.enterText((EditText) solo.getView(R.id.edit_email),"wdong2@ualberta.ca");

        solo.enterText((EditText) solo.getView(R.id.edit_phone),"1234567890");

        solo.enterText((EditText) solo.getView(R.id.edit_passward),"abc");

        solo.clickOnButton("Back");

        solo.assertCurrentActivity("Wrong Activity", ProviderMainActivity.class);

    }

    public void testProviderMapActivity() {

        solo.assertCurrentActivity("Wrong Activity", ProviderMainActivity.class);

        //solo.goBack();

        //solo.assertCurrentActivity("Wrong Activity", UserCharacterActivity.class);

        solo.clickOnButton("View On Map");

        solo.assertCurrentActivity("Wrong Activity", ProviderMapActivity.class);

        solo.goBack();

        solo.assertCurrentActivity("Wrong Activity", ProviderMainActivity.class);
    }

    public void testProviderBidHistoryActivity() {
        solo.assertCurrentActivity("Wrong Activity", ProviderMainActivity.class);

        solo.clickOnButton("Bid History");

        solo.assertCurrentActivity("Wrong Activity", ProviderBidHistoryActivity.class);

        solo.clickOnButton("Back");

        solo.assertCurrentActivity("Wrong Activity", ProviderMainActivity.class);
    }
}