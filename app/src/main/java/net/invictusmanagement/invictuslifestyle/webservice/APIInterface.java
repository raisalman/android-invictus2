package net.invictusmanagement.invictuslifestyle.webservice;

import net.invictusmanagement.invictuslifestyle.models.DigitalKey;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIInterface {

    interface ChatImageService {
        @Multipart
        @POST("chat/chatfileupload")
        retrofit2.Call<ResponseBody> chatImageRequest(
                @Part MultipartBody.Part file,
                @Part("AdminId") RequestBody adminuserId,
                @Part("ChatRequestId") RequestBody chatRequestId
        );
    }

    interface NewMaintenanceRequestService {

    }

    interface FileUploadService {
        @Multipart
        @POST("LoyaltyPointsRequest")
        retrofit2.Call<ResponseBody> upload(
                @Part MultipartBody.Part file,
                @Part("orderAmount") RequestBody orderAmount,
                @Part("receiptNumber") RequestBody receiptNumber,
                @Part("promotionId") RequestBody promotionId,
                @Part("businessId") RequestBody businessId,
                @Part("isApproved") RequestBody isApproved,
                @Part("applicationUserId") RequestBody userId
        );
    }

    interface MarketPostService {
        @Multipart
        @POST("BulletinBoard")
        retrofit2.Call<ResponseBody> postMarketPlace(
                @Part List<MultipartBody.Part> file,
                @Part("Id") RequestBody Id,
                @Part("IsService") RequestBody IsService,
                @Part("MarketPlaceCategoryId") RequestBody MarketPlaceCategoryId,
                @Part("Title") RequestBody Title,
                @Part("Description") RequestBody Description,
                @Part("Price") RequestBody Price,
                @Part("ConditionType") RequestBody ConditionType,
                @Part("AvailableDate") RequestBody AvailableDate,
                @Part("ContactType") RequestBody ContactType,
                @Part("ContactTime") RequestBody ContactTime,
                @Part("IsSoldOut") RequestBody IsSoldOut,
                @Part("IsClosed") RequestBody IsClosed,
                @Part("IsApproved") RequestBody IsApproved,
                @Part("isHourPrice") RequestBody isHourPrice
        );
    }

    /*public List<DigitalKey> getDigitalKeys() throws IOException, ForbiddenException {

        String json = get("digitalkeys");
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(json, new TypeToken<List<DigitalKey>>() {
        }.getType());
    }*/

    @GET("digitalkeys")
    Call<List<DigitalKey>> getDigitalKeys();

}
