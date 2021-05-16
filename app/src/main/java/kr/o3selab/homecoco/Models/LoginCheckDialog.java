package kr.o3selab.homecoco.Models;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import kr.o3selab.homecoco.Activities.Member.LoginActivity;
import kr.o3selab.homecoco.Activities.Member.RegisterActivity;
import kr.o3selab.homecoco.R;

public class LoginCheckDialog extends AlertDialog.Builder {

    private Context mContext;

    public LoginCheckDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public AlertDialog build(final Intent intent) {
        return new AlertDialog.Builder(mContext)
                .setMessage(R.string.login_message)
                .setPositiveButton(R.string.login_message_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mContext.startActivity(new Intent(mContext, RegisterActivity.class));
                    }
                })
                .setNeutralButton(R.string.login_message_neutral, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mContext.startActivity(new Intent(mContext, LoginActivity.class));
                    }
                })
                .setNegativeButton(R.string.login_message_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mContext.startActivity(intent);
                    }
                })
                .setCancelable(true)
                .create();
    }
}
