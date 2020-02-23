package menuFragments;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import adapters.ViewPagerAdapter;
import io.eyec.bombo.stokies.MainActivity;
import io.eyec.bombo.stokies.R;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import static properties.accessKeys.setExitApplication;


/**
 * A simple {@link Fragment} subclass.
 */
public class history extends android.app.Fragment {
    View myview;
    ViewPager viewPager;
    private TabLayout tabLayout;
    androidx.fragment.app.Fragment fragment1;
    androidx.fragment.app.Fragment fragment2;
    ViewPagerAdapter adapter;
    FragmentManager fragmentManager;
    int savedImagePosition = 0;


    public history() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_history, container, false);
        if(savedInstanceState == null) {
            savedImagePosition =0;
        }
        viewPager = myview.findViewById(R.id.pager);
        tabLayout = myview.findViewById(R.id.tab);
        fragmentManager = ((MainActivity) getActivity()).getSupportFragmentManager();
        adapter = new ViewPagerAdapter(fragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        //adapter = new ViewPagerAdapter(fragmentManager);
        //adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new pending(), "pending...");
        adapter.addFragment(new successful(), "successful...");
        fragment1 =  new pending();
        fragment2 =  new successful();
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(savedImagePosition,true);
        viewPager.setSaveFromParentEnabled(false);
        viewPager.setOffscreenPageLimit(2);
        setExitApplication(true);
        MainActivity.setFragmentTag("history");
        return myview;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //fragmentManager.putFragment(savedInstanceState,"fragment1",fragment1);
        //fragmentManager.putFragment(savedInstanceState,"fragment2",fragment2);
        //savedInstanceState.putInt("savedImagePosition",savedImagePosition);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!= null) {
            //fragment1 = fragmentManager.getFragment(savedInstanceState, "fragment1");
            //fragment1 = fragmentManager.getFragment(savedInstanceState, "fragment2");
            //savedImagePosition = savedInstanceState.getInt("savedImagePosition");
        }
    }

}
