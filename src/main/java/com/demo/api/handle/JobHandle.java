package com.demo.api.handle;

import com.demo.api.entity.City;
import com.demo.api.entity.EAccount;
import com.demo.api.entity.EConfig;
import com.demo.api.entity.EEntity;
import com.demo.api.repository.*;
import com.demo.api.utils.ApiUtils;
import com.demo.api.utils.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jp.nextengine.api.sdk.NeApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mhh on 2017/3/25.
 */
@SuppressWarnings("ALL")
public class JobHandle extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(JobHandle.class);

    // 1分钟时间
    private static final int MIN_TIME = 60 * 1000;

    @Autowired
    private EAccountRepository eAccountRepository;

    @Autowired
    private EEntityRepository eEntityRepository;

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private EConfigRepository eConfigRepository;

    @Autowired
    private ConfProperties confProperties;

    private NeApiClient client;

    private Boolean running = false;

    EAccount account;

    EConfig config;

    private long lrtime;

    public void refreshClient(String access_token, String refresh_token) {

        try {
            account = eAccountRepository.findOne();
            client = new NeApiClient(access_token, refresh_token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshConfig() {

        config = eConfigRepository.findOne(1L);
    }

    public void run() {
        running = true;
        while (running) {
            long ltime = -1;
            long rtime = -1;
            long stime = 24 * 60;
            if(config == null) {
                refreshConfig();
            }
            if (config != null) {
                ltime = config.getLoadTime() == null ? 0L : config.getLoadTime();
                rtime = config.getReTime() == null ? 0L : config.getReTime();
                stime = config.getTime();
                if (stime < 2) {
                    // 默认 1天
                    stime = 24 * 60;
                }
            }

            try {

                int reNum = config.getReNum();
                int interval = config.getInterva();
                long cTime = System.currentTimeMillis();

                // 数据库是否连接成功
                if(ltime != -1) {
                    if(account == null) {
                        account = eAccountRepository.findOne();
                    }
                    if (account != null) {
                        if (client == null) {
                            client = new NeApiClient(account.getAccessToken(), account.getRefreshToken());
                        }
                        if (!account.isAccessValid() || !account.isRefreshValid()
                                || System.currentTimeMillis() - lrtime > 6 * 60 * MIN_TIME) {

                            logger.info(" refresh token ");
                            lrtime = System.currentTimeMillis();
                            account.setAccessToken(client.getAccessToken());
                            account.setRefreshToken(client.getRefreshToken());
                            account.setInvalidRefresh(new Date((System.currentTimeMillis() + (long) (2.8 * EAccount.ONE_DAY))));
                            account.setInvalidAccess(new Date((System.currentTimeMillis() + (long) (EAccount.ONE_DAY))));
                            eAccountRepository.save(account);

                            String access_token = account.getAccessToken();
                            String refresh_token = account.getRefreshToken();
                            client = new NeApiClient(access_token, refresh_token);
                        }

                        List<EEntity> upData = new ArrayList<>();
                        if(cTime - ltime > stime * MIN_TIME) {
                            logger.info(" Start updating data ");
                            config.setLoadTime(System.currentTimeMillis());

                            eConfigRepository.save(config);
                            HashMap<String, Object> map = getData(account.getAccessToken(), account.getRefreshToken());

                            logger.info(" End the update data ");
//                            System.out.println("data="+JsonUtil.objectToJson(map));

//                            logger.info("result:"+map);

                            String ls = JsonUtil.objectToJson(map.get("data"));

                            logger.info("data:"+ls);

                            Type type = new TypeToken<List<EEntity>>() {
                            }.getType();
                            List<EEntity> data = new Gson().fromJson(ls, type);

                            logger.info(" End the update data, count="+(data == null ? 0 : data.size()));

                            if (data != null) {
                                for (EEntity e : data) {

                                    EEntity d = eEntityRepository.findOne(e.getReceive_order_id());
                                    if (d != null) {
                                        continue;
                                    }

                                    // 添加到列表
                                    upData.add(e);
                                    e.setUpdateDate(new Date());
                                    e.setStatus("init");
                                    e.setRenum(0);

                                    String d1 = e.getReceive_order_date();
                                    try {
                                        e.setReceive_order_date(new SimpleDateFormat("yyyy-MM-dd").format(new
                                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(d1)));
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                        e.setReceive_order_date(d1.split(" ")[0]);
                                    }

                                    List<Map<String, String>> add1 = null;
                                    List<Map<String, String>> add2 = null;


                                    try {
                                        String code1 = e.getReceive_order_consignee_zip_code();
                                        String code2 = e.getReceive_order_purchaser_zip_code();

                                        String str = ApiUtils.sendGet("http://zipcloud.ibsnet.co.jp/api/search", "zipcode="+code1);
                                        Map<String, Object> result = JsonUtil.jsonToMap(str);

                                        if(result != null && result.containsKey("results")) {
                                            add1 = (List<Map<String, String>>) result.get("results");
                                        }

                                        if(code2 != null) {
                                            if(code2.equals(code1)) {
                                                add2 = add1;
                                            }
                                        }
                                    } catch (Exception e1) {
                                        logger.error("exception msg:"+e1.getMessage());
                                    }

                                    setAddress(add1, e, e.getReceive_order_consignee_address1(), true);

                                    setAddress(add2, e, e.getReceive_order_purchaser_address1(), false);
                                }
                            }

                            logger.info(" End the update data,  Need to upload the number="+(upData == null ? 0 : upData.size()));
                        }

                        if (CollectionUtils.isEmpty(upData)) {
                            if(rtime != -1 && reNum > 0 && interval > 0 && cTime - rtime >= interval*MIN_TIME) {

                                logger.info(" Start searching for failed data " );

                                config.setReTime(cTime);
                                eConfigRepository.save(config);
                                upData = eEntityRepository.findErrors(reNum+1);

                                logger.info(" End searching for failed data, count="+(upData == null ? 0 : upData.size()));
                            }
                        } else {

                            logger.info(" End the update data save to db "+JsonUtil.objectToJson(upData));
                            eEntityRepository.save(upData);
                        }

                        if (!CollectionUtils.isEmpty(upData)) {

                            EntityData entityData = new EntityData();
                            entityData.setData(upData);
                            String content = entityData.toString();

                            logger.info(" Start to save the CSV file,  count"+(upData == null ? 0 : upData.size())+"##### content="+content);

                            String filePath = confProperties.getFileCsvPath()+new SimpleDateFormat("HHmmss").format(new Date());

                            boolean success = ApiUtils.exportCsv(new File(filePath+".csv"), content);

                            logger.info(" End to save the CSV file, success="+success);

                            if (success) {
                                boolean uploadCsv =
//                                    false;
                                        false;
                                String error = null;
                                try {
                                    logger.info(" Start uploading the CSV file  ");
                                    uploadCsv = ApiUtils.postCsv(config.getUname(), config.getPassword(), filePath+".csv");
                                    logger.info(" End uploads the CSV file, success="+uploadCsv);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    logger.error(" Upload a CSV file exception, message="+e.getMessage());
                                    if ("401".equals(e.getMessage())) {
                                        error = "401";
                                    }
                                }

                                try {
                                    new File(filePath+".csv").renameTo(new File(filePath+(uploadCsv ? "-success.csv" : "-failed.csv")));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (uploadCsv) {
                                    for (EEntity e : upData) {
                                        e.setRenum(1);
                                        e.setStatus("success");
                                    }
                                } else {
                                    for (EEntity e : upData) {
                                        e.setRenum(e.getRenum() + 1);
                                        e.setStatus("failed" + (StringUtils.isEmpty(error) ? "" : "(" + error + ")") + "-" + e.getRenum());
                                    }
                                }

                                eEntityRepository.save(upData);
                            } else {
                                for (EEntity e : upData) {
                                    e.setStatus("error");
                                    e.setRenum(0);
                                }

                                eEntityRepository.save(upData);
                            }

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Send data to send exception, message:"+e.getMessage());
            } finally {
                // 2 分钟
                try {
                    sleep(2 * MIN_TIME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String[] matchAddress(List<Map<String, String>> sList, String address) {
//        if(sList == null || sList.isEmpty()) {
//            return null;
//        }
        String[] add = new String[5];
        String add1;
        int index = address.indexOf("県");
        if (index == -1 || index > 3) {
            index = address.indexOf("府");
        }
        if (index == -1 || index > 3) {
            index = address.indexOf("道");
        }
        if (index == -1 || index > 3) {
            index = address.indexOf("都");
        }
        if (index > -1 && index < 4) {
            add1 = address.substring(0, index + 1);
        } else {
            add1 = address.substring(0, 2);
        }
        if (!CollectionUtils.isEmpty(sList)) {
            for (Map<String, String> s : sList) {
                String address1 = s.get("address1");
                if (address1 != null && address1.startsWith(add1)) {
                    add[0] = address1;
                    String address2 = s.get("address2");
                    if (!StringUtils.isEmpty(address2) && address.contains(address2.substring(0, address2.length() - 1))) {
                        add[1] = address2;
                    }
                    // 为空 | 不匹配
                    else {
                        String str;
                        if (address.startsWith(address1)) {
                            str = address.substring(address1.length() - 1);
                        } else {
                            str = address.substring(add1.length() - 1);
                        }
                        String str2 = str.substring(0, Math.max(2, str.length() - 1));
                        List<City> citys = cityRepository.findByName(5, str2);

                        for (int i = 0; i < Math.max(3, str.length()); i++) {
                            if (CollectionUtils.isEmpty(citys)) {
                                try {
                                    str2 = str.substring(i + 1, i + 3);
                                    citys = cityRepository.findByName(5, str2);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                break;
                            }
                        }

                        if (citys != null && citys.size() > 0) {
                            if (citys.size() == 1) {
                                add[1] = citys.get(0).getName();

                            } else {
                                int ms = 0;
                                for (City c : citys) {
                                    int ms2 = matchSize(c.getName(), str);

                                    if (ms2 > ms) {
                                        add[1] = c.getName();
                                        ms = ms2;
                                    }
                                }

                                if (add[1] == null) {
                                    add[1] = citys.get(0).getName();
                                }
                            }
                        }
                        // 未找到匹配的市
//                        if (add[1] == null) {
//                            add[1] = str;
//                        }
                    }

                    String address3 = s.get("address3");
                    if (!StringUtils.isEmpty(address3) && address.contains(address3.substring(0, address3.length() - 1))) {
                        add[2] = address3;
                        if (address.contains(address3)) {
                            set34(add, address, address.indexOf(address3) + address3.length());
                        } else {
                            set34(add, address, address.indexOf(address3.substring(0,
                                    address3.length() - 1)) + address3.length() - 1);
                        }
                    }
                    // 为空|不匹配
                    else {
                        String str3;
                        if (address.contains(address2)) {
                            str3 = address.substring(address.indexOf(address2) + address2.length());
                        } else {
                            str3 = address.substring(address.indexOf(address2.substring(0,
                                    address2.length() - 1)) + address2.length() - 1);
                        }
                        if (StringUtils.isEmpty(str3)) {
                            str3 = "";
                        }
                        int num = matchNum(str3);
                        if (num != -1) {
                            add[2] = str3.substring(0, num);
                            add[3] = str3.substring(num, str3.length());
                        } else {
                            add[2] = str3;
                        }
                    }
//                    return add;
                }
            }
        }
        if (add[0] == null || add[1] == null) {
            List<City> citys = cityRepository.findByName(1, add1);
            if (CollectionUtils.isEmpty(citys) && add1.length() > 2) {
                citys = cityRepository.findByName(1, add1.substring(0, 2));
            }
            if (citys != null && citys.size() > 0) {
                if (citys.size() == 1) {
                    add[0] = citys.get(0).getName();
                } else {
                    int ms = 0;
                    for (City c : citys) {
                        int ms2 = matchSize(c.getName(), address);
                        if (ms2 > ms) {
                            add[0] = c.getName();
                            ms = ms2;
                        }
                    }
                    if (add[0] == null) {
                        add[0] = citys.get(0).getName();
                    }
                }

                String str = address.substring(2);

                String str2 = str.substring(0, 2);
                citys = cityRepository.findByName(5, str2);

                for (int i = 0; i < Math.max(3, str.length()); i++) {
                    if (CollectionUtils.isEmpty(citys)) {
                        try {
                            str2 = str.substring(i + 1, i + 3);
                            citys = cityRepository.findByName(5, str2);
                        } catch (Exception e) {
                            logger.error("error:"+e.getMessage());
                        }
                    } else {
                        break;
                    }
                }
                if (citys != null && citys.size() > 0) {
                    if (citys.size() == 1) {
                        add[1] = citys.get(0).getName();
                    } else {
                        int ms = 0;
                        for (City c : citys) {
                            int ms2 = matchSize(c.getName(), str2);
                            if (ms2 > ms) {
                                add[1] = c.getName();
                                ms = ms2;
                            }
                        }
                        if (add[1] == null) {
                            add[1] = citys.get(0).getName();
                        }
                    }
//                    if(add[1] != null && str.indexOf(str2) != -1) {
//                        String str3 = str.replace(add[1],"")
//                                .replace(str2,"");
//                        add[2] = str3;
//                    }
                    String str3 = null;
                    if (str.contains(add[1])) {
                        str3 = str.substring(str.indexOf(add[1]) + add[1].length());
                    } else {
                        str3 = str.substring(str.indexOf(str2) + str2.length());
                    }
                    if (StringUtils.isEmpty(str3)) {
                        str3 = "";
                    }
                    int num = matchNum(str3);
                    if (num != -1) {
                        add[2] = str3.substring(0, num);
                        add[3] = str3.substring(num, str3.length());
                    } else {
                        add[2] = str3;
                    }
                }
            }
        }
        // 检查长度
        if (add[0] != null && add[0].length() > 4) {
            add[0] = add[0].substring(0, 4);
        }
        if (add[1] != null && add[2] != null
                && add[1].length() + add[2].length() > 12) {
            add[3] = add[2].substring(12 - add[1].length()) + getTxt(add[3]);
            add[2] = add[2].substring(0, 12 - add[1].length());
        }
        if (add[3] != null && add[3].length() > 16) {
            add[4] = add[3].substring(16);
            add[3] = add[3].substring(0, 16);
        }
        return add;
    }

    private String getTxt(String txt) {
        return StringUtils.isEmpty(txt) ? "" : txt;
    }

    /**
     * @param s1 标准市名
     * @param s2 需要匹配字符串
     * @return
     */
    private int matchSize(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return 0;
        }
        int size = 0;
        int y = 0;
        for (int i = 0; i < s1.length(); i++) {
            i = Math.max(0, i);
            if (s2.length() > y) {
                if (s1.charAt(i) == s2.charAt(y)) {
                    size += 1;
                } else {
                    if (size == 0) {
                        i -= 1;
                    }
                }
            } else {
                break;
            }
            y += 1;
        }
        return size;
    }

    private void set34(String[] add, String address, int index) {
        if (address.length() > index) {
            add[3] = address.substring(index);
        }
    }

    private int matchNum(String address) {
        String num = "0０1１2２3３45６6789";
        if (!StringUtils.isEmpty(address)) {
            for (int i = 0; i < address.length(); i++) {
                if (num.contains(String.valueOf(address.charAt(i)))) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void setAddress(List<Map<String, String>> sList, EEntity e, String address, boolean first) {
        if (StringUtils.isEmpty(address) || e == null) {
            return;
        }
        String[] add = matchAddress(sList, address);
        if (first) {
            e.setC_address1(add[0]);
            e.setC_address2(add[1]);
            e.setC_address3(add[2]);
            e.setC_address4(add[3]);
            e.setC_address5(add[4]);
        } else {
            e.setP_address1(add[0]);
            e.setP_address2(add[1]);
            e.setP_address3(add[2]);
            e.setP_address4(add[3]);
            e.setP_address5(add[4]);
        }
    }

    private HashMap<String, Object> getData(String access_token, String refresh_token) throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("fields", "goods_id, goods_name, stock_quantity, supplier_name,receive_order_id," +
                "receive_order_row_shop_cut_form_id,receive_order_date,receive_order_consignee_name," +
                "receive_order_consignee_zip_code,receive_order_consignee_address1,receive_order_consignee_address2," +
                "receive_order_consignee_name,receive_order_consignee_tel,receive_order_purchaser_mail_address," +
                "receive_order_purchaser_name,receive_order_purchaser_zip_code,receive_order_purchaser_address1," +
                "receive_order_purchaser_address2,receive_order_purchaser_name,receive_order_purchaser_tel," +
                "receive_order_purchaser_tel,receive_order_total_amount,receive_order_total_amount," +
                "receive_order_hope_delivery_time_slot_id,receive_order_hope_delivery_time_slot_name," +
                "receive_order_point_amount,receive_order_other_amount,receive_order_id,receive_order_row_no," +
                "receive_order_row_goods_id,receive_order_row_goods_name,receive_order_row_quantity," +
                "receive_order_row_unit_price,receive_order_row_unit_price," +
                "receive_order_row_receive_order_id,receive_order_order_status_id,receive_order_order_status_name," +
                "goods_supplier_id");
        params.put("offset", "0");
        params.put("limit", "1000");
//        params.put("receive_order_order_status_id-eq", "1");
        if(config != null && !StringUtils.isEmpty(config.getGoodSupplierId())) {
            params.put("goods_supplier_id-eq", config.getGoodSupplierId());
        }
        long time = System.currentTimeMillis();
        return client.apiExecute("/api_v1_receiveorder_row/search", params);
    }

    /**
     * 同步数据入口 -- 授权登录成功后调用
     */
    public void startHandle() {
        refreshConfig();
        // 启动线程
        if (!running) {
            this.start();
        }
    }

}
