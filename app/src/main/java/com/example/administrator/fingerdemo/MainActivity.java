package com.example.administrator.fingerdemo;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.administrator.fingerdemo.fingerVerfication.FingerVerficationManager;


public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private FingerVerficationManager fingerVerficationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fingerVerficationManager=FingerVerficationManager.getManager(this);
         textView=findViewById(R.id.tv_fingerPrint);
         textView.setOnClickListener(new View.OnClickListener() {
             @RequiresApi(api = Build.VERSION_CODES.M)
             @Override
             public void onClick(View v) {
                if(fingerLockCheck()){
                    FingerVerficationManager.getManager(MainActivity.this).authenticate(new FingerVerficationManager.VerficationCallback() {
                        @Override
                        public void onUsePassword() {
                            Toast.makeText(MainActivity.this,"使用密码登录",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSucceeded() {
                            Toast.makeText(MainActivity.this,"验证成功",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed() {
                            Toast.makeText(MainActivity.this,"指纹不匹配",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(int code, String reason) {
                            Toast.makeText(MainActivity.this,"指纹错误",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
             }
         });
    }
    /**
     * 验证指纹是否可以使用
     */
    public boolean fingerLockCheck(){
        if (!fingerVerficationManager.isHardwareDetected()) {
            //没有相关硬件，无法使用指纹解锁
            Toast.makeText(this,"没有相关硬件，无法使用指纹解锁",Toast.LENGTH_SHORT);
            return false;
        } else {
            //当前设备是否处于安全保护（你的设备必须是使用屏幕锁保护的）
            KeyguardManager keyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager.isKeyguardSecure()) {
                //this device is secure;
                //判断系统中是否有注册的指纹
                if (!fingerVerficationManager.hasEnrolledFingerprints()) {
                    //没有注册的指纹，需要用户在设置中自行设置
                    Toast.makeText(this,"当前设备没有已注册的指纹，需到设置中设置",Toast.LENGTH_SHORT);
                    AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(MainActivity.this);
                    normalDialog.setMessage("当前设备没有已注册的指纹，需到设置中设置");
                    normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent =  new Intent(Settings.ACTION_SECURITY_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    normalDialog.show();
                    return false;
                } else {
                    return true;
                }
            } else {
                Toast.makeText(this,"当前设备未处于安全保护",Toast.LENGTH_SHORT);
                return false;
            }
        }
    }

}
