package com.junction.pojo;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * @Author mgg
 * @Description  各厂家rtsp地址格式如下：https://www.cnblogs.com/wanggang123/p/8442683.html
 * @Date 16:27 2020/11/24
 **/
public class CameraPojo implements Serializable {
	private static final long serialVersionUID = 8183688502930584159L;

	// 一 .海康、中威摄像机 rtsp://admin:12345@192.168.1.64:554/Streaming/Channels/1
	// 二. 大华            rtsp://admin:12345@192.168.1.64:554/cam/realmonitor?channel=1&subtype=0
	// 三. 英飞拓          rtsp://admin:admin@192.168.1.64/1/1080p
	// 四. 三星            rtsp://admin:admin001@192.168.1.64:554/onvif/profile2/media.smp（720P）
	//五. 宇视：           rtsp://admin:admin@192.168.1.64/media/video1/multicast
	//六. LG               rtsp://admin:admin@192.168.1.64:554/Master-0
	//七、 派尔高网络摄像机 rtsp://192.168.1.64/h264
	//八、 安讯士网络摄像机 rtsp://root:123456@192.168.1.64/axis-media/media.amp?videocodec=h264&resolution=1280x720&fps=25
	//九. 非凡             rtsp://admin:12345@192.168.1.64:554/streaming/channels/101
	//十. 金三立           rtsp://Admin:111111@192.168.1.64/stream/av0_0
	//其他 rtsp://60.28.26.18:21007/video?p_id=6784&stream=1
	private String username;// 摄像头账号
	private String password;// 摄像头密码
	@NotNull(message = "ip不能为空")
	private String ip;// 摄像头ip
	private Long port=80l;//端口 80默认
	private String urlPath;//后缀的请求
	private String channel;// 摄像头通道
	private String stream;// 摄像头码流
	private HashMap<String,String> querys;//其他参数配置
	private String rtsp;// rtsp地址
	private String rtmp;// rtmp地址
	private String url;// 播放地址
	private Date startTime;// 回放开始时间
	private Date endTime;// 回放结束时间
	private Date openTime;// 打开时间
	private int count = 0;// 使用人数
	private String token;
	public Long getPort() {
		return port;
	}

	public void setPort(Long port) {
		this.port = port;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public HashMap<String, String> getQuerys() {
		return querys;
	}

	public void setQuerys(HashMap<String, String> querys) {
		this.querys = querys;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}

	public String getRtsp() {
		return rtsp;
	}

	public void setRtsp(String rtsp) {
		this.rtsp = rtsp;
	}

	public String getRtmp() {
		return rtmp;
	}

	public void setRtmp(String rtmp) {
		this.rtmp = rtmp;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "CameraPojo [username=" + username + ", password=" + password + ", ip=" + ip + ", channel=" + channel
				+ ", stream=" + stream + ", rtsp=" + rtsp + ", rtmp=" + rtmp + ", url=" + url + ", startTime="
				+ startTime + ", endTime=" + endTime + ", openTime=" + openTime + ", count=" + count + ", token="
				+ token + "]";
	}

}
