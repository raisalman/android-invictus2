package net.invictusmanagement.invictuslifestyle;

import android.app.Application;
import android.content.Context;

import com.brivo.sdk.BrivoSDK;
import com.brivo.sdk.BrivoSDKInitializationException;
import com.brivo.sdk.model.BrivoConfiguration;

import net.invictusmanagement.invictuslifestyle.utils.BrivoSampleConstants;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

public class MyApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        //Parse SDK stuff goes here
        context = this;
        // init webservice
        WebService.init(this);
        //init Brivo
        try {
            BrivoSDK.getInstance().init(getApplicationContext(), new BrivoConfiguration(
                    BrivoSampleConstants.CLIENT_ID,
                    BrivoSampleConstants.CLIENT_SECRET,
                    true));
        } catch (BrivoSDKInitializationException e) {
            e.printStackTrace();
        }
    }
}
