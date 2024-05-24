package com.holomatic.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.holomatic.service.PopService;

/**
 * @author 比才-贾硕哲
 * @time 24/5/2024 10:23
 * @desc
 */
public class MainActivity extends Activity {

    int REQUEST_CODE = 101;

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }else {
                popWindow();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE){
            popWindow();
        }
    }

    private void popWindow(){
        Intent intent = new Intent(MainActivity.this, PopService.class);
        startService(intent);
        finish();
    }
}
