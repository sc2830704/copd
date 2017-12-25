package azuresky.smartdrug.SmartCushion;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import azuresky.smartdrug.R;

/**
 * Created by MSI-GP60-2QE on 2015/11/29.
 */
public class NotificationActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_layout);
        // 取得NotificationManager系統服務
        NotificationManager notiMgr = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // 取消狀態列的提醒訊息
        notiMgr.cancel(getIntent().getExtras().getInt("NOTIFICATION_ID"));
    }
}
