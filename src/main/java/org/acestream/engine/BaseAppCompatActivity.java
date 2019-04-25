package org.acestream.engine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.acestream.sdk.AceStream;

import androidx.appcompat.app.AppCompatActivity;

public class BaseAppCompatActivity extends AppCompatActivity {
    private static final String TAG = "AS/BaseACA";

    // broadcast receiver
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null) return;
            if(TextUtils.equals(intent.getAction(), AceStream.ACTION_STOP_APP)) {
                Log.d(TAG, "receiver: stop app: class=" + BaseAppCompatActivity.this.getClass().getSimpleName());
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AceStream.getBaseApplicationFactory().initialize(this);
        super.onCreate(savedInstanceState);

        final IntentFilter filter = new IntentFilter();
        filter.addAction(AceStream.ACTION_STOP_APP);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void attachBaseContext(Context base) {
        AceStream.getBaseApplicationFactory().initialize(base);
        super.attachBaseContext(AceStreamEngineBaseApplication.updateBaseContextLocale(base));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
