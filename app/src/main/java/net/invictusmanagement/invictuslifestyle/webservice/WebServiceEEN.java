package net.invictusmanagement.invictuslifestyle.webservice;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import net.invictusmanagement.invictuslifestyle.models.EENAuthenticate;
import net.invictusmanagement.invictuslifestyle.models.EENAuthorise;
import net.invictusmanagement.invictuslifestyle.models.UserDeviceId;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebServiceEEN {

    private final String TAG = this.getClass().getSimpleName();
    private final Context mContext;
    private static WebServiceEEN instance;
    private RestService restService;
    private RestClientEEN restClient;

    private WebServiceEEN(Context mContext) {
        this.mContext = mContext;
        RestClientEEN.getInstance().init(mContext);
        restClient = RestClientEEN.getInstance();
        restService = restClient.getRestService();
    }


    public static void init(Context mContext) {
        if (instance == null) {
            instance = new WebServiceEEN(mContext);
        }
    }

    public static boolean isInitialized() {
        return instance == null;
    }

    public static WebServiceEEN getInstance() {
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

    public void eenAuthenticate(EENAuthenticate model,
                                RestCallBack<EENAuthorise> callBack) {
        String json = new Gson().toJson(model);
        Call<EENAuthorise> call = restService.authenticateEEN(json);
        call.enqueue(new Callback<EENAuthorise>() {
            @Override
            public void onResponse(@NonNull Call<EENAuthorise> call,
                                   @NonNull Response<EENAuthorise> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<EENAuthorise> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void eenAuthorise(EENAuthorise model,
                             RestEmptyCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Call<ResponseBody> call = restService.authorizeEEN(json);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                if (callBack != null) {
                    callBack.onResponse(response.body());
                    List<String> Cookielist = response.headers().values("Set-Cookie");
                    if (Cookielist != null) {
                        for (String cookie : Cookielist) {
                            if (cookie.contains("auth_key")) {
                                Log.e("cookies", ">> " + cookie.split(";")[0]
                                        .split("=")[1]);
                                restClient.setAuthenticationCookie(cookie);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }


    public void eenDeviceList(RestCallBack<String> callBack) {
        Call<String> call = restService.getEENDevices();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,
                                   @NonNull Response<String> response) {
                if (callBack != null) {
                    callBack.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void eenImage(String id, RestCallBack<ResponseBody> callBack) {
        Call<ResponseBody> call = restService.getEENCameraImage(id, "now", "thumb");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                if (callBack != null) {
                    callBack.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

}