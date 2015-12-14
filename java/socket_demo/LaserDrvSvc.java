

import cn.byodwork.orm.base.HlPrintMachineState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by frankie on 2015-11-6.
 */

public class LaserDrvSvc {

    private static  String IP_ADDR = "127.0.0.1";
    private static  int PORT = 9001;
    private static int readTimeout = 10*1000;
    private Socket socket = null;
    DataOutputStream out = null;
    DataInputStream input = null;

    public String queryIp() {
        return IP_ADDR;
    }
    public int queryPort() {
        return PORT;
    }

    public boolean config(String ipAddr, int port){
        IP_ADDR = ipAddr;
        PORT = port;
        System.out.println("config set ip=" + IP_ADDR + " port=" + PORT);
        return true;
    }

    public boolean configIp(String ipAddr){
        IP_ADDR = ipAddr;
        System.out.println("configIp set ip=" + IP_ADDR );
        return true;
    }

    public boolean configPort(int port){
        PORT = port;
        System.out.println("configPort set port=" + PORT);
        return true;
    }

    public String checkMachineState() {
        if(!connectLaserSer()) {
            return HlPrintMachineState.connet_error;
        }
        String state = getLaserPrinterStatus();
        disconnectLaserSer();
        return state;
    }

    public Map<String, String> runLaserPrintTask(String templateName,String trajectoryType,String imageName) {
        Map<String, String> retMap = new HashMap<String, String>();
        retMap.put("status","fail");

        if(!connectLaserSer()) {
            retMap.put("errType",HlPrintMachineState.connet_error);
            return retMap;
        }

        if(!initLaserCmd(templateName)) {
            retMap.put("errType",HlPrintMachineState.init_error);
            disconnectLaserSer();
            return retMap;
        }

        if(!sendLaserData(imageName)) {
            retMap.put("errType",HlPrintMachineState.sendData_error);
            disconnectLaserSer();
            return retMap;
        }

        if(!startLaserPrint(trajectoryType, imageName)) {
            retMap.put("errType",HlPrintMachineState.startPrinter_error);
            disconnectLaserSer();
            return retMap;
        }

        if(!closeLaserPrint()) {
            retMap.put("errType",HlPrintMachineState.closePrinter_error);
            disconnectLaserSer();
            return retMap;
        }

        disconnectLaserSer();

        retMap.put("errType", HlPrintMachineState.no_error);
        retMap.put("status","success");
        return retMap;
    }

    public boolean connectLaserSer() {
        try {
            socket = new Socket(IP_ADDR, PORT);
            socket.setSoTimeout(readTimeout);
            logger.debug("connectLaserSer success: ip=" + IP_ADDR + " port=" + PORT);
            out = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
        }
        catch (SocketException se) {
            System.out.println("connectLaserSer socket error:" + se.getMessage());
            return false;
        }
        catch (Exception e) {
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
        }
        catch (SocketException se) {
            System.out.println("disconnectLaserSer socket error:" + se.getMessage());
            return false;
        }
        catch (Exception e) {
                System.out.println("disconnectLaserSer error: " + e.getMessage());
                return false;
        }
        return true;
    }

    public String getLaserPrinterStatus(){
        try {
            String cmdStr = "\u0002"+"$GetStatus_"+"\u0003";
            out.write(cmdStr.getBytes());

            byte readbyte[] = new byte[128];
            int n = input.read(readbyte);
            String ret = new String(readbyte);
            ret = ret.substring(0,n);
            logger.debug("getLaserPrinterStatus receive laser cmd:" + ret);

            String markingStr = "\u0002"+"$Status_Marking"+"\u0003";
            String readyStr = "\u0002"+"$Status_Ready"+"\u0003";
            if(markingStr.equals(ret)) {
                return HlPrintMachineState.marking;
            }
            else if(readyStr.equals(ret)) {
                return HlPrintMachineState.ready;
            }
            else {
                System.out.println("getLaserPrinterStatus fail");
                return HlPrintMachineState.machine_error;
            }
        }
        catch (SocketException se) {
            System.out.println("initLaserCmd socket error:" + se.getMessage());
            return HlPrintMachineState.connet_error;
        }
        catch (Exception e) {
            System.out.println("initLaserCmd error:" + e.getMessage());
            return HlPrintMachineState.connet_error;
        }
    }

    public boolean initLaserCmd(String templateName){
        try {
            String cmdStr = "\u0002"+"$Initialize_"+templateName+"\u0003";
            out.write(cmdStr.getBytes());

            byte readbyte[] = new byte[128];
            int n = input.read(readbyte);

            String ret = new String(readbyte);
            ret = ret.substring(0,n);
            logger.debug("initLaserCmd receive laser cmd:" + ret);

            String okStr = "\u0002"+"$OK_"+templateName+"\u0003";
            if(okStr.equals(ret)) {
                return true;
            }
            else {
                System.out.println("initLaserCmd fail");
                return false;
            }
        }
        catch (SocketException se) {
            System.out.println("initLaserCmd socket error:" + se.getMessage());
            return false;
        }
        catch (Exception e) {
            System.out.println("initLaserCmd error:" + e.getMessage());
            return false;
        }
    }

    public boolean sendLaserData(String imageName){
        try {
            String cmdStr = "\u0002"+"$Data_"+imageName+"\u0003";
            out.write(cmdStr.getBytes());

            byte readbyte[] = new byte[128];
            int n = input.read(readbyte);

            String ret = new String(readbyte);
            ret = ret.substring(0,n);
            logger.debug("sendLaserData receive laser cmd:" + ret);

            String okStr = "\u0002"+"$OK_"+imageName+"\u0003";
            if(okStr.equals(ret)) {
                return true;
            }
            else {
                System.out.println("sendLaserData fail");
                return false;
            }

        } catch (SocketException se) {
            System.out.println("sendLaserData socket error:" + se.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("sendLaserData error:" + e.getMessage());
            return false;
        }
    }

    public boolean startLaserPrint(String trajectoryType, String imageName){
        try {
            String cmdStr = "\u0002"+"$MarkStart"+ trajectoryType +"_"+"\u0003";
            out.write(cmdStr.getBytes());

            byte readbyte[] = new byte[128];
            int n = input.read(readbyte);

            String ret = new String(readbyte);
            ret = ret.substring(0,n);
            logger.debug("startLaserPrint receive laser cmd:" + ret);

            String okStr = "\u0002"+"$OK_MarkStart"+trajectoryType+"\u0003";
            if(!okStr.equals(ret)) {
                System.out.println("startLaserPrint fail");
                return false;
            }

            //Wait Notify from Printer when task is finished
            System.out.println("startLaserPrint waiting notify...");
            byte notifyByte[] = new byte[128];
            n = input.read(readbyte);

            String notifyStr = new String(notifyByte);
            ret = ret.substring(0,n);
            logger.debug("startLaserPrint receive notify:" + notifyStr);

            String notifyOkStr = "\u0002"+"$Mark_"+imageName+"\u0003";
            if(okStr.equals(ret)) {
                return true;
            }
            else {
                System.out.println("sendLaserData receive notify fail");
                return false;
            }
        }
        catch (SocketException se) {
            System.out.println("startLaserPrint socket error:" + se.getMessage());
            return false;
        }
        catch (Exception e) {
            System.out.println("startLaserPrint error:" + e.getMessage());
            return false;
        }
    }

    public boolean closeLaserPrint(){
        try {
            String cmdStr = "\u0002"+"$Close_"+"\u0003";
            out.write(cmdStr.getBytes());

            byte readbyte[] = new byte[128];
            int n = input.read(readbyte);

            String ret = new String(readbyte);
            ret = ret.substring(0,n);
            logger.debug("closeLaserPrint receive laser cmd:" + ret);

            String okStr = "\u0002"+"$OK_Close"+"\u0003";
            if(okStr.equals(ret)) {
                return true;
            }
            else {
                System.out.println("closeLaserPrint fail");
                return false;
            }
        }
        catch (SocketException se) {
            System.out.println("closeLaserPrint socket error:" + se.getMessage());
            return false;
        }
        catch (Exception e) {
            System.out.println("closeLaserPrint error:" + e.getMessage());
            return false;
        }
    }

    
    public static void main(String[] args){
        System.out.println("Hello World!");
        String c="\u0002";//ETX
        byte[] buf=c.getBytes();
        System.out.println(buf[0]);
        String cmdStr = c+"Init"+"\u0003";
        System.out.println(cmdStr);

      	//配置大族打印机的IP和端口
        laserDrvSvc.init("127.0.0.1",9001);
		//配置要打印的模板名和图片文件名
        Map<String, String> retMap = laserDrvSvc.runLaserPrintTask("mytemp", "A", "myimage.jpg");
		System.out.println("LaserDrvSvc Print Result="
			+retMap.get("status")+" errType="+retMap.get("errType") );

    }
    

}
