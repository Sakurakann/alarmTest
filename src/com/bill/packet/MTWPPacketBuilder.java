package com.bill.packet;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.gs.packet.DWORD;
import com.gs.packet.WORD;

public final class MTWPPacketBuilder {

    public static Logger logger = Logger.getLogger("SServerModule");

	public static MTWPPacket createHeartPacket(){
		return new MTWPPacket(
                MTWPPacket.MARK,
                MTWPPacket.NAME,
                MTWPPacket.VERSION,
				MTWPPacket.CODE_HEART,
				null);
	}
    
    public static MTWPPacket createPacket(byte code, byte[] value){
        //logger.debug("MTWPPacket Code="+code);
        return new MTWPPacket(
                MTWPPacket.MARK,
                MTWPPacket.NAME,
                MTWPPacket.VERSION,
                code,
                value);
    }
    public static MTWPPacket createPacket2(byte code, byte[] value){
        //logger.debug("MTWPPacket Code="+code);
        return new MTWPPacket(
                MTWPPacket.MARK,
                MTWPPacket.NAME2,
                MTWPPacket.VERSION,
                code,
                value);
    }
    
    public static MTWPPacket  createJTPacket(byte code,String xml){
    	//logger.info("MTWPPacket JTSvr话单 XML::"+xml);
        byte[] src = new byte[3 + xml.getBytes().length];
    	int off = 0;
        System.arraycopy(new byte[]{(byte)0x04},0,src,off,1);                                                  off+=1;
        System.arraycopy(new WORD((short)xml.getBytes().length).getBytes(), 0, src, off, 2);                   off+=2;
        System.arraycopy(xml.getBytes(), 0, src, off, xml.getBytes().length);                                  off+=xml.getBytes().length;

       return new MTWPPacket(
                MTWPPacket.MARK,
                MTWPPacket.NAME,
                MTWPPacket.VERSION,
                code,
                src);
    }
    
    public static MTWPPacket  createAlarmPacket(byte code,String type,String testCode,String phone){
    	logger.debug("MTWPPacket 告警请求号码 testCode::"+testCode+"phone::"+phone);
        byte[] src = new byte[9 +type.getBytes().length+testCode.getBytes().length+ phone.getBytes().length];
    	int off = 0;
    	
    	System.arraycopy(new byte[]{(byte)0x10},0,src,off,1);                                                  off++;
        System.arraycopy(new WORD((short)type.getBytes().length).getBytes(), 0, src, off, 2);                  off+=2;
        System.arraycopy(type.getBytes(), 0, src, off, type.getBytes().length);                                off+=type.getBytes().length;
        
        System.arraycopy(new byte[]{(byte)0x04},0,src,off,1);                                                  off+=1;
        System.arraycopy(new WORD((short)testCode.getBytes().length).getBytes(), 0, src, off, 2);              off+=2;
        System.arraycopy(testCode.getBytes(), 0, src, off, testCode.getBytes().length);                         off+=testCode.getBytes().length;

        System.arraycopy(new byte[]{(byte)0x02},0,src,off,1);                                                  off+=1;
        System.arraycopy(new WORD((short)phone.getBytes().length).getBytes(), 0, src, off, 2);                  off+=2;
        System.arraycopy(phone.getBytes(), 0, src, off, phone.getBytes().length);                               off+=phone.getBytes().length;

       return new MTWPPacket(
                MTWPPacket.MARK,
                MTWPPacket.NAME,
                MTWPPacket.VERSION,
                code,
                src);
    }
    
    //////////////gprs资源发送//////
    public static MTWPPacket  createGprsResourcePacket(byte code,String url,String keyword,String remark,String id){
    	//logger.debug("MTWPPacket GPRS资源下发 url::"+url+"keyword::"+keyword+"remark::"+remark);
        byte[] src = new byte[12 +url.getBytes().length+ keyword.getBytes().length+ remark.getBytes().length+ id.getBytes().length];
    	int off = 0;
        System.arraycopy(new byte[]{(byte)0x01},0,src,off,1);                                                  off++;
        System.arraycopy(new WORD((short)url.getBytes().length).getBytes(), 0, src, off, 2);                   off+=2;
        System.arraycopy(url.getBytes(), 0, src, off, url.getBytes().length);                                  off+=url.getBytes().length;

        System.arraycopy(new byte[]{(byte)0x02},0,src,off,1);                                                  off++;
        System.arraycopy(new WORD((short)keyword.getBytes().length).getBytes(), 0, src, off, 2);               off+=2;
        System.arraycopy(keyword.getBytes(), 0, src, off, keyword.getBytes().length);                          off+=keyword.getBytes().length;

        
        System.arraycopy(new byte[]{(byte)0x03},0,src,off,1);                                                  off+=1;
        System.arraycopy(new WORD((short)remark.getBytes().length).getBytes(), 0, src, off, 2);                off+=2;
        System.arraycopy(remark.getBytes(), 0, src, off, remark.getBytes().length);                            off+=remark.getBytes().length;
       
        
        System.arraycopy(new byte[]{(byte)0x04},0,src,off,1);                                                  off+=1;
        System.arraycopy(new WORD((short)id.getBytes().length).getBytes(), 0, src, off, 2);                    off+=2;
        System.arraycopy(id.getBytes(), 0, src, off, id.getBytes().length);                                    off+=id.getBytes().length;

       return new MTWPPacket(
                MTWPPacket.MARK,
                MTWPPacket.NAME,
                MTWPPacket.VERSION,
                code,
                src);
    }
    
    
    //////////////gprs资源删除//////
    public static MTWPPacket  delGprsResourcePacket(byte code,String id){
    	logger.debug("MTWPPacket GPRS资源删除 id::"+id);
        byte[] src = new byte[3 + id.getBytes().length];
    	int off = 0;
        System.arraycopy(new byte[]{(byte)0x04},0,src,off,1);                                                  off+=1;
        System.arraycopy(new WORD((short)id.getBytes().length).getBytes(), 0, src, off, 2);                    off+=2;
        System.arraycopy(id.getBytes(), 0, src, off, id.getBytes().length);                                    off+=id.getBytes().length;

       return new MTWPPacket(
                MTWPPacket.MARK,
                MTWPPacket.NAME,
                MTWPPacket.VERSION,
                code,
                src);
    }
    //////////////////////发送号码imsi
    public static MTWPPacket  createIMSIPacket(byte code,int num,String phone){
    	logger.info("MTWPPacket 发送号码号码为::["+num+"] 号码详细内容为::"+phone);
    	 int off = 0;
         off+=3;
         byte[] b1_0 = new byte[]{0x19};
         byte[] b1 = new DWORD(num).getBytes();                                     off+=b1.length;
         off+=3;
         byte[] b2_0 = new byte[]{0x17};
         byte[] b2 = phone.getBytes();                                              off+=b2.length;
         
         
         byte[] value = new byte[off];
         off = 0;
         byte[] src = b1;
         System.arraycopy(b1_0, 0, value, off, 1);                                        off++;
         System.arraycopy(new WORD((short)src.length).getBytes(), 0, value, off, 2);      off+=2;
         System.arraycopy(src, 0, value, off, src.length);                                off+=src.length;
         src = b2;
         System.arraycopy(b2_0, 0, value, off, 1);                                        off++;
         System.arraycopy(new WORD((short)src.length).getBytes(), 0, value, off, 2);      off+=2;
         System.arraycopy(src, 0, value, off, src.length);                                off+=src.length;

       return new MTWPPacket(
                MTWPPacket.MARK,
                MTWPPacket.NAME,
                MTWPPacket.VERSION,
                code,
                value);
    }
    
    public static MTWPPacket  createTestCodePacket(byte code,String testCode,String taskid){
    	logger.debug("MTWPPacket 请求测试类型返回 testCode::"+testCode+"taskid::"+taskid);
        byte[] src = new byte[6 +testCode.getBytes().length+ taskid.getBytes().length];
    	int off = 0;
        System.arraycopy(new byte[]{(byte)0x06},0,src,off,1);                                                  off++;
        System.arraycopy(new WORD((short)testCode.getBytes().length).getBytes(), 0, src, off, 2);              off+=2;
        System.arraycopy(testCode.getBytes(), 0, src, off, testCode.getBytes().length);                         off+=testCode.getBytes().length;

        System.arraycopy(new byte[]{(byte)0x07},0,src,off,1);                                                  off+=1;
        System.arraycopy(new WORD((short)taskid.getBytes().length).getBytes(), 0, src, off, 2);                  off+=2;
        System.arraycopy(taskid.getBytes(), 0, src, off, taskid.getBytes().length);                               off+=taskid.getBytes().length;

       return new MTWPPacket(
                MTWPPacket.MARK,
                MTWPPacket.NAME,
                MTWPPacket.VERSION,
                code,
                src);
    }
    
    public static MTWPPacket  createAlarmPolicyPacket(byte code,String taskid,String testcode){
    	logger.debug("MTWPPacket 告警策略修改 taskid::"+taskid+"testcode::"+testcode);
        byte[] src = new byte[6 +taskid.getBytes().length+ testcode.getBytes().length];
    	int off = 0;
        System.arraycopy(new byte[]{(byte)0x01},0,src,off,1);                                                  off++;
        System.arraycopy(new WORD((short)taskid.getBytes().length).getBytes(), 0, src, off, 2);                off+=2;
        System.arraycopy(taskid.getBytes(), 0, src, off, taskid.getBytes().length);                            off+=taskid.getBytes().length;

        System.arraycopy(new byte[]{(byte)0x02},0,src,off,1);                                                  off+=1;
        System.arraycopy(new WORD((short)testcode.getBytes().length).getBytes(), 0, src, off, 2);              off+=2;
        System.arraycopy(testcode.getBytes(), 0, src, off, testcode.getBytes().length);                        off+=testcode.getBytes().length;

       return new MTWPPacket(
                MTWPPacket.MARK,
                MTWPPacket.NAME,
                MTWPPacket.VERSION,
                code,
                src);
    }
        
    public static MTWPPacket createPhonePacket(byte subCode, String[] phones){
        logger.debug("MTWPPacket 发送号码 subCode="+subCode + " Phone="+Arrays.toString(phones));
        int len;
        byte[] value;
        if(phones==null){
            len = 0;
            value = new byte[0];
        }else{
            len = phones.length;
            value = StringUtils.join(phones, " ").getBytes();
        }
        byte[] src = new byte[3 + value.length];
        int off = 0;
        System.arraycopy(new byte[]{subCode}, 0, src, off, 1);                     off+=1;
        System.arraycopy(new WORD((short)len).getBytes(), 0, src, off, 2);         off+=2;
        System.arraycopy(value, 0, src, off, value.length);
        
        return new MTWPPacket(
                MTWPPacket.MARK,
                MTWPPacket.NAME,
                MTWPPacket.VERSION,
                MTWPPacket.CODE_SCPHONE_SEND,
                src);
    }
    
    public static MTWPPacket  createNePacket(byte code,int neType,String neCode,String neName){
    	logger.debug("MTWPPacket 网元修改 neType::"+neType+"neCode::"+neCode+"neName::"+neName);
    	int off = 0;
        off+=3;
        byte[] b1_0 = new byte[]{0x08};
        byte[] b1 = new WORD((short)neType).getBytes();                                     off+=b1.length;
        off+=3;
        byte[] b2_0 = new byte[]{0x09};
        byte[] b2 = neCode.getBytes();                                                      off+=b2.length;
        off+=3;
        byte[] b3_0 = new byte[]{0x0a};
        byte[] b3 = neName.getBytes();                                                      off+=b3.length;

        
        byte[] value = new byte[off];
        off = 0;
        byte[] src = b1;
        System.arraycopy(b1_0, 0, value, off, 1);                                        off+=1;
        System.arraycopy(new WORD((short)src.length).getBytes(), 0, value, off, 2);      off+=2;
        System.arraycopy(src, 0, value, off, src.length);                                off+=src.length;
        src = b2;
        System.arraycopy(b2_0, 0, value, off, 1);                                        off+=1;
        System.arraycopy(new WORD((short)src.length).getBytes(), 0, value, off, 2);      off+=2;
        System.arraycopy(src, 0, value, off, src.length);                                off+=src.length;

        src = b3;
        System.arraycopy(b3_0, 0, value, off, 1);                                        off+=1;
        System.arraycopy(new WORD((short)src.length).getBytes(), 0, value, off, 2);      off+=2;
        System.arraycopy(src, 0, value, off, src.length);                                off+=src.length;

       return new MTWPPacket(
                MTWPPacket.MARK,
                MTWPPacket.NAME,
                MTWPPacket.VERSION,
                code,
                value);
    }
    
    
    public static MTWPPacket RegisterPacket(String key){
        logger.info("MTWPPacket 注册话单类型 Key="+key);
        int len=key.length();
        byte[] value=key.getBytes();
        byte[] src = new byte[3 + value.length];
        int off = 0;
        System.arraycopy(new byte[]{0x51}, 0, src, off, 1);                        off+=1;
        System.arraycopy(new WORD((short)len).getBytes(), 0, src, off, 2);         off+=2;
        System.arraycopy(value, 0, src, off, value.length);
        
        return new MTWPPacket(
                MTWPPacket.MARK,
                MTWPPacket.NAME,
                MTWPPacket.VERSION,
                MTWPPacket.CODE_REGISTER_KEY,
                src);
    }
    
    
    
    public static MTWPPacket  createMsgNePacket(byte code,String neIp,String neName,int doMain){
    	logger.debug("MTWPPacket 网元地址修改 neIp::"+neIp+"neName::"+neName+"doMain::"+doMain);
    	int off = 0;
        off+=3;
        byte[] b1_0 = new byte[]{0x01};
        byte[] b1 = neName.getBytes();                                                      off+=b1.length;
        off+=3;
        byte[] b2_0 = new byte[]{0x02};
        byte[] b2 = neIp.getBytes();                                                        off+=b2.length;
        off+=3;
        byte[] b3_0 = new byte[]{0x03};
        byte[] b3 =new DWORD((short)doMain).getBytes();                                      off+=b3.length;

        
        byte[] value = new byte[off];
        off = 0;
        byte[] src = b1;
        System.arraycopy(b1_0, 0, value, off, 1);                                        off+=1;
        System.arraycopy(new WORD((short)src.length).getBytes(), 0, value, off, 2);      off+=2;
        System.arraycopy(src, 0, value, off, src.length);                                off+=src.length;
        src = b2;
        System.arraycopy(b2_0, 0, value, off, 1);                                        off+=1;
        System.arraycopy(new WORD((short)src.length).getBytes(), 0, value, off, 2);      off+=2;
        System.arraycopy(src, 0, value, off, src.length);                                off+=src.length;

        src = b3;
        System.arraycopy(b3_0, 0, value, off, 1);                                        off+=1;
        System.arraycopy(new WORD((short)src.length).getBytes(), 0, value, off, 2);      off+=2;
        System.arraycopy(src, 0, value, off, src.length);                                off+=src.length;

       return new MTWPPacket(
                MTWPPacket.MARK,
                MTWPPacket.NAME2,
                MTWPPacket.VERSION,
                code,
                value);
    }
    
    public static MTWPPacket  createNeLinkPacket(byte code,String neName,String linkNeName,int neType,int doMain){
    	logger.debug("MTWPPacket 网元地址修改 neName::"+neName+"linkNeName::"+linkNeName+"doMain::"+doMain+"neType::"+neType);
    	int off = 0;
        off+=3;
        byte[] b1_0 = new byte[]{0x01};
        byte[] b1 = neName.getBytes();                                                      off+=b1.length;
        off+=3;
        byte[] b2_0 = new byte[]{0x07};
        byte[] b2 = linkNeName.getBytes();                                                  off+=b2.length;
        off+=3;
        byte[] b3_0 = new byte[]{0x08};
        byte[] b3 =new DWORD((short)neType).getBytes();                                     off+=b3.length;
        off+=3;
        byte[] b4_0 = new byte[]{0x03};
        byte[] b4 =new DWORD((short)doMain).getBytes();                                     off+=b4.length;

        
        byte[] value = new byte[off];
        off = 0;
        byte[] src = b1;
        System.arraycopy(b1_0, 0, value, off, 1);                                        off+=1;
        System.arraycopy(new WORD((short)src.length).getBytes(), 0, value, off, 2);      off+=2;
        System.arraycopy(src, 0, value, off, src.length);                                off+=src.length;
        src = b2;
        System.arraycopy(b2_0, 0, value, off, 1);                                        off+=1;
        System.arraycopy(new WORD((short)src.length).getBytes(), 0, value, off, 2);      off+=2;
        System.arraycopy(src, 0, value, off, src.length);                                off+=src.length;
        src = b3;
        System.arraycopy(b3_0, 0, value, off, 1);                                        off+=1;
        System.arraycopy(new WORD((short)src.length).getBytes(), 0, value, off, 2);      off+=2;
        System.arraycopy(src, 0, value, off, src.length);                                off+=src.length;
        src = b4;
        System.arraycopy(b4_0, 0, value, off, 1);                                        off+=1;
        System.arraycopy(new WORD((short)src.length).getBytes(), 0, value, off, 2);      off+=2;
        System.arraycopy(src, 0, value, off, src.length);                                off+=src.length;


       return new MTWPPacket(
                MTWPPacket.MARK,
                MTWPPacket.NAME2,
                MTWPPacket.VERSION,
                code,
                value);
    }
}
