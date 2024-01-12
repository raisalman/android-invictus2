package net.invictusmanagement.invictuslifestyle.webservice;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import net.invictusmanagement.invictuslifestyle.models.AccessCodeResponse;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;
import net.invictusmanagement.invictuslifestyle.models.AccessPointFavUnFavRequest;
import net.invictusmanagement.invictuslifestyle.models.AccessPointUpdate;
import net.invictusmanagement.invictuslifestyle.models.AddBrivoSmartHomeUser;
import net.invictusmanagement.invictuslifestyle.models.AddTechnician;
import net.invictusmanagement.invictuslifestyle.models.AddVendor;
import net.invictusmanagement.invictuslifestyle.models.AmenitiesBooking;
import net.invictusmanagement.invictuslifestyle.models.AuthenticationResult;
import net.invictusmanagement.invictuslifestyle.models.Business;
import net.invictusmanagement.invictuslifestyle.models.BusinessType;
import net.invictusmanagement.invictuslifestyle.models.ChatTopic;
import net.invictusmanagement.invictuslifestyle.models.CheckCameraAccess;
import net.invictusmanagement.invictuslifestyle.models.CommunityNotificationList;
import net.invictusmanagement.invictuslifestyle.models.CouponsAdvertisement;
import net.invictusmanagement.invictuslifestyle.models.DigitalKey;
import net.invictusmanagement.invictuslifestyle.models.DigitalKeyUpdate;
import net.invictusmanagement.invictuslifestyle.models.EENDeviceList;
import net.invictusmanagement.invictuslifestyle.models.GuestEntryDoor;
import net.invictusmanagement.invictuslifestyle.models.Login;
import net.invictusmanagement.invictuslifestyle.models.MRSRating;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequest;
import net.invictusmanagement.invictuslifestyle.models.MarketPlaceCategories;
import net.invictusmanagement.invictuslifestyle.models.NotificationCount;
import net.invictusmanagement.invictuslifestyle.models.NotificationRecipient;
import net.invictusmanagement.invictuslifestyle.models.NotificationStatus;
import net.invictusmanagement.invictuslifestyle.models.PaymentTransactionResponse;
import net.invictusmanagement.invictuslifestyle.models.RecentChat;
import net.invictusmanagement.invictuslifestyle.models.ResidentChat;
import net.invictusmanagement.invictuslifestyle.models.ServiceKey;
import net.invictusmanagement.invictuslifestyle.models.UpdateTopicStatus;
import net.invictusmanagement.invictuslifestyle.models.User;
import net.invictusmanagement.invictuslifestyle.models.UserDeviceId;
import net.invictusmanagement.invictuslifestyle.models.UserStatus;
import net.invictusmanagement.invictuslifestyle.models.UserUpdate;
import net.invictusmanagement.invictuslifestyle.models.Vendors;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebService {

    private final String TAG = this.getClass().getSimpleName();
    private final Context mContext;
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

    public void getDigitalKey(final RestCallBack<List<DigitalKey>> callBack) {
        Call<List<DigitalKey>> call = restService.getDigitalKey();
        call.enqueue(new Callback<List<DigitalKey>>() {
            @Override
            public void onResponse(@NonNull Call<List<DigitalKey>> call,
                                   @NonNull Response<List<DigitalKey>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<DigitalKey>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void markAccessPointFavUnFav(AccessPointFavUnFavRequest model,
                                        RestEmptyCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Call<ResponseBody> call = restService.markAccessPointFavUnFav(json);
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

    public void getNotificationCount(final RestCallBack<NotificationCount> callBack) {
        Call<NotificationCount> call = restService.getNotificationCount();
        call.enqueue(new Callback<NotificationCount>() {
            @Override
            public void onResponse(@NonNull Call<NotificationCount> call,
                                   @NonNull Response<NotificationCount> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<NotificationCount> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void getNotificationStatus(final RestCallBack<NotificationStatus> callBack) {
        Call<NotificationStatus> call = restService.getNotificationStatusList();
        call.enqueue(new Callback<NotificationStatus>() {
            @Override
            public void onResponse(@NonNull Call<NotificationStatus> call,
                                   @NonNull Response<NotificationStatus> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<NotificationStatus> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void setNotificationStatus(String id, RestEmptyCallBack<ResponseBody> callBack) {
//        String json = new Gson().toJson(id);
        Call<ResponseBody> call = restService.setNotificationStatus(id);
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

    public void updateNotificationOneTime(boolean value, RestEmptyCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(value);
        Call<ResponseBody> call = restService.updateNotificationOneTime(json);
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

    public void checkIsUserActive(final RestCallBack<UserStatus> callBack) {
        Call<UserStatus> call = restService.checkUserIsActive();
        call.enqueue(new Callback<UserStatus>() {
            @Override
            public void onResponse(@NonNull Call<UserStatus> call,
                                   @NonNull Response<UserStatus> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<UserStatus> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void getAdvertisements(final RestCallBack<List<CouponsAdvertisement>> callBack) {
        Call<List<CouponsAdvertisement>> call = restService.getAdvertisementList();
        call.enqueue(new Callback<List<CouponsAdvertisement>>() {
            @Override
            public void onResponse(@NonNull Call<List<CouponsAdvertisement>> call,
                                   @NonNull Response<List<CouponsAdvertisement>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<CouponsAdvertisement>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
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


    public void getUserData(final RestCallBack<User> callBack) {
        Call<User> call = restService.getUser();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call,
                                   @NonNull Response<User> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void getAccessPoints(boolean isGuestUser, final RestCallBack<List<AccessPoint>> callBack) {
        Call<List<AccessPoint>> call;
        if (isGuestUser) {
            call = restService.getAccessPointsForGuest();
        } else {
            call = restService.getAccessPoints();
        }
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

    public void getPaymentHistory(final RestCallBack<List<PaymentTransactionResponse>> callBack) {
        Call<List<PaymentTransactionResponse>> call = restService.getPaymentHistory();
        call.enqueue(new Callback<List<PaymentTransactionResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<PaymentTransactionResponse>> call,
                                   @NonNull Response<List<PaymentTransactionResponse>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<PaymentTransactionResponse>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void getBusinessTypes(final RestCallBack<List<BusinessType>> callBack) {
        Call<List<BusinessType>> call = restService.getBusinessTypes();
        call.enqueue(new Callback<List<BusinessType>>() {
            @Override
            public void onResponse(@NonNull Call<List<BusinessType>> call,
                                   @NonNull Response<List<BusinessType>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<BusinessType>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void getBusiness(String id, final RestCallBack<List<Business>> callBack) {
        Call<List<Business>> call = restService.getBusinesses(id);
        call.enqueue(new Callback<List<Business>>() {
            @Override
            public void onResponse(@NonNull Call<List<Business>> call,
                                   @NonNull Response<List<Business>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Business>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void login(Login model,
                      RestCallBack<AuthenticationResult> callBack) {
        String json = new Gson().toJson(model);
        Call<ResponseBody> call = restService.accountLogin(json);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                List<String> Cookielist = response.headers().values("Set-Cookie");
                String jsessionid = (Cookielist.get(0).split(";"))[0];

                AuthenticationResult result = null;
                if (response.body() != null) {
                    result = new AuthenticationResult();
                    result.setAuthenticationCookie(jsessionid);
                    result.setId(Long.parseLong(response.body().toString()));
                }
                if (callBack != null)
                    callBack.onResponse(result);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void getGuestEntry(final RestCallBack<List<GuestEntryDoor>> callBack) {
        Call<List<GuestEntryDoor>> call = restService.getGuestEntry();
        call.enqueue(new Callback<List<GuestEntryDoor>>() {
            @Override
            public void onResponse(@NonNull Call<List<GuestEntryDoor>> call,
                                   @NonNull Response<List<GuestEntryDoor>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<GuestEntryDoor>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void getServiceKey(final RestCallBack<List<ServiceKey>> callBack) {
        Call<List<ServiceKey>> call = restService.getServiceKey();
        call.enqueue(new Callback<List<ServiceKey>>() {
            @Override
            public void onResponse(@NonNull Call<List<ServiceKey>> call,
                                   @NonNull Response<List<ServiceKey>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<ServiceKey>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void revokeDigitalKey(DigitalKeyUpdate model,
                                 RestEmptyCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Call<ResponseBody> call = restService.revokeDigitalKey(json);
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

    public void revokeServiceKey(DigitalKeyUpdate model,
                                 RestEmptyCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Call<ResponseBody> call = restService.revokeServiceKey(json);
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

    public void resetServiceKey(long id,
                                RestEmptyCallBack<ResponseBody> callBack) {
        Call<ResponseBody> call = restService.resetServiceKey(id);
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

    public void createServiceKey(ServiceKey serviceKey,
                                 RestEmptyCallBack<ResponseBody> callBack) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        if (serviceKey.getMapFile() != null) {
            parts.add(MultipartBody.Part.createFormData("mapImage",
                    serviceKey.getFileName(),
                    RequestBody.create(MediaType.parse("multipart/form-data"),
                            serviceKey.getMapFile())));

        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Call<ResponseBody> call = restService.createServiceKey(
                convertRequestBody(String.valueOf(serviceKey.getId())),
                convertRequestBody(String.valueOf(serviceKey.getCompanyId())),
                convertRequestBody(serviceKey.getRecipient()),
                convertRequestBody(serviceKey.getTechnicianName()),
                convertRequestBody(dateFormat.format(serviceKey.getFromUtc())),
                convertRequestBody(dateFormat.format(serviceKey.getToUtc())),
                convertRequestBody(dateFormat.format(serviceKey.getStart())),
                convertRequestBody(dateFormat.format(serviceKey.getEnd())),
                convertRequestBody(String.valueOf(serviceKey.isNoEndDate())),
                convertRequestBody(String.valueOf(serviceKey.isFullDay())),
                convertRequestBody(String.valueOf(serviceKey.isRevoked())),
                convertRequestBody(String.valueOf(serviceKey.getRepeatType())),
                convertRequestBody(String.valueOf(serviceKey.getRepeatValueList())),
                convertRequestBody(serviceKey.getSelectedEntryJSON()),
                convertRequestBody(serviceKey.getNotes()),
                parts);
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

    private RequestBody convertRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value == null ? "" : value);

    }

    public void deleteMaintenanceReq(long id,
                                     RestEmptyCallBack<ResponseBody> callBack) {
        Call<ResponseBody> call = restService.deleteMaintenanceReq(id);
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

    public void createMaintenanceRequest(MaintenanceRequest maintenanceRequest,
                                         RestEmptyCallBack<ResponseBody> callBack) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        if (maintenanceRequest.getUploadMaintenanceRequestFiles().size() > 0) {
            for (int i = 0; i < maintenanceRequest.getUploadMaintenanceRequestFiles().size(); i++) {
                parts.add(MultipartBody.Part.createFormData("uploadMaintenanceRequestFiles",
                        maintenanceRequest.getUploadMaintenanceRequestFiles().get(i).getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"),
                                maintenanceRequest.getUploadMaintenanceRequestFiles().get(i))));
            }
        }
        Call<ResponseBody> call = restService.newMaintenanceRequest(parts,
                RequestBody.create(MediaType.parse("text/plain"),
                        maintenanceRequest.getDescription()),
                RequestBody.create(MediaType.parse("text/plain"),
                        maintenanceRequest.getTitle()),
                RequestBody.create(MediaType.parse("text/plain"), "0"),
                RequestBody.create(MediaType.parse("text/plain"),
                        maintenanceRequest.getNeedPermission().toString()));
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

    public void editMaintenanceRequest(MaintenanceRequest maintenanceRequest,
                                       RestEmptyCallBack<ResponseBody> callBack) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        if (maintenanceRequest.getUploadMaintenanceRequestFiles().size() > 0) {
            for (int i = 0; i < maintenanceRequest.getUploadMaintenanceRequestFiles().size(); i++) {
                parts.add(MultipartBody.Part.createFormData("uploadMaintenanceRequestFiles",
                        maintenanceRequest.getUploadMaintenanceRequestFiles().get(i).getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"),
                                maintenanceRequest.getUploadMaintenanceRequestFiles().get(i))));
            }
        }

        String json = new Gson().toJson(maintenanceRequest.getMaintenanceRequestFiles());

        Call<ResponseBody> call = restService.editMaintenanceRequest(parts,
                RequestBody.create(MediaType.parse("text/plain"),
                        maintenanceRequest.getDescription()),
                RequestBody.create(MediaType.parse("text/plain"),
                        maintenanceRequest.getTitle()),
                RequestBody.create(MediaType.parse("text/plain"),
                        String.valueOf(maintenanceRequest.getId())),
                RequestBody.create(MediaType.parse("text/plain"),
                        String.valueOf(maintenanceRequest.getCompanyId())),
                RequestBody.create(MediaType.parse("text/plain"),
                        String.valueOf(maintenanceRequest.getTechnicianName())),
                RequestBody.create(MediaType.parse("text/plain"),
                        maintenanceRequest.getNeedPermission().toString()),
                RequestBody.create(MediaType.parse("text/plain"),
                        maintenanceRequest.getStatus().toString()),
                RequestBody.create(MediaType.parse("text/plain"),
                        maintenanceRequest.getDeleteMaintenanceRequestFiles()),
                RequestBody.create(MediaType.parse("text/plain"), json));
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

    public void getAmenitiesBookingList(final RestCallBack<List<AmenitiesBooking>> callBack) {
        Call<List<AmenitiesBooking>> call = restService.getAmenitiesBookingList();
        call.enqueue(new Callback<List<AmenitiesBooking>>() {
            @Override
            public void onResponse(@NonNull Call<List<AmenitiesBooking>> call,
                                   @NonNull Response<List<AmenitiesBooking>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<AmenitiesBooking>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void updateAmenitiesStatus(long id, boolean isApproved,
                                      final RestCallBack<ResponseBody> callBack) {
        Call<ResponseBody> call = restService.updateAmenitiesStatus(id, isApproved);
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

    public void getRecentCHatList(final RestCallBack<List<RecentChat>> callBack) {
        Call<List<RecentChat>> call = restService.getRecentChatList();
        call.enqueue(new Callback<List<RecentChat>>() {
            @Override
            public void onResponse(@NonNull Call<List<RecentChat>> call,
                                   @NonNull Response<List<RecentChat>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<RecentChat>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void getChatTopicList(int applicationUserId, final RestCallBack<List<ChatTopic>> callBack) {
        Call<List<ChatTopic>> call = restService.getTopicList(applicationUserId);
        call.enqueue(new Callback<List<ChatTopic>>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatTopic>> call,
                                   @NonNull Response<List<ChatTopic>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<ChatTopic>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void getPDKActivationCode(final RestCallBack<String> callBack) {
        Call<String> call = restService.getPDKActivationCode();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,
                                   @NonNull Response<String> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void getUsersChatList(final RestCallBack<List<ResidentChat>> callBack) {
        Call<List<ResidentChat>> call = restService.getResidentList();
        call.enqueue(new Callback<List<ResidentChat>>() {
            @Override
            public void onResponse(@NonNull Call<List<ResidentChat>> call,
                                   @NonNull Response<List<ResidentChat>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<ResidentChat>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void updateTopicStatus(UpdateTopicStatus model,
                                  RestEmptyCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Call<ResponseBody> call = restService.updateTopicStatus(json);
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

    public void checkCameraAccess(CheckCameraAccess model,
                                  RestCallBack<String> callBack) {
        String json = new Gson().toJson(model);
        Call<String> call = restService.checkCameraAccess(json);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,
                                   @NonNull Response<String> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void updateUserSettings(UserUpdate model,
                                   RestCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Call<ResponseBody> call = restService.updatehapticvalue(json);
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

    public void getPendingChatRequests(final RestCallBack<List<ChatTopic>> callBack) {
        Call<List<ChatTopic>> call = restService.getPendingChatRequest();
        call.enqueue(new Callback<List<ChatTopic>>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatTopic>> call,
                                   @NonNull Response<List<ChatTopic>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<ChatTopic>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void checkMaintenanceRating(RestCallBack<String> callBack) {
        Call<String> call = restService.checkForMaintenanceRating();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,
                                   @NonNull Response<String> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void updateRatingMRS(MRSRating model,
                                RestEmptyCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Call<ResponseBody> call = restService.updateMRSRating(json);
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

    public void updateAccessPoints(ArrayList<AccessPointUpdate> model,
                                   RestEmptyCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Call<ResponseBody> call = restService.updateAccessPoints(json);
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

    public void getMarketPlaceCategories(final RestCallBack<List<MarketPlaceCategories>> callBack) {
        Call<List<MarketPlaceCategories>> call = restService.getMarketPlaceCategories();
        call.enqueue(new Callback<List<MarketPlaceCategories>>() {
            @Override
            public void onResponse(@NonNull Call<List<MarketPlaceCategories>> call,
                                   @NonNull Response<List<MarketPlaceCategories>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<MarketPlaceCategories>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void updatePushSilentValue(UserUpdate model,
                                      RestCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Call<ResponseBody> call = restService.updatePushSilentValue(json);
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

    public void getCommunityNotifications(final RestCallBack<List<CommunityNotificationList>> callBack) {
        Call<List<CommunityNotificationList>> call = restService.getCommunityNotifications();
        call.enqueue(new Callback<List<CommunityNotificationList>>() {
            @Override
            public void onResponse(@NonNull Call<List<CommunityNotificationList>> call,
                                   @NonNull Response<List<CommunityNotificationList>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<CommunityNotificationList>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void deleteCommunityNotification(long id,
                                            RestEmptyCallBack<ResponseBody> callBack) {
        Call<ResponseBody> call = restService.deleteCommunityNotification(id);
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

    public void createCommunityNotification(CommunityNotificationList model,
                                            RestCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Log.e("CREATE JSON >> ", json);
        Call<ResponseBody> call = restService.createCommunityNotification(json);
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

    public void updateCommunityNotification(CommunityNotificationList model,
                                            RestCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Call<ResponseBody> call = restService.updateCommunityNotification(json);
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

    public void getNotificationRecipient(long id, final RestCallBack<NotificationRecipient> callBack) {
        Call<NotificationRecipient> call = restService.getNotificationRecipient(id);
        call.enqueue(new Callback<NotificationRecipient>() {
            @Override
            public void onResponse(@NonNull Call<NotificationRecipient> call,
                                   @NonNull Response<NotificationRecipient> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<NotificationRecipient> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void getNotificationUsers(final RestCallBack<List<ResidentChat>> callBack) {
        Call<List<ResidentChat>> call = restService.getNotificationUserList();
        call.enqueue(new Callback<List<ResidentChat>>() {
            @Override
            public void onResponse(@NonNull Call<List<ResidentChat>> call,
                                   @NonNull Response<List<ResidentChat>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<ResidentChat>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }


    public void getVendors(final RestCallBack<List<Vendors>> callBack) {
        Call<List<Vendors>> call = restService.getVendors();
        call.enqueue(new Callback<List<Vendors>>() {
            @Override
            public void onResponse(@NonNull Call<List<Vendors>> call,
                                   @NonNull Response<List<Vendors>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Vendors>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void getTechnicians(long id, final RestCallBack<List<Vendors.Technicians>> callBack) {
        Call<List<Vendors.Technicians>> call = restService.getTechnicians(id);
        call.enqueue(new Callback<List<Vendors.Technicians>>() {
            @Override
            public void onResponse(@NonNull Call<List<Vendors.Technicians>> call,
                                   @NonNull Response<List<Vendors.Technicians>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Vendors.Technicians>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void getServiceKeyGuestEntry(final RestCallBack<List<GuestEntryDoor>> callBack) {
        Call<List<GuestEntryDoor>> call = restService.getServiceKeyGuestEntry();
        call.enqueue(new Callback<List<GuestEntryDoor>>() {
            @Override
            public void onResponse(@NonNull Call<List<GuestEntryDoor>> call,
                                   @NonNull Response<List<GuestEntryDoor>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<GuestEntryDoor>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void resendOTP(Login model,
                          RestCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Log.e("CREATE JSON >> ", json);
        Call<ResponseBody> call = restService.resendOTP(json);
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

    public void checkUserDeviceLogin(Login model,
                                     RestCallBack<Integer> callBack) {
        String json = new Gson().toJson(model);
        Log.e("JSON >> ", json);
        Call<Integer> call = restService.checkUserDeviceLogin(json);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call,
                                   @NonNull Response<Integer> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void saveUserId(Login model, RestCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Log.e("JSON >> ", json);
        Call<ResponseBody> call = restService.saveUserId(json);
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


    public void getUserDeviceId(RestCallBack<UserDeviceId> callBack) {
        Call<UserDeviceId> call = restService.getUserDeviceId();
        call.enqueue(new Callback<UserDeviceId>() {
            @Override
            public void onResponse(@NonNull Call<UserDeviceId> call,
                                   @NonNull Response<UserDeviceId> response) {
                if (callBack != null) {
                    callBack.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDeviceId> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void getParkingPass(long digitalKeyId, RestCallBack<String> callBack) {
        Call<String> call = restService.getParkingPass(digitalKeyId);
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

    public void addVendors(AddVendor model, RestCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Log.e("JSON >> ", json);
        Call<ResponseBody> call = restService.addVendors(json);
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

    public void addTechnicians(AddTechnician model, RestCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Log.e("JSON >> ", json);
        Call<ResponseBody> call = restService.addTechnicians(json);
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

    public void getCameraList(final RestCallBack<List<EENDeviceList>> callBack) {
        Call<List<EENDeviceList>> call = restService.getCameraList();
        call.enqueue(new Callback<List<EENDeviceList>>() {
            @Override
            public void onResponse(@NonNull Call<List<EENDeviceList>> call,
                                   @NonNull Response<List<EENDeviceList>> response) {
                if (callBack != null)
                    callBack.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<EENDeviceList>> call, @NonNull Throwable t) {
                setFailure(callBack, t);
            }
        });
    }

    public void addBrivoSmartHomeUser(AddBrivoSmartHomeUser model, RestCallBack<ResponseBody> callBack) {
        String json = new Gson().toJson(model);
        Log.e("JSON >> ", json);
        Call<ResponseBody> call = restService.addBrivoSmartHomeUser(json);
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