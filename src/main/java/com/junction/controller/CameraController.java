package com.junction.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.junction.cache.CacheUtil;
import com.junction.pojo.CameraPojo;
import com.junction.pojo.Config;
import com.junction.thread.CameraThread;
import com.junction.util.IpUtil;

/**
 * @Title CameraController.java
 * @description controller
 * @time 2019年12月16日 上午9:00:27
 * @author wuguodong
 **/
@Api(value = "监控信息",description = "监控管理")
@RestController
public class CameraController {

	private final static Logger logger = LoggerFactory.getLogger(CameraController.class);

	@Autowired
	public Config config;// 配置文件bean

	// 存放任务 线程
	public static Map<String, CameraThread.MyRunnable> jobMap = new HashMap<String, CameraThread.MyRunnable>();

	/**
	 * @Title: openCamera
	 * @Description: 开启视频流
	 * @return Map<String,String>
	 **/
	@ApiOperation(value = "开启监控")
	@RequestMapping(value = "/cameras", method = RequestMethod.POST)
	public CameraPojo openCamera(@Validated @RequestBody CameraPojo pojo) {
		// 返回结果
		// 校验参数
			// 获取当前时间
			openStream(pojo);
			Set<String> keys = CacheUtil.STREAMMAP.keySet();
			if(keys!=null&&keys.size()>0){
				for (String key : keys) {
					if (pojo.getToken().equals(CacheUtil.STREAMMAP.get(key).getToken())) {// 存在直播流
						pojo=CacheUtil.STREAMMAP.get(key);
						openStream(pojo);
						break;
					}
				}
			}
		// 执行任务
		CameraThread.MyRunnable job = new CameraThread.MyRunnable(pojo);
		CameraThread.MyRunnable.es.execute(job);
		jobMap.put(pojo.getToken(), job);
		return pojo;
	}

	/**
	 * @Title: openStream
	 * @Description: 推流器
	 * @return
	 * @return CameraPojo
	 **/
	private void openStream(CameraPojo cameraPojo) {
		// 生成token
		String token = UUID.randomUUID().toString();
		String rtsp = "rtsp://";
		String rtmp = "";
		String IP = IpUtil.IpConvert(cameraPojo.getIp());
		String url = "";
		if(!StringUtils.isEmpty(cameraPojo.getUsername())&&!StringUtils.isEmpty(cameraPojo.getPassword())){
			rtsp+=cameraPojo.getUsername()+":"+cameraPojo.getPassword()+"@";
		}
		rtsp+=IP;
		if(cameraPojo.getPort()!=null){
			rtsp+=":"+cameraPojo.getPort();
		}
		if(!StringUtils.isEmpty(cameraPojo.getUrlPath())){
			rtsp+=cameraPojo.getUrlPath();
		}else{
			rtsp+="/";
		}
		cameraPojo.setToken(rtsp);
		rtsp+="?tt=0";//为了后边正确的参数
		if(!StringUtils.isEmpty(cameraPojo.getChannel())){
			rtsp+="&channel="+cameraPojo.getChannel();
		}
		if(!StringUtils.isEmpty(cameraPojo.getStream())){
			rtsp+="&stream="+cameraPojo.getStream();
		}
		if(cameraPojo.getQuerys()!=null&&!cameraPojo.getQuerys().isEmpty()){
			for (Map.Entry<String,String> entry:cameraPojo.getQuerys().entrySet()){
				rtsp+="&"+entry.getKey()+"="+entry.getValue();
			}
		}

		if(cameraPojo.getStartTime()!=null&&cameraPojo.getEndTime()!=null){
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String startTime = df.format(cameraPojo.getStartTime().getTime() - 60 * 1000);
			String endTime = df.format(cameraPojo.getEndTime().getTime() + 60 * 1000);
			rtsp+="&starttime="+startTime.substring(0, 8)+ "t" + startTime.substring(8) + "z";
			rtsp+="&endtime="+endTime.substring(0, 8)+ "t" + endTime.substring(8) + "z";
		}
		rtmp = "rtmp://" + IpUtil.IpConvert(config.getPush_host()) + ":" + config.getPush_port() + "/live/"
				+ token;
		if (config.getHost_extra().equals("127.0.0.1")) {
			url = rtmp;
		} else {
			url = "rtmp://" + IpUtil.IpConvert(config.getHost_extra()) + ":" + config.getPush_port() + "/live/"
					+ token;
		}
		cameraPojo.setRtmp(rtmp);
		cameraPojo.setUrl(url);
		// 历史流
		cameraPojo.setIp(IP);
		cameraPojo.setRtsp(rtsp);
		cameraPojo.setCount(1);
	}

	/**
	 * @Title: closeCamera
	 * @Description:关闭视频流
	 * @param tokens
	 * @return void
	 **/
	@RequestMapping(value = "/cameras/{tokens}", method = RequestMethod.DELETE)
	public void closeCamera(@PathVariable("tokens") String tokens) {
		if (null != tokens && !"".equals(tokens)) {
			String[] tokenArr = tokens.split(",");
			for (String token : tokenArr) {
				if (jobMap.containsKey(token) && CacheUtil.STREAMMAP.containsKey(token)) {
					if (0 < CacheUtil.STREAMMAP.get(token).getCount()) {
						// 人数-1
						CacheUtil.STREAMMAP.get(token).setCount(CacheUtil.STREAMMAP.get(token).getCount() - 1);
						logger.info("关闭：" + CacheUtil.STREAMMAP.get(token).getRtsp() + ";当前使用人数为："
								+ CacheUtil.STREAMMAP.get(token).getCount());
					}
				}
			}
		}
	}

	/**
	 * @Title: getCameras
	 * @Description:获取视频流
	 * @return Map<String, CameraPojo>
	 **/
	@RequestMapping(value = "/cameras", method = RequestMethod.GET)
	public Map<String, CameraPojo> getCameras() {
		logger.info("获取视频流信息：" + CacheUtil.STREAMMAP.toString());
		return CacheUtil.STREAMMAP;
	}

	/**
	 * @Title: keepAlive
	 * @Description:视频流保活
	 * @param tokens
	 * @return void
	 **/
	@RequestMapping(value = "/cameras/{tokens}", method = RequestMethod.PUT)
	public void keepAlive(@PathVariable("tokens") String tokens) {
		// 校验参数
		if (null != tokens && !"".equals(tokens)) {
			String[] tokenArr = tokens.split(",");
			for (String token : tokenArr) {
				CameraPojo cameraPojo = new CameraPojo();
				// 直播流token
				if (null != CacheUtil.STREAMMAP.get(token)) {
					cameraPojo = CacheUtil.STREAMMAP.get(token);
					// 更新当前系统时间
					cameraPojo.setOpenTime(new Date());
					logger.info("视频流：" + cameraPojo.getRtmp() + "保活！");
				}
			}
		}
	}

	/**
	 * @Title: getConfig
	 * @Description: 获取服务信息
	 * @return Map<String, Object>
	 **/
	@RequestMapping(value = "/status", method = RequestMethod.GET)
	public Map<String, Object> getConfig() {
		// 获取当前时间
		long nowTime = new Date().getTime();
		String upTime = (nowTime - CacheUtil.STARTTIME) / (1000 * 60 * 60) + "h"
				+ (nowTime - CacheUtil.STARTTIME) % (1000 * 60 * 60) / (1000 * 60) + "m"
				+ (nowTime - CacheUtil.STARTTIME) % (1000 * 60 * 60) / (1000) + "s";
		logger.info("获取服务信息：" + config.toString() + ";服务运行时间：" + upTime);
		Map<String, Object> status = new HashMap<String, Object>();
		status.put("config", config);
		status.put("uptime", upTime);
		return status;
	}

}
