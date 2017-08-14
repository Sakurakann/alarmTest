package com.bill.packet;

import com.gs.packet.BytesUtil;
import com.gs.packet.Packet;
import com.gs.packet.WORD;

public class MTWPHead implements Packet{

    private static final long serialVersionUID = 4000838991159677610L;

    final public static int MTWP_HEAD_SIZE = 7;
    
    private byte[]  mark = null; //帧头标志
    private Byte    name = null; //协议名称
    private Byte    version = null; //版本号
    private Byte    code = null; //指令
    private WORD    length = new WORD((short)0);

    public MTWPHead(byte[] mark,
                    byte   name,
                    byte   version,
                    byte   code,
                    WORD   length){
        this.mark = mark;
        this.name = name;
        this.version = version;
        this.code = code;
        this.length = length;
    }

    public MTWPHead(byte[] src){
        int off = 0;
        mark = BytesUtil.GetBytes(src,off,2);                                           off+=2;
        name = src[off];                                                                off+=1;
        version = src[off];                                                             off+=1;
        code = src[off];                                                                off+=1;
        if(off<src.length) length = new WORD(BytesUtil.GetBytes(src,off,2));            off+=2;
    }

    public byte[] getMark() {
        return mark;
    }

    public void setMark(byte[] mark) {
        this.mark = mark;
    }

    public Byte getName() {
        return name;
    }

    public void setName(Byte name) {
        this.name = name;
    }

    public Byte getVersion() {
        return version;
    }

    public void setVersion(Byte version) {
        this.version = version;
    }

    public Byte getCode() {
        return code;
    }

    public void setCode(Byte code) {
        this.code = code;
    }

    public int getLength() {
        return length.toIntValue();
    }

    public void setLength(WORD length) {
        this.length = length;
    }

    public byte[] buffer() {
        byte[] buf = new byte[MTWP_HEAD_SIZE];
        int off = 0;
        if (mark != null) System.arraycopy(mark, 0, buf, off, 2);                           off+=2;
        if (name != null) System.arraycopy(new byte[]{name}, 0, buf, off, 1);               off+=1;
        if (version != null) System.arraycopy(new byte[]{version}, 0, buf, off, 1);         off+=1;
        if (code != null) System.arraycopy(new byte[]{code}, 0, buf, off, 1);               off+=1;
        if (length != null) System.arraycopy(length.getBytes(), 0, buf, off, 2);            off+=2;
        return buf;
    }

    public void read(byte[] arg0) {
        // TODO Auto-generated method stub
        
    }

    public String toString(){
        StringBuffer sb = new StringBuffer();
        if(this.mark!=null) sb.append("mark=").append(new String(mark)).append(",");
        if(this.name!=null) sb.append("name=").append(name).append(",");
        if(this.version!=null) sb.append("version=").append(version).append(",");
        if(this.code!=null) sb.append("code=").append(code).append(",");
        if(this.length!=null) sb.append("length=").append(length.toIntValue()).append(",");
        return sb.toString();
    }
}
