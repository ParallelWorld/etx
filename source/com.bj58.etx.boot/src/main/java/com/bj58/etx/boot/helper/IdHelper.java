package com.bj58.etx.boot.helper;

import com.bj58.etx.core.util.EtxIdUtil;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IdHelper {

    private static Logger log = LoggerFactory.getLogger(IdHelper.class);
    private static int machineNo = 1;

    public static int getMachineNo() {
        return machineNo;
    }

    /**
     * 初始化机器码
     */
    @SuppressWarnings("unchecked")
    public static void init(String path) {
        try {
            List<String> localIpList = getIPV4LocalList();
            log.info("------localIP:" + localIpList);
            SAXReader reader = new SAXReader();
            Document document = reader.read(path);
            Element rootEle = document.getRootElement();
            List<Element> childElements = rootEle.elements();
            for (Element child : childElements) {
                String ip = child.attribute("ip").getValue();
                if (StringUtils.isBlank(ip)) {
                    continue;
                }
                for (String localIP : localIpList) {
                    if (ip.equals(localIP)) {
                        String no = child.attribute("no").getValue();
                        machineNo = Integer.valueOf(no);
                        log.info("-----mapping ip:" + localIP + " machineNo:" + machineNo);
                        return;
                    }
                }
            }
            log.warn("not find machine config, set default value=" + machineNo);
        } catch (Exception e) {
            throw new RuntimeException("init machine.xml error");
        }
    }

    public static long genId() {
        return EtxIdUtil.genId(machineNo);
    }


    @SuppressWarnings("rawtypes")
    private static List<String> getIPV4LocalList() throws SocketException {
        List<String> ipV4List = new ArrayList<String>();
        Enumeration all = NetworkInterface.getNetworkInterfaces();
        InetAddress ip;
        while (all.hasMoreElements()) {
            NetworkInterface netinter = (NetworkInterface) all.nextElement();
            Enumeration addresses = netinter.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address) {
                    ipV4List.add(ip.getHostAddress());
                }
            }
        }
        return ipV4List;
    }
}
