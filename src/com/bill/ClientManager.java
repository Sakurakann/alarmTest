package com.bill;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.gs.net.sf.Future;
import com.gs.net.sf.FutureListener;
import com.gs.net.sf.PacketDecoder;
import com.gs.net.sf.PacketEncoder;
import com.gs.net.sf.Session;
import com.gs.net.sf.SessionHandler;
import com.gs.net.sf.SessionType;
import com.gs.net.sf.session.SessionFactory;
import com.gs.packet.Packet;


public class ClientManager  {
	public static Logger logger = Logger.getLogger("ClientManager");

	private String clientName = ""; // 程序名称
	

	private Packet heartPacket = null; // 心跳包

	private long heartTime = 30 * 1000; // 心跳间隔

	private boolean aliveCheck = false; // 是否需要检测连接情况

	private static final long reStartTime = 2 * 60 * 1000; // 重启服务的等待时间：System.currentTimeMillis()-lastSendTime
	// >=reStartTime
	// 时重启服务
	
	private SocketClient client=null;

	private ScheduledExecutorService executor = null;

	private ConcurrentHashMap<Future, Object> poolMap = new ConcurrentHashMap<Future, Object>(
			20);

	/**
	 * 最近成功发送的时间
	 */
	private long lastSendTime = 0;

	public ClientManager(String name) {
		clientName = name;
	}

	/**
	 * 心跳包
	 * 
	 * @param packet
	 */
	public void setHeartPacket(Packet packet) {
		heartPacket = packet;
		aliveCheck = true;
	}

	/**
	 * 心跳间隔，小于0时，不发心跳包，不监测连接情况
	 * 
	 * @param time
	 */
	public void setHeartInterval(long time) {
		if (time > 0)
			heartTime = time;
		else {
			aliveCheck = false;
		}
	}

	/**
	 * 设置远程地址
	 * 
	 * @param address
	 */
	private SocketAddress _remoteAddress=null;
	public void setRemoteAddress(SocketAddress address) {
		_remoteAddress = address;
	}
	private SocketAddress _localAddress=null;
	public void setLocalAddress(SocketAddress address) {
		_localAddress = address;
//		
	}
	private SessionHandler _handler=null;
	public void setSessionHandler(SessionHandler handler) {
		_handler = handler;
	}
	
	private PacketEncoder _encoder=null;
	public void setPacketEncoder(PacketEncoder encoder) {
		_encoder = encoder;
	}

	private PacketDecoder _decoder=null;
	public void setPacketDecoder(PacketDecoder decoder) {
		_decoder = decoder;
	}
    int i=0;
	public Future send(Object obj) {
		Future future = client.session.send(obj);
		
		poolMap.put(future, obj);
		future.addListener(new FutureListener() {
			public void futureCompleted(Future paramFuture) throws Exception {
				poolMap.remove(paramFuture);
				if (paramFuture.isSucceeded()) {
					i++; 
					if(i%2000==0){
						System.out.println("发送["+i+"]条");
					}
					if (aliveCheck) {
						lastSendTime = System.currentTimeMillis();
					}
				}
			}
		});
		
		return future;
	}
	
	public boolean sendSyc(Object obj){
		return client.session.send(obj).complete();
	}

	/**
	 * 返回远程连接信息
	 * 
	 * @return
	 */
	public String getRemoteInfo() {
		return "Remote address:" + _remoteAddress.toString();
	}

	public boolean start() {
		client = new SocketClient();
		client.start();
		return client.isStart;
	}

	public Future close() {
		lastSendTime = 0;
		shutDownHeart();
		if (client == null) {
			return null;
		}
		client.interrupt();
		return client.session.close();
	}

	private void shutDownHeart() {
		if (executor != null) {
			executor.shutdown();
		}
	}

	private void reStart() {
		Future future = close();
		if (future != null) {
			future.complete();
		}
		Object[] objs = poolMap.values().toArray();
		poolMap.clear();
		if (start()) {
			logger.info(clientName + "已重新启动......");
			logger.info(clientName + "重新后有[" + objs.length + "]个任务需要重发!");
			for (Object obj : objs) {
				// session.send(obj);
				if (obj == heartPacket) {
					continue;
				}
				send(obj);
			}
		} else {
			logger.error(clientName + "重连失败，等待下次尝试......");
			keepHeart();
		}
	}

	private void keepHeart() {
		if (heartPacket == null)
			return;

		executor = Executors.newSingleThreadScheduledExecutor();

		executor.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				long cur = System.currentTimeMillis();
				if (!client.session.isStarted() || cur - lastSendTime >= reStartTime) {
					reStart();
				} else if (cur - heartTime >= 0) {
					// session.send(heartPacket);
					send(heartPacket);
				}
			}
		}, heartTime, heartTime / 2, TimeUnit.MILLISECONDS);
	}
	
	private class SocketClient extends Thread{
		public Session session = null;
		public boolean isStart=false;
		public SocketClient(){
			session = SessionFactory.createSession(SessionType.TCP);
			session.setRemoteAddress(_remoteAddress);
			session.setLocalAddress(_localAddress);
			session.setSessionHandler(_handler);
			session.setPacketEncoder(_encoder);
			session.setPacketDecoder(_decoder);
		}

		@Override
		public synchronized void start() {
			super.start();
			
			Future future = session.start();
			if (aliveCheck) {
				future.addListener(new FutureListener() {
					public void futureCompleted(Future paramFuture)
							throws Exception {
						if (paramFuture.isSucceeded()) {
							lastSendTime = System.currentTimeMillis();
						}
					}
				});
				if (future.complete()) {
					keepHeart();
				}
			}
			isStart = future.complete();
		}
	}

}

