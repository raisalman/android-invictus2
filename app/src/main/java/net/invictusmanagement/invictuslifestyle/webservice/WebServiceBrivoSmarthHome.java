package net.invictusmanagement.invictuslifestyle.webservice;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import net.invictusmanagement.invictuslifestyle.models.AddBrivoSmartHomeUser;
import net.invictusmanagement.invictuslifestyle.models.EENAuthenticate;
import net.invictusmanagement.invictuslifestyle.models.EENAuthorise;
import net.invictusmanagement.invictuslifestyle.models.LoginBrivoSmartHomeUser;
import net.invictusmanagement.invictuslifestyle.models.ResponseListBrivoSmartHome;
import net.invictusmanagement.invictuslifestyle.models.ResponseLoginBrivoSmartHome;
import net.invictusmanagement.invictuslifestyle.models.Settings;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebServiceBrivoSmarthHome {

    private final String TAG = this.getClass().getSimpleName();
    private final Context mContext;
    private static WebServiceBrivoSmarthHome instance;
    private RestService restService;
    private RestClientBrivoSmartHome restClient;

    private WebServiceBrivoSmarthHome(Context mContext) {
        this.mContext = mContext;
        RestClientBrivoSmartHome.getInstance().init(mContext);
        restClient = RestClientBrivoSmartHome.getInstance();
        restService = restClient.getRestService();
    }


    public static void init(Context mContext) {
        if (instance == null) {
            instance = new WebServiceBrivoSmarthHome(mContext);
        }
    }

    public static boolean isInitialized() {
        return instance == null;
    }

    public static WebServiceBrivoSmarthHome getInstance() {
        if (instance == null) {
            throw new RuntimeException(
                    "You must provide a valid context when initializing SDK");
        }
        return instance;
    }

    public void stopAllWebService() {
        if (restClient.getOkHttpClient() != null)
            restClient.getOkHttpClient().dispatcher().cancelAll();
    }

    private void setFailure(RestCallBack callBack, Throwable t) {
        if (callBack == null)
            return;

        if (t instanceof WSException)
            callBack.onFailure((WSException) t);
        else {
            WSException throwable = new WSException();
            throwable.setCode(-1);
            throwable.setServerMessage(t.getMessage());
            callBack.onFailure(throwable);
        }
    }

    private void setFailure(RestEmptyCallBack callBack, Throwable t) {
        if (callBack == null)
            return;

        if (t instanceof WSException)
            callBack.onFailure((WSException) t);
        else {
            WSException throwable = new WSException();
            throwable.setCode(-1);
            throwable.setServerMessage(t.getMessage());
            callBack.onFailure(throwable);
        }
    }

    public static boolean isNetworkAvailable(final Context context) {
        boolean isNetAvailable = false;
        if (context != null) {
            final ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mConnectivityManager != null) {
                final NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
                if (activeNetwork != null) isNetAvailable = true;
            }
        }
        return isNetAvailable;
    }

    public void loginBrivoSmartHome(LoginBrivoSmartHomeUser model, RestCallBack<ResponseLoginBrivoSmartHome> callBack) {
        String json = new Gson().toJson(model);
        Log.e("JSON >> ", json);
        Call<ResponseLoginBrivoSmartHome> call = restService.loginBrivoSmartHomeUser(json);
        call.enqueue(new Callback<ResponseLoginBrivoSmartHome>() {
            @Override
            public void onResponse(@NonNull Call<ResponseLoginBrivoSmartHome> call,
                                   @NonNull Response<ResponseLoginBrivoSmartHome> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseLoginBrivoSmartHome> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void getBrivoSmartHomeDevices(String token, RestCallBack<ResponseListBrivoSmartHome> callBack) {
        Log.e("Token >> ", token);
        Call<ResponseListBrivoSmartHome> call = restService.getBrivoSmartHomeDevice("Token " + token);
        call.enqueue(new Callback<ResponseListBrivoSmartHome>() {
            @Override
            public void onResponse(@NonNull Call<ResponseListBrivoSmartHome> call,
                                   @NonNull Response<ResponseListBrivoSmartHome> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseListBrivoSmartHome> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void lockSmartHomeDoor(int id, String token, RestCallBack<ResponseBody> callBack) {
        Log.e("Token >> ", token);
        Call<ResponseBody> call = restService.lockSmartHome(id, "Token " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void unlockSmartHomeDoor(int id, String token, RestCallBack<ResponseBody> callBack) {
        Log.e("Token >> ", token);
        Call<ResponseBody> call = restService.unlockSmartHome(id, "Token " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void turnOnSmartHomeSwitch(int id, String token, RestCallBack<ResponseBody> callBack) {
        Log.e("Token >> ", token);
        Call<ResponseBody> call = restService.turnOnSmartHomeSwitch(id, "Token " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void turnOffSmartHomeSwitch(int id, String token, RestCallBack<ResponseBody> callBack) {
        Log.e("Token >> ", token);
        Call<ResponseBody> call = restService.turnOffSmartHomeSwitch(id, "Token " + token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }


    public void setThermostatSetting(int id, String token, Settings settings, RestCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(settings);
        Log.e("JSON >> ", json);
        Call<ResponseBody> call = restService.setThermostatSetting(id, "Token " + token, json);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }


}