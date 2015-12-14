package cn.byodwork.utility;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utility {
	
    private static Logger logger = LoggerFactory.getLogger(Utility.class);
    
    
    public static boolean isValidJson(String json) {
	if (StringUtils.isBlank(json)) {
            return false;
	}
	try {
            new JsonParser().parse(json);
            return true;
	} catch (JsonParseException e) {
            return false;
	}
    }
    
	/**
	 * 获取当前时间 yyyyMMddHHmmss
	 * @return String
	 */ 
	public static String getCurrTime() {
		Date now = new Date();
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String s = outFormat.format(now);
		return s;
	}
	
	/**
	 * 获取当前日期 yyyyMMdd
	 * @param date
	 * @return String
	 */
	public static String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String strDate = formatter.format(date);
		return strDate;
	}
        
        /**
	 * 时间转换成字符串
	 * @param date 时间
	 * @param formatType 格式化类型
	 * @return String
	 */
	public static String date2String(Date date, String formatType) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatType);
		return sdf.format(date);
	}
        
        /**
	 * 获取unix时间，从1970-01-01 00:00:00开始的秒数
	 * @param date
	 * @return long
	 */
	public static long getUnixTime(Date date) {
            if( null == date ) {
		return 0;
            }		
            return date.getTime()/1000;
	}
        
        public void timeCmp() {
    	
            //SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");    
            //String str = "2010-08-09 ";
            //系统当前时间
            Calendar calCur = Calendar.getInstance();
        
            Calendar calOld = Calendar.getInstance();
            calOld.add(Calendar.DAY_OF_MONTH, -1);
        
            if(  calCur.getTime().compareTo(calOld.getTime()) > 0 ) {     	
        	calOld = calCur;
            }      
        }
	
        
        
	/**
	 * 取出一个指定长度大小的随机正整数.
	 * 
	 * @param length
	 *            int 设定所取出随机数的长度。length小于11
	 * @return int 返回生成的随机数。
	 */
	public static int buildRandom(int length) {
		int num = 1;
		double random = Math.random();
		if (random < 0.1) {
			random = random + 0.1;
		}
		for (int i = 0; i < length; i++) {
			num = num * 10;
		}
		return (int) ((random * num));
	}
		
        
	//-------MD5 -------------------------------------
	
	private static String byteArrayToHexString(byte b[]) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
			resultSb.append(byteToHexString(b[i]));

		return resultSb.toString();
	}

        private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
                
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static String MD5Encode(String origin, String charsetname) {
            String resultString = null;
            try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			if (charsetname == null || "".equals(charsetname))
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes()));
			else
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes(charsetname)));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("MD5Encode:"+e.getMessage());
            }
            
            return resultString;
	}


    public static String createXML(Map<String, Object> map){
	String xml = "<xml>";
	Set<String> set = map.keySet();
	Iterator<String> i = set.iterator();
        
	while(i.hasNext()){
            String str = i.next();
            xml+="<"+str+">"+"<![CDATA["+map.get(str)+"]]>"+"</"+str+">";
	}
        
	xml+="</xml>";		
	return xml;
    }
	
    public static Map<String,String> paserXML(String xmlStr)  {
		 Map<String, String> mapXML = new HashMap<String, String>();
	       
		//创建SAXReader对象  
       //SAXReader reader = new SAXReader();  
       //读取文件 转换成Document  
       //org.dom4j.Document  document = reader.read(new File("src/cn/com/yy/dom4j/s.xml")); 
              
       //获取根节点元素对象  
       /*
   	Element employees = document.getRootElement();
           for (Iterator i = employees.elementIterator(); i.hasNext();) {
               Element employee = (Element) i.next();
               for (Iterator j = employee.elementIterator(); j.hasNext();) {
                   Element node = (Element) j.next();
                   System.out.println(node.getName() + ":" + node.getText());
               }
           }  
        */
                 
        try {
            org.dom4j.Document document = DocumentHelper.parseText(xmlStr);
            org.dom4j.Element employees = document.getRootElement();
            for (Iterator i = employees.elementIterator(); i.hasNext();) {   	
                	org.dom4j.Element node = (org.dom4j.Element) i.next();
               	
               	mapXML.put(node.getName(), node.getText());
                   //System.out.println(node.getName() + ":" + node.getText());
            }
           
	} catch (Exception e) {
            e.printStackTrace();
            logger.error("paserXML:"+e.getMessage());
	}	
          
	return mapXML;
    }
	
           
        /*
    
        public static String QRfromGoogle(String chl)
	    {
	        int widhtHeight = 300;
	        String EC_level = "L";
	        int margin = 0;
	        String QRfromGoogle;
	        
	        try{
	        chl = URLEncoder.encode(chl, "UTF-8");
	        }
	        catch(Exception e) {
				e.printStackTrace();
			}        
	        QRfromGoogle = "http://chart.apis.google.com/chart?chs=" + widhtHeight + "x" + widhtHeight + "&cht=qr&chld=" + EC_level + "|" + margin + "&chl=" + chl;
	       
	        return QRfromGoogle;
	}
    
	public void wechatMoneyRandom(String tm, String tp, String mm) {
		
		double total_money; // 红包总金额
		int total_people; // 抢红包总人数
		double min_money; // 每个人最少能收到0.01元

		//total_money = 10.0;
		//total_people = 8;
		//min_money = 0.01;
		total_money = Double.parseDouble(tm);
		total_people = Integer.parseInt(tp);
		min_money = Double.parseDouble(mm);

		for (int i = 0; i < total_people - 1; i++) {
			int j = i + 1;
			double safe_money = (total_money - (total_people - j) * min_money)
					/ (total_people - j);
			double tmp_money = (Math.random()
					* (safe_money * 100 - min_money * 100) + min_money * 100) / 100;
			total_money = total_money - tmp_money;
			//out.format("第 %d 个红包： %.2f 元，剩下： %.2f 元\n<br/>", j, tmp_money,total_money);
		}
		//out.format("第 %d 个红包： %.2f 元，剩下： 0 元\n<br/>", total_people,total_money);
	}

	
	public void wechatMoneyRandom2(String tm, String tp, String mm) {

			int number= Integer.parseInt(tp);
			float total=Float.parseFloat(tm);
			double min= Double.parseDouble(mm);
		
		float money; 
		double max; 
		int i=1; 
		 
		List math=new ArrayList(); 
		while(i<number) 
		{ 
		 
		max = total- min*(number- i); 
		int k = (int)((number-i)/2); 
		if (number -i <= 2) 
		{k = number -i;} 
		max = max/k; 
		money=(int)(min*100+Math.random()*(max*100-min*100+1)); 
		money=(float)money/100; 
		total=total-money; 
		math.add(money); 
		//out.println("第"+i+"个人拿到"+money+"剩下"+total+"<br/>"); 
		i++; 
		if(i==number) 
		{ 
		math.add(total); 
		//out.println("第"+i+"个人拿到"+total+"剩下0"+"<br/>"); 
		} 
		
		} 
		 
		//out.println("本轮发红包中第"+(math.indexOf(Collections.max(math))+1)+"个人手气最佳"+"<br/>"); 
	} 
        */



}
