package com.mengge.gatewayfunction.gateTest;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class AppAuthJavaFormDemo {
    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";

    private static String getGMTTime(){
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String GMTTime = sdf.format(cd.getTime());
        return GMTTime;
    }

    private static String sortQueryParams(String queryParam){
        // parameters should be in alphabetical order
        if (queryParam == null || queryParam == ""){
            return "";
        }

        String[] queryParams = queryParam.split("&");
        Map<String, String> queryPairs = new TreeMap<>();
        for(String query: queryParams){
            String[] kv = query.split("=");
            queryPairs.put(kv[0], kv[1]);
        }

        StringBuilder sortedParamsBuilder = new StringBuilder();
        Iterator iter = queryPairs.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry) iter.next();
            sortedParamsBuilder.append(entry.getKey());
            sortedParamsBuilder.append("=");
            sortedParamsBuilder.append(entry.getValue());
            sortedParamsBuilder.append("&");
        }
        String sortedParams = sortedParamsBuilder.toString();
        sortedParams = sortedParams.substring(0, sortedParams.length() - 1);

        return sortedParams;
    }

    private static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        Mac mac = Mac.getInstance(MAC_NAME);
        mac.init(secretKey);

        byte[] text = encryptText.getBytes(ENCODING);
        return mac.doFinal(text);
    }

    private static String base64Encode(byte[] key) {
        final Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(key);
    }

    public static void main(String[] args) throws Exception {
        String url = "https://service-eul1kzgi-1307863402.gz.apigw.tencentcs.com/test1";
        String host = "service-eul1kzgi-1307863402.gz.apigw.tencentcs.com";
        String apiAppKey = "APID20S7gcmi80U13xwmpdd7524wPCl9ehue2Nr";
        String apiAppSecret = "j1mtMvwm9i9ckoWjmbyvCf2Kar2Qk0kT2FqtPGK5";
        String httpMethod = "POST";
        String acceptHeader = "application/json";

        // Parse form data and assemble request body
        Map<String, Object> reqBodyMap = new TreeMap<>();
        reqBodyMap.put("id", 1);
        reqBodyMap.put("isDeleted", 0);

        StringBuffer reqBodyBuffer = new StringBuffer();
        for (Map.Entry<String, Object> e : reqBodyMap.entrySet()) {
            reqBodyBuffer.append(e.getKey());
            reqBodyBuffer.append("=");
            reqBodyBuffer.append(e.getValue());
            reqBodyBuffer.append("&");
        }
        String reqBody = reqBodyBuffer.toString();
        reqBody = reqBody.substring(0, reqBody.length() - 1);

        String contentType = "application/x-www-form-urlencoded";
        String contentMD5 = "";

        // Parse URL and assemble string to sign
        URL parsedUrl = new URL(url);
        String pathAndParams = parsedUrl.getPath();
//        if (parsedUrl.getQuery() != null) {
//            pathAndParams = pathAndParams + "?" + sortQueryParams(parsedUrl.getQuery());
//        }
        if (reqBody != "" && reqBody.length() > 0){
            pathAndParams = pathAndParams + "?" + reqBody;
        }

        String xDate = getGMTTime();
        String stringToSign = String.format("x-date: %s\n%s\n%s\n%s\n%s\n%s", xDate, httpMethod, acceptHeader, contentType, contentMD5, pathAndParams);

        // Encode string with HMAC and base64
        byte[] hmacStr = HmacSHA1Encrypt(stringToSign, apiAppSecret);
        String signature = base64Encode(hmacStr);
        String authHeader = String.format("hmac id=\"%s\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\"%s\"", apiAppKey, signature);

        // Generate request
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", acceptHeader);
        httpPost.setHeader("Host", host);
        httpPost.setHeader("x-date", xDate);
        httpPost.setHeader("Content-Type", contentType);
        httpPost.setHeader("Content-MD5", contentMD5);
        httpPost.setHeader("Authorization", authHeader);
        StringEntity stringEntity = new StringEntity(reqBody, ENCODING);
        httpPost.setEntity(stringEntity);

        CloseableHttpResponse response;

        // Send request
        response = httpClient.execute(httpPost);

        // Receive response
        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null) {
            System.out.println("Response status code: " + response.getStatusLine());
            System.out.println("Response body: " + EntityUtils.toString(responseEntity));
        }
    }

}