package com.opensource.api.order.service.util;

import org.apache.zookeeper.ZooKeeper;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by amg871 on 6/15/17.
 */

@Named
public class ZookeeperUtil {
    public static Logger log = LoggerFactory.getLogger(ZookeeperUtil.class);
    public String getBrokers(String zookeeperAddress){
        List<String> brokers = new ArrayList<>();
        try{
            ZooKeeper zk = new ZooKeeper(zookeeperAddress, 10000, null);
            List<String> ids = zk.getChildren("/brokers/ids", false);
            for (String id : ids) {
                String brokerInfo = new String(zk.getData("/brokers/ids/" + id, false, null));
                JSONObject jsonObject = new JSONObject(brokerInfo);
                System.out.println(jsonObject.get("host") + ":" + jsonObject.get("port"));
                brokers.add(jsonObject.get("host") + ":" + jsonObject.get("port"));
            }
        }
        catch(Exception e) {

//            Throwing below error during startup, need to address later
//            java.lang.NullPointerException: null
//            at org.apache.zookeeper.ClientCnxn$EventThread.processEvent(ClientCnxn.java:530) [zookeeper-3.4.10.jar:3.4.10-39d3a4f269333c922ed3db283be479f9deacaa0f]
//            at org.apache.zookeeper.ClientCnxn$EventThread.run(ClientCnxn.java:505) [zookeeper-3.4.10.jar:3.4.10-39d3a4f269333c922ed3db283be479f9deacaa0f]

            log.error(e.getMessage());
        }
        return String.join(",", brokers);

    }
}
