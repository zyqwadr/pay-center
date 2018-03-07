package xyz.nesting.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import xyz.nesting.common.client.ErrorCode;
import xyz.nesting.common.exceptions.BusinessException;
import xyz.nesting.common.hash.MD5;

/**
 * 数据签名加密工具
 * @author nesting
 *
 */
public class EncryptionUtil {

	/**
	 * 获取签名
	 * @param map
	 * @param salt
	 * @return
	 */
	public static String getSign(Map<String,Object> map, String salt){
        ArrayList<String> list = new ArrayList<String>();
        for(Map.Entry<String,Object> entry:map.entrySet()){
        		if (entry.getKey().equals("sign")) {
				continue;
			}
            if(entry.getValue()!=""){
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "salt=" + salt;
        System.out.println(result);
        result = MD5.MD5Encode(result).toUpperCase();
        System.out.println("sign:" + result);
        return result;
    }
	 
	/**
	 * 获取签名
	 * @param obj
	 * @param salt
	 * @return
	 */
	public static String getSign(Object obj, String salt) {
		Map<String, Object> map = objectToMapUtil(obj);
		return getSign(map, salt);
	}

	/**
	 * 验证签名
	 * @param map
	 * @param salt
	 * @return
	 */
	public static boolean validateSign(Map<String, Object> map, String salt) {
		ArrayList<String> list = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getKey().equals("sign")) {
				continue;
			}
			if (entry.getValue() != "") {
				list.add(entry.getKey() + "=" + entry.getValue() + "&");
			}
		}
		int size = list.size();
		String[] arrayToSort = list.toArray(new String[size]);
		Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(arrayToSort[i]);
		}
		String result = sb.toString();
		result += "salt=" + salt;
		System.out.println(result);
		result = MD5.MD5Encode(result).toUpperCase();
		System.out.println("sign:" + result);
		return map.get("sign").equals(result);
	}
	
	/**
	 * 验证签名
	 * @param obj
	 * @param salt
	 * @return
	 */
	public static boolean validateSign(Object obj, String salt) {
		Map<String, Object> map = objectToMapUtil(obj);
		return validateSign(map, salt);
	}

	/**
	 * 对象map转换
	 * @param obj
	 * @return
	 */
	private static Map<String, Object> objectToMapUtil(Object obj) {
	    Map<String,Object> reMap = new HashMap<String,Object>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for(int i = 0; i < fields.length; i++) {
            Field subField;
			try {
				subField = obj.getClass().getDeclaredField(fields[i].getName());
	            subField.setAccessible(true);
	            reMap.put(fields[i].getName(), subField.get(obj));
			} catch (Exception e) {
				BusinessException.error(ErrorCode.FAILED	, "objectToMapUtil fail");
			}
        }
        return reMap;
	}
}
