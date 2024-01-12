package net.invictusmanagement.invictuslifestyle.helium_communication;

import com.openpath.mobileaccesscore.OpenpathLogging;
import com.openpath.mobileaccesscore.OpenpathMobileAccessCore;

import net.invictusmanagement.invictuslifestyle.BuildConfig;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LoginTask implements Runnable {

    private String endpoint, email, password, organizationId, userId, mfaCode;

    public LoginTask(String endpoint, String email, String password,
                     String organizationId, String userID, String mfaCode) {
        this.endpoint = endpoint;
        this.email = email;
        this.password = password;
        this.organizationId = organizationId;
        this.mfaCode = mfaCode;
        this.userId = userID;
    }

    @Override
    public void run() {
        try {
            String resource = "/auth/login";
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);
            if (mfaCode.length() > 0) {
                JSONObject mfa = new JSONObject();
                mfa.put("totpCode", mfaCode);
                body.put("mfa", mfa);
            }
            HashMap<String, String> options = new HashMap<>();
            options.put("method", "POST");
            options.put("body", body.toString());
            HeliumApiResponse result = heliumApi(endpoint, resource, options);
            JSONObject responseJson = result.getBody();
            if (responseJson.has("error")) {
                System.out.println(responseJson.get("error"));
            } else {
                String token = responseJson.getJSONObject("data").getString("token");
                String orgId = responseJson.getJSONObject("data").getJSONArray("tokenScopeList").getJSONObject(0).getJSONObject("org").getString("id");
//                String userId = responseJson.getJSONObject("data").getJSONArray("tokenScopeList").getJSONObject(0).getJSONObject("user").getString("id");
//                resource = "/orgs/" + orgId + "/users/" + userId + "/credentials";
//                userId = userId;
                orgId = organizationId;
                resource = "/orgs/" + orgId + "/users/" + userId + "/credentials";
                options = new HashMap<>();
                options.put("method", "GET");
                options.put("apiToken", token);
                result = heliumApi(endpoint, resource, options);
                responseJson = result.getBody();
                if (responseJson.has("error")) {
                    System.out.println(responseJson.get("error"));
                } else {
                    JSONArray credentials = responseJson.getJSONArray("data");
                    String credentialId = "";
                    for (int i = 0; i < credentials.length(); i++) {
                        JSONObject credential = credentials.getJSONObject(i);
                        if (credential.getJSONObject("credentialType").getString("modelName").equals("mobile")) {
                            credentialId = credential.getString("id");
                            break;
                        }
                    }
                    if (credentialId.length() > 0) {
                        resource = "/orgs/" + orgId + "/users/" + userId + "/credentials/" + credentialId + "/generateSetupMobileToken";
                        options = new HashMap<>();
                        options.put("method", "POST");
                        options.put("apiToken", token);
                        result = heliumApi(endpoint, resource, options);
                        responseJson = result.getBody();
                        if (responseJson.has("error")) {
                            System.out.println(responseJson.get("error"));
                        } else {
                            ProgressDialog.dismissProgress();
                            String setupMobileToken = responseJson.getJSONObject("data").getString("setupMobileToken");
                            OpenpathMobileAccessCore.getInstance().provision(BuildConfig.APP_NAME, setupMobileToken);
                        }
                    }
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private HeliumApiResponse heliumApi(String endpoint, String resource, HashMap<String, String> options) throws IOException {
        String address = endpoint + resource;
        OpenpathLogging.v("calling helium api " + address);
        URL url = new URL(address);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setSSLSocketFactory(getSslSocketFactory());
        urlConnection.setHostnameVerifier((s, sslSession) -> true);
        urlConnection.setRequestMethod(options.get("method"));
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        if (options.containsKey("apiToken")) {
            urlConnection.setRequestProperty("Authorization", options.get("apiToken"));
        }
        urlConnection.setConnectTimeout(15000);
        urlConnection.setReadTimeout(15000);

        if (options.containsKey("body")) {
            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            out.write(options.get("body").getBytes());
            out.flush();
            out.close();
        } else {
            urlConnection.connect();
        }

        InputStream inputStream;
        int responseCode = urlConnection.getResponseCode();
        if (responseCode < HttpsURLConnection.HTTP_BAD_REQUEST) {
            inputStream = urlConnection.getInputStream();
        } else {
            inputStream = urlConnection.getErrorStream();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return new HeliumApiResponse(responseCode, response.toString());
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
}
