package net.invictusmanagement.invictuslifestyle.webservice;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import net.invictusmanagement.invictuslifestyle.models.AccessCodeResponse;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;
import net.invictusmanagement.invictuslifestyle.models.AccessPointAudit;
import net.invictusmanagement.invictuslifestyle.models.OpenAccessPoint;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebService {

    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private static WebService instance;
    private RestService restService;
    private RestClient restClient;

    private WebService(Context mContext) {
        this.mContext = mContext;
        RestClient.getInstance().init(mContext);
        restClient = RestClient.getInstance();
        restService = restClient.getRestService();
    }


    public static void init(Context mContext) {
        if (instance == null) {
            instance = new WebService(mContext);
        }
    }

    public static boolean isInitialized() {
        return instance == null;
    }

    public static WebService getInstance() {
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

    public void getAccessCode(final RestCallBack<AccessCodeResponse> callBack) {
        Call<AccessCodeResponse> call = restService.getAccessCode();
        call.enqueue(new Callback<AccessCodeResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccessCodeResponse> call,
                                   @NonNull Response<AccessCodeResponse> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<AccessCodeResponse> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void getAccessPoints(final RestCallBack<List<AccessPoint>> callBack) {
        Call<List<AccessPoint>> call = restService.getAccessPoints();
        call.enqueue(new Callback<List<AccessPoint>>() {
            @Override
            public void onResponse(@NonNull Call<List<AccessPoint>> call,
                                   @NonNull Response<List<AccessPoint>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<AccessPoint>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void postAccessPointAudit(AccessPointAudit model,
                                     RestEmptyCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Call<ResponseBody> call = restService.postAccessPointAudit(json);
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

    public void openAccessPoints(OpenAccessPoint model,
                                 RestEmptyCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Call<ResponseBody> call = restService.openAccessPoint(json);
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