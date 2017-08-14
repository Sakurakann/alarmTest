package com.bill;

import com.bill.packet.MTWPPacket;
import com.bill.packet.MTWPPacketBuilder;
import com.gs.net.sf.Packet;
import com.gs.net.sf.PacketEncoder;
import com.gs.net.sf.Session;
import com.gs.net.sf.packet.DefaultPacket;
import com.gs.packet.BytesUtil;
import com.gs.packet.WORD;
import com.utils.StringUtils;
import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BillResultPacketEncoder implements PacketEncoder {

	public Packet encode(Session arg0, Object paramObject) throws Exception {
		if (paramObject instanceof String) {
			return warpMTWPPacket((String) paramObject);
		} else if (paramObject instanceof Packet) {
			return (Packet) paramObject;
		} else if (paramObject instanceof com.gs.packet.Packet) {
			return new DefaultPacket(((com.gs.packet.Packet) paramObject).buffer());
		}
		return null;
	}

	private Packet warpMTWPPacket(String sql) {
		int from = sql.indexOf("(");
		String heard = sql.substring(0, from).toLowerCase();
		from  = heard.indexOf("into");
		String tableName = null;
		if(from > 0){
			tableName = StringUtils.trim(heard.substring(from+4));
		}
		if ("BL_SINGEALBILL".equalsIgnoreCase(tableName)){
			return  warpMTWPPacketTLV(sql);
		}

		byte[] b1_0 = new byte[]{0x01};  //标志位
		byte[] b1_1 = sql.getBytes();
		byte[] b1 = new WORD((short) b1_1.length).getBytes();
		byte[] value = new byte[1 + 2 + b1_1.length];
		int off = 0;
		System.arraycopy(b1_0, 0, value, off, 1);
		off += 1;
		System.arraycopy(b1, 0, value, off, 2);
		off += 2;
		System.arraycopy(b1_1, 0, value, off, b1_1.length);
		off += b1_1.length;

		return new DefaultPacket(MTWPPacketBuilder.createPacket(MTWPPacket.CODE_TASK_BILL_SQL, value).buffer());
	}

	//TODO 方法修饰为private
	private Packet warpMTWPPacketTLV(String sql) {
		boolean ret = true;
		Map<Integer, Object> map = new ConcurrentHashMap<Integer, Object>(20);
		String[] groups = StringUtils.splitLikeJson(sql, '(', ')');
		ret = groups.length == 2;
		if (ret) {
			String[] items = groups[0].split(",");
			String[] values = groups[1].split(",");

			int count = 0;
			for ( int i = 0;i < values.length;i++ ){
				if (values[i].startsWith("'yyyy-mm-dd")){
					values[i-1] = values[i-1]+","+values[i];
					values[i] = null;
					count++;
				}
			}
			String newValues[] = new String[values.length - count];
			int index = 0;
			for ( String s : values ) {
				if (s != null){
					newValues[index++] = s;
				}
			}
			ret = items.length == newValues.length;
			if (ret) {
				Class<?> clazz = null;
				try {
					//类名怎么动态传入?
					clazz = Class.forName("com.utils.SingEalBill");
					if (clazz != null) {
						Field[] fields = clazz.getDeclaredFields();
						for ( Field field : fields ) {
							field.setAccessible(true);

							for ( int i = 0;i < items.length;i++ ){
								if (items[i].equalsIgnoreCase(field.getName())) {
									try {
										map.put((Integer) field.get(clazz), newValues[i]);
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return warpMTWPPacketTLV(map);
	}

	// TODO tlv格式 k-val形式的
	// 如果数据是数字型的 length的值为4 value的长度固定为4,tag的长度固定为2,length的长度固定为2

	/**
	 *
	 * @param map 封装好的Tag-Value
	 * @return TLV打包数据
	 */
	private Packet warpMTWPPacketTLV(Map<Integer, Object> map) {
		Set<Integer> keySet = map.keySet();
		byte[] values = null;
		for ( Integer inte : keySet ) {
			//tag
			byte[] b1_0 = BytesUtil.Short2Bytes(inte.shortValue());
			try {
				//value 如果value是数字类型
				byte[] b1_1 = BytesUtil.Int2Bytes(Integer.valueOf(map.get(inte).toString()));
				//length
				byte[] b1 = new WORD((short) 4).getBytes();

				int off = 0;
				byte[] value = new byte[2 + 2 + b1_1.length];

				System.arraycopy(b1_0, 0, value, off, 2);
				off += 2;
				System.arraycopy(b1, 0, value, off, 2);
				off += 2;
				System.arraycopy(b1_1, 0, value, off, b1_1.length);
				off += b1_1.length;

				values = ArrayUtils.addAll(values, value);
				continue;
			} catch (Exception e) {
				//value 如果value不是数字类型
				byte[] b1_1 = map.get(inte).toString().getBytes();
				//length
				byte[] b1 = new WORD((short) b1_1.length).getBytes();

				int off = 0;
				byte[] value = new byte[2 + 2 + b1_1.length];

				System.arraycopy(b1_0, 0, value, off, 2);
				off += 2;
				System.arraycopy(b1, 0, value, off, 2);
				off += 2;
				System.arraycopy(b1_1, 0, value, off, b1_1.length);
				off += b1_1.length;

				//将多个数组合并 递归
				values = ArrayUtils.addAll(values, value);
			}
		}
		return new DefaultPacket(MTWPPacketBuilder.createPacket((byte)0xBF, values).buffer());
	}
}