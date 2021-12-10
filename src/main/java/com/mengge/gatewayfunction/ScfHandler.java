package com.mengge.gatewayfunction;

import com.mengge.gatewayfunction.entity.QueryData;
import com.mengge.gatewayfunction.service.QueryDataService;
import com.mengge.gatewayfunction.util.AuthUtil;
import com.mengge.gatewayfunction.util.SpringUtil;
import com.qcloud.services.scf.runtime.events.APIGatewayProxyRequestEvent;
import com.qcloud.services.scf.runtime.events.APIGatewayProxyResponseEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScfHandler {


    private static volatile boolean cold_launch;

    static {
        cold_launch = true;
    }

    public Object mainHandler(APIGatewayProxyRequestEvent request){

        if (cold_launch){
            log.info("cold_launch:{}",cold_launch);
            log.info("spring:{}","start");
            GatewayFunctionApplication.main(new String[]{""});
            cold_launch = false;
        }


        QueryDataService queryDataService = SpringUtil.getBean(QueryDataService.class);

        log.info("begin");

        log.info("body:{}",request.getBody());
        log.info("path:{}",request.getPath());
        log.info("header:{}",request.getHeaders());
        log.info("queryString:{}",request.getQueryString());
        log.info("pathParaneters:{}",request.getPathParameters());


        QueryData queryData = new QueryData();
        queryData.setBodyData(request.getBody() == null ? "" : request.getBody());
        queryData.setPath(request.getPath());
        queryData.setReqHeader(request.getHeaders() == null ? "" : request.getHeaders().toString());
        queryData.setQueryParameters(request.getQueryString() == null ? "" : request.getQueryString().toString());
        queryData.setPathParameters(request.getPathParameters() == null ? "" : request.getPathParameters().toString());

        log.info("queryData:{}",queryData.toString());
        queryDataService.save(queryData);

//         class DoSave {
//            @Autowired
//            private QueryDataService queryDataService;
//
//             public void saveData(){
//                log.info("begin");
//
//                QueryData queryData = new QueryData();
//                queryData.setBodyData(request.getBody());
//                queryData.setPath(request.getPath());
//                queryData.setReqHeader(request.getHeaders().toString());
//                queryData.setQueryParameters(request.getQueryString().toString());
//                queryData.setPathParameters(request.getPathParameters().toString());
//
//                log.info("queryData:{}",queryData.toString());
//                queryDataService.save(queryData);
//            }
//
//        }
//
//
//        DoSave doSave = new DoSave();
//        doSave.saveData();
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        return "finish";
    }
}
