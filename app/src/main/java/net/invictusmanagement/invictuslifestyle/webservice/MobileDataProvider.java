package net.invictusmanagement.invictuslifestyle.webservice;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import net.invictusmanagement.invictuslifestyle.BuildConfig;
import net.invictusmanagement.invictuslifestyle.ForbiddenException;
import net.invictusmanagement.invictuslifestyle.activities.AmenitiesCalenderActivity;
import net.invictusmanagement.invictuslifestyle.activities.FavouriteRedeemActivity;
import net.invictusmanagement.invictuslifestyle.activities.GeneralChatActivity;
import net.invictusmanagement.invictuslifestyle.activities.GuestSignupActivity;
import net.invictusmanagement.invictuslifestyle.activities.NewDigitalKeyActivity;
import net.invictusmanagement.invictuslifestyle.activities.RedeemActivity;
import net.invictusmanagement.invictuslifestyle.activities.SellActivity;
import net.invictusmanagement.invictuslifestyle.activities.ServiceActivity;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;
import net.invictusmanagement.invictuslifestyle.models.AccessPointAudit;
import net.invictusmanagement.invictuslifestyle.models.Amenities;
import net.invictusmanagement.invictuslifestyle.models.AuthenticationGuestResult;
import net.invictusmanagement.invictuslifestyle.models.AuthenticationResult;
import net.invictusmanagement.invictuslifestyle.models.AutoPaymentRequest;
import net.invictusmanagement.invictuslifestyle.models.BookAmenity;
import net.invictusmanagement.invictuslifestyle.models.BulletinBoard;
import net.invictusmanagement.invictuslifestyle.models.ChatMessageList;
import net.invictusmanagement.invictuslifestyle.models.ChatToken;
import net.invictusmanagement.invictuslifestyle.models.CheckAvailBookAmenity;
import net.invictusmanagement.invictuslifestyle.models.CreateGuestUser;
import net.invictusmanagement.invictuslifestyle.models.DigitalKey;
import net.invictusmanagement.invictuslifestyle.models.DigitalKeyRenew;
import net.invictusmanagement.invictuslifestyle.models.DigitalKeyUpdate;
import net.invictusmanagement.invictuslifestyle.models.Feedback;
import net.invictusmanagement.invictuslifestyle.models.ForceUpdateCheck;
import net.invictusmanagement.invictuslifestyle.models.GroupMassageSend;
import net.invictusmanagement.invictuslifestyle.models.GuestDigitalKey;
import net.invictusmanagement.invictuslifestyle.models.GuestLogin;
import net.invictusmanagement.invictuslifestyle.models.GuestLoginData;
import net.invictusmanagement.invictuslifestyle.models.HealthVideo;
import net.invictusmanagement.invictuslifestyle.models.Insurance;
import net.invictusmanagement.invictuslifestyle.models.InsuranceBasicInfo;
import net.invictusmanagement.invictuslifestyle.models.Login;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequestResponse;
import net.invictusmanagement.invictuslifestyle.models.MarkNotificationRead;
import net.invictusmanagement.invictuslifestyle.models.MarketPlaceCategories;
import net.invictusmanagement.invictuslifestyle.models.MarketPlaceCondition;
import net.invictusmanagement.invictuslifestyle.models.MarketPlacePost;
import net.invictusmanagement.invictuslifestyle.models.Notification;
import net.invictusmanagement.invictuslifestyle.models.OpenAccessPoint;
import net.invictusmanagement.invictuslifestyle.models.PaymentRequest;
import net.invictusmanagement.invictuslifestyle.models.PromotionFav;
import net.invictusmanagement.invictuslifestyle.models.QuickDigitalKeyResponse;
import net.invictusmanagement.invictuslifestyle.models.RedeemCoupons;
import net.invictusmanagement.invictuslifestyle.models.RedeemCouponsString;
import net.invictusmanagement.invictuslifestyle.models.RentalPayment;
import net.invictusmanagement.invictuslifestyle.models.Service;
import net.invictusmanagement.invictuslifestyle.models.ServiceKey;
import net.invictusmanagement.invictuslifestyle.models.Survey;
import net.invictusmanagement.invictuslifestyle.models.SurveyList;
import net.invictusmanagement.invictuslifestyle.models.SurveyResult;
import net.invictusmanagement.invictuslifestyle.models.Topic;
import net.invictusmanagement.invictuslifestyle.models.UserUpdate;
import net.invictusmanagement.invictuslifestyle.models.VoiceMail;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileDataProvider {

    private static final String JsonDateFormatString = "yyyy-MM-dd'T'HH:mm:ss";
    private static final int HttpsReadTimeout = 30000;
    private static final int HttpsConnectTimeout = 10000;
    private static final MobileDataProvider _instance = new MobileDataProvider();
    public static Boolean isMRSEnable = false;
    public static Boolean isAmenitiesEnable = false;
    public static String mrsString = "";
    public static String amenitiesString = "";
    private final String _baseUrl;
    private String _authenticationCookie = null;
    private onForbiddenListener _onForbiddenListener;
    private String httpMessage;
    public static String loginErrorMessage = "";
    public static String guestLoginErrorMessage = "";

    private MobileDataProvider() {
        _baseUrl = BuildConfig._baseUrl;
    }

    public static MobileDataProvider getInstance() {
        return _instance;
    }

    public void setAuthenticationCookie(String cookie) {
        //TODO change cookie
        RestClient.getInstance().setAuthenticationCookie(cookie);
        _authenticationCookie = cookie;
    }

    public void setOnForbiddenListener(onForbiddenListener listener) {
        _onForbiddenListener = listener;
    }

    public String getOnHttpMessage() {
        return httpMessage;
    }

    public void setOnHttpMessage(String message) {
        httpMessage = message;
    }

    public AuthenticationResult authenticate(Login payload) throws IOException, ForbiddenException {

        AuthenticationResult result = new AuthenticationResult();
        HttpsURLConnection https = null;
        try {
            Log.d(Utilities.TAG, "Authenticating");
//            URL url = new URL(_baseUrl + "account/login");
            URL url = new URL(_baseUrl + "account/newlogin");
            Log.d(Utilities.TAG, "" + url.toString());

            https = (HttpsURLConnection) url.openConnection();
            https.setSSLSocketFactory(getSslSocketFactory());
            https.setHostnameVerifier((s, sslSession) -> true);
            https.setReadTimeout(HttpsReadTimeout);
            https.setConnectTimeout(HttpsConnectTimeout);
            https.setRequestProperty("Content-Type", "application/json");
            https.setRequestProperty("Accept", "application/json");
            https.setRequestMethod("POST");
            https.setDoInput(true);
            https.connect();
            OutputStream outputStream = https.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            writer.write(new GsonBuilder().create().toJson(payload));
            writer.flush();
            writer.close();
            outputStream.close();

            int responseCode = https.getResponseCode();
            Log.d(Utilities.TAG, "Authenticate returned: " + responseCode);
            Log.d(Utilities.TAG, "_baseUrl: " + _baseUrl);
            Log.d(Utilities.TAG, "Request: " + new GsonBuilder().create().toJson(payload));

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                StringBuilder json = new StringBuilder();
                InputStream inputStream = https.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();
                inputStream.close();

                Log.e("login response >>", json.toString());
                result = new Gson().fromJson(json.toString(), AuthenticationResult.class);

//                result.setId(Long.parseLong(json.toString()));

                Map<String, List<String>> headerFields = https.getHeaderFields();
                List<String> cookiesHeader = headerFields.get("Set-Cookie");

                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        if (cookie.contains("AspNetCore.MobileIdentity.Application")) {
                            result.setAuthenticationCookie(cookie);
                            break;
                        }
                    }
                }
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                if (_onForbiddenListener != null)
                    _onForbiddenListener.forbidden();
                throw new ForbiddenException();
            } else {
                BufferedReader br;
                if (responseCode == 400) {
                    br = new BufferedReader(new InputStreamReader(https.getErrorStream()));
                    String errorLine = br.readLine();
                    loginErrorMessage = errorLine;
                    if (loginErrorMessage != null) {
                        if (loginErrorMessage.length() > 0) {
                            loginErrorMessage = loginErrorMessage.replace("\"", "");
                        }
                    }
                }
                throw new IOException("Invalid response code.  Code: " + responseCode + " >> " + loginErrorMessage);
            }


        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (https != null)
                https.disconnect();
        }
        return result;
    }
    private SSLSocketFactory getSslSocketFactory() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            // Set the custom SSLContext as the default SSLContext for the application
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public AuthenticationResult verifyOTP(Login payload) throws IOException, ForbiddenException {

        AuthenticationResult result = new AuthenticationResult();
        HttpsURLConnection https = null;
        try {
            Log.d(Utilities.TAG, "Authenticating");
            URL url = new URL(_baseUrl + "account/newverifyotp");
            Log.d(Utilities.TAG, "" + url.toString());
            https = (HttpsURLConnection) url.openConnection();
            https.setSSLSocketFactory(getSslSocketFactory());
            https.setHostnameVerifier((s, sslSession) -> true);
            https.setReadTimeout(HttpsReadTimeout);
            https.setConnectTimeout(HttpsConnectTimeout);
            https.setRequestProperty("Content-Type", "application/json");
            https.setRequestProperty("Accept", "application/json");
            https.setRequestMethod("POST");
            https.setDoInput(true);
            https.connect();
            OutputStream outputStream = https.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            writer.write(new GsonBuilder().create().toJson(payload));
            writer.flush();
            writer.close();
            outputStream.close();

            int responseCode = https.getResponseCode();
            Log.d(Utilities.TAG, "Authenticate returned: " + responseCode);
            Log.d(Utilities.TAG, "_baseUrl: " + _baseUrl);
            Log.d(Utilities.TAG, "Request: " + new GsonBuilder().create().toJson(payload));

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                StringBuilder json = new StringBuilder();
                InputStream inputStream = https.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();
                inputStream.close();

                Log.e("login response >>", json.toString());
                result = new Gson().fromJson(json.toString(), AuthenticationResult.class);

//                result.setId(Long.parseLong(json.toString()));

                CookieManager cookieManager = new java.net.CookieManager();
                Map<String, List<String>> headerFields = https.getHeaderFields();
                List<String> cookiesHeader = headerFields.get("Set-Cookie");

                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        if (cookie.contains("AspNetCore.MobileIdentity.Application")) {
                            result.setAuthenticationCookie(cookie);
                            break;
                        }
                    }
                }
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                if (_onForbiddenListener != null)
                    _onForbiddenListener.forbidden();
                throw new ForbiddenException();
            } else {
                BufferedReader br = null;
                if (responseCode == 400) {
                    br = new BufferedReader(new InputStreamReader(https.getErrorStream()));
                    String errorLine = br.readLine();
                    loginErrorMessage = errorLine;
                }
                throw new IOException("Invalid response code.  Code: " + responseCode);
            }


        } finally {
            if (https != null)
                https.disconnect();
        }
        return result;
    }

    public AuthenticationGuestResult authenticateGuest(String email, String activationCode) throws IOException, ForbiddenException {

        AuthenticationGuestResult result = new AuthenticationGuestResult();
        HttpsURLConnection https = null;
        try {
            Log.d(Utilities.TAG, "Authenticating");
            URL url = new URL(_baseUrl + "account/guestlogin");
            https = (HttpsURLConnection) url.openConnection();
            https.setSSLSocketFactory(getSslSocketFactory());
            https.setHostnameVerifier((s, sslSession) -> true);
            https.setReadTimeout(HttpsReadTimeout);
            https.setConnectTimeout(HttpsConnectTimeout);
            https.setRequestProperty("Content-Type", "application/json");
            https.setRequestProperty("Accept", "application/json");
            https.setRequestMethod("POST");
            https.setDoInput(true);

            GuestLogin payload = new GuestLogin();
            payload.email = email;
            payload.password = activationCode;
            TimeZone timezone = TimeZone.getDefault();
            payload.timeZoneOffset = timezone.getOffset(Calendar.ZONE_OFFSET) / 1000 / 60;

            https.connect();
            OutputStream outputStream = https.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            writer.write(new GsonBuilder().create().toJson(payload));
            writer.flush();
            writer.close();
            outputStream.close();

            int responseCode = https.getResponseCode();
            Log.d(Utilities.TAG, "Authenticate returned: " + responseCode);
            Log.d(Utilities.TAG, "_baseUrl: " + _baseUrl);
            Log.d(Utilities.TAG, "email: " + email);
            Log.d(Utilities.TAG, "Request: " + new GsonBuilder().create().toJson(payload));

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                StringBuilder json = new StringBuilder();
                InputStream inputStream = https.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();
                inputStream.close();
                GuestLoginData guestLoginData = new Gson().fromJson(json.toString(), new TypeToken<GuestLoginData>() {
                }.getType());
                result.userId = guestLoginData.id;
                result.activationCode = guestLoginData.activationCode;

                CookieManager cookieManager = new java.net.CookieManager();
                Map<String, List<String>> headerFields = https.getHeaderFields();
                List<String> cookiesHeader = headerFields.get("Set-Cookie");

                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        if (cookie.contains("AspNetCore.MobileIdentity.Application")) {
                            result.authenticationCookie = cookie;
                            break;
                        }
                    }
                }
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                if (_onForbiddenListener != null)
                    _onForbiddenListener.forbidden();
                throw new ForbiddenException();
            } else {
                BufferedReader br = null;
                if (responseCode == 400) {
                    br = new BufferedReader(new InputStreamReader(https.getErrorStream()));
                    String errorLine = br.readLine();
                    guestLoginErrorMessage = errorLine;
                }
                throw new IOException("Invalid response code.  Code: " + responseCode);
            }


        } finally {
            if (https != null)
                https.disconnect();
        }
        return result;
    }

    public void createGuestUser(CreateGuestUser item, GuestSignupActivity guestSignupActivity) throws IOException, ForbiddenException {
        String json = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).create().toJson(item);
        Log.i("signup>>", json);
        putOrPostMessage(false, "Account/guestregistartion", json, guestSignupActivity);
    }

    public ChatToken getChatToken(String room) throws IOException, ForbiddenException {

        String json = get("chat?room=" + room);
        return new Gson().fromJson(json, new TypeToken<ChatToken>() {
        }.getType());
    }

    public List<VoiceMail> getVoiceMail() throws IOException, ForbiddenException {

        String json = get("chat/getvoicemails");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<List<VoiceMail>>() {
        }.getType());
    }

    public List<HealthVideo> getHealthVideos() throws IOException, ForbiddenException {

        String json = get("HealthVideo");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<List<HealthVideo>>() {
        }.getType());
    }

    public void videoAsFavorite(HealthVideo item) throws IOException, ForbiddenException {
        putOrPost(false, "HealthVideo", String.valueOf(item.id));
    }

    public void videoAsUnFavorite(HealthVideo item) throws IOException, ForbiddenException {
        putOrPost(false, "HealthVideo/markvideoasunfavorite", String.valueOf(item.id));
    }

    public void videoAsViewed(HealthVideo item) throws IOException, ForbiddenException {
        putOrPost(false, "HealthVideo/markvideoasviewed", String.valueOf(item.id));
    }

    public void couponsAsFavorite(Long item) throws IOException, ForbiddenException {
        putOrPost(false, "Businesses/markpromotionasfavorite", String.valueOf(item));
    }

    public void couponsAsUnFavorite(Long item) throws IOException, ForbiddenException {
        putOrPost(false, "Businesses/markpromotionasunfavorite", String.valueOf(item));
    }

    public Double getLoyaltyPoint() throws IOException, ForbiddenException {

        String json = get("LoyaltyPointsRequest/gettotalloyaltypoint");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<Double>() {
        }.getType());
    }

    public List<PromotionFav> getFavCoupons() throws IOException, ForbiddenException {
        String json = get("Businesses/getfavoritepromotions");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<List<PromotionFav>>() {
        }.getType());
    }

    public void redeemCoupon(Context context, RedeemCoupons redeemCoupons, RedeemActivity redeemActivity) throws IOException, ForbiddenException {
        redeemNewCoupon(context, redeemCoupons, redeemActivity);
    }

    public void redeemCoupon(Context context, RedeemCoupons redeemCoupons, FavouriteRedeemActivity redeemActivity) throws IOException, ForbiddenException {
        redeemNewFavCoupon(context, redeemCoupons, redeemActivity);
    }

    public List<BulletinBoard> getBulletinBoard() throws IOException, ForbiddenException {
        String json = get("BulletinBoard");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<List<BulletinBoard>>() {
        }.getType());
    }

    public List<MarketPlaceCategories> getMarketPlaceCategories() throws IOException, ForbiddenException {
        String json = get("BulletinBoard/marketplacecategories");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<List<MarketPlaceCategories>>() {
        }.getType());
    }

    public List<MarketPlaceCondition> getMarketPlacePostCondition() throws IOException, ForbiddenException {
        String json = get("BulletinBoard/marketplacepostconditions");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<List<MarketPlaceCondition>>() {
        }.getType());
    }

    public void markAsCloseOrSoldOut(int item) throws IOException, ForbiddenException {
        putOrPost(false, "BulletinBoard/markascloseorsoldout", String.valueOf(item));
    }

    public void deleteMarketPlaceImage(List<Integer> item) throws IOException, ForbiddenException {
        putOrPost(false, "BulletinBoard/deletemarketplaceimage", String.valueOf(item));
    }

    public void markPostAsFav(int item) throws IOException, ForbiddenException {
        putOrPost(false, "BulletinBoard/markpostasfavorite", String.valueOf(item));
    }

    public void markPostAsUnFav(int item) throws IOException, ForbiddenException {
        putOrPost(false, "BulletinBoard/markpostasunfavorite", String.valueOf(item));
    }

    public void postMarketItem(Context context, MarketPlacePost marketPlacePost, SellActivity sellActivity, ServiceActivity serviceActivity, Boolean isForEdit) throws IOException, ForbiddenException {
        postMarketPlaceFile(marketPlacePost, sellActivity, serviceActivity, isForEdit);
    }

    public List<Notification> getNotifications() throws IOException, ForbiddenException {

        String json = get("notifications");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<List<Notification>>() {
        }.getType());
    }

    public void readNotification(MarkNotificationRead item) throws IOException, ForbiddenException {
        putOrPost(true, "notifications/marknotificationasread", String.valueOf(item.notificationId));
    }

    public void watchVoice(VoiceMail item) throws IOException, ForbiddenException {
        putOrPost(false, "chat/markvideomailasread", String.valueOf(item.id));
    }

    public void deleteVoiceMail(VoiceMail item) throws IOException, ForbiddenException {
        putOrPost(false, "chat/deletevoicemails", String.valueOf(item.id));
    }

    public List<MaintenanceRequestResponse> getMaintenanceRequests() throws IOException, ForbiddenException {

        String json = getWithMRSResponse("maintenancerequests");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<List<MaintenanceRequestResponse>>() {
        }.getType());
    }

    public Service getDigitalServiceType() throws IOException, ForbiddenException {

        String json = get("digitalkeys/servicetypes");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<Service>() {
        }.getType());
    }

    public List<GuestDigitalKey> getDigitalKeysForGuest() throws IOException, ForbiddenException {

        String json = get("digitalkeys/getguestdigitalkey");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<List<GuestDigitalKey>>() {
        }.getType());
    }

    public void createDigitalKey(DigitalKey item) throws IOException, ForbiddenException {

        String json = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).create().toJson(item);
        Log.e("Post >> ", json);
        putOrPost(false, "digitalkeys/createguestkey", json);
    }

    public void createQuickDigitalKey(DigitalKey item, NewDigitalKeyActivity newDigitalKeyActivity) throws IOException, ForbiddenException {

        String json = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).create().toJson(item);
        putOrPost(false, "digitalKeys/quickkey", json, newDigitalKeyActivity);
    }

    public String createQuickDigitalKey(DigitalKey item) throws IOException, ForbiddenException {

        String json = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).create().toJson(item);
        String string = putOrPostWithReturn(false, "digitalKeys/quickkey", json);
        return string;
    }

    public InsuranceBasicInfo getInsuranceBasicInfo() throws IOException, ForbiddenException {

        String json = get("insuranceRequest");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<InsuranceBasicInfo>() {
        }.getType());
    }

    public RentalPayment getPaymentRequest() throws IOException, ForbiddenException {
        String json = get("PaymentRequest");
        Log.e("getPaymentRequest >> ", json);
        return new GsonBuilder().registerTypeAdapter(Date.class,
                new DateDeserializer()).create().fromJson(json,
                new TypeToken<RentalPayment>() {
                }.getType());
    }

    public String postPaymentRequest(PaymentRequest model) throws IOException, ForbiddenException {
        String json = new GsonBuilder().registerTypeAdapter(Date.class,
                new DateSerializer()).create().toJson(model);
        String successModel = putOrPostWithReturn(false, "PaymentRequest", json);
        if (successModel != null) {
            // https://portaldev.invictusmanagement.net/payment/cardinfo?paymentRequestId=6
            Log.e("PostPaymentRequest >> ", successModel);
        }
        return successModel;
    }

    public String setAutoPayment(AutoPaymentRequest model) throws IOException, ForbiddenException {
        String json = new GsonBuilder().registerTypeAdapter(Date.class,
                new DateSerializer()).create().toJson(model);
        Log.e("setAutoPayment Request", json);
        String successModel = putOrPostWithReturn(false,
                "PaymentRequest/setautopay", json);
        if (successModel != null) {
            Log.e("setAutoPayment >> ", successModel);
        }
        return successModel;
    }

    public String digitalKeyRenew(DigitalKeyRenew model) throws IOException, ForbiddenException {
        String json = new GsonBuilder().registerTypeAdapter(Date.class,
                new DateSerializer()).create().toJson(model);
        String successModel = putOrPostWithReturn(false,
                "DigitalKeys/renewdigitalkey", json);
        if (successModel != null) {
            Log.e("cancelautopay >> ", successModel);
        }
        return successModel;
    }

    public String cancelAutoPayment() throws IOException, ForbiddenException {
        String successModel = putOrPostWithReturn(false,
                "PaymentRequest/cancelautopay", null);
        if (successModel != null) {
            Log.e("cancelautopay >> ", successModel);
        }
        return successModel;
    }

    public void accessPointForAudit(AccessPointAudit model) throws IOException, ForbiddenException {
        String json = new GsonBuilder().registerTypeAdapter(Date.class,
                new DateSerializer()).create().toJson(model);
        putOrPost(false, "AccessPoints/accesspointaudit", json);
    }

    public void submitInsurance(Insurance item) throws IOException, ForbiddenException {

        String json = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).create().toJson(item);
        putOrPost(false, "InsuranceRequest", json);
    }

    public void createFeedback(Feedback item) throws IOException, ForbiddenException {

        String json = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).create().toJson(item);
        putOrPost(false, "feedback", json);
    }

    public void openAccessPoint(OpenAccessPoint item) throws IOException, ForbiddenException {

        String json = new GsonBuilder().create().toJson(item);
        Log.e("post >> ", json);
        putOrPost(false, "accesspoints", json);
    }

    public void sendToVoiceMail(long accessPointId) throws IOException, ForbiddenException {
        String json = new GsonBuilder().create().toJson(accessPointId);
        putOrPost(false, "chat", json);
    }

    public List<Amenities> getAmenitiesList() throws IOException, ForbiddenException {
        String json = getWithAmenitiesResponse("amenities");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<List<Amenities>>() {
        }.getType());
    }

    public void getAmenitiesBookingList(Long id, AmenitiesCalenderActivity amenitiesCalenderActivity) throws IOException, ForbiddenException {

        String json = new GsonBuilder().create().toJson(id);
        putOrPost(false, "amenities/amenitiesbookinglist", json, amenitiesCalenderActivity);
    }

    public void checkAvailiblity(CheckAvailBookAmenity id, AmenitiesCalenderActivity amenitiesCalenderActivity) throws IOException, ForbiddenException {

        String json = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).create().toJson(id);
        Log.e("JSON for availability ", json);
        putOrPost(false, "amenities/checkbookingavailablity", json, amenitiesCalenderActivity, false);
    }

    public void bookAmenity(BookAmenity id, AmenitiesCalenderActivity amenitiesCalenderActivity) throws IOException, ForbiddenException {

        String json = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).create().toJson(id);
        Log.e("bookamenities >> ", json);
        putOrPost(false, "amenities/bookamenities", json, amenitiesCalenderActivity, true);
    }

    public void forceUpdateCheck(ForceUpdateCheck forceUpdateCheck) throws IOException, ForbiddenException {
        String json = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).create().toJson(forceUpdateCheck);
        putOrPostForceUpdate(false, "account/forceupdatecheck", json);
    }

    public void getChatMessageList(ChatMessageList chatMessageList) throws IOException, ForbiddenException {

        String json = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).create().toJson(chatMessageList);
        putOrPostMessageList(false, "chat/getchatmessagelist", json);
    }

    public List<Topic> getTopicList() throws IOException, ForbiddenException {

        String json = get("chat/getchattopiclist");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<List<Topic>>() {
        }.getType());
    }


    public void chatImage(Context context, File file, int adminUserId, long chatRequestId, GeneralChatActivity generalChatActivity) throws IOException, ForbiddenException {
        uploadChatImage(context, file, adminUserId, chatRequestId, generalChatActivity);
    }

    public List<SurveyList> getSurveyList() throws IOException, ForbiddenException {
        String json = get("SurveyForm");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<List<SurveyList>>() {
        }.getType());
    }

    public List<Survey> getSurvey(long id) throws IOException, ForbiddenException {
        String json = get("SurveyForm/GetQuestionList(" + id + ")");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<List<Survey>>() {
        }.getType());
    }

    public void submitQuiz(ArrayList<SurveyResult> item) throws IOException, ForbiddenException {
        String json = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).create().toJson(item);
        putOrPost(false, "SurveyForm/PostQuestionList", json);
    }

    private String get(String path) throws IOException, ForbiddenException {

        StringBuilder json = new StringBuilder();
        HttpsURLConnection https = null;
        try {
            Log.d(Utilities.TAG, "Executing Https Get: " + _baseUrl + path);
            URL url = new URL(_baseUrl + path);
            https = (HttpsURLConnection) url.openConnection();
            https.setSSLSocketFactory(getSslSocketFactory());
            https.setHostnameVerifier((s, sslSession) -> true);
            https.setReadTimeout(HttpsReadTimeout);
            https.setConnectTimeout(HttpsConnectTimeout);
            if (!TextUtils.isEmpty(_authenticationCookie)) {
                https.setRequestProperty("Cookie", _authenticationCookie);
            }
            https.setSSLSocketFactory(getSslSocketFactory());
            https.setHostnameVerifier((s, sslSession) -> true);
            https.connect();

            int responseCode = https.getResponseCode();
            if (responseCode == 400) {
                InputStream stream = https.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(" --> Error 400 <-- " + line);
                }
                reader.close();
                stream.close();
            }
            Log.d(Utilities.TAG, "Https Get returned: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                if (_onForbiddenListener != null)
                    _onForbiddenListener.forbidden();
                throw new ForbiddenException();
            } else if (responseCode != HttpsURLConnection.HTTP_OK && responseCode != HttpsURLConnection.HTTP_CREATED)
                throw new IOException("Invalid response code.  Code: " + responseCode);

            InputStream stream = https.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();
            stream.close();
        } finally {
            if (https != null)
                https.disconnect();
        }
        return json.toString();
    }

    private void putOrPost(boolean isPut, String path, String payload) throws IOException, ForbiddenException {

        HttpsURLConnection https = null;
        try {
            Log.e(Utilities.TAG, "Executing Https Put/Post: " + _baseUrl + path);
            URL url = new URL(_baseUrl + path);
            https = (HttpsURLConnection) url.openConnection();
            https.setSSLSocketFactory(getSslSocketFactory());
            https.setHostnameVerifier((s, sslSession) -> true);
            https.setReadTimeout(HttpsReadTimeout);
            https.setConnectTimeout(HttpsConnectTimeout);
            https.setRequestProperty("Content-Type", "application/json");
            https.setRequestProperty("Accept", "application/json");
            https.setRequestMethod(isPut ? "PUT" : "POST");

            https.setDoInput(true);
            if (!TextUtils.isEmpty(_authenticationCookie)) {
                https.setRequestProperty("Cookie", _authenticationCookie);
            }
            https.connect();
            OutputStream stream = https.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
            writer.write(payload);
            writer.flush();
            writer.close();
            stream.close();

            int responseCode = https.getResponseCode();
            if (responseCode == 400) {
                InputStream stream2 = https.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream2));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(" --> Error 400 <-- " + line);
                }
                reader.close();
                stream2.close();
            }
            Log.d(Utilities.TAG, "Https Put/Post returned: " + responseCode);
            Log.d(Utilities.TAG, "Https Put/Post returned: " + https.getResponseMessage());
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                if (_onForbiddenListener != null)
                    _onForbiddenListener.forbidden();
                throw new ForbiddenException();
            } else if (responseCode != HttpsURLConnection.HTTP_OK && responseCode != HttpsURLConnection.HTTP_CREATED)
                throw new IOException("Invalid response code.  Code: " + responseCode);
        } finally {
            if (https != null)
                https.disconnect();
        }
    }

    private String putOrPostWithReturn(boolean isPut, String path, String payload) throws IOException, ForbiddenException {

        HttpsURLConnection https = null;
        String successModel = null;
        try {
            Log.e(Utilities.TAG, "Executing Https Put/Post: " + _baseUrl + path);
            URL url = new URL(_baseUrl + path);
            https = (HttpsURLConnection) url.openConnection();
            https.setSSLSocketFactory(getSslSocketFactory());
            https.setHostnameVerifier((s, sslSession) -> true);
            https.setReadTimeout(HttpsReadTimeout);
            https.setConnectTimeout(HttpsConnectTimeout);
            https.setRequestProperty("Content-Type", "application/json");
            https.setRequestProperty("Accept", "application/json");
            https.setRequestMethod(isPut ? "PUT" : "POST");

            https.setDoInput(true);
            if (!TextUtils.isEmpty(_authenticationCookie)) {
                https.setRequestProperty("Cookie", _authenticationCookie);
            }
            https.connect();
            OutputStream stream = https.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
            if (payload != null) {
                writer.write(payload);
            }
            writer.flush();
            writer.close();
            stream.close();

            int responseCode = https.getResponseCode();
            BufferedReader br;
            if (responseCode == 400) {
                br = new BufferedReader(new InputStreamReader(https.getErrorStream()));
            } else if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(https.getInputStream()));
                successModel = br.readLine();
            }

            if (responseCode == 400) {
                InputStream stream2 = https.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream2));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(" --> Error 400 <-- " + line);
                }
                reader.close();
                stream2.close();
            }
            Log.d(Utilities.TAG, "Https Put/Post returned: " + responseCode);
            Log.d(Utilities.TAG, "Https Put/Post returned: " + https.getResponseMessage());
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                if (_onForbiddenListener != null)
                    _onForbiddenListener.forbidden();
                throw new ForbiddenException();
            } else if (responseCode != HttpsURLConnection.HTTP_OK && responseCode != HttpsURLConnection.HTTP_CREATED)
                throw new IOException("Invalid response code.  Code: " + responseCode);
        } finally {
            if (https != null)
                https.disconnect();
        }

        return successModel;
    }

    private void putOrPostForceUpdate(boolean isPut, String path, String payload) throws IOException, ForbiddenException {

        HttpsURLConnection https = null;
        try {
            Log.d(Utilities.TAG, "Executing Https Put/Post: " + _baseUrl + path);
            URL url = new URL(_baseUrl + path);
            https = (HttpsURLConnection) url.openConnection();
            https.setSSLSocketFactory(getSslSocketFactory());
            https.setHostnameVerifier((s, sslSession) -> true);
            https.setReadTimeout(HttpsReadTimeout);
            https.setConnectTimeout(HttpsConnectTimeout);
            https.setRequestProperty("Content-Type", "application/json");
            https.setRequestProperty("Accept", "application/json");
            https.setRequestMethod(isPut ? "PUT" : "POST");

            https.setDoInput(true);
            if (!TextUtils.isEmpty(_authenticationCookie)) {
                https.setRequestProperty("Cookie", _authenticationCookie);
            }
            https.connect();
            OutputStream stream = https.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
            writer.write(payload);
            writer.flush();
            writer.close();
            stream.close();

            int responseCode = https.getResponseCode();
            BufferedReader br = null;
            if (responseCode == 400) {
                br = new BufferedReader(new InputStreamReader(https.getErrorStream()));
                String errorLine = br.readLine();
            } else if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(https.getInputStream()));
                String successModel = br.readLine();
                TabbedActivity.responseForceUpdate(successModel);
            }

            Log.d(Utilities.TAG, "Https Put/Post returned: " + responseCode);
            Log.d(Utilities.TAG, "Https Put/Post returned: " + https.getResponseMessage());
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                if (_onForbiddenListener != null)
                    _onForbiddenListener.forbidden();
                throw new ForbiddenException();
            } else if (responseCode != HttpsURLConnection.HTTP_OK && responseCode != HttpsURLConnection.HTTP_CREATED)
                throw new IOException("Invalid response code.  Code: " + responseCode);
        } finally {
            if (https != null)
                https.disconnect();
        }
    }

    private void putOrPostMessageList(boolean isPut, String path, String payload) throws IOException, ForbiddenException {

        HttpsURLConnection https = null;
        try {
            Log.d(Utilities.TAG, "Executing Https Put/Post: " + _baseUrl + path);
            Log.d(Utilities.TAG, "Params " + payload);
            URL url = new URL(_baseUrl + path);
            https = (HttpsURLConnection) url.openConnection();
            https.setSSLSocketFactory(getSslSocketFactory());
            https.setHostnameVerifier((s, sslSession) -> true);
            https.setReadTimeout(HttpsReadTimeout);
            https.setConnectTimeout(HttpsConnectTimeout);
            https.setRequestProperty("Content-Type", "application/json");
            https.setRequestProperty("Accept", "application/json");
            https.setRequestMethod(isPut ? "PUT" : "POST");

            https.setDoInput(true);
            if (!TextUtils.isEmpty(_authenticationCookie)) {
                https.setRequestProperty("Cookie", _authenticationCookie);
            }
            https.connect();
            OutputStream stream = https.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
            writer.write(payload);
            writer.flush();
            writer.close();
            stream.close();

            int responseCode = https.getResponseCode();
            BufferedReader br = null;
            if (responseCode == 400) {
                br = new BufferedReader(new InputStreamReader(https.getErrorStream()));
                String errorLine = br.readLine();
                System.out.println("errorLine: " + errorLine);
            } else if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(https.getInputStream()));
                String successModel = br.readLine();
                GeneralChatActivity.generalChatActivity.responseMessageChat(successModel);
            }

            Log.d(Utilities.TAG, "Https Put/Post returned: " + responseCode);
            Log.d(Utilities.TAG, "Https Put/Post returned: " + https.getResponseMessage());
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                if (_onForbiddenListener != null)
                    _onForbiddenListener.forbidden();
                throw new ForbiddenException();
            } else if (responseCode != HttpsURLConnection.HTTP_OK && responseCode != HttpsURLConnection.HTTP_CREATED)
                throw new IOException("Invalid response code.  Code: " + responseCode);
        } finally {
            if (https != null)
                https.disconnect();
        }
    }

    private String getWithMRSResponse(String path) throws IOException, ForbiddenException {

        StringBuilder json = new StringBuilder();
        HttpsURLConnection https = null;
        try {
            Log.d(Utilities.TAG, "Executing Https Get: " + _baseUrl + path);
            URL url = new URL(_baseUrl + path);
            https = (HttpsURLConnection) url.openConnection();
            https.setSSLSocketFactory(getSslSocketFactory());
            https.setHostnameVerifier((s, sslSession) -> true);
            https.setReadTimeout(HttpsReadTimeout);
            https.setConnectTimeout(HttpsConnectTimeout);
            if (!TextUtils.isEmpty(_authenticationCookie)) {
                https.setRequestProperty("Cookie", _authenticationCookie);
            }
            https.connect();

            int responseCode = https.getResponseCode();
            if (responseCode == 200) {
                InputStream stream = https.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                Log.i("maintenance json", json.toString());
                reader.close();
                stream.close();
                isMRSEnable = true;
            } else if (responseCode == 400) {
                InputStream stream = https.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    mrsString = line;
                    json.append(line);
                }
                reader.close();
                stream.close();
                isMRSEnable = false;
            }

            Log.d(Utilities.TAG, "Https Get returned: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                if (_onForbiddenListener != null)
                    _onForbiddenListener.forbidden();
                throw new ForbiddenException();
            } else if (responseCode != HttpsURLConnection.HTTP_OK && responseCode != HttpsURLConnection.HTTP_CREATED)
                throw new IOException("Invalid response code.  Code: " + responseCode);


        } finally {
            if (https != null)
                https.disconnect();
        }
        return json.toString();
    }

    private String getWithAmenitiesResponse(String path) throws IOException, ForbiddenException {

        StringBuilder json = new StringBuilder();
        HttpsURLConnection https = null;
        try {
            Log.d(Utilities.TAG, "Executing Https Get: " + _baseUrl + path);
            URL url = new URL(_baseUrl + path);
            https = (HttpsURLConnection) url.openConnection();
            https.setSSLSocketFactory(getSslSocketFactory());
            https.setHostnameVerifier((s, sslSession) -> true);
            https.setReadTimeout(HttpsReadTimeout);
            https.setConnectTimeout(HttpsConnectTimeout);
            if (!TextUtils.isEmpty(_authenticationCookie)) {
                https.setRequestProperty("Cookie", _authenticationCookie);
            }
            https.connect();

            int responseCode = https.getResponseCode();
            if (responseCode == 200) {
                InputStream stream = https.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();
                stream.close();
                isAmenitiesEnable = true;
            } else if (responseCode == 400) {
                InputStream stream = https.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    amenitiesString = line;
                    json.append(line);
                }
                reader.close();
                stream.close();
                isAmenitiesEnable = false;
            }

            Log.d(Utilities.TAG, "Https Get returned: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                if (_onForbiddenListener != null)
                    _onForbiddenListener.forbidden();
                throw new ForbiddenException();
            } else if (responseCode != HttpsURLConnection.HTTP_OK && responseCode != HttpsURLConnection.HTTP_CREATED)
                throw new IOException("Invalid response code.  Code: " + responseCode);


        } finally {
            if (https != null)
                https.disconnect();
        }
        return json.toString();
    }

    private void putOrPostMessage(boolean isPut, String path, String payload, GuestSignupActivity guestSignupActivity) throws IOException, ForbiddenException {

        HttpsURLConnection https = null;
        try {
            Log.d(Utilities.TAG, "Executing Https Put/Post: " + _baseUrl + path);
            URL url = new URL(_baseUrl + path);
            https = (HttpsURLConnection) url.openConnection();
            https.setSSLSocketFactory(getSslSocketFactory());
            https.setHostnameVerifier((s, sslSession) -> true);
            https.setReadTimeout(HttpsReadTimeout);
            https.setConnectTimeout(HttpsConnectTimeout);
            https.setRequestProperty("Content-Type", "application/json");
            https.setRequestProperty("Accept", "application/json");
            https.setRequestMethod(isPut ? "PUT" : "POST");

            https.setDoInput(true);
            if (!TextUtils.isEmpty(_authenticationCookie)) {
                https.setRequestProperty("Cookie", _authenticationCookie);
            }
            https.connect();
            OutputStream stream = https.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
            writer.write(payload);
            writer.flush();
            writer.close();
            stream.close();

            int responseCode = https.getResponseCode();
            BufferedReader br = null;
            if (responseCode == 400) {
                br = new BufferedReader(new InputStreamReader(https.getErrorStream()));
                String errorLine = br.readLine();
                setOnHttpMessage(errorLine);
                if (guestSignupActivity != null) {
                    guestSignupActivity.showDialogActivity(errorLine);
                }
            } else if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(https.getInputStream()));
                String errorLine = br.readLine();
                String suuc = errorLine;
            }

            Log.d(Utilities.TAG, "Https Put/Post returned: " + responseCode);
            Log.d(Utilities.TAG, "Https Put/Post returned: " + https.getResponseMessage());
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                if (_onForbiddenListener != null)
                    _onForbiddenListener.forbidden();
                throw new ForbiddenException();
            } else if (responseCode != HttpsURLConnection.HTTP_OK && responseCode != HttpsURLConnection.HTTP_CREATED)
                throw new IOException("Invalid response code.  Code: " + responseCode);
        } finally {
            if (https != null)
                https.disconnect();
        }
    }

    private void putOrPost(boolean isPut, String path, String payload, AmenitiesCalenderActivity amenitiesCalenderActivity, Boolean isForBooking) throws IOException, ForbiddenException {

        HttpsURLConnection https = null;
        try {
            Log.d(Utilities.TAG, "Executing Https Put/Post: " + _baseUrl + path);
            URL url = new URL(_baseUrl + path);
            https = (HttpsURLConnection) url.openConnection();
            https.setSSLSocketFactory(getSslSocketFactory());
            https.setHostnameVerifier((s, sslSession) -> true);
            https.setReadTimeout(HttpsReadTimeout);
            https.setConnectTimeout(HttpsConnectTimeout);
            https.setRequestProperty("Content-Type", "application/json");
            https.setRequestProperty("Accept", "application/json");
            https.setRequestMethod(isPut ? "PUT" : "POST");

            https.setDoInput(true);
            if (!TextUtils.isEmpty(_authenticationCookie)) {
                https.setRequestProperty("Cookie", _authenticationCookie);
            }
            https.connect();
            OutputStream stream = https.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
            writer.write(payload);
            writer.flush();
            writer.close();
            stream.close();

            int responseCode = https.getResponseCode();
            BufferedReader br = null;
            if (responseCode == 400) {
                br = new BufferedReader(new InputStreamReader(https.getErrorStream()));
                String errorLine = br.readLine();
                amenitiesCalenderActivity.responseBookAmenity(errorLine, false);
            } else if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(https.getInputStream()));
                String successModel = br.readLine();
                if (isForBooking) {
                    amenitiesCalenderActivity.responseBookAmenity("Booking Request has been submitted successfully.", true);
                } else {
                    amenitiesCalenderActivity.checkAvail(successModel);

                }
            }

            Log.d(Utilities.TAG, "Https Put/Post returned: " + responseCode);
            Log.d(Utilities.TAG, "Https Put/Post returned: " + https.getResponseMessage());
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                if (_onForbiddenListener != null)
                    _onForbiddenListener.forbidden();
                throw new ForbiddenException();
            } else if (responseCode != HttpsURLConnection.HTTP_OK && responseCode != HttpsURLConnection.HTTP_CREATED)
                throw new IOException("Invalid response code.  Code: " + responseCode);
        } finally {
            if (https != null)
                https.disconnect();
        }
    }

    private void putOrPost(boolean isPut, String path, String payload, AmenitiesCalenderActivity amenitiesCalenderActivity) throws IOException, ForbiddenException {

        HttpsURLConnection https = null;
        try {
            Log.d(Utilities.TAG, "Executing Https Put/Post: " + _baseUrl + path);
            URL url = new URL(_baseUrl + path);
            https = (HttpsURLConnection) url.openConnection();
            https.setSSLSocketFactory(getSslSocketFactory());
            https.setHostnameVerifier((s, sslSession) -> true);
            https.setReadTimeout(HttpsReadTimeout);
            https.setConnectTimeout(HttpsConnectTimeout);
            https.setRequestProperty("Content-Type", "application/json");
            https.setRequestProperty("Accept", "application/json");
            https.setRequestMethod(isPut ? "PUT" : "POST");

            https.setDoInput(true);
            if (!TextUtils.isEmpty(_authenticationCookie)) {
                https.setRequestProperty("Cookie", _authenticationCookie);
            }
            https.connect();
            OutputStream stream = https.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
            writer.write(payload);
            writer.flush();
            writer.close();
            stream.close();

            int responseCode = https.getResponseCode();
            BufferedReader br = null;
            if (responseCode == 400) {
                br = new BufferedReader(new InputStreamReader(https.getErrorStream()));
                String errorLine = br.readLine();
            } else if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(https.getInputStream()));
                String successModel = br.readLine();
                if (successModel != null) {
                    amenitiesCalenderActivity.getAmenitiesBookingListResponse(successModel);
                }
            }


            Log.d(Utilities.TAG, "Https Put/Post returned: " + responseCode);
            Log.d(Utilities.TAG, "Https Put/Post returned: " + https.getResponseMessage());
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                if (_onForbiddenListener != null)
                    _onForbiddenListener.forbidden();
                throw new ForbiddenException();
            } else if (responseCode != HttpsURLConnection.HTTP_OK && responseCode != HttpsURLConnection.HTTP_CREATED)
                throw new IOException("Invalid response code.  Code: " + responseCode);
        } finally {
            if (https != null)
                https.disconnect();
        }
    }

    private void putOrPost(boolean isPut, String path, String payload,
                           NewDigitalKeyActivity newDigitalKeyActivity) throws IOException, ForbiddenException {

        HttpsURLConnection https = null;
        try {
            Log.d(Utilities.TAG, "Executing Https Put/Post: " + _baseUrl + path);
            URL url = new URL(_baseUrl + path);
            https = (HttpsURLConnection) url.openConnection();
            https.setSSLSocketFactory(getSslSocketFactory());
            https.setHostnameVerifier((s, sslSession) -> true);
            https.setReadTimeout(HttpsReadTimeout);
            https.setConnectTimeout(HttpsConnectTimeout);
            https.setRequestProperty("Content-Type", "application/json");
            https.setRequestProperty("Accept", "application/json");
            https.setRequestMethod(isPut ? "PUT" : "POST");

            https.setDoInput(true);
            if (!TextUtils.isEmpty(_authenticationCookie)) {
                https.setRequestProperty("Cookie", _authenticationCookie);
            }
            https.connect();
            OutputStream stream = https.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
            writer.write(payload);
            writer.flush();
            writer.close();
            stream.close();

            int responseCode = https.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(https.getInputStream()));
                String string = br.readLine();
                if (newDigitalKeyActivity != null) {
                    QuickDigitalKeyResponse quickDigitalKeyResponse = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(string, new TypeToken<QuickDigitalKeyResponse>() {
                    }.getType());
                    newDigitalKeyActivity.setString(quickDigitalKeyResponse);
                }
            }


            Log.d(Utilities.TAG, "Https Put/Post returned: " + responseCode);
            Log.d(Utilities.TAG, "Https Put/Post returned: " + https.getResponseMessage());
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                if (_onForbiddenListener != null)
                    _onForbiddenListener.forbidden();
                throw new ForbiddenException();
            } else if (responseCode != HttpsURLConnection.HTTP_OK && responseCode != HttpsURLConnection.HTTP_CREATED)
                throw new IOException("Invalid response code.  Code: " + responseCode);
        } finally {
            if (https != null)
                https.disconnect();
        }
    }

    public void postMarketPlaceFile(MarketPlacePost redeemCoupons, SellActivity sellActivity, ServiceActivity serviceActivity, Boolean isForEdit) {
        // create upload service client
        OkHttpClient.Builder httpClienet = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        httpClienet.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.@NotNull Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Cookie", _authenticationCookie).addHeader("Content-Type", "multipart/form-data").build();
                return chain.proceed(request);
            }
        });

        APIInterface.MarketPostService service = APIServiceGenerator.createService(APIInterface.MarketPostService.class, httpClienet);
        // finally, execute the request
        List<MultipartBody.Part> parts = new ArrayList<>();
        if (redeemCoupons.MarketPlaceImages.size() > 0) {
            for (int i = 0; i < redeemCoupons.MarketPlaceImages.size(); i++) {
                parts.add(MultipartBody.Part.createFormData("PostImages", redeemCoupons.MarketPlaceImages.get(i).getName(), RequestBody.create(MediaType.parse("multipart/form-data"), redeemCoupons.MarketPlaceImages.get(i))));
            }
        } else {
            parts.add(MultipartBody.Part.createFormData("PostImages", "", RequestBody.create(MediaType.parse("multipart/form-data"), "")));
        }

        retrofit2.Call<ResponseBody> call = service.postMarketPlace(parts,
                RequestBody.create(MediaType.parse("text/plain"),
                        String.valueOf(redeemCoupons.id)),
                RequestBody.create(MediaType.parse("text/plain"),
                        String.valueOf(redeemCoupons.IsService)),
                RequestBody.create(MediaType.parse("text/plain"),
                        String.valueOf(redeemCoupons.MarketPlaceCategoryId)),
                RequestBody.create(MediaType.parse("text/plain"),
                        String.valueOf(redeemCoupons.Title)),
                RequestBody.create(MediaType.parse("text/plain"),
                        String.valueOf(redeemCoupons.Description)),
                RequestBody.create(MediaType.parse("text/plain"),
                        String.valueOf(redeemCoupons.Price)),
                RequestBody.create(MediaType.parse("text/plain"),
                        String.valueOf(redeemCoupons.ConditionType)),
                RequestBody.create(MediaType.parse("text/plain"),
                        String.valueOf(redeemCoupons.AvailableDate)),
                RequestBody.create(MediaType.parse("text/plain"),
                        String.valueOf(redeemCoupons.ContactType)),
                RequestBody.create(MediaType.parse("text/plain"),
                        String.valueOf(redeemCoupons.ContactTime)),
                RequestBody.create(MediaType.parse("text/plain"),
                        String.valueOf(redeemCoupons.IsSoldOut)),
                RequestBody.create(MediaType.parse("text/plain"),
                        String.valueOf(redeemCoupons.IsClosed)), RequestBody.create(MediaType.parse("text/plain"), String.valueOf(redeemCoupons.IsApproved)), RequestBody.create(MediaType.parse("text/plain"), String.valueOf(redeemCoupons.IsHourPrice)));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");

                BufferedReader br = null;
                BulletinBoard bulletinBoard = null;
                if (response.code() == 400) {
                    br = new BufferedReader(new InputStreamReader(response.errorBody().byteStream()));
                } else if (response.code() == 200) {
                    br = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                    try {
                        bulletinBoard = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(br.readLine(), new TypeToken<BulletinBoard>() {
                        }.getType());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (response.isSuccessful()) {
                    if (sellActivity != null) {
                        if (isForEdit) {
                            sellActivity.successFullyUploaded("Product updated successfully, please wait for approval", bulletinBoard);
                        } else {
                            sellActivity.successFullyUploaded("Product added successfully, please wait for approval", bulletinBoard);
                        }

                    } else if (serviceActivity != null) {
                        if (isForEdit) {
                            serviceActivity.successFullyUploaded("Service updated successfully, please wait for approval", bulletinBoard);
                        } else {
                            serviceActivity.successFullyUploaded("Service added successfully, please wait for approval", bulletinBoard);
                        }

                    }
                } else {
                    if (sellActivity != null) {
                        if (isForEdit) {
                            sellActivity.failToUpload("Fail to update product, please try again.");
                        } else {
                            sellActivity.failToUpload("Fail to add, please try again.");
                        }

                    } else if (serviceActivity != null) {
                        if (isForEdit) {
                            serviceActivity.failToUpload("Fail to update service, please try again.");
                        } else {
                            serviceActivity.failToUpload("Fail to add service, please try again.");
                        }
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                if (sellActivity != null) {
                    sellActivity.failToUpload("Fail to upload product, please try again.");
                } else if (serviceActivity != null) {
                    serviceActivity.failToUpload("Fail to upload service, please try again.");
                }
            }
        });


    }

    public void redeemNewCoupon(Context context, RedeemCoupons redeemCoupons, RedeemActivity redeemActivity) {
        // create upload service client
        OkHttpClient.Builder httpClienet = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        httpClienet.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.@NotNull Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Cookie", _authenticationCookie).addHeader("Content-Type", "multipart/form-data").build();
                return chain.proceed(request);
            }
        });

        APIInterface.FileUploadService service = APIServiceGenerator.createService(APIInterface.FileUploadService.class, httpClienet);
        // finally, execute the request
        retrofit2.Call<ResponseBody> call = service.upload(MultipartBody.Part.createFormData("ReceiptImage", redeemCoupons.receiptImage.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), redeemCoupons.receiptImage)), RequestBody.create(MediaType.parse("text/plain"), redeemCoupons.orderAmount), RequestBody.create(MediaType.parse("text/plain"), redeemCoupons.receiptNumber), RequestBody.create(MediaType.parse("text/plain"), String.valueOf(redeemCoupons.promotionId)), RequestBody.create(MediaType.parse("text/plain"), String.valueOf(redeemCoupons.businessId)), RequestBody.create(MediaType.parse("text/plain"), String.valueOf(false)), RequestBody.create(MediaType.parse("text/plain"), String.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getLong("userId", 0))));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
                if (response.isSuccessful()) {
                    redeemActivity.successFullyUploaded("Receipt uploaded successfully.");
                } else {
                    redeemActivity.successFullyUploaded("Receipt uploaded failed, please try again.");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                redeemActivity.failToUpload("Receipt uploaded failed, please try again.");
            }
        });


    }

    public void redeemNewFavCoupon(Context context, RedeemCoupons redeemCoupons, FavouriteRedeemActivity redeemActivity) {
        // create upload service client
        OkHttpClient.Builder httpClienet = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        ;
        httpClienet.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.@NotNull Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Cookie", _authenticationCookie).addHeader("Content-Type", "multipart/form-data").build();
                return chain.proceed(request);
            }
        });

        APIInterface.FileUploadService service = APIServiceGenerator.createService(APIInterface.FileUploadService.class, httpClienet);
        // finally, execute the request
        retrofit2.Call<ResponseBody> call = service.upload(MultipartBody.Part.createFormData("ReceiptImage", redeemCoupons.receiptImage.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), redeemCoupons.receiptImage)), RequestBody.create(MediaType.parse("text/plain"), redeemCoupons.orderAmount), RequestBody.create(MediaType.parse("text/plain"), redeemCoupons.receiptNumber), RequestBody.create(MediaType.parse("text/plain"), String.valueOf(redeemCoupons.promotionId)), RequestBody.create(MediaType.parse("text/plain"), String.valueOf(redeemCoupons.businessId)), RequestBody.create(MediaType.parse("text/plain"), String.valueOf(false)), RequestBody.create(MediaType.parse("text/plain"), String.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getLong("userId", 0))));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
                if (response.isSuccessful()) {
                    redeemActivity.successFullyUploaded("Receipt uploaded successfully.");
                } else {
                    redeemActivity.successFullyUploaded("Receipt uploaded failed, please try again.");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                redeemActivity.failToUpload("Receipt uploaded failed, please try again.");
            }
        });


    }

    public void uploadChatImage(Context context, File file, int adminUserId, long chatRequestId, GeneralChatActivity generalChatActivity) {
        // create upload service client
        OkHttpClient.Builder httpClienet = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        ;
        httpClienet.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.@NotNull Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Cookie", _authenticationCookie).addHeader("Content-Type", "multipart/form-data").build();
                return chain.proceed(request);
            }
        });

        APIInterface.ChatImageService service = APIServiceGenerator.createService(APIInterface.ChatImageService.class, httpClienet);
        // finally, execute the request
        retrofit2.Call<ResponseBody> call = service.chatImageRequest(MultipartBody.Part.createFormData("Attechments", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file)), RequestBody.create(MediaType.parse("text/plain"), String.valueOf(adminUserId)), RequestBody.create(MediaType.parse("text/plain"), String.valueOf(chatRequestId)));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");

                BufferedReader br = null;
                GroupMassageSend groupMassageSend = null;
                String output = "";
                if (response.code() == 400) {
                    br = new BufferedReader(new InputStreamReader(response.errorBody().byteStream()));
                } else if (response.code() == 200) {
                    br = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                    try {
                        output = br.readLine();
                        System.out.println(output);
                        groupMassageSend = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(output, new TypeToken<GroupMassageSend>() {
                        }.getType());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (response.isSuccessful()) {
                    generalChatActivity.successFullyUploaded(response.isSuccessful(), "Image successfully sended.", groupMassageSend, output);
                } else {
                    generalChatActivity.successFullyUploaded(response.isSuccessful(), "Image failed to send, please try again.", new GroupMassageSend(), "");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                generalChatActivity.failToUpload("Image failed to send, please try again.");
            }
        });


    }

    public interface onForbiddenListener {
        void forbidden();
    }

    public static class DateDeserializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            String date = element.getAsString();

            SimpleDateFormat formatter = new SimpleDateFormat(JsonDateFormatString);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            try {
                return formatter.parse(date);
            } catch (ParseException ex) {
                Log.e(Utilities.TAG, "JsonDeserializer<Date>() failed", ex);
                return null;
            }
        }
    }

    public class DateSerializer implements JsonSerializer<Date> {

        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            SimpleDateFormat formatter = new SimpleDateFormat(JsonDateFormatString);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return new JsonPrimitive(formatter.format(src));
        }
    }

}

