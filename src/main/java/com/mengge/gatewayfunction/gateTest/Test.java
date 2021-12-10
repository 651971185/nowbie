package com.mengge.gatewayfunction.gateTest;

import com.mengge.gatewayfunction.util.AuthUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Map;
import java.util.TreeMap;

public class Test {

    public static void main(String[] args) throws Exception {
        String url = "https://service-eul1kzgi-1307863402.gz.apigw.tencentcs.com/test1";
        String host = "service-eul1kzgi-1307863402.gz.apigw.tencentcs.com";
        String apiAppKey = "APID20S7gcmi80U13xwmpdd7524wPCl9ehue2Nr";
        String apiAppSecret = "j1mtMvwm9i9ckoWjmbyvCf2Kar2Qk0kT2FqtPGK5";

        Map<String, Object> reqBodyMap = new TreeMap<>();
        reqBodyMap.put("id", 5);
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

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeaders(AuthUtil.AppAuthForForm(url,host,apiAppKey,apiAppSecret,reqBodyMap));
        StringEntity stringEntity = new StringEntity(reqBody, "UTF-8");
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
