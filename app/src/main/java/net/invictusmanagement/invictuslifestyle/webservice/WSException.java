package net.invictusmanagement.invictuslifestyle.webservice;

import android.text.TextUtils;

import java.io.IOException;

public class WSException extends IOException {
    private int mCode;
    private String mServerMessage;

    public WSException() {
        mCode = 0;
        mServerMessage = "";
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public String getServerMessage() {
        if (TextUtils.isEmpty(mServerMessage)) {
            mServerMessage = "";
        }
        return mServerMessage;
    }

    public void setServerMessage(String serverMessage) {
        mServerMessage = serverMessage;
    }
}
