package net.invictusmanagement.invictuslifestyle.webservice;

import net.invictusmanagement.invictuslifestyle.models.AccessCodeResponse;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;
import net.invictusmanagement.invictuslifestyle.models.AmenitiesBooking;
import net.invictusmanagement.invictuslifestyle.models.Business;
import net.invictusmanagement.invictuslifestyle.models.BusinessType;
import net.invictusmanagement.invictuslifestyle.models.ChatTopic;
import net.invictusmanagement.invictuslifestyle.models.CommunityNotificationList;
import net.invictusmanagement.invictuslifestyle.models.CouponsAdvertisement;
import net.invictusmanagement.invictuslifestyle.models.DigitalKey;
import net.invictusmanagement.invictuslifestyle.models.EENAuthorise;
import net.invictusmanagement.invictuslifestyle.models.EENDeviceList;
import net.invictusmanagement.invictuslifestyle.models.GuestEntryDoor;
import net.invictusmanagement.invictuslifestyle.models.MarketPlaceCategories;
import net.invictusmanagement.invictuslifestyle.models.NotificationCount;
import net.invictusmanagement.invictuslifestyle.models.NotificationRecipient;
import net.invictusmanagement.invictuslifestyle.models.NotificationStatus;
import net.invictusmanagement.invictuslifestyle.models.PaymentTransactionResponse;
import net.invictusmanagement.invictuslifestyle.models.RecentChat;
import net.invictusmanagement.invictuslifestyle.models.ResidentChat;
import net.invictusmanagement.invictuslifestyle.models.ResponseListBrivoSmartHome;
import net.invictusmanagement.invictuslifestyle.models.ResponseLoginBrivoSmartHome;
import net.invictusmanagement.invictuslifestyle.models.ServiceKey;
import net.invictusmanagement.invictuslifestyle.models.User;
import net.invictusmanagement.invictuslifestyle.models.UserDeviceId;
import net.invictusmanagement.invictuslifestyle.models.UserStatus;
import net.invictusmanagement.invictuslifestyle.models.Vendors;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RestService {

    @GET("digitalkeys")
    Call<List<DigitalKey>> getDigitalKey();

    @Headers({"Content-Type: application/json "})
    @POST("AccessPoints/markaccesspointentryasfavunfav")
    Call<ResponseBody> markAccessPointFavUnFav(@Body String body);

    @GET("Notifications/getnotificationcountofuser")
    Call<NotificationCount> getNotificationCount();

    @GET("Notifications/GetNotificationStatusList")
    Call<NotificationStatus> getNotificationStatusList();

    @POST("Notifications/NotificationStatusUpdate")
    @Headers({"Content-Type: application/json "})
    Call<ResponseBody> setNotificationStatus(@Body String body);

    @Headers({"Content-Type: application/json "})
    @POST("Notifications/updateapplicationopentime")
    Call<ResponseBody> updateNotificationOneTime(@Body String body);

    @GET("businesses/getbussinessaddvertise")
    Call<List<CouponsAdvertisement>> getAdvertisementList();

    @GET("businesses/types")
    Call<List<BusinessType>> getBusinessTypes();

    @GET("businesses/{business_id}")
    Call<List<Business>> getBusinesses(@Path(value = "business_id", encoded = true) String businessId);

    @GET("Account/checkuserisactive")
    Call<UserStatus> checkUserIsActive();

    @GET("account/profile")
    Call<User> getUser();

    @POST("account/login")
    Call<ResponseBody> accountLogin(@Body String body);

    @GET("AccessPoints/getbrivoaccesscode")
    Call<AccessCodeResponse> getAccessCode();

    @GET("accesspoints")
    Call<List<AccessPoint>> getAccessPoints();

    @GET("AccessPoints/getmyinvitationsentrylist")
    Call<List<AccessPoint>> getAccessPointsForGuest();

    @GET("PaymentTransactions")
    Call<List<PaymentTransactionResponse>> getPaymentHistory();

    @GET("DigitalKeys/guestentry")
    Call<List<GuestEntryDoor>> getGuestEntry();

    @GET("LocationKeys")
    Call<List<ServiceKey>> getServiceKey();

    @Headers({"Content-Type: application/json "})
    @POST("LocationKeys/revokeservicekey")
    Call<ResponseBody> revokeServiceKey(@Body String body);

    @Headers({"Content-Type: application/json "})
    @PUT("DigitalKeys")
    Call<ResponseBody> revokeDigitalKey(@Body String body);

    @POST("LocationKeys/resetservicekey")
    Call<ResponseBody> resetServiceKey(@Query("id") long id);

    @Multipart
    @POST("LocationKeys")
    Call<ResponseBody> createServiceKey(@Part("id") RequestBody id,
                                        @Part("companyId") RequestBody companyId,
                                        @Part("recipient") RequestBody recipent,
                                        @Part("technicianName") RequestBody technicianName,
                                        @Part("fromUtc") RequestBody fromUtc,
                                        @Part("toUtc") RequestBody toUtc,
                                        @Part("start") RequestBody start,
                                        @Part("end") RequestBody end,
                                        @Part("noEndDate") RequestBody noEndDate,
                                        @Part("fullDay") RequestBody fullday,
                                        @Part("isRevoked") RequestBody isRevoked,
                                        @Part("repeatType") RequestBody repeatType,
                                        @Part("repeatValueList") RequestBody repeat,
                                        @Part("selectedEntryJSON") RequestBody selectedEntryJSON,
                                        @Part("notes") RequestBody notes,
                                        @Part List<MultipartBody.Part> file
    );

    @POST("MaintenanceRequests/delete")
    Call<ResponseBody> deleteMaintenanceReq(@Query("id") long id);

    @Multipart
    @POST("maintenancerequests")
    Call<ResponseBody> newMaintenanceRequest(@Part List<MultipartBody.Part> file,
                                             @Part("description") RequestBody description,
                                             @Part("title") RequestBody title,
                                             @Part("id") RequestBody id,
                                             @Part("needPermission") RequestBody needPermission
    );

    @Multipart
    @POST("MaintenanceRequests/editmaintenancerequest")
    Call<ResponseBody> editMaintenanceRequest(@Part List<MultipartBody.Part> file,
                                              @Part("description") RequestBody description,
                                              @Part("title") RequestBody title,
                                              @Part("id") RequestBody id,
                                              @Part("companyId") RequestBody companyId,
                                              @Part("technicianName") RequestBody technicianName,
                                              @Part("needPermission") RequestBody needPermission,
                                              @Part("status") RequestBody status,
                                              @Part("DeleteMaintenanceRequestFiles") RequestBody DeleteMaintenanceRequestFiles,
                                              @Part("maintenanceRequestFiles") RequestBody maintenanceRequestFiles
    );

    @POST("Amenities/allamenitiesbookinglist")
    Call<List<AmenitiesBooking>> getAmenitiesBookingList();

    @FormUrlEncoded
    @POST("Amenities/updatebookingstatus")
    Call<ResponseBody> updateAmenitiesStatus(@Field("amenitiesRequestId") long id,
                                             @Field("isApproved") boolean isApproved);

    @GET("Chat/getrecentchatlist")
    Call<List<RecentChat>> getRecentChatList();

    @GET("Chat/getadminchattopiclist")
    Call<List<ChatTopic>> getTopicList(@Query("applicationUserId") int userId);

    @GET("AccessPoints/getpdkactivationcode")
    Call<String> getPDKActivationCode();

    @GET("Chat/getresidentlist")
    Call<List<ResidentChat>> getResidentList();

    @Headers({"Content-Type: application/json "})
    @POST("Chat/updatetopicstatus")
    Call<ResponseBody> updateTopicStatus(@Body String body);

    @Headers({"Content-Type: application/json "})
    @POST("g/aaa/authenticate")
    Call<EENAuthorise> authenticateEEN(@Body String body);

    @Headers({"Content-Type: application/json "})
    @POST("g/aaa/authorize")
    Call<ResponseBody> authorizeEEN(@Body String body);

    @GET("g/device/list")
    Call<String> getEENDevices();

    @GET("asset/prev/image.jpeg")
    Call<ResponseBody> getEENCameraImage(@Query("id") String id,
                                         @Query("timestamp") String timestamp,
                                         @Query("asset_class") String asset_class);


    @GET
    Call<String> getEENStreamURL(@Url String url);

    @Headers({"Content-Type: application/json "})
    @POST("Camera/checkcameraaccess")
    Call<String> checkCameraAccess(@Body String body);

    @GET("Camera")
    Call<List<EENDeviceList>> getCameraList();

    @Headers({"Content-Type: application/json "})
    @POST("Account/updatehapticvalue")
    Call<ResponseBody> updatehapticvalue(@Body String body);

    @GET("chat/getpandingchatlist")
    Call<List<ChatTopic>> getPendingChatRequest();

    @GET("MaintenanceRequests/checkforclosemrsreviewalert")
    Call<String> checkForMaintenanceRating();

    @Headers({"Content-Type: application/json "})
    @POST("MaintenanceRequests/updatereview")
    Call<ResponseBody> updateMRSRating(@Body String body);

    @Headers({"Content-Type: application/json "})
    @POST("AccessPoints/updateaccesspointdisplayorder")
    Call<ResponseBody> updateAccessPoints(@Body String body);

    @GET("BulletinBoard/marketplacecategories")
    Call<List<MarketPlaceCategories>> getMarketPlaceCategories();

    @Headers({"Content-Type: application/json "})
    @POST("Account/UpdatePushSilentValue")
    Call<ResponseBody> updatePushSilentValue(@Body String body);

    @GET("communityNotifications")
    Call<List<CommunityNotificationList>> getCommunityNotifications();

    @DELETE("communityNotifications")
    Call<ResponseBody> deleteCommunityNotification(@Query("id") long id);

    @Headers({"Content-Type: application/json "})
    @POST("communityNotifications")
    Call<ResponseBody> createCommunityNotification(@Body String body);

    @Headers({"Content-Type: application/json "})
    @PUT("communityNotifications")
    Call<ResponseBody> updateCommunityNotification(@Body String body);

    @GET("communitynotifications/getrecipientlist")
    Call<NotificationRecipient> getNotificationRecipient(@Query("id") long id);

    @GET("communitynotifications/getresidentlist")
    Call<List<ResidentChat>> getNotificationUserList();

    @GET("vendors")
    Call<List<Vendors>> getVendors();

    @GET("technicians")
    Call<List<Vendors.Technicians>> getTechnicians(@Query("vendorMappingId") long id);

    @GET("locationkeys/accesspointlist")
    Call<List<GuestEntryDoor>> getServiceKeyGuestEntry();


    @Headers({"Content-Type: application/json "})
    @POST("account/resendotp")
    Call<ResponseBody> resendOTP(@Body String body);

    @Headers({"Content-Type: application/json "})
    @POST("account/CheckUserDeviceLogin")
    Call<Integer> checkUserDeviceLogin(@Body String body);

    @Headers({"Content-Type: application/json "})
    @POST("Account/SaveUserDeviceId")
    Call<ResponseBody> saveUserId(@Body String body);

    @GET("account/newgetUserDeviceid")
    Call<UserDeviceId> getUserDeviceId();

    @GET("digitalkeys/getdigitalkeyparkingpass")
    Call<String> getParkingPass(@Query("digitalKeyId") long id);


    @Headers({"Content-Type: application/json "})
    @POST("vendors")
    Call<ResponseBody> addVendors(@Body String body);


    @Headers({"Content-Type: application/json "})
    @POST("technicians")
    Call<ResponseBody> addTechnicians(@Body String body);

    @Headers({"Content-Type: application/json "})
    @POST("Account/UpdateBSHCredential")
    Call<ResponseBody> addBrivoSmartHomeUser(@Body String body);

    @Headers({"Content-Type: application/json "})
    @POST("login")
    Call<ResponseLoginBrivoSmartHome> loginBrivoSmartHomeUser(@Body String body);

    @Headers({"Content-Type: application/json "})
    @GET("device")
    Call<ResponseListBrivoSmartHome> getBrivoSmartHomeDevice(@Header("Authorization") String token);

    @Headers({"Content-Type: application/json "})
    @PUT("device/{id}/lock")
    Call<ResponseBody> lockSmartHome(@Path("id") int id, @Header("Authorization") String token);

    @Headers({"Content-Type: application/json "})
    @PUT("device/{id}/unlock")
    Call<ResponseBody> unlockSmartHome(@Path("id") int id, @Header("Authorization") String token);


    @Headers({"Content-Type: application/json "})
    @PUT("device/{id}/on")
    Call<ResponseBody> turnOnSmartHomeSwitch(@Path("id") int id, @Header("Authorization") String token);

    @Headers({"Content-Type: application/json "})
    @PUT("device/{id}/off")
    Call<ResponseBody> turnOffSmartHomeSwitch(@Path("id") int id, @Header("Authorization") String token);

    @Headers({"Content-Type: application/json "})
    @PUT("device/{id}/settings")
    Call<ResponseBody> setThermostatSetting(@Path("id") int id, @Header("Authorization") String token,@Body String data);
}
