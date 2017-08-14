package com.bill;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import com.bill.packet.MTWPPacketBuilder;
import com.gs.net.sf.Session;
import com.gs.net.sf.SessionHandler;



public class BillClinet extends Thread {
	private static Logger logger = Logger.getLogger(BillClinet.class);

	private boolean startStat = false;
	private ClientManager client = null;

	public BillClinet(String ip, int port, String name) {
		client = new ClientManager(name);
		client.setRemoteAddress(new InetSocketAddress(ip, port));
		client.setSessionHandler(new BillSessionHandler());
//		client.setPacketDecoder(new MTWPPacketDecoder());
		client.setPacketEncoder(new BillResultPacketEncoder());
		client.setHeartPacket(MTWPPacketBuilder.createHeartPacket());
		if (!client.start()) {
			logger.fatal(name + "没有启动成功!.......");
		}
	}

	public void send(Object obj) {
		client.send(obj);
	}
	
	public boolean sendSyc(Object obj){
		return client.sendSyc(obj);
	}

	private class BillSessionHandler implements SessionHandler {

		public void exceptionCaught(Session paramSession,
				Throwable paramThrowable) {

		}

		public void objectReceived(Session paramSession, Object paramObject)
				throws Exception {
		}

		public void objectSent(Session paramSession, Object paramObject)
				throws Exception {
			// System.out.println(paramObject);
		}

		public void sessionClosed(Session paramSession) throws Exception {
			logger.error("com.bill 连接中断....");
			startStat = false;
		}

		public void sessionStarted(Session paramSession) throws Exception {
			logger.info("com.bill 连接成功 "+client.getRemoteInfo());
			startStat = true;
		}

		public void sessionTimeout(Session paramSession) throws Exception {

		}

	}

	public boolean getStatus() {
		return startStat;
	}

	public static void main(String[] arg) {
	}

}
