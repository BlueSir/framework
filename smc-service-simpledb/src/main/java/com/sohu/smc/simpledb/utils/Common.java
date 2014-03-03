package com.sohu.smc.simpledb.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: huixiao200068
 * Date: 13-1-6
 * Time: 上午11:52
 * To change this template use File | Settings | File Templates.
 */
public class Common {

    public static Map<String, Character> map = new HashMap<String, Character>();
    private static Map<String, String> cacheOfDate36Digit = new HashMap<String, String>();

    static {
        String string = "abcdefghijklmnopqrstuvwxyz";
        for(int i=0; i<string.length(); i++) {
            map.put(String.valueOf(i+10), string.charAt(i));
        }
    }

    public static String strTo36Digit(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        try{
            if(str != null) {
                String result = cacheOfDate36Digit.get(str);
                if(result != null) return result;
                else {
                    String temp;
                    int num = 0;
                    for(int i=0; i<str.length(); i=i+2) {
                        temp = str.substring(i,i+2);
                        if(StringUtils.isNumeric(temp)) {
                            num = Integer.parseInt(temp);
                        }
                        Character c = get36Digit(num);
                        stringBuffer.append(c == null ? "" : c);
                    }
                    cacheOfDate36Digit.put(str, stringBuffer.toString());
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return stringBuffer.toString();
    }

    public static Character get36Digit(int num) {
        Character result = null;
        if(num >=0 && num <= 9) return Character.forDigit(num, 10);
        else if(num < 0) return result;
        else {
            return map.get(String.valueOf(num));
        }
    }

    public static boolean allNotNull(Object[] objects) {
        boolean flag = true;
        if(objects == null) flag = false;
        else{
            for(Object obj : objects) {
                if(obj == null) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }
}
