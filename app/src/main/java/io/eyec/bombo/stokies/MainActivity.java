package io.eyec.bombo.stokies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.widget.Toast;

import menuFragments.groceries;
import menuFragments.login;
import menuFragments.mainScreen;
import properties.accessKeys;

import static properties.accessKeys.isExitApplication;

public class MainActivity extends AppCompatActivity {
    Fragment mContent;
    SharedPreferences sharedPreferences;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Key = "lastFragment" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            if (accessKeys.isLoggedin()) {
                methods.globalMethods.loadFragmentWithTag(R.id.main, new mainScreen(), this,"groceries");
            } else {
                methods.globalMethods.loadFragmentWithTag(R.id.main, new login(), this, "login");
            }
        }else{
            mContent = getFragmentManager().getFragment(savedInstanceState, "Fragment");
            methods.globalMethods.loadFragmentsNavBack(R.id.main, mContent, this);
        }
    }
    private static String fragmentTag;

    public static String getFragmentTag() {
        return fragmentTag;
    }

    public static void setFragmentTag(String fragmentTag) {
        MainActivity.fragmentTag = fragmentTag;
    }

    boolean backpressed = false;
    @Override
    public void onBackPressed() {
        if (accessKeys.isExitApplication()) {
            if (getFragmentTag() != null){
                if (getFragmentTag().equalsIgnoreCase("groceries")) {
                    methods.globalMethods.loadFragments(R.id.main, new mainScreen(), this);
                    //setFragmentTag(null);
                } else if (getFragmentTag().equalsIgnoreCase("cart")) {
                    methods.globalMethods.loadFragments(R.id.main, new groceries(), this);
                    //setFragmentTag(null);
                } else if (getFragmentTag().equalsIgnoreCase("history")) {
                    methods.globalMethods.loadFragments(R.id.main, new groceries(), this);
                    //setFragmentTag(null);
                }
            }else {
                if (backpressed) {
                    //super.onBackPressed();
                    finish();
                    //System.exit(0);
                }
                this.backpressed = true;
                Toast.makeText(this, "double click to exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        backpressed = false;
                    }
                }, 2000);
            }
        } else {
            super.onBackPressed();
            //isOnMainMenu = true;
            return;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        getFragmentManager().putFragment(outState, "Fragment", mContent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mContent = getFragmentManager().getFragment(savedInstanceState, "Fragment");
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences = getSharedPreferences("MyPREFERENCES", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Fragment fragment = getFragmentManager().findFragmentById(R.id.main);
        //FragmentManager fragmentManager = getSupportFragmentManager();
        editor.putString(Key, fragment.getTag());
        editor.apply();
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String Tag = sharedPreferences.getString(Key, "");
        Fragment fragment = getFragmentManager().findFragmentByTag(Key);
        methods.globalMethods.loadFragmentWithTag(R.id.main, fragment, this, Tag);
    }
}
