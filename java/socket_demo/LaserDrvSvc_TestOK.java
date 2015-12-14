
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by frankie on 2015-11-11.
 */

public class LaserDrvSvc {

    private static  String IP_ADDR = "localhost";
    private static  int PORT = 12345;
    private Socket socket = null;
    DataOutputStream out = null;
    DataInputStream input = null;

	public static String asciiToString(String value)
	{
		StringBuffer sbu = new StringBuffer();
		String[] chars = value.split(",");
		for (int i = 0; i < chars.length; i++) {
			sbu.append((char) Integer.parseInt(chars[i]));
		}
		return sbu.toString();
	}
	
	public static void main(String[] args){
        System.out.println("LaserDrvSvc is running");    
        LaserDrvSvc laserDrvSvc = new LaserDrvSvc();
		
		//配置大族打印机的IP和端口
        laserDrvSvc.init("112.74.76.96", 808);
		//配置要打印的模板名和图片文件名
        Map<String, String> retMap = laserDrvSvc.runLaserPrintTask("mytemp", "myimage.jpg");
		
		System.out.println("LaserDrvSvc Print Result="
			+retMap.get("status")+" errType="+retMap.get("errType") );
		laserDrvSvc.disconnectLaserSer();
    }
	
	
    public void init(String ipAddr, int port){
        IP_ADDR = ipAddr;
        PORT = port;
        System.out.println("init set ip=" + IP_ADDR + " port=" + PORT);
    }

    public Map<String, String> runLaserPrintTask(String templateName,String imageName) {
        Map<String, String> retMap = new HashMap<String, String>();
        retMap.put("status","fail");
		retMap.put("errType","unknown");
		
        if(!connectLaserSer()) {
            retMap.put("errType","connect");
            return retMap;
        }

        if(!initLaserCmd(templateName)) {
            retMap.put("errType","init");
            return retMap;
        }

        if(!sendLaserData(imageName)) {
            retMap.put("errType","send");
            return retMap;
        }

        if(!startLaserPrint()) {
            retMap.put("errType","print");
            return retMap;
        }

        if(!closeLaserPrint()) {
            retMap.put("errType","close");
            return retMap;
        }

        retMap.put("status","success");

        if(!disconnectLaserSer()) {
            retMap.put("errType","disconnect");
            return retMap;
        }

        return retMap;
    }

    public boolean connectLaserSer() {
        try {
            socket = new Socket(IP_ADDR, PORT);
            System.out.println("connectLaserSer success: ip=" + IP_ADDR + " port=" + PORT);

            out = new DataOutputStream(socket.getOutputStream());

            input = new DataInputStream(socket.getInputStream());

        } catch (Exception e) {
            System.out.println("connectLaserSer error: "+e.getMessage());
            return false;
        }
        return true;
    }

    public boolean disconnectLaserSer() {

        try {
            if(out != null) {
                out.close();
                out = null;
            }

            if(input != null) {
                input.close();
                input = null;
            }

            if (socket != null) {
                socket.close();
                System.out.println("disconnectLaserSer success");
                socket = null;
            }
        } catch (Exception e) {
                System.out.println("disconnectLaserSer error: " + e.getMessage());
                return false;
        }

        return true;
    }

    public boolean initLaserCmd(String templateName){
        System.out.println("initLaserCmd enter templateName="+templateName);
        try {

			String cmdStr = "\u0002"+"$Initialize_"+templateName+"\u0003";
            out.write(cmdStr.getBytes());
			byte readbyte[] = new byte[256];
            int n= input.read(readbyte);
			String ret = new String(readbyte);
 
            System.out.println("initLaserCmd receive laser cmd:" + ret);

            String okStr = "\u0002"+"$OK_"+templateName+"\u0003";
            if(okStr.equals(ret)) {
                System.out.println("initLaserCmd success");
                return true;
            }
            else {
                System.out.println("initLaserCmd fail");
                //return false;
                return true;
            }

        } catch (Exception e) {
            System.out.println("initLaserCmd error:" + e.getMessage());
            return false;
        }
    }

    public boolean sendLaserData(String imageName){
        System.out.println("sendLaserData enter imageName="+imageName);
        try {
			
            String cmdStr = "\u0002"+"$Data_"+imageName+"\u0003";
            out.write(cmdStr.getBytes());
			byte readbyte[] = new byte[256];
            int n= input.read(readbyte);
			String ret = new String(readbyte);
			
            System.out.println("sendLaserData receive laser cmd:"+ret);

            String okStr = "\u0002"+"$OK_"+imageName+"\u0003";
            if(okStr.equals(ret)) {
                System.out.println("sendLaserData success");
                return true;
            }
            else {
                System.out.println("sendLaserData fail");
                //return false;
                return true;
            }

        } catch (Exception e) {
            System.out.println("sendLaserData error:" + e.getMessage());
            return false;
        }
    }

    public boolean startLaserPrint(){
        System.out.println("sendLaserData enter");
        try {

            String cmdStr = "\u0002"+"$MarkStart_"+"\u0003";
            out.write(cmdStr.getBytes());
			byte readbyte[] = new byte[256];
            int n= input.read(readbyte);
			String ret = new String(readbyte);
            System.out.println("startLaserPrint receive laser cmd:" + ret);

            if(ret.contains("$OK _MarkStart")) {
                System.out.println("startLaserPrint success");
                return true;
            }
            else {
                System.out.println("startLaserPrint fail");
                //return false;
                return true;
            }

        } catch (Exception e) {
            System.out.println("startLaserPrint error:" + e.getMessage());
            return false;
        }
    }

    public boolean closeLaserPrint(){
        System.out.println("closeLaserPrint enter");
        try {

            String cmdStr = "\u0002"+"$Close_"+"\u0003";
            out.write(cmdStr.getBytes());
			byte readbyte[] = new byte[256];
            int n= input.read(readbyte);
			String ret = new String(readbyte);
            System.out.println("closeLaserPrint receive laser cmd:"+ret);

            String okStr = "\u0002"+"$OK_Close"+"\u0003";
            if(okStr.equals(ret)) {
                System.out.println("closeLaserPrint success");
                return true;
            }
            else {
                System.out.println("closeLaserPrint fail");
                //return false;
                return true;
            }

        } catch (Exception e) {
            System.out.println("closeLaserPrint error:" + e.getMessage());
            return false;
        }
    }

	/*
	public static void main(String[] args){
        System.out.println("Hello World!");
        String c="\u0002";//ETX
        byte[] buf=c.getBytes();
        System.out.println(buf[0]);
        String cmdStr = c+"Init"+"\u0003";
        System.out.println(cmdStr);
    }
    */
}
