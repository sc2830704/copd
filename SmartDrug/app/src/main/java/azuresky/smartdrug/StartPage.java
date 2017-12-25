package azuresky.smartdrug;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by sky on 2016/2/29.
 */
public class StartPage extends Activity {

    private ImageView img;
    private TextView appName,appNameEng;
    private Handler myhandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            finish();
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.startpage_layout);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(2000);

        img = (ImageView)findViewById(R.id.startImg);
        img.setImageResource(R.drawable.pillicon);
        img.startAnimation(fadeIn);
        appName = (TextView)findViewById(R.id.appName);
        appNameEng = (TextView)findViewById(R.id.appNameEng);
        appName.setAnimation(fadeIn);
        appNameEng.setAnimation(fadeIn);
        Message msg = new Message();

        myhandler.sendMessageDelayed(msg,3500);

    }

    @Override
    public void onBackPressed() {
        //do nothing..
    }
}
