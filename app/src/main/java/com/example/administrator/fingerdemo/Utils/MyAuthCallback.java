package com.example.administrator.fingerdemo.Utils;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Handler;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.widget.Toast;

import com.example.administrator.fingerdemo.AppContext;
import com.example.administrator.fingerdemo.consts.FingerPrintConsts;


public class MyAuthCallback extends FingerprintManagerCompat.AuthenticationCallback{
    private Handler handler = null;

    public MyAuthCallback(Handler handler) {
        super();
        this.handler = handler;
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        super.onAuthenticationError(errMsgId, errString);
        //验证错误时，回调该方法。当连续验证5次错误时，将会走onAuthenticationFailed()方法
        if (handler != null) {
            if(errMsgId!=5){
                handler.obtainMessage(FingerPrintConsts.MSG_AUTH_ERROR, errMsgId, 0).sendToTarget();
            }else {
                Toast.makeText(AppContext.getInstance(),"再次吊起验证的问题",Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        super.onAuthenticationHelp(helpMsgId, helpString);
        if (handler != null) {
            handler.obtainMessage(FingerPrintConsts.MSG_AUTH_HELP, helpMsgId, 0).sendToTarget();
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        //验证成功时，回调该方法。fingerprint对象不能再验证
//        try {
//            result.getCryptoObject().getCipher().doFinal();
        if (handler != null) {
            handler.obtainMessage(FingerPrintConsts.MSG_AUTH_SUCCESS).sendToTarget();
        }
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        //验证失败时，回调该方法。fingerprint对象不能再验证并且需要等待一段时间才能重新创建指纹管理对象进行验证
        if (handler != null) {
            handler.obtainMessage(FingerPrintConsts.MSG_AUTH_FAILED).sendToTarget();
        }
    }
}
