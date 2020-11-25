package com.junction.util;

import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.junction.cache.CacheUtil;
import com.junction.controller.CameraController;
import com.junction.pojo.Config;

/**
 * @Title TimerUtil.java
 * @description 定时任务
 * @time 2019年12月16日 下午3:10:08
 * @author wuguodong
 **/
@Component
public class TimerUtil implements CommandLineRunner {

	private final static Logger logger = LoggerFactory.getLogger(TimerUtil.class);

	@Autowired
	private Config config;// 配置文件bean

	public static Timer timer;

	@Override
	public void run(String... args) {
		// 超过5分钟，结束推流
		timer = new Timer("timeTimer");
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				logger.info("******   执行定时任务       BEGIN   ******");
				// 管理缓存
				if (null != CacheUtil.STREAMMAP && 0 != CacheUtil.STREAMMAP.size()) {
					Set<String> keys = CacheUtil.STREAMMAP.keySet();
					for (String key : keys) {
							// 最后打开时间
							long openTime = CacheUtil.STREAMMAP.get(key).getOpenTime().getTime();
							// 当前系统时间
							long newTime = new Date().getTime();
							// 如果通道使用人数为0，则关闭推流
							if (CacheUtil.STREAMMAP.get(key).getCount() == 0) {
								// 结束线程
								CameraController.jobMap.get(key).setInterrupted();
								// 清除缓存
								CacheUtil.STREAMMAP.remove(key);
								CameraController.jobMap.remove(key);
							} else if ((newTime - openTime) / 1000 / 60 > Integer.valueOf(config.getKeepalive())) {
								CameraController.jobMap.get(key).setInterrupted();
								logger.debug("[定时任务：]  结束： " + CacheUtil.STREAMMAP.get(key).getRtsp() + "  推流任务！");
								CameraController.jobMap.remove(key);
								CacheUtil.STREAMMAP.remove(key);
							}
					}
				}
				logger.info("******   执行定时任务       END     ******");
			}
		}, 1, 1000 * 60);
	}
}
