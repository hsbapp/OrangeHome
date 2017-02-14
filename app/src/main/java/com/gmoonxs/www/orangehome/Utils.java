package com.gmoonxs.www.orangehome;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 威 on 2016/7/15.
 */
public class Utils {
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

}
