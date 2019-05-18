package com.example.administrator.fingerdemo.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import com.example.administrator.fingerdemo.fingerVerfication.CryptoObjectHelper;

public class FingerPrintUtils  {
    private FingerprintManagerCompat fingerprintManagerCompat;
    private Handler handler;
    private Context context;
    private CancellationSignal cancellationSignal;

    public FingerPrintUtils(Context context,FingerprintManagerCompat fingerprintManagerCompat) {
        this.context = context;
        this.fingerprintManagerCompat=fingerprintManagerCompat;
        cancellationSignal = new CancellationSignal();
    }

    /**
     * 验证指纹api23到api27
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void authenticate(final FingerPrintListener fingerPrintListener) {
        try {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                   fingerPrintListener.getFingerPrintStatus(msg.what);
                }
            };
            MyAuthCallback callback = new MyAuthCallback(handler);
            CryptoObjectHelper cryptoObjectHelper = new CryptoObjectHelper();
//            fingerprintManagerCompat.authenticate(cryptoObjectHelper.buildCryptoObject(), 0, cancellationSignal, callback, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * android9.0及以上版本验证
     */
    public void authenticate(){
        BiometricPrompt biometricPrompt=new BiometricPrompt
                .Builder(context)
                .setTitle("Verfication")
                .setDescription("指纹验证开始")
                .setSubtitle("")
                .setNegativeButton("usePassword", context.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancellationSignal.cancel();
                    }
                })
                .build();
    }

    /**
     * 取消指纹识别
     */
    public void cancelFingerScanner(){
        if(cancellationSignal!=null){
            cancellationSignal.cancel();
        }
    }
    /**
     * 指纹解锁回调
     */
    public interface FingerPrintListener{
        void getFingerPrintStatus(int status);
    }
}
