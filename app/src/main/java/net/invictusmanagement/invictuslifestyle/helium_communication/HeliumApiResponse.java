package net.invictusmanagement.invictuslifestyle.helium_communication;

import org.json.JSONException;
import org.json.JSONObject;

public class HeliumApiResponse {
    private int mResponseCode;
    private String mBody;

    HeliumApiResponse(int responseCode, String body) {
        mResponseCode = responseCode;
        mBody = body;
    }

    int getResponseCode() {
        return mResponseCode;
    }

    JSONObject getBody() throws JSONException {
        return new JSONObject(mBody);
    }

    String getBodyRaw() {
        return mBody;
    }
}
