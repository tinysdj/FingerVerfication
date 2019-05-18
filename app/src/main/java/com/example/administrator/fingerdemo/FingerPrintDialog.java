package com.example.administrator.fingerdemo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class FingerPrintDialog extends Dialog {
    private TextView tvCancel;
    private TextView tvUsePassword;
    private FingerDialogCallback fingerDialogCallback;

    public FingerPrintDialog(@NonNull Context context) {
        super(context);
        setFingerPrintDialog();
    }
    public void setOnFingerDialogClickListener(FingerDialogCallback fingerDialogCallback){
        this.fingerDialogCallback=fingerDialogCallback;
    }

    private void setFingerPrintDialog() {
        View view=LayoutInflater.from(getContext()).inflate(R.layout.fingerprint_dialog_view,null);
        tvCancel=view.findViewById(R.id.tv_cancel);
        tvUsePassword=view.findViewById(R.id.tv_use_password);
        super.setContentView(view);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fingerDialogCallback!=null){
                    fingerDialogCallback.onCancel();
                }
            }
        });
        tvUsePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fingerDialogCallback!=null){
                    fingerDialogCallback.onUsePassword();
                }
            }
        });
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(fingerDialogCallback!=null){
                    fingerDialogCallback.onDismiss();
                }
            }
        });

    }


    public interface FingerDialogCallback{
        void onCancel();

        void onUsePassword();

        void onDismiss();
    }
}
