package com.example.administrator.fingerdemo.fingerVerfication;

import android.app.Activity;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.administrator.fingerdemo.FingerPrintDialog;

@RequiresApi(Build.VERSION_CODES.M)
public class FingerVerficationApi23 implements IFingerVerfication {
    private static final String TAG = "FingerVerficationApi23";
    private Activity mActivity;
    private FingerprintManager mFingerprintManager;
    private CancellationSignal mCancellationSignal;
    private FingerVerficationManager.VerficationCallback mManagerIdentifyCallback;
    private FingerprintManager.AuthenticationCallback mFmAuthCallback = new FingerprintManageCallbackImpl();
    private FingerPrintDialog dialog;

    public FingerVerficationApi23(Activity mActivity) {
        this.mActivity = mActivity;
        this.mFingerprintManager = getFingerprintManager(mActivity);
    }

    @Override
    public void authenticate(CancellationSignal cancel, FingerVerficationManager.VerficationCallback callback) {
        mManagerIdentifyCallback=callback;
        dialog=new FingerPrintDialog(mActivity);
        dialog.setOnFingerDialogClickListener(new FingerPrintDialog.FingerDialogCallback() {
            @Override
            public void onCancel() {
                mManagerIdentifyCallback.onCancel();
                mCancellationSignal.cancel();
                dialog.dismiss();
            }

            @Override
            public void onUsePassword() {
                mManagerIdentifyCallback.onUsePassword();
                mCancellationSignal.cancel();
                dialog.dismiss();
            }

            @Override
            public void onDismiss() {
                //当dialog消失的时候，包括点击userPassword、点击cancel、和识别成功之后
                if (mCancellationSignal != null && !mCancellationSignal.isCanceled()) {
                    mCancellationSignal.cancel();
                }
            }
        });
        dialog.show();
        mCancellationSignal = cancel;
        if (mCancellationSignal == null) {
            mCancellationSignal = new CancellationSignal();
        }
        mCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                dialog.dismiss();
            }
        });

        try {
            CryptoObjectHelper cryptoObjectHelper = new CryptoObjectHelper();
            getFingerprintManager(mActivity).authenticate(cryptoObjectHelper.buildCryptoObject(), mCancellationSignal,
                    0, mFmAuthCallback, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private class FingerprintManageCallbackImpl extends FingerprintManager.AuthenticationCallback {

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            //指纹，5次以后会调用onAuthenticationFailed方法
            super.onAuthenticationError(errorCode, errString);
            Log.d(TAG, "onAuthenticationError() called with: errorCode = [" + errorCode + "], errString = [" + errString + "]");
            //手动执行取消操作也会执行错误回调所以在这里加一个判断
            if(errorCode!=FingerprintManager.FINGERPRINT_ERROR_CANCELED) {
                mManagerIdentifyCallback.onError(errorCode, errString.toString());
            }
        }

        @Override
        public void onAuthenticationFailed() {
            //验证失败，隔一段时间才可以继续使用
            super.onAuthenticationFailed();
            Log.d(TAG, "onAuthenticationFailed() called");
            mManagerIdentifyCallback.onFailed();
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
            Log.d(TAG, "onAuthenticationHelp() called with: helpCode = [" + helpCode + "], helpString = [" + helpString + "]");
            mManagerIdentifyCallback.onFailed();

        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            Log.i(TAG, "onAuthenticationSucceeded: ");
            mManagerIdentifyCallback.onSucceeded();
            dialog.dismiss();
        }
    }

    private FingerprintManager getFingerprintManager(Context context) {
        if (mFingerprintManager == null) {
            mFingerprintManager = context.getSystemService(FingerprintManager.class);
        }
        return mFingerprintManager;
    }

    /**
     * 判断是否有相关硬件
     * @return
     */
    public boolean isHardwareDetected() {
        if (mFingerprintManager != null) {
            return mFingerprintManager.isHardwareDetected();
        }
        return false;
    }

    /**
     * 是否有已注册指纹
     * @return
     */
    public boolean hasEnrolledFingerprints() {
        if (mFingerprintManager != null) {
            return mFingerprintManager.hasEnrolledFingerprints();
        }
        return false;
    }
}
