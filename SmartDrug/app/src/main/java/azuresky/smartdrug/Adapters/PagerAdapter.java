package azuresky.smartdrug.Adapters;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.List;
/**
 * Created by User on 2015/12/7.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    public PagerAdapter(FragmentManager fm,List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
        //Log.d("PagerAdapter",fm.toString());
        //Log.d("PagerAdapter","");
    }
    @Override
    public android.app.Fragment getItem(int position) {
        return fragments.get(position);
    }
    @Override
    public int getCount() {
        return fragments.size();
    }
}
