package com.example.michaelh.fridgeapp.uitest;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.example.michaelh.fridgeapp.MainActivity;
import com.robotium.solo.Solo;
import junit.framework.TestCase;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo mySolo;

    public MainActivityTest() {

        super(MainActivity.class);
    }

    public void setUp() throws Exception {

        super.setUp();
        mySolo = new Solo(getInstrumentation(),getActivity());
    }

    public void tearDown() throws Exception {

    }

    public void testButtonAll() {
        mySolo.clickOnButton("Ablaufdatum");
        mySolo.clickOnButton("SCAN");
    }

   public void testTextView() {
       TextView text_view = mySolo.getText("Strichcode");
   }

   public void testScanStrichcode() {
       mySolo.clickOnButton("SCAN");
      // TextView text_view = mySolo.getText("Strichcode");
       mySolo.sleep(6000);
       TextView text_view = mySolo.getText("Strichcode");
       String text = text_view.getText().toString();
       //checkt ob text gesuchten Strichcode entspricht
       assertEquals("90311789",text);

   }
}