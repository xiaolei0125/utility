/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.byodwork.utility;

import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author FrankieXiao
 */
public class GeoMapUtil {
    private static double EARTH_RADIUS = 6371;
    private static Logger logger = LoggerFactory.getLogger(GeoMapUtil.class);
    
        /**
	 *计算某个经纬度的周围某段距离的正方形的四个点
	 *
	 *@param lng float 经度
	 *@param lat float 纬度
	 *@param distance float 该点所在圆的半径，该圆与此正方形内切，默认值为0.5千米
	 *@return array 正方形的四个点的经纬度坐标
	 * 默认地球半径
	 */
		 
	/**
	 * 计算经纬度点对应正方形4个点的坐标
	 *
	 * @param longitude
	 * @param latitude
	 * @param distance
	 * @return
	 */
	public static Map<String, double[]> returnLLSquarePoint(double longitude,
	        double latitude, double distance) {
	    Map<String, double[]> squareMap = new HashMap<String, double[]>();
	    // 计算经度弧度,从弧度转换为角度
	    double dLongitude = 2 * (Math.asin(Math.sin(distance
	            / (2 * EARTH_RADIUS))
	            / Math.cos(Math.toRadians(latitude))));
	    dLongitude = Math.toDegrees(dLongitude);
	    // 计算纬度角度
	    double dLatitude = distance / EARTH_RADIUS;
	    dLatitude = Math.toDegrees(dLatitude);
	    // 正方形
	    double[] leftTopPoint = { latitude + dLatitude, longitude - dLongitude };
	    double[] rightTopPoint = { latitude + dLatitude, longitude + dLongitude };
	    double[] leftBottomPoint = { latitude - dLatitude,
	            longitude - dLongitude };
	    double[] rightBottomPoint = { latitude - dLatitude,
	            longitude + dLongitude };
	    squareMap.put("leftTopPoint", leftTopPoint);
	    squareMap.put("rightTopPoint", rightTopPoint);
	    squareMap.put("leftBottomPoint", leftBottomPoint);
	    squareMap.put("rightBottomPoint", rightBottomPoint);
	    return squareMap;
	}

	public static boolean checkInGeoScope(double centerLng, double centerLat, double distance, double lng, double lat) {		
            Map<String, double[]> squareMap;
            
            squareMap = returnLLSquarePoint( centerLng, centerLat, distance); 		
            double[] leftTopPoint = squareMap.get("leftTopPoint");
            double[] rightTopPoint = squareMap.get("rightTopPoint");
            double[] leftBottomPoint = squareMap.get("leftBottomPoint");
            double[] rightBottomPoint = squareMap.get("rightBottomPoint");
	    
            if( (lng < rightTopPoint[1]) && (lng > leftTopPoint[1]) 
		&& (lat > leftBottomPoint[0]) && (lat < leftTopPoint[0])) {			
		return true;			
            }		
            return false;
	}
	
	public static Object getBDGeoCoordFromGPS(String lng, String lat) {
		String res = "";
		String mySta = "fail";
		String BDlng = "null";
		String BDlat = "null";
		JSONObject jo = new JSONObject();
		
		//String url = "http://api.map.baidu.com/geoconv/v1/?ak=D36fbece5cc372f8c3ee7fc7ccfed69e&from=1&to=5"
		//		+"&coords=114.21892734521,29.575429778924";
 		//114.2189,29.5754";               
		String url = "http://api.map.baidu.com/geoconv/v1/?ak=piCl9wFNZm9mvFqyG8j1e4Cp&from=1&to=5&coords="+lng+","+lat;  
            
		/* Post Request
                System.out.println(new HttpRequestor().doPost("http://localhost:8080/OneHttpServer/", dataMap));
                */
                
		HttpRequester httpReq = new HttpRequester();
		try {			
			res = httpReq.doGet(url);      
			
			JSONObject jb = JSONObject.fromObject(res);
			String status = jb.getString("status");		
			if( status.equals("0") ){
				JSONArray ja = jb.getJSONArray("result");
				BDlng = ja.getJSONObject(0).getString("x");
				BDlat = ja.getJSONObject(0).getString("y");
				mySta = "success";		
			}
			else {
                            mySta = "fail";
                            logger.error("getBDGeoCoordFromGPS errpr:"+res);
			}
					
		} catch (Exception e) {
                    e.printStackTrace();
                    logger.error("getBDGeoCoordFromGPS error:"+e.getMessage());
		} 	
		
		jo.put("status", mySta);	
		jo.put("lng", BDlng);
		jo.put("lat", BDlat);

		//logger.debug("getBDGeoCoordFromGPS return:"+jo.toString());		
                return jo;
	}
	
//http://api.map.baidu.com/geoconv/v1/?ak=piCl9wFNZm9mvFqyG8j1e4Cp&from=1&to=5&coords=114.386505,30.511227
//{"status":0,"result":[{"x":114.39858124696,"y":30.51489113986}]}
//http://api.map.baidu.com/geocoder/v2/?ak=piCl9wFNZm9mvFqyG8j1e4Cp&location=30.5148,114.3985&output=json&pois=0
		
	public static Object getBDGeoAddrFromGPS(String lng, String lat) {
		String res = "";
		String mySta = "fail";
		String formatted_address = "null";
		String BDlng = "null";
		String BDlat = "null";
		HttpRequester httpReq = new HttpRequester();
		JSONObject joOut = new JSONObject();
		
		JSONObject joIn = (JSONObject)getBDGeoCoordFromGPS(lng, lat);		
		if( joIn.getString("status").equals("success") ) {
			BDlng = joIn.getString("lng");
			BDlat = joIn.getString("lat");
				
                    try {  
			String url = "http://api.map.baidu.com/geocoder/v2/?ak=piCl9wFNZm9mvFqyG8j1e4Cp&location="
					+BDlat+","+BDlng+"&output=json&pois=0";
					//+"30.511227,114.386505&output=json&pois=0";
			
			res = httpReq.doGet(url);
					
			JSONObject jb2 = JSONObject.fromObject(res);
			String status = jb2.getString("status");
			if( status.equals("0") ) {
				JSONObject jbresult = jb2.getJSONObject("result");
				formatted_address = jbresult.getString("formatted_address");
				//String y1 = ja2.getJSONObject(0).getString("y");	
				mySta = "success";
			}
			else{				
				mySta = "fail";
			}
			
			} catch (Exception e) {
                            e.printStackTrace();
                            logger.error("getBDGeoAddrFromGPS error:"+e.getMessage());
			} 	
		}
		
		joOut.put("status", mySta);	
		joOut.put("formatted_address", formatted_address);

		//logger.debug("getBDGeoAddrFromGPS return: "+joOut.toString());		
		return joOut;
	}
        
        /*
        boolean ret =  checkInGeoScope("114.3892", "30.512852", "1", lng,lat);
        boolean isInScope =  geo.checkInGeoScope(getConfigValue("centerLng"), getConfigValue("centerLat"), getConfigValue("centerDistance"), su.longitude, su.latitude);
		if(isInScope){
			su.geoStatus = "报告厅";
		}else {
			su.geoStatus = "不在报告厅";
		}
		
      
		 JSONObject joAddr = (JSONObject)geo.getBDGeoAddrFromGPS(su.longitude, su.latitude);
		 if ( joAddr.getString("status").equals("success") ) {
			 su.address = joAddr.getString("formatted_address");
		 }else {
			 su.address = "未知";
		 }              
        */
        
}
