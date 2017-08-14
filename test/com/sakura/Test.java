package com.sakura;

import com.bill.BillResultPacketEncoder;
import com.gs.packet.BytesUtil;
import com.gs.packet.WORD;
import com.utils.SingEalBill;
import org.apache.commons.lang.ArrayUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Test {
	public static void main(String[] args) {
		BillResultPacketEncoder brpe = new BillResultPacketEncoder();
		/*Map<Integer, Object> map = new ConcurrentHashMap<Integer, Object>();
		map.put(SingEalBill.TASKID, 12341234);
		map.put(SingEalBill.AUTHRESULT, "failure");

		BillResultPacketEncoder brpe = new BillResultPacketEncoder();

		brpe.warpMTWPPacket(map);

		byte[] bytes = null;
		for ( byte i = 1; i < 5; i++ ) {
			byte[] b = new byte[]{i};
			bytes = ArrayUtils.addAll(bytes,b);
		}
		System.out.println(bytes.length);
		for ( byte aByte : bytes ) {
			System.out.println(aByte);
		}*/

//		String insert = "insert into gwcallbill(task_id,redo,prog_id,PCMNo,TimeSlot,CallerNo,CalleeNo,CallType,Start_Time,End_Time,duration,Talk_status,IAI_time,ACM_Time,Respond_Time,SS7_Content,code_data,remark)values(D(taskid),D(0),D(progid),D(1),D(<10),S(z_taskinfo.caller),S(z_taskinfo.called),FIX(0@@1@@2@@3),T(sysdate),T(sysdate),D(60),D(2),T(sysdate),D(<1000),D(<1600),IP,S(<1000),FIX('c825c825c825c825$d219d219d219d219'@@'d219d219d219d219$c825c825c825c825'))";
//		brpe.warpMTWPPacket(insert);

		Integer inte = 400;
		byte[] bytes = BytesUtil.Short2Bytes(inte.shortValue());
		for ( byte aByte : bytes ) {
			System.out.println(aByte);
		}

		int code = new WORD(bytes).toIntValue();
		System.out.println(code);


	}

	public static byte[] intToBytes2(int value) {
		byte[] src = new byte[4];
		src[0] = (byte) ((value >> 24) & 0xFF);
		src[1] = (byte) ((value >> 16) & 0xFF);
		src[2] = (byte) ((value >> 8) & 0xFF);
		src[3] = (byte) (value & 0xFF);
		return src;
	}
}