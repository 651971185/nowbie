package com.mengge.gatewayfunction.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class AuthUtil {

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

    private static String getMD5(String str) {
        String md5Hex = DigestUtils.md5Hex(str);
        return md5Hex;
    }

    public static Header[] AppAuthForJSON(String url,String host,String apiAppKey,String apiAppSecret,Object reqBody) throws Exception {
        String httpMethod = "POST";
        String acceptHeader = "application/json";
        String contentType = "application/json";

        String contentMD5 = base64Encode(getMD5(reqBody.toString()).getBytes());

        // Parse URL and assemble string to sign
        URL parsedUrl = new URL(url);
        String pathAndParams = parsedUrl.getPath();
        if (parsedUrl.getQuery() != null) {
            pathAndParams = pathAndParams + "?" + sortQueryParams(parsedUrl.getQuery());
            System.out.println(pathAndParams);
        }

        String xDate = getGMTTime();
        String stringToSign = String.format("x-date: %s\n%s\n%s\n%s\n%s\n%s", xDate, httpMethod, acceptHeader, contentType, contentMD5, pathAndParams);
        // Encode string with HMAC and base64
        byte[] hmacStr = HmacSHA1Encrypt(stringToSign, apiAppSecret);
        /**
         * 把二进制的签名和Secret进行base64转码
         */
        String signature = base64Encode(hmacStr);
        String authHeader = String.format("hmac id=\"%s\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\"%s\"", apiAppKey, signature);

        Header[] headers = {new BasicHeader("Accept", acceptHeader),
                            new BasicHeader("Host", host),
                            new BasicHeader("x-date", xDate),
                            new BasicHeader("Content-Type", contentType),
                            new BasicHeader("Content-MD5", contentMD5),
                            new BasicHeader("Authorization", authHeader)};
        return headers;
    }

    public static Header[] AppAuthForForm(String url,String host,String apiAppKey,String apiAppSecret,Map<String, Object> reqBodyMap) throws Exception {
        String httpMethod = "POST";
        String acceptHeader = "application/json";
        String contentType = "application/x-www-form-urlencoded";

        StringBuffer reqBodyBuffer = new StringBuffer();
        for (Map.Entry<String, Object> e : reqBodyMap.entrySet()) {
            reqBodyBuffer.append(e.getKey());
            reqBodyBuffer.append("=");
            reqBodyBuffer.append(e.getValue());
            reqBodyBuffer.append("&");
        }
        String reqBody = reqBodyBuffer.toString();
        reqBody = reqBody.substring(0, reqBody.length() - 1);

        String contentMD5 = "";

        // Parse URL and assemble string to sign
        URL parsedUrl = new URL(url);
        String pathAndParams = parsedUrl.getPath();
        if (reqBody != "" && reqBody.length() > 0){
            pathAndParams = pathAndParams + "?" + reqBody;
        }

        String xDate = getGMTTime();
        String stringToSign = String.format("x-date: %s\n%s\n%s\n%s\n%s\n%s", xDate, httpMethod, acceptHeader, contentType, contentMD5, pathAndParams);

        // Encode string with HMAC and base64
        byte[] hmacStr = HmacSHA1Encrypt(stringToSign, apiAppSecret);
        String signature = base64Encode(hmacStr);
        String authHeader = String.format("hmac id=\"%s\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\"%s\"", apiAppKey, signature);

        Header[] headers = {new BasicHeader("Accept", acceptHeader),
                new BasicHeader("Host", host),
                new BasicHeader("x-date", xDate),
                new BasicHeader("Content-Type", contentType),
                new BasicHeader("Content-MD5", contentMD5),
                new BasicHeader("Authorization", authHeader)};
        return headers;
    }

    public static Header[] AppAuthForGet(String url,String host,String apiAppKey,String apiAppSecret) throws Exception {

        String httpMethod = "GET";
        String acceptHeader = "application/json";
        String contentType = "";
        String contentMD5 = "";

        URL parsedUrl = new URL(url);
        String pathAndParams = parsedUrl.getPath();
        if (parsedUrl.getQuery() != null) {
            pathAndParams = pathAndParams + "?" + sortQueryParams(parsedUrl.getQuery());
        }

        String xDate = getGMTTime();
        String stringToSign = String.format("x-date: %s\n%s\n%s\n%s\n%s\n%s", xDate, httpMethod, acceptHeader, contentType, contentMD5, pathAndParams);

        // Encode string with HMAC and base64
        byte[] hmacStr = HmacSHA1Encrypt(stringToSign, apiAppSecret);
        String signature = base64Encode(hmacStr);
        String authHeader = String.format("hmac id=\"%s\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\"%s\"", apiAppKey, signature);

        Header[] headers = {new BasicHeader("Accept", acceptHeader),
                new BasicHeader("Host", host),
                new BasicHeader("x-date", xDate),
                new BasicHeader("Content-Type", contentType),
                new BasicHeader("Content-MD5", contentMD5),
                new BasicHeader("Authorization", authHeader)};
        return headers;
    }
}
