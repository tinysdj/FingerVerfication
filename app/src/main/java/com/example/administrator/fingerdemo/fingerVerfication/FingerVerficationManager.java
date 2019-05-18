package com.example.administrator.fingerdemo.fingerVerfication;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

public class FingerVerficationManager {
    private IFingerVerfication iFingerVerfication;
    private Activity mActivity;

    public interface VerficationCallback{
        void onUsePassword();

        void onSucceeded();

        void onFailed();

        void onError(int code,String reason);

        void onCancel();
    }
    public static FingerVerficationManager getManager(Activity activity ){
        return new FingerVerficationManager(activity);
    }
    private FingerVerficationManager(Activity activity){
            mActivity=activity;
        if (isAboveApi28()) {
            iFingerVerfication = new FingerVerficationApi28(activity);
        } else if (isAboveApi23()) {
            iFingerVerfication = new FingerVerficationApi23(activity);
        }
    }
    private boolean isAboveApi28() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    private boolean isAboveApi23() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
    public void authenticate(@NonNull VerficationCallback callback) {
        iFingerVerfication.authenticate(new CancellationSignal(), callback);
    }

    /**
     * 是否设置指纹
     * @return
     */
    public boolean hasEnrolledFingerprints() {
        if (isAboveApi28()) {
            //TODO 这是Api23的判断方法，也许以后有针对Api28的判断方法
            final FingerprintManager manager = mActivity.getSystemService(FingerprintManager.class);
            return manager != null && manager.hasEnrolledFingerprints();
        } else if (isAboveApi23()) {
            return ((FingerVerficationApi23)iFingerVerfication).hasEnrolledFingerprints();
        } else {
            return false;
        }
    }

    /**
     * 是否有指纹相关硬件
     */
    public boolean isHardwareDetected() {
        if (isAboveApi28()) {
            //TODO 这是Api23的判断方法，也许以后有针对Api28的判断方法
            final FingerprintManager fm = mActivity.getSystemService(FingerprintManager.class);
            return fm != null && fm.isHardwareDetected();
        } else if (isAboveApi23()) {
            return ((FingerVerficationApi23)iFingerVerfication).isHardwareDetected();
        } else {
            return false;
        }
    }

    /**
     * 是否有安全保护（鸡肋，设置指纹必须条件是设置安全保护）
     * @return
     */
    public boolean isKeyguardSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) mActivity.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.isKeyguardSecure()) {
            return true;
        }

        return false;
    }

    /**
     *设备是否支持指纹识别
     * @return
     */
    public boolean isFingerVerficationEnable() {
        return isAboveApi23()
                && isHardwareDetected()
                && hasEnrolledFingerprints()
                && isKeyguardSecure();
    }

}
