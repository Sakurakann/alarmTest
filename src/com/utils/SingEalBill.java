package com.utils;

public class SingEalBill {
//0-15 0x0-
	public static final int TASKID = 0x02;
	public static final int TESTCODE = 0x03;
	public static final int PROGID = 0x04;
	public static final int CALLTYPE = 0x05;
	public static final int CALLER = 0x06;
	public static final int IMEI = 0x07;
	public static final int IMSI = 0x08;
	public static final int CALLERHOMEAREA = 0x09;
	public static final int CALLERVISITAREA = 0x0a;
	public static final int CALLED = 0x0b;
	public static final int CALLEDHOMEAREA = 0x0c;
	public static final int CALLEDVISITAREA = 0x0d;
	public static final int STARTTIME = 0x0e;
	public static final int ENDTIME = 0x0f;

//	16-31 0x1-
	public static final int TASKEXETIME = 0x10;
	public static final int DURATION = 0x11;
	public static final int TALKSTATUS = 0x12;
	public static final int ENBUES1APID = 0x13;
	public static final int FAILURECAUSE = 0x14;
	public static final int DETAILCAUSE = 0x15;
	public static final int FAILURECODE = 0x16;
	public static final int FAILURECODEDESCRIBE = 0x17;
	public static final int SIMEQUIPMENAME = 0x18;
	public static final int ATTACHSTARTTIME = 0x19;
	public static final int ATTACHENDTIME = 0x1a;
	public static final int ATTACHDELAY = 0x1b;
	public static final int ATTACHRESULT = 0x1c;
	public static final int AUTHSTARTTIME = 0x1d;
	public static final int AUTHENDTIME = 0x1e;
	public static final int AUTHDELAY = 0x1f;

//	32-47 0x2-
	public static final int AUTHRESULT = 0x20;
	public static final int SECURITYSTARTTIME = 0x21;
	public static final int SECURITYHENDTIME = 0x22;
	public static final int SECURITYDELAY = 0x23;
	public static final int SECURITYRESULT = 0x24;
	public static final int IDENTITYSTARTTIME = 0x25;
	public static final int IDENTITYHENDTIME = 0x26;
	public static final int IDENTITYDELAY = 0x27;
	public static final int IDENTITYRESULT = 0x28;
	public static final int ACTIVATIONADDR = 0x29;
	public static final int DEFAULTPGWMANE = 0x2a;
	public static final int DNSADDR = 0x2b;
	public static final int SECONDDNSADDR = 0x2c;
	public static final int ATTACHLAC = 0x2d;
	public static final int ATTACHTAC = 0x2e;
	public static final int ATTACHCELLID = 0x2f;

//  48-63 0x3-
	public static final int MMECODE = 0x30;
	public static final int MMEGROUPID = 0x31;
	public static final int ATTACHMTMSI = 0x32;
	public static final int ATTACHPTMSI = 0x33;
	public static final int ATTACHMMENAME = 0x34;
	public static final int ATTACHMMEADDR = 0x35;
	public static final int DEFBEAERSTRATTIME = 0x36;
	public static final int DEFBEAERENDTIME = 0x37;
	public static final int DEFBEAERDELAY = 0x38;
	public static final int DEFBEAERRESULT = 0x39;
	public static final int DEFBEAERLOCALADDR = 0x3a;
	public static final int DEFBEAERLOCALPORT = 0x3b;
	public static final int DEFBEAERREMOTEADDR = 0x3c;
	public static final int DEFBEAERREMOTEPORT = 0x3d;
	public static final int DEFBEAERSGWNAME = 0x3e;
	public static final int DEFBEAERQCI = 0x3f;

//	64-79 0x4-
	public static final int QOSDELAYCLASS = 0x40;
	public static final int QOSRELIABILITYCLASS = 0x41;
	public static final int QOSPEAKTHROUGHPUT = 0x42;
	public static final int QOSPRECEDENCECLASS = 0x43;
	public static final int VDEFBEAERSTRATTIME = 0x44;
	public static final int VDEFBEAERENDTIME = 0x45;
	public static final int VDEFBEAERDELAY = 0x46;
	public static final int VDEFBEAERRESULT = 0x47;
	public static final int VDEFBEAERLOCALADDR = 0x48;
	public static final int VDEFBEAERLOCALPORT = 0x49;
	public static final int VDEFBEAERREMOTEADDR = 0x4a;
	public static final int VDEFBEAERREMOTEPORT = 0x4b;
	public static final int VDEFBEAERACTIVATIONADDR = 0x4c;
	public static final int VDEFBEAERQCI = 0x4d;
	public static final int VPCSCFADDR = 0x4e;
	public static final int VPCSCFNAME = 0x4f;

//	80-95 0x5-
	public static final int IMSQOSDELAYCLASS = 0x50;
	public static final int IMSQOSRELIABILITYCLASS = 0x51;
	public static final int IMSQOSPEAKTHROUGHPUT = 0x52;
	public static final int IMSQOSPRECEDENCECLASS = 0x53;
	public static final int RELDEFBEAERSTRATTIME = 0x54;
	public static final int RELDEFBEAERENDTIME = 0x55;
	public static final int RELDEFBEAERDELAY = 0x56;
	public static final int RELDEFBEAERRESULT = 0x57;
	public static final int DEDBEAERSTRATTIME = 0x58;
	public static final int DEDBEAERENDTIME = 0x59;
	public static final int DEDBEAERDELAY = 0x5a;
	public static final int DEDBEAERRESULT = 0x5b;
	public static final int DEDBEAERLOCALADDR = 0x5c;
	public static final int DEDBEAERLOCALPORT = 0x5d;
	public static final int DEDBEAERREMOTEADDR = 0x5e;
	public static final int DEDBEAERREMOTEPORT = 0x5f;

//	96-111 0x6-
	public static final int DEDBEAERSGWNAME = 0x60;
	public static final int DEDBEAERQCI = 0x61;
	public static final int VOICEQOSDELAYCLASS =0x62;
	public static final int VOICEQOSRELIABILITYCLASS = 0x63;
	public static final int VOICEQOSPEAKTHROUGHPUT = 0x64;
	public static final int VOICEQOSPRECEDENCECLASS = 0x65;
	public static final int VOICERABMAXIMUMBITRATEDL = 0x66;
	public static final int VOICERABMAXIMUMBITRATEUL = 0x67;
	public static final int VOICERABGUARANTEEDBITRATEDL = 0x68;
	public static final int VOICERABGUARANTEEDBITRATEUL = 0x69;
	public static final int RELDEDBEAERSTRATTIME = 0x6a;
	public static final int RELDEDBEAERENDTIME = 0x6b;
	public static final int RELDEDBEAERDELAY = 0x6c;
	public static final int RELDEDBEAERRESULT = 0x6d;
	public static final int VDEDBEAERSTRATTIME = 0x6e;
	public static final int VDEDBEAERENDTIME = 0x6f;

//	112-127 0x7-
	public static final int VDEDBEAERDELAY = 0x70;
	public static final int VDEDBEAERRESULT = 0x71;
	public static final int VDEDBEAERLOCALADDR = 0x72;
	public static final int VDEDBEAERLOCALPORT = 0x73;
	public static final int VDEDBEAERREMOTEADDR = 0x74;
	public static final int VDEDBEAERREMOTEPORT = 0x75;
	public static final int VDEDBEAERSGWNAME = 0x76;
	public static final int VDEDBEAERQCI = 0x77;
	public static final int VIDEOQOSDELAYCLASS = 0x78;
	public static final int VIDEOQOSRELIABILITYCLASS = 0x79;
	public static final int VIDEOQOSPEAKTHROUGHPUT = 0x7a;
	public static final int VIDEOQOSPRECEDENCECLASS = 0x7b;
	public static final int VIDEORABMAXIMUMBITRATEDL = 0x7c;
	public static final int VIDEORABMAXIMUMBITRATEUL = 0x7d;
	public static final int VIDEORABGUARANTEEDBITRATEDL = 0x7e;
	public static final int VIDEORABGUARANTEEDBITRATEUL = 0x7f;

//	128-143 0x8-
	public static final int RELVDEDBEAERSTRATTIME = 0x80;
	public static final int RELVDEDBEAERENDTIME = 0x81;
	public static final int RELVDEDBEAERDELAY = 0x82;
	public static final int RELVDEDBEAERRESULT = 0x83;
	public static final int DETTACHSTRATTIME = 0x84;
	public static final int DETTACHENDTIME = 0x85;
	public static final int DETTACHDELAY = 0x86;
	public static final int DETTACHRESULT = 0x87;
	public static final int TAUSTARTTIME = 0x88;
	public static final int TAUENDTIME = 0x89;
	public static final int TAUSTARTDELAY = 0x8a;
	public static final int TAUSTARTRESULT = 0x8b;
	public static final int TAULAC = 0x8c;
	public static final int TAUTAC = 0x8d;
	public static final int TAUCELLID = 0x8e;
	public static final int TAUMMECODE = 0x8f;

//	144-159 0x9-
	public static final int TAUMMEGROUPID = 0x90;
	public static final int TAUMTMSI = 0x91;
	public static final int TAUPTMSI = 0x92;
	public static final int TAUMMENAME = 0x93;
	public static final int TAUMMEADDR = 0x94;
	public static final int TAUACTIVATIONADDR = 0x95;
	public static final int TAUSGWADDR = 0x96;
	public static final int TAUSGWPORT = 0x97;
	public static final int TAUSGWNAME = 0x98;
	public static final int TAUPGWNAME = 0x99;
	public static final int TAULOCALADDR = 0x9a;
	public static final int TAULOCALAPORT = 0x9b;
	public static final int REGISTERSTARTTIME = 0x9c;
	public static final int REGISTERENDTIME = 0x9d;
	public static final int REGISTERDELAY = 0x9e;
	public static final int REGISTERESULT = 0x9f;

//	160-175 0xa-
	public static final int FIRSTREGISTERSTARTTIME = 0xa0;
	public static final int FIRSTREGISTERENDTIME = 0xa1;
	public static final int FIRSTREGISTERDELAY = 0xa2;
	public static final int FIRSTREGISTERESULT = 0xa3;
	public static final int SECONDREGISTERSTARTTIME = 0xa4;
	public static final int SECONDREGISTERENDTIME = 0xa5;
	public static final int SECONDREGISTERDELAY = 0xa6;
	public static final int SECONDREGISTERESULT = 0xa7;
	public static final int PRECODITIONSTARTTIME = 0xa8;
	public static final int PRECODITIONENDTIME = 0xa9;
	public static final int PRECODITIONTIME = 0xaa;
	public static final int PRECODITIONRESULT = 0xab;
	public static final int DEGISTERSTARTTIME = 0xac;
	public static final int DEGISTERENDTIME = 0xad;
	public static final int DEGISTERDELAY = 0xae;
	public static final int DEGISTERESULT = 0xaf;

//	176-191 0xb-
	public static final int VOLTECALLSTARTTIME = 0xb0;
	public static final int VOLTECALLENDTIME = 0xb1;
	public static final int VOLTECALLDELAY = 0xb2;
	public static final int VOLTECALLRESULT = 0xb3;
	public static final int VOLTECANCELSTARTTIME = 0xb4;
	public static final int VOLTECANCELENDTIME = 0xb5;
	public static final int VOLTECANCELDELAY = 0xb6;
	public static final int VOLTECANCELRESULT = 0xb7;
	public static final int VOLTELOCALVOICEIP = 0xb8;
	public static final int VOLTELOCALVOICEPORT = 0xb9;
	public static final int VOLTEREMOTEVOICEIP = 0xba;
	public static final int VOLTEREMOTEVOICEPORT = 0xbb;
	public static final int VOLTLOCALVIDEOIP = 0xbc;
	public static final int VOLTLOCALVIDEOPORT = 0xbd;
	public static final int VOLTEREMOTEVIDEOIP = 0xbe;
	public static final int VOLTEREMOTEVIDEOPORT = 0xbf;

//	192-207 0xc-
	public static final int MEDIANAME = 0xc0;
	public static final int RECORDNAME = 0xc1;
	public static final int CAPTUREFILENAME = 0xc2;
	public static final int PSVIRTUALIP = 0xc3;
	public static final int CSVIRTUALIP = 0xc4;
	public static final int MEDIACODE = 0xc5;
	public static final int DTMFTYPE = 0xc6;
	public static final int DTMFFORMAT = 0xc7;
	public static final int SENDDTMF = 0xc8;
	public static final int RECVDTMF = 0xc9;
	public static final int HOSTARTTIME = 0xca;
	public static final int HOENDTIME = 0xcb;
	public static final int HODELAY = 0xcc;
	public static final int HORESULT = 0xcd;
	public static final int HOMEDIASTARTTIME = 0xce;
	public static final int HOMEDIAENDTIME = 0xcf;

//	208-223 0xd-
	public static final int HOMEDIADELAY = 0xd0;
	public static final int HOMEDIARESULT = 0xd1;
	public static final int SMSSTARTTIME = 0xd2;
	public static final int SMSENDTIME = 0xd3;
	public static final int SMSTIME = 0xd4;
	public static final int SMSRESULT = 0xd5;
	public static final int SMSMOCONTENT = 0xd6;
	public static final int SMSCENTERADDR = 0xd7;
	public static final int SMSCENTERNAME = 0xd8;
	public static final int SIGNALFLOW = 0xd9;
	public static final int REDO = 0xda;
	public static final int RESERVEDSTARTTIME_1 = 0xdb;
	public static final int RESERVEDSTR_1 = 0xdc;
	public static final int RESERVEDENDETIME_1 = 0xdd;
	public static final int RESERVEDTIME_1 = 0xde;
	public static final int RESERVEDRESULT_1 = 0xdf;

//	224-239 0xe-
	public static final int RESERVEDSHORTSTR_1 = 0xe0;


}
