package azuresky.smartdrug;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2016/1/17.
 */
public class FragmentOverview extends Fragment {
    private ViewPager mViewPager;
    private View view;
    private android.support.design.widget.TabLayout mTabs;
    private String TAG = "FragmentOverview";
    Activity activity;
    @Override
    public void onActivityCreated(Bundle savedInstantState) {
        super.onActivityCreated(savedInstantState);
        Log.d(TAG,"onActivityCreated");
        initialTabs();
        initialViewPager();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstantState)
    {
        Log.d(TAG,"onCreateView");
        view = inflater.inflate(R.layout.slide_layout,container,false);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("FragmentSlide", "onAttach");
        this.activity = activity;
    }


    private void initialTabs() {
        mTabs = (android.support.design.widget.TabLayout) view.findViewById(R.id.tabs);
        mTabs.addTab(mTabs.newTab().setIcon(R.mipmap.schedule));
        mTabs.addTab(mTabs.newTab().setIcon(R.mipmap.upload));
        mTabs.addTab(mTabs.newTab().setIcon(R.mipmap.noticed));
        mTabs.addTab(mTabs.newTab().setIcon(R.mipmap.drop));
    }
    private void initialViewPager() {
        Log.d("FragmentOverview", "initialViewPager");
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new FragmentPage());
        fragments.add(new FragmentHttp());
        fragments.add(new FragmentCushionData());
        fragments.add(new FragmentENV());
        Log.d("FragmentOverview", "size"+fragments.size());
        PagerAdapter myPagerAdapter = new azuresky.smartdrug.Adapters.PagerAdapter(getChildFragmentManager(),fragments);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(myPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));

        //設定點擊tabs的監聽器，讓viewpager切到選擇的tab，冰
        mTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()){
                    case 0:
                        tab.setIcon(R.mipmap.schedule);
                        break;
                    case 1:
                        tab.setIcon(R.mipmap.upload);
                        break;
                    case 2:
                        tab.setIcon(R.mipmap.noticed);
                        break;
                    case 3:
                        tab.setIcon(R.mipmap.drop);
                        break;
                }

            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        tab.setIcon(R.mipmap.schedule);
                        break;
                    case 1:
                        tab.setIcon(R.mipmap.upload);
                        break;
                    case 2:
                        tab.setIcon(R.mipmap.noticed);
                        break;
                    case 3:
                        tab.setIcon(R.mipmap.drop);
                        break;
                }
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }
}
