
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class LaserMachine {

    public static void main(String args[]) throws IOException {
        //为了简单起见，所有的异常信息都往外抛
        int port = 9001;
        //定义一个ServerSocket监听在端口8899上
        ServerSocket server = new ServerSocket(port);
        while (true) {
            //server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
            Socket socket = server.accept();
			socket.setSoTimeout(10000);
            //每接收到一个Socket就建立一个新的线程来处理它
            new Thread(new Task(socket)).start();
        }
    }

    /**
     * 用来处理Socket请求的
     */
    static class Task implements Runnable {

        private Socket socket;
		private String imageName = "";

        public Task(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                handleSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 跟客户端Socket进行通信
         * @throws Exception
         */
        private void handleSocket() throws Exception {
		
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());
			while(true) {
				byte readbyte[] = new byte[256];
				int n= input.read(readbyte);
				
				System.out.println("receive n=" + n);
				if( n < 1 )break;
				
				String ret = new String(readbyte);
				ret = ret.substring(0,n);
				System.out.println("receive :" + ret);
				
				Thread.sleep(3000);
				
				String cmdStr = "\u0002"+"$Initialize_"+"hi"+"\u0003";
				try {
					String afterStr = ret.substring(ret.indexOf('_')+1);
					String preStr = ret.substring(1, ret.indexOf('_'));
					System.out.println("preStr=" + preStr+ " afterStr="+afterStr);
					String outStr = "";
					if("$Initialize".equals(preStr) ) {
						outStr = "\u0002"+"$OK_"+afterStr;
						out.write(outStr.getBytes());
					}
					else if ("$Data".equals(preStr) ) {
						outStr = "\u0002"+"$OK_"+afterStr;
						imageName = afterStr.substring(0, afterStr.length()-1);
						out.write(outStr.getBytes());
					}
					else if ("$MarkStartA".equals(preStr) ) {
						outStr = "\u0002"+"$OK_"+"MarkStartA"+"\u0003";
						out.write(outStr.getBytes());
						Thread.sleep(8000);
						String outStr2 = "\u0002"+"$Mark_"+imageName+"\u0003";
						out.write(outStr2.getBytes());
					}
					else if ("$MarkStartB".equals(preStr) ) {
						outStr = "\u0002"+"$OK_"+"MarkStartA"+"\u0003";
						out.write(outStr.getBytes());
						Thread.sleep(8000);
						String outStr2 = "\u0002"+"$Mark_"+imageName+"\u0003";
						out.write(outStr2.getBytes());	
					}
					else if ("$Close".equals(preStr) ) {
						outStr = "\u0002"+"$OK_"+"Close"+"\u0003";
						out.write(outStr.getBytes());
					}
					else if ("$GetStatus".equals(preStr) ) {
						outStr = "\u0002"+"$Status_Ready"+"\u0003";
						out.write(outStr.getBytes());
					}						
					
				}
				catch (Exception e) {
					System.out.println("write error: "+e.getMessage());
					break;
				}
			}
			System.out.println("Now close socket");
			input.close();
			out.close();
			socket.close();
        }
    }
}
