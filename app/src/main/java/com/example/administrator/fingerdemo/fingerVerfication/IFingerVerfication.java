package com.example.administrator.fingerdemo.fingerVerfication;

import android.os.CancellationSignal;

public interface IFingerVerfication {
    void authenticate(CancellationSignal cancel, FingerVerficationManager.VerficationCallback callback);
}
