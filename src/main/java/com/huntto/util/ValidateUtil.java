package com.huntto.util;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ValidateUtil {
	//身份证校验用
	private final static char[] ID_CDS = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
	private final static int[] ID_PATTERNS = {7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2};

	public static boolean isEmpty(Object obj){
		if(obj instanceof String){
			return (obj == null || "".equals(obj));
		}
		return (obj == null);
	}

	public static boolean isDate8(String str){
		if(str == null || str.length() != 8) return false;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			df.parse(str);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isDate14(String str){
		if(str == null || str.length() != 14) return false;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			df.parse(str);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isLen(String str, int expectedLen) {
		return (str != null && str.length()==expectedLen);
	}

	public static boolean isLen(String str, int max, int min) {
		return (str != null && str.length()>=min && str.length()<=max);
	}

	public static boolean isValue(long value, long maxValue, long minValue) {
		return (value>=minValue && value<=maxValue);
	}

	public static boolean contains(String expected, String src) {
		return (expected != null && expected.contains(src));
	}

	public static boolean contains(String[] expected, String src) {
		for(int i=0; i<expected.length; i++){
			if(expected[i].equals(src)) return true;
		}
		return false;
	}

	public static boolean eq(String src, String expected) {
		return (expected != null && expected.equals(src));
	}

	public static boolean isInt(String value) {
		try {
			new Integer(value);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isLong(String value) {
		try {
			new Long(value);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 身份证号码校验
	 * @param value
	 * @return
	 */
	public static boolean isIdCard01(String value) {
		if(isEmpty(value)) return false;
		if(value.length()==15) return isIdCard01_15(value);
		if(value.length()==18) return isIdCard01_18(value);
		return false;
	}
	
	/**
	 * 身份证号码校验
	 * @param value
	 * @return
	 */
	public static boolean isIdCard01AndNotNull(String value) {
		if(value.length()==15) return isIdCard01_15(value);
		if(value.length()==18) return isIdCard01_18(value);
		return false;
	}
	
	/**
	 * 15位身份证号码校验
	 * @param value
	 * @return
	 */
	public static boolean isIdCard01_15(String value) {
		if(value == null || value.length() != 15) return false;

		//生日检查
		String birth = value.substring(6,12);
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");

		try {
			df.parse(birth);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	/**
	 * 18位身份证号码校验
	 * @param value
	 * @return
	 */
	public static boolean isIdCard01_18(String value) {
		if(value == null || value.length() != 18) return false;
		
		//生日检查
		String birth = value.substring(6,14);
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

		try {
			df.parse(birth);
		} catch (Exception e) {
			return false;
		}
		
		//验证码
		char cd = getIdCard01_18CD(value);
		
		return (cd == value.charAt(17));
	}

	/**
	 * 计算18位身份证校验码
	 * @param value
	 * @return
	 */
	public static char getIdCard01_18CD(String value) {
		int sum = 0;

		for(int i=0; i<17; i++){
			sum += Integer.parseInt(value.substring(i,i+1))*ID_PATTERNS[i];
		}
		
		int cd = sum % 11;
		return ID_CDS[cd];
	}
	
	/**
	 * 组织机构代码校验
	 * @param sOCode
	 * @return
	 */
	public static boolean validateOCode(String sOCode){
		sOCode = sOCode.toUpperCase();
		
		String[] ws = {"3", "7", "9", "10", "5", "8", "4", "2" };
		String str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String reg = "^([0-9A-Z]){8}-[0-9|X]$"; 
		if (!sOCode.matches(reg)) {
			return false;
		}
		int sum = 0;
		for ( int i = 0; i < 8; i++) {
			sum +=  str.indexOf(sOCode.charAt(i))* Integer.valueOf(ws[i]);
		}
		String c9 = String.valueOf(11 - (sum % 11));
		if("10".equals(c9)){
			c9 = "X";
		}else if("11".equals(c9)){
			c9="0";
		}
		return c9.equals(String.valueOf(sOCode.charAt(9)));
	}
	
	/**
	 * 手机号码校验
	 * @param phone
	 * @return
	 */
	public static boolean validatePhone(String phone){
		String REGEX_MOBILE = "^(1[3578]{1})\\d{9}$";
		String REGEX_PHONE = "^\\d{3,4}-?\\d{7,9}$";
		return Pattern.matches(REGEX_MOBILE,phone) || Pattern.matches(REGEX_PHONE,phone);
	}
	
	/**
	 * 邮编校验
	 * @param postcode
	 * @return
	 */
	public static boolean validatePOSTCODE(String postcode){
		String REGEX_POSTCODE = "^[1-9][0-9]{5}$";
		return Pattern.matches(REGEX_POSTCODE,postcode);
	}
	
	/**
	 * 传真
	 * @param fax
	 * @return
	 */
	public static boolean validateFax(String fax){
		String REGEX_FAX = "(^(\\d{3,4}-)?\\d{7,8}$)";
		return Pattern.matches(REGEX_FAX,fax);
	}
	
	/**
	 * 工商营业执照号
	 * @param value
	 * @return
	 */
	public static boolean isBusCodeAndNotNull(String value) {
		if(value.length()==15) return validateBusCode(value);
		if(value.length()==18) return validateCreditCode(value);
		return false;
	}
	/**
	 * 校验社会信用代码
	 * @param sCreditCode
	 * @return
	 */
	public static boolean validateCreditCode(String sCreditCode){
		sCreditCode = sCreditCode.toUpperCase();
		
		if (sCreditCode.length() != 18) {
			return false;
		}
		
		String baseCode = "0123456789ABCDEFGHJKLMNPQRTUWXY";
		char[] baseCodeArray = baseCode.toCharArray();
		Map<Character, Integer> codes = new HashMap<Character, Integer>();
		for(int i = 0; i < baseCode.length(); i++) {
			codes.put(baseCodeArray[i], i);
		}
		char [] businessCodeArray = sCreditCode.toCharArray();
	 	Character check = businessCodeArray[17];
		if (baseCode.indexOf(check)==-1) {
			return false;
		}
	 	int[] wi = { 1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};
	 	int sum = 0;
	 	for (int i = 0; i < 17; i++) {
	 		Character key = businessCodeArray[i];
	 		if (baseCode.indexOf(key)==-1) {
	 			return false;
	 		}
	 		sum += (codes.get(key) * wi[i]);
	 	}
	 	int value = 31 - sum % 31;
	 	if(value == 31){ value = 0;}
		return value == codes.get(check);
	}
	
	/**
	 * 工商营业执照15位校验
	 * @param busCode
	 * @return
	 */
	public static boolean validateBusCode(String busCode){
		busCode = busCode.toUpperCase();
		if(busCode.length()!=15){
			return false;
		}
		String busPrex14 = busCode.substring(0,14);// 获取营业执照注册号前14位数字用来计算校验码  
		String busPrex15 = busCode.substring(14,busCode.length());// 获取营业执照号的校验码
		char[] chars = busPrex14.toCharArray();  
		int[] ints = new int[chars.length]; 
		for (int i = 0; i < chars.length; i++) {
			ints[i] = Integer.parseInt(String.valueOf(chars[i]));
		}
		getCheckCode(ints);
		if(busPrex15.equals(getCheckCode(ints)+"")){// 比较 填写的营业执照注册号的校验码和计算的校验码是否一致  
			return  true;  
		}  
		return false;  
	}
	
	private static int getCheckCode(int[] ints) {
		if (null != ints && ints.length > 1) {
			int ti = 0;
			int si = 0;// pi|11+ti
			int cj = 0;// （si||10==0？10：si||10）*2
			int pj = 10;// pj=cj|11==0?10:cj|11
			for (int i = 0; i < ints.length; i++) {
				ti = ints[i];
				pj = (cj % 11) == 0 ? 10 : (cj % 11);
				si = pj + ti;
				cj = (0 == si % 10 ? 10 : si % 10) * 2;
				if (i == ints.length - 1) {
					pj = (cj % 11) == 0 ? 10 : (cj % 11);
					return pj == 1 ? 1 : 11 - pj;
				}
			}
		}
		return -1;
	}
}
