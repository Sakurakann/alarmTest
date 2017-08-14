package com.bill.packet;

import java.util.Arrays;

import com.gs.packet.BytesUtil;
import com.gs.packet.Packet;
import com.gs.packet.WORD;

public class MTWPPacket implements Packet{

    private static final long serialVersionUID = -5350297965874380926L;
    
    /*包头*/
    final public static int MTWP_MARK_SIZE = 2;
    public final static byte[] MARK = new byte[]{(byte)0x5D,(byte)0x13}; //帧头标志
    public final static byte NAME = (byte)0x01; //协议名称   
    public final static byte NAME2 = (byte)0xb1; //协议名称   
    public final static byte VERSION = (byte)0x10; //版本号1.0
    /*命令*/
    public final static Byte CODE_HEART = (byte)0x00;
    
    public final static Byte CODE_REGISTER_TESTCODE = (byte)0xFF;          //注册测试方式
    public final static Byte CODE_REGISTER_KEY = (byte)0xEF;               //注册话单格式
    
    public final static Byte CODE_TASK_TEST_SEND = (byte)0x01;             //任务下发命令
    public final static Byte CODE_TASK_STATUS_GET = (byte)0x02;            //任务状态查询
    public final static Byte CODE_TASK_STATUS_REPORT = (byte)0x03;         //任务状态上报
    public final static Byte CODE_TASK_STATUS_CONTROL = (byte)0x04;        //任务状态控制
    public final static Byte CODE_TASK_BILL_SQL = (byte)0x05;              //任务话单SQL
    public final static Byte CODE_TASK_TEST_RESULT = (byte)0x06;           //任务话单分析结果
    public final static Byte CODE_TASK_TEST_RESULT_SQL = (byte)0x0b;       //任务话单分析结果sQL
    public final static Byte CODE_TEST_RESULT_JT = (byte)0xDF;            //JTsvr话单XML格式
    
    public final static Byte CODE_SMGW_REQ = (byte)0x07;                   //短信网关数据请求
    public final static Byte CODE_SMGW_SEND = (byte)0x08;                  //短信网关数据
    
    public final static Byte CODE_SCPHONE_REQ = (byte)0x09;                //短信中心号段数据请求
    public final static Byte CODE_SCPHONE_SEND = (byte)0x0a;               //短信中心号段数据
    
    public final static Byte CODE_SMC_PHONE_REQ = (byte)0x0c;                //短信转发号码数据请求
    public final static Byte CODE_SMC_PHONE_SEND = (byte)0x0d;               //短信转发号码数据
    
    public final static Byte CODE_GRPSWEB_SEND = (byte)0x11;			   //gprs手机上网发送命令	09-08-04 liyue
    public final static Byte CODE_GRPSWEB_RCV = (byte)0x12;  			   //gprs手机上网接受页面数据   09-08-04 liyue

    public final static Byte CODE_MSISDN_QUERY  = (byte)0x13;             //号码属性请求
    public final static Byte CODE_MSISDN_DETAIL_SEND  = (byte)0x14;       //号码属性发送
    public final static Byte CODE_MSISDN_DETAIL_ADD  = (byte)0x15;       //增加号码属性
    public final static Byte CODE_MSISDN_DETAIL_EDIT  = (byte)0x16;       //修改号码属性
    public final static Byte CODE_MSISDN_DETAIL_DEL  = (byte)0x17;       //删除号码属性
    public final static Byte REQUEST_SQL_UPDATE  = (byte)0x18;           //请求变更号码状态
    public final static Byte CODE_IMS_QUERY  = (byte)0x19;               //请求IMS拥护属性状态
    
    
    public final static Byte CODE_IMSUA_QUERY  = (byte)0x20;             //IMS用户信息请求
    public final static Byte CODE_IMSUA_DETAIL_SEND  = (byte)0x21;       //IMS终端用户请求的响应
    public final static Byte CODE_IMSUA_DETAIL_ADD  = (byte)0x22;       //新增IMS终端用户发送消息
    public final static Byte CODE_IMSUA_DETAIL_EDIT  = (byte)0x23;       //IMS终端用户修改发送消息
    public final static Byte CODE_IMSUA_DETAIL_DEL  = (byte)0x24;       //IMS终端用户删除发送消息

    
    public final static Byte PHONE_QUERY  = (byte)0x50;                  //请求告警号码
    public final static Byte PHONE_DETAIL_SEND  = (byte)0x51;            //请求告警号码响应
    public final static Byte QUERY_TASKCODE  = (byte)0x55;               //任务测试类型请求
    public final static Byte QUERY_TASKCODE_SEND  = (byte)0x56; 
    
    public final static Byte SEND_DATA_STATUS  = (byte)0x60;//发送任务状态
    public final static Byte JS_BILL_FILE_RESPONSE= (byte)0x61;//信令文件上报
    public final static Byte JS_NUMBER_REQUEST =(byte)0x62;//请求号码信息
    public final static Byte JS_NUMBER_RESPONSE =(byte)0x63;//返回号码信息
    
    public final static Byte UPDATE_ALARMPOLICY_TASK =(byte)0x65;//告警策略下发
    
    
    public final static Byte GPRSRESOURCE_QUERY =(byte)0x66;//GPRS资源请求
    public final static Byte GPRSRESOURCE_SEND =(byte)0x67;//GPRS资源发送
    
    
    public final static Byte CODE_GPRSRESOURCE_ADD  = (byte)0x68;       //增加号码属性
    public final static Byte CODE_GPRSRESOURCE_EDIT  = (byte)0x69;       //修改号码属性
    public final static Byte CODE_GPRSRESOURCE_DEL  = (byte)0x70; 
    
    
    public final static Byte RNC_REQUEST  = (byte)0x80;             //请求RNC资源信息
    public final static Byte RNC_RESPONSE_SEND  = (byte)0x81;       //RNC资源请求响应
    public final static Byte RNC_RESOUCE_ADD  = (byte)0x82;       //新增RNC资源
    public final static Byte RNC_RESOUCE_EDIT  = (byte)0x83;       //修改RNC资源
    public final static Byte RNC_RESOUCE_DEL  = (byte)0x84;       //删除RNC资源
    
    public final static Byte TASK_REQUEST  = (byte)0x65;       // 请求任务消息
    public final static Byte TASK_RESPONSE  = (byte)0x66;       // 请求任务响应
  
    public final static Byte NE_DETAIL_ADD  = (byte)0x6a;       //新增网元
    public final static Byte NE_DETAIL_EDIT  = (byte)0x6b;       //修改网元消息
    public final static Byte NE_DETAIL_DEL  = (byte)0x6c;       //删除网元消息
    
    
    public final static Byte JS_MSG_QUERY_NE=(byte)0x6d ;             //路径探测程序向SAP程序请求网元信息消息
    public final static Byte JS_MSG_ADD_NE=(byte)0x6e;                //SAP程序向路径探测程序下发添加网元信息消息
    public final static Byte JS_MSG_DEL_NE=(byte)0x6f;                 //SAP程序向路径探测程序下发删除网元信息消息
    public final static Byte JS_MSG_ROUTER_INFO=(byte)0x70;           //SAP程序向路径探测程序下发删除网元信息消息
    public final static Byte JS_MSG_ADD_NECON_INFO=(byte)0x71;        //添加网元连接关系信息	
    public final static Byte JS_MSG_DEL_NECON_INFO=(byte)0x72;        //删除网元连接关系信息
    
    //任务测试类型请求返回
    /*子命令*/
    public final static Byte SUBCODE_RELOAD = 0; //全部清除后更新
    public final static Byte SUBCODE_ADD = 1; //添加
    public final static Byte SUBCODE_DEL = 2; //删除
    public final static Byte SUBCODE_OVER = 3;//更新结束

    public final static Byte ENDPACKET_YES = 1; //尾包
    public final static Byte ENDPACKET_NO = 0; //不是尾包
    
    /*结果*/
    public final static short NORMAL = 0;
    public final static short EXCEPTION = 1;
    public final static short TIMEOUT = 2;
    public final static short FAILURE = 3;  
    /*包体*/  
    
    private MTWPHead    head = null;
    private byte[]      value = null;

    public MTWPPacket(byte[] mark,
                      byte name,
                      byte version,
                      byte code,
                      WORD length,
                      byte[] value){
        this.head = new MTWPHead(mark, name, version, code, length);
        this.value = value;
    }

    public MTWPPacket(byte[] mark,
                      byte name,
                      byte version,
                      byte code,
                      byte[] value){
        this.head = new MTWPHead(mark,
                                 name,
                                 version,
                                 code,
                                 new WORD((short)(value==null?0:value.length)));
        this.value = value;
    }

    public MTWPPacket(byte[] src){
        int off = 0;
        this.head = new MTWPHead(src);                                    off+=MTWPHead.MTWP_HEAD_SIZE;
        value = BytesUtil.GetBytes(src,off,head.getLength());             off+=head.getLength();
    }

    public byte[] buffer() {
        byte[] buf = new byte[head.getLength()+MTWPHead.MTWP_HEAD_SIZE];
        System.arraycopy(head.buffer(), 0, buf, 0, MTWPHead.MTWP_HEAD_SIZE);
        if (value != null) System.arraycopy(value, 0, buf, MTWPHead.MTWP_HEAD_SIZE, head.getLength());
        return buf;
    }

    public void read(byte[] arg0) {
        // TODO Auto-generated method stub
        
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (this.head != null) sb.append(head.toString());
        if (this.value != null) sb.append(Arrays.toString(value));
        return sb.toString();
    }

    public byte getCode() {
        return head.getCode();
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }
}
