package com.zabbix4j;


import com.zabbix4j.graph.GraphGetRequest;
import com.zabbix4j.graph.GraphGetResponse;
import com.zabbix4j.graph.GraphObject;
import com.zabbix4j.host.HostGetRequest;
import com.zabbix4j.host.HostGetResponse;
import com.zabbix4j.screenitem.ScreenItemCreateRequest;
import com.zabbix4j.screenitem.ScreenItemCreateResponse;
import com.zabbix4j.screenitem.ScreenItemObject;

import java.util.ArrayList;


/**
 * Created by ben on 12/28/15.
 */
public class ApiDiskMonitor extends GrowingMonitorBase {

    private ArrayList<Integer> getHosts(Integer groupId) {
        HostGetRequest request = new HostGetRequest();
        HostGetRequest.Params params = request.getParams();

        ArrayList<Integer> groupIds = new ArrayList<Integer>();
        groupIds.add(groupId);
        params.setGroupids(groupIds);

        ArrayList<Integer> hostIds = new ArrayList<Integer>();

        try {
            HostGetResponse response = zabbixApi.host().get(request);
            for (HostGetResponse.Result result : response.getResult()) {
                hostIds.add(result.getHostid());
            }

        } catch (ZabbixApiException e) {
            logger.error(e.getMessage());
        }

        return hostIds;
    }

    private ArrayList<Integer> getHostResoureceId(Integer hostId) {

        ArrayList<Integer> graphIds = new ArrayList<Integer>();
        try {
            GraphGetRequest request = new GraphGetRequest();
            GraphGetRequest.Params params = request.getParams();
            params.addHostId(hostId);
            params.setSelectDiscoveryRule("Mounted filesystem discovery");
            GraphGetResponse response = zabbixApi.graph().get(request);
            for (GraphObject result : response.getResult()) {
                if (result.getName().contains("Disk space usage"))
                    graphIds.add(result.getGraphid());
            }
        } catch (ZabbixApiException e) {
            logger.error(e.getMessage());
        }

        return graphIds;
    }

    private void addScreenItem(Integer resourceId, Integer x, Integer y) {
        ScreenItemCreateRequest request = new ScreenItemCreateRequest();

        ScreenItemObject obj = new ScreenItemObject();
        obj.setScreenid(50);
        obj.setResourcetype(ScreenItemObject.RESOURCE_TYPE.GRAPH.value);
        obj.setResourceid(resourceId);
        obj.setRowspan(1);
        obj.setColspan(1);
        obj.setX(x);
        obj.setY(y);
        obj.setHeight(100);
        obj.setWidth(250);
        request.addScreenItem(obj);

        try {
            ScreenItemCreateResponse response = zabbixApi.screenItem().create(request);
            logger.debug(getGson().toJson(response));
        } catch (ZabbixApiException e) {
            logger.error(e.getMessage());
        }

    }

    private void createApiDiskUsageScreen() {
        ArrayList<Integer> hosts = getHosts(14);

        Integer y = 0;
        Integer x;
        for (Integer hostId : hosts) {
            x = 0;
            ArrayList<Integer> graphs = getHostResoureceId(hostId);
            for (Integer graphId : graphs) {
                addScreenItem(graphId, x, y);
                x++;
            }
            if (x > 0)
                y++;

        }
    }

    public static void main(String[] args) {

        ApiDiskMonitor diskMonitor = new ApiDiskMonitor();
        diskMonitor.createApiDiskUsageScreen();
    }

}
