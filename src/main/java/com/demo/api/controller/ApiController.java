package com.demo.api.controller;

import com.demo.api.entity.EAccount;
import com.demo.api.entity.EConfig;
import com.demo.api.entity.EEntity;
import com.demo.api.handle.JobHandle;
import com.demo.api.repository.ConfProperties;
import com.demo.api.repository.EAccountRepository;
import com.demo.api.repository.EEntityRepository;
import com.demo.api.repository.EConfigRepository;
import com.demo.api.utils.ApiUtils;
import com.demo.api.utils.JsonUtil;
import jp.nextengine.api.sdk.NeApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 *
 * Created by mhh on 2017/3/17.
 */
@RestController
@RequestMapping("api")
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private ConfProperties confProperties;

    @Autowired
    private JobHandle jobHandle;

    @Autowired
    private EAccountRepository eAccountRepository;

    @Autowired
    private EEntityRepository eEntityRepository;

    @Autowired
    private EConfigRepository eConfigRepository;

    @GetMapping(value = "/callback")
    @ResponseBody
    public void reget(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> params = ApiUtils.getRequestParams(request);
        for(String key : params.keySet()) {
            System.out.println(key+"="+params.get(key));
        }

        try {
            EConfig config = eConfigRepository.findOne(1L);
            String client_id = null;
            String client_secret = null;
            if(config != null) {
                client_id = config.getKid();
                client_secret = config.getSecret();
            }
            if(StringUtils.isEmpty(client_id) || StringUtils.isEmpty(client_secret)) {
                client_id = confProperties.getClientId();
                client_secret = confProperties.getClientSecret();
            }
            NeApiClient client = new NeApiClient(request, response, client_id,
                    client_secret,"http://localhost:"+serverPort+"/api/re2");
            HashMap<String, String> apiParams = new HashMap<>();
            apiParams.put("uid",params.get("uid"));
            apiParams.put("state",params.get("state"));
            HashMap<String, Object> params2 = client.apiExecuteNoRequiredLogin("/api_neauth",apiParams);

            if(params2 != null && params2.containsKey("refresh_token")
                    && params2.containsKey("access_token")) {

                EAccount account = null;

                EAccount accounts = eAccountRepository.findOne();
                if (accounts == null) {
                    account = new EAccount();
                    account.setUid(params.get("uid"));
                    account.setState(params.get("state"));
                } else {
                    account = accounts;
                }
                account.setRefreshToken(String.valueOf(params2.get("refresh_token")));
                account.setInvalidRefresh(new Date((System.currentTimeMillis() + (long) (2.8 * EAccount.ONE_DAY))));
                account.setInvalidAccess(new Date((System.currentTimeMillis() + (long) (EAccount.ONE_DAY))));
                account.setAccessToken(String.valueOf(params2.get("access_token")));

                eAccountRepository.save(account);

                if(jobHandle != null) {
                    jobHandle.refreshClient(account.getAccessToken(), account.getRefreshToken());
                    jobHandle.startHandle();
                }

                response.sendRedirect("/");
            }

            for(String key : params2.keySet()) {
                System.out.println(key+"="+params2.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/data2")
    @ResponseBody
    public String data2(HttpServletRequest request, HttpServletResponse response) {
        Map<String , String> params = ApiUtils.getRequestParams(request);
        Map<String, Object> map = new HashMap<>();
        Page<EEntity> page = null;
        try {
            int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
            int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
            PageRequest pageRequest = new PageRequest(iDisplayStart/iDisplayLength,
                    iDisplayLength, new Sort(Sort.Direction.DESC, "updateDate"));
//            pageRequest
            page = eEntityRepository.findAll(pageRequest);
            long mSize = Math.min(100000, page.getTotalElements());
            map.put("iTotalRecords", mSize);
            map.put("sTotal", mSize);
            map.put("iTotalDisplayRecords", mSize);
//            map.put("iTotalDisplayRecords", page.getNumberOfElements());
            map.put("aaData", page.getContent());
        } catch (Exception e) {
            List<EEntity> logs = eEntityRepository.findAll();
            map.put("iTotalRecords", logs != null ? logs.size() : 0);
            map.put("iTotalDisplayRecords", logs != null ? logs.size() : 0);
            map.put("aaData", logs);
            if(logs != null) {
                for (EEntity l: logs) {
                    jobHandle.setAddress(null, l, l.getReceive_order_consignee_address1(), true);
                    jobHandle.setAddress(null, l, l.getReceive_order_purchaser_address1(), false);
                }
            }
        }
        return JsonUtil.objectToJson(map);
    }

    @GetMapping(value = "/re2")
    @ResponseBody
    public void reget2(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> params = ApiUtils.getRequestParams(request);
        for(String key : params.keySet()) {
            System.out.println(key+"="+params.get(key));
        }
    }


    @GetMapping(value = "/auth")
    @ResponseBody
    public Object auth(HttpServletRequest request, HttpServletResponse response) {

        try {
            EConfig config = eConfigRepository.findOne(1L);
            String client_id = null;
            String client_secret = null;
            if(config != null) {
                client_id = config.getKid();
                client_secret = config.getSecret();
            }
            if(StringUtils.isEmpty(client_id) || StringUtils.isEmpty(client_secret)) {
                client_id = confProperties.getClientId();
                client_secret = confProperties.getClientSecret();
            }
            NeApiClient client = new NeApiClient(request, response, client_id,
//                    client_secret,"http://localhost:8090/api/callback");
                    client_secret,"http://localhost:"+serverPort+"/api/callback");
            client.neLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @GetMapping(value = "/config")
    @ResponseBody
    public Object config(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        EConfig config = eConfigRepository.findOne(1L);
        if(config != null) {
            map.put("result", 1);
            map.put("data", config);
        } else {
            map.put("result", 0);
        }

        return JsonUtil.objectToJson(map);
    }

    @PostMapping(value = "/save")
    @ResponseBody
    public Object save(HttpServletRequest request, EConfig config) {
        Map<String, Object> map = new HashMap<>();
        config.setId(1L);
        EConfig e = eConfigRepository.findOne(1L);
        if(e != null) {
            config.setReTime(e.getReTime());
            config.setLoadTime(e.getLoadTime());
        }
        boolean needAuth = false;
        if(!Objects.equals(config.getKid(), e.getKid())
                || !Objects.equals(config.getSecret(), e.getSecret())) {
            needAuth = true;
        }
        config = eConfigRepository.save(config);
        if(jobHandle != null) {
            jobHandle.refreshConfig();
        }
        if(needAuth) {
            map.put("result", 2);
        } else {
            map.put("result", 1);
        }
        return JsonUtil.objectToJson(map);
    }
}
