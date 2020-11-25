package com.junction.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpUtil {
	/**
	 * localhost 转127.0.0.1
	 * @param domainName
	 * @return
	 */
	public static String IpConvert(String domainName) {
		String ip = domainName;
		try {
			ip = InetAddress.getByName(domainName).getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return domainName;
		}
		return ip;
	}
}
