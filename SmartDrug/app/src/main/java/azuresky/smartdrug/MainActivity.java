package azuresky.smartdrug;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import azuresky.smartdrug.Ball.FragmentBall;
import azuresky.smartdrug.SmartCushion.FragmentCushion;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private String TAG = "MainActivity_LIFE";
    private String[] list = new String[]{"總覽","智慧坐墊","智慧藥盒","丟球訓練","關於"};
    private ListView viewlist;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private Menu menu;
    private int FragmentPosition=0;
    public static BluetoothAdapter bluetoothAdapter;
    private List<Fragment> fragments;
    private Fragment fragmentOverview,fragmentSchedule,currentFragment,fragmentCushion,fragmentBall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
        initialToolBar();

        fragmentManager = getFragmentManager();
        fragmentOverview = new FragmentOverview();
        fragmentSchedule = new FragmentSchedule();
        fragmentCushion = new FragmentCushion();
        fragmentBall = new FragmentBall();
        currentFragment = fragmentOverview;
        fragmentManager.beginTransaction().replace(R.id.content, fragmentOverview).commit();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //fragmentManager = getFragmentManager();


        viewlist = (ListView) findViewById(R.id.left_drawer);
        viewlist.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list));
        viewlist.setOnItemClickListener(this);//設定ListView的動作監聽器給MainActivity
        //viewlist.setDividerHeight(0);
    }


    private void initialToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("總覽");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        Toast.makeText(MainActivity.this, "SettingClick", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.about:
                        Toast.makeText(MainActivity.this, "AboutClick", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.exit:
                        finish();
                        break;
                }
                return false;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Toast.makeText(this,"OnNewIntent",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        this.menu = menu;
        //initialSearchView(menu);
        return true;
    }

    private void initialSearchView(Menu menu) {
        MenuItem menuSearchItem = menu.findItem(R.id.mySearch);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        // Get the SearchView and set the searchable configuration
        SearchView searchView = (SearchView)menuSearchItem.getActionView();
        //SearchView searchView = (SearchView) menuSearchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d("stillsomething","werwer");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(position==0){
            menu.clear();   //清除原本的menu的xml佈局
            getMenuInflater().inflate(R.menu.menu_main, menu);    //載入新的佈局
            switchContent(currentFragment, fragmentOverview);
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }else if(position ==1){
            menu.clear();   //清除原本的menu的xml佈局
            getMenuInflater().inflate(R.menu.option_menu, menu);    //載入新的佈局
            switchContent(currentFragment, fragmentCushion);
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
        else if(position ==2){
            switchContent(currentFragment, fragmentSchedule);
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }else if(position ==3){
            menu.clear();   //清除原本的menu的xml佈局
            getMenuInflater().inflate(R.menu.option_menu, menu);    //載入新的佈局
            switchContent(currentFragment, fragmentBall);
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }else if(position ==4){
            //Intent websiteIntent = new Intent();
            //websiteIntent.setClass(MainActivity.this,HintActivity.class);
            //Uri uri = Uri.parse("http://mitlab.no-ip.org:8080/index.php");
            // websiteIntent.setData(uri);
           // startActivity(websiteIntent);
        }


    }
    public void switchContent(Fragment from, Fragment to) {
        if (currentFragment != to) {
            currentFragment = to;
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!to.isAdded()) {    // 判斷是否在已經add到fragment中
                //transaction.addToBackStack("21");
                //transaction.replace(R.id.content,to).commit();
                transaction.hide(from).add(R.id.content, to).commit(); // 隱藏當前的fragment，add下一個到Activity中)
            } else {
                //transaction.replace(R.id.content,to).commit();
                transaction.hide(from).show(to).commit(); // 隱藏當前的fragment，顯示下一个
            }
        }
    }

    @Override
    public void onBackPressed() {

        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
        else
            finish();
    }

}
