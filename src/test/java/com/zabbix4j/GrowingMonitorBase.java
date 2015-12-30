package com.zabbix4j;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by ben on 12/28/15.
 */
public class GrowingMonitorBase {

    public static final String ZBX_URL = "http://cnzbx/zabbix/api_jsonrpc.php";

    protected static Logger logger = LoggerFactory.getLogger(ZabbixApiTestBase.class);
    protected String user = "admin";
    protected String password = "";

    protected ZabbixApi zabbixApi;

    public GrowingMonitorBase() {
        login(user, password);
    }

    protected void login(String user, String password) {
        try {
            zabbixApi = new ZabbixApi(ZBX_URL);
            zabbixApi.login(user, password);
        } catch (ZabbixApiException e) {
            logger.error(e.getMessage());
        }
    }

    protected Gson getGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }
}
