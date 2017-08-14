package com.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;


/**
 * 提供了字串替换、删除,数组拷贝、合并等等方法.
 */
@SuppressWarnings("deprecation")
public class StringUtils {

	private static final Logger log = Logger.getLogger(StringUtils.class);

	/**
	 * 替换所有字串.
	 * 
	 * @param oldStr
	 *            要进行替换的原字串
	 * 
	 * @param findStr
	 *            查找的字串
	 * 
	 * @param replStr
	 *            替换为的字串
	 * @return 替换后的字串
	 */
	public static String replace(String oldStr, String findStr, String replStr) {
		return repl(oldStr, findStr, replStr, 0);
	}

	/**
	 * 替换字串,指定替换次数.
	 * 
	 * @param oldStr
	 *            要进行替换的原字串
	 * 
	 * @param findStr
	 *            查找的字串
	 * 
	 * @param replStr
	 *            替换为的字串
	 * @param times
	 *            次数
	 * @return 替换后的字串
	 */
	public static String replace(String oldStr, String findStr, String replStr,
			int times) {
		return repl(oldStr, findStr, replStr, times);
	}

	/** 检查是否需要替换 */
	private static boolean isValid(String oldStr, String findStr, String replStr) {
		if (oldStr == null || oldStr.length() < 1 || findStr == null
				|| findStr.length() < 1 || replStr == null
				|| findStr.equals(replStr))
			return false;

		return true;
	}

	/**
	 * 替换实现.
	 * 
	 * @see replace(String oldStr,String findStr,String replStr,int times)
	 * @see replace(String oldStr,String findStr,String replStr)
	 */
	private static String repl(String oldStr, String findStr, String replStr,
			int times) {
		if (!isValid(oldStr, findStr, replStr))
			return oldStr;

		StringBuffer strBuff = new StringBuffer();

		if (times < 1) { // 替换所有

			if (findStr.length() == 1 && replStr.length() == 1) { // 单字符替换

				return oldStr.replace(findStr.charAt(0), replStr.charAt(0));
			} else { // 多字符替换

				for (int i = 0, len = oldStr.length(); i < len;) {
					int j = oldStr.indexOf(findStr, i);

					if (j >= 0) {// 找到要替换的字串
						strBuff = strBuff.append(oldStr.substring(i, j));
						strBuff = strBuff.append(replStr);
						i = j + findStr.length();
					} else {// 找不到要替换的字串

						strBuff = strBuff.append(oldStr.substring(i));
						break;
					}
				}
				return new String(strBuff);
			}
		} else { // 替换指定次数
			int i = 0;
			int len = oldStr.length();

			for (int k = 0; i < len && k < times;) {
				int j = oldStr.indexOf(findStr, i);

				if (j >= 0) {// 找到要替换的字串
					strBuff = strBuff.append(oldStr.substring(i, j));
					strBuff = strBuff.append(replStr);
					i = j + findStr.length();
					k++;
				} else {// 找不到要替换的字串

					strBuff = strBuff.append(oldStr.substring(i));
					i = len;
				}
			}

			if (i < len) { // 完成替换次数，但串中还有可替换的字串
				strBuff = strBuff.append(oldStr.substring(i));
			}

			return new String(strBuff);
		}
	}

	/**
	 * 从原字串中删除从开始字串到结束字串之间的字串,匹配指定次数.如 * 比如<br>
	 * <code>
	 *    String s = "abcd";
	 *    String t = deleteString("abcd","b","c",1);
	 *    t.equals("ad") (true)
	 * </code>
	 * </p>
	 * 
	 * @param oldStr
	 *            原字串.
	 * @param beginStr
	 *            开始字串.
	 * @param endStr
	 *            终止字串.
	 * @param times
	 *            删除指定次数,如小于1,立即返回;否则一直匹配到指定次数为止
	 * @return 删除后的字串.
	 */
	public static String deleteString(String oldStr, String beginStr,
			String endStr, int times) {
		if (times < 1)
			return oldStr;

		if (!isValidStr(oldStr))
			return oldStr;

		String retStr = oldStr;
		String tmpStr = delStr(oldStr, beginStr, endStr);
		while (times-- > 0 && !(retStr.equals(tmpStr))) {
			retStr = tmpStr;
			tmpStr = delStr(retStr, beginStr, endStr);
		}
		return retStr;
	}

	/**
	 * 从原字串中删除从开始字串到结束字串之间的字串,一直匹配到不可再匹配为止.如 * 比如<br>
	 * <code>
	 *    String s = "abcd";
	 *    String t = deleteString("abcdbcd","b","c");
	 *    t.equals("add") (true)
	 * </code>
	 * </p>
	 * 
	 * @param oldStr
	 *            原字串.
	 * @param beginStr
	 *            开始字串.
	 * @param endStr
	 *            终止字串.
	 * @return 删除后的字串.
	 */
	public static String deleteString(String oldStr, String beginStr,
			String endStr) {
		if (!isValidStr(oldStr))
			return oldStr;

		String retStr = oldStr;
		String tmpStr = delStr(oldStr, beginStr, endStr);
		while (!(retStr.equals(tmpStr))) {
			retStr = tmpStr;
			tmpStr = delStr(retStr, beginStr, endStr);
		}
		return retStr;
	}

	/**
	 * 获得不含路径的文件名.
	 * 
	 * @param fileName
	 *            文件名,可能含，也可能不含路径.为空/空串返回空串
	 * @return 不含路径的文件名.
	 */
	public static String getFileNameNoPath(String fileName) {
		fileName = trim(fileName);
		if (fileName.length() < 1)
			return fileName;

		String tmpStr = fileName;
		int idx1 = fileName.lastIndexOf("/");
		int idx2 = fileName.lastIndexOf("\\");
		idx1 = idx1 >= idx2 ? idx1 : idx2;
		tmpStr = fileName.substring(idx1 + 1);
		return tmpStr;
	}

	/**
	 * 检查是否空串或null.
	 */
	private static boolean isValidStr(String str) {
		if (str == null || str.length() < 1)
			return false;
		return true;
	}

	/**
	 * 删除字串的实现.
	 */
	private static String delStr(String oldStr, String beginStr, String endStr) {

		if (oldStr == null || oldStr.length() < 1 || beginStr == null
				|| beginStr.length() < 1 || endStr == null
				|| endStr.length() < 1)
			return oldStr;

		String retStr = oldStr;
		int i, j, k;
		i = oldStr.indexOf(beginStr);// find beginStr
		if (i >= 0) {
			j = i + beginStr.length();
			if (j < oldStr.length()) {
				k = oldStr.indexOf(endStr, j);// find endStr
				if (k >= 0) {
					k += endStr.length();
					retStr = oldStr.substring(0, i) + oldStr.substring(k);
				}
			}
		}

		return retStr;
	}

	/**
	 * 去掉字串两边空格.
	 * 
	 * @param s
	 *            原字串,如为null,返回空串.
	 * @return 去掉两边空格的字串
	 * 
	 */
	public static String trim(String s) {
		return s == null ? "" : s.trim();
	}



	/**
	 * 进行二维数组某一列字符串的替换.
	 * 
	 * 使用示例： String[][] string= new String[][]{{"string", "cying"},{"ss-strstd",
	 * "string"}, {"dir-str", ""},{"teacher",""}}; String[][] newStr =
	 * ReplaceString.replace(str, 0, "str", "cry"); 你将会得到这样的结果数组： newStr =
	 * {{"crying", "cying"},{"ss-crystd", "string"}, {"dir-cry",
	 * ""},{"teacher",""}};
	 * 
	 * @param str
	 *            要进行替换的字符串
	 * 
	 * @param columnIndex
	 *            要进行替换的字符串所在的列数 columnIndex从0开始
	 * 
	 * @param oldString
	 *            原字符串
	 * @param newString
	 *            新字符串
	 * @return 新的二维数组
	 * @author 张岭
	 */
	public static String[][] replace(String[][] str, int columnIndex,
			String oldString, String newString) {
		if (str == null)// 二位数组为空，返回原来的二位数组;
			return str;

		if (str.length < 1)// 数组长度小于1，返回原来的二位数组;
			return str;

		if (str[0].length < columnIndex + 1)// 数组的列数小于index + 1,返回原来的二位数组;
			return str;

		for (int i = 0; i < str.length; i++) {
			String replacedString = str[i][columnIndex];

			int replacedIndex = replacedString.indexOf(oldString) + 1;
			@SuppressWarnings("unused")
			int oldStringLength = oldString.length();

			System.out.println("replaceIndex = " + replacedIndex);

			if (replacedString.indexOf(oldString) == -1)
				continue;

			String first = replacedString.substring(0, replacedIndex - 1);
			String middle = newString;
			String end = replacedString.substring(replacedIndex - 1
					+ oldString.length());

			String newStrings = first + middle + end;

			str[i][columnIndex] = newStrings;
		}

		return str;
	}

	/**
	 * 根据传入的包名，获得相对的文件路径. 比如<br>
	 * <code>
	 *    String s = getRelativeDir("com.js.util");
	 *    s.equals("com/hj/util")
	 * </code>
	 * </p>
	 * 
	 * @param packageName
	 *            包名,packageName为null或一空串时，返回null
	 * @return String 与包名对应的相对路径
	 */
	public static String getRelativeDir(String packageName) {
		packageName = packageName == null ? "" : packageName.trim();

		if (packageName.length() < 1)
			return null;

		if (packageName.indexOf(".") >= 0) {
			return replace(packageName, ".", "/");
		} else
			return packageName;
	}

	/**
	 * 将字符串的第一个字母变大写.
	 * 
	 * @param s
	 *            要转变的字串
	 */
	public static String firstUpperCase(String s) {
		if (s == null || s.length() < 1)
			return s;

		String first = s.substring(0, 1);
		String other = s.substring(1);
		s = first.toUpperCase() + other;
		return s;
	}

	/**
	 * 将字符串的第一个字母变大写,其余是小写.
	 * 
	 * @param s
	 *            要转变的字串
	 * @return 将字符串的第一个字母变大写,其余是小写
	 * 
	 */
	public static String firstUpperCaseOnly(String s) {
		if (s == null || s.length() < 1)
			return s;

		return firstUpperCase(s.toLowerCase());
	}

	/**
	 * 将字符串的第一个字母变小写.
	 * 
	 * @param s
	 *            要转变的字串
	 */
	public static String firstLowerCase(String s) {
		if (s != null && s.length() > 0) {
			String first = s.substring(0, 1);
			String other = s.substring(1);
			s = first.toLowerCase() + other;
		}
		return s;
	}

	/**
	 * 判断指定的字符串是否是合法的Java标识符.
	 * 
	 * @param s
	 *            要判断的字符串串
	 * @return true,如果是Java的标识符; false，如果不是Java标识符或者是null.
	 */
	public static boolean isJavaIdentifier(String s) {
		if (s == null || "".equals(s))
			return false;
		char[] arr = s.toCharArray();
		if (!Character.isJavaIdentifierStart(arr[0]))
			return false;
		for (int i = 1, len = arr.length; i < len; i++) {
			if (!Character.isJavaIdentifierPart(arr[i]))
				return false;
		}
		return true;
	}

	/**
	 * 将源字符串数组中的指定范围中的元素拷贝到目标字符串数组中，注意：这里的拷贝是值拷贝。
	 * 
	 * 需要注意的是，如果任何下面的情况出现了，会抛出空指针异常（NullPointerException）， 但源数组与目标都保持不便：
	 * 
	 * 如果dst为null； 如果src为null.
	 * 如果任何下面的情况出现了，会抛出数组越界异常（IndexOutOfBoundsException），而 源数组与目标数组保持不变：
	 * 
	 * 如果srcOffset为负数； 如果dstOffset为负数； 如果length为负数； 如果srcOffset +
	 * length大于src.length， 如果dstOffset + length大于dst.length.
	 * 
	 * @param src
	 *            源数组
	 * 
	 * @param src_position
	 *            源数组起始位置
	 * 
	 * @param dst
	 *            目标数组
	 * @param dst_position
	 *            目标数组起始位置
	 * @param length
	 *            长度
	 * @throws NullPointerException
	 *             (如上)
	 * @throws IndexOutOfBoundsException
	 *             (如上)
	 */
	public static void arrayCopy(String[][] src, int src_position,
			String[][] dst, int dst_position, int length) {
		System.arraycopy(src, src_position, dst, dst_position, length);
		for (int i = src_position; i < src_position + length; i++) {
			String[] tem = new String[src[i].length];
			System.arraycopy(src[i], 0, tem, 0, tem.length);
			src[i] = tem;
		}
	}

	/**
	 * 拷贝源字符串数组中的指定范围中的元素到一个新的字符串数组中，注意：这里的拷贝是值拷贝。
	 * 
	 * 需要注意的是，如果任何下面的情况出现了，会抛出空指针异常（NullPointerException）， 但源数组保持不便： 如果src为null.
	 * 如果任何下面的情况出现了，会抛出数组越界异常（IndexOutOfBoundsException），而 源数组保持不变：
	 * 如果srcOffset为负数； 如果length为负数； 如果srcOffset + length大于src.length，
	 * 
	 * 
	 * @param src
	 *            源数组
	 * 
	 * @param src_position
	 *            源数组起始位置
	 * 
	 * @param length
	 *            长度
	 * @throws NullPointerException
	 *             (如上)
	 * @throws IndexOutOfBoundsException
	 *             (如上)
	 */
	public static String[][] arrayCopy(String[][] src, int src_position,
			int length) {
		String[][] dst = new String[length][];
		arrayCopy(src, src_position, dst, 0, length);
		return dst;
	}

	/**
	 * 将源字符串数组中的所有元素拷贝到新的字符串数组中，注意：这里的拷贝是值拷贝。
	 * 
	 * 需要注意的是，如果任何下面的情况出现了，会抛出空指针异常（NullPointerException）， 但源数组保持不便： 如果src为null.
	 * 如果任何下面的情况出现了，会抛出数组越界异常（IndexOutOfBoundsException），而 源数组保持不变：
	 * 如果srcOffset为负数； 如果length为负数； 如果srcOffset + length大于src.length，
	 * 
	 * 
	 * @param src
	 *            源数组
	 * 
	 * @throws NullPointerException
	 *             (如上)
	 * @throws IndexOutOfBoundsException
	 *             (如上)
	 */
	public static String[][] arrayCopy(String[][] src) {
		return arrayCopy(src, 0, src.length);
	}
	/**
	 * 分割字符串，试用存在内嵌开始和结束符号的字符
	 * @param s
	 * @param delimiter1 开始符号
	 * @param delimiter2 结束符号
	 * @return
	 */
	public static String[] splitLikeJson(String s,char delimiter1,char delimiter2){
		List<Character> chars = new ArrayList<Character>(5);
		List<String> result = new ArrayList<String>();
		int from = s.indexOf(delimiter1)+1;
		for(int i=from;i>0 && i<s.length();i++){
			char c = s.charAt(i);
			if(c==delimiter2 && chars.size()==0){
				result.add(s.substring(from, i));
				i= s.indexOf(delimiter1,i);
				if(i>-1){
					from =i+1;
				}else{
					break;
				}
			}else if(c==delimiter1){
				chars.add(c);
			}else if(c==delimiter2 && chars.size()>0){
				chars.remove(0);
			}
		}
		String[] ret = new String[result.size()];
		result.toArray(ret);
		return ret;
		
	}

	/**
	 * 
	 * @param s
	 * @param delimiter
	 * @return String[]
	 */
	public static String[] split(String s, String delimiter) {
		StringTokenizer tokenizer = new StringTokenizer(s, delimiter);
		String[] result = new String[tokenizer.countTokens()];
		int i = 0;
		while (tokenizer.hasMoreTokens())
			result[i++] = tokenizer.nextToken();
		return result;
	}
	
	public static String[] split2(String s,String delimiter){
		List<String> list = new ArrayList<String>();
		int j=-1;
		int index =-1;
		do{
			j= j+1;
			index = s.indexOf(",", j);
			if(index==-1){
				list.add(s.substring(j));
			}else{
				list.add(s.substring(j,index));
				j=index;
			}
		}while(index>-1);
		String[] strs = new String[list.size()];
		list.toArray(strs);
		return strs;
	}

	/**
	 * 比较两个字符串是否相等.
	 * 
	 * @param s1
	 *            String
	 * @param s2
	 *            String
	 */
	public static boolean equals(Object s1, Object s2) {
		if (s1 == s2)
			return true;
		if (s1 == null)
			return false;
		return s1.equals(s2);
	}

	/**
	 * 将传入的LIST中的数组自动合并成一个大数组.
	 * 
	 * @param list
	 *            要合并的数组的列表,要求数组列数必须相等.
	 * @return 合并后的数组.
	 */
	@SuppressWarnings("unchecked")
	public static String[][] arrayCombine(List list) {

		if (list == null || list.size() < 1) {
			return null;
		}

		String[][] str = null;
		String[][] ret = null;

		// 合计所有数组的行数
		int size = 0;
		for (int i = list.size() - 1; i >= 0; i--) {
			str = (String[][]) list.get(i);
			if (str != null && str.length > 0) {
				size += str.length;
			}
		}

		if (size > 0) {
			int j = 0; // 目前COPY数组的位置

			int k = 0; // 每个数组的长度

			ret = new String[size][];
			for (int i = 0, len = list.size(); i < len; i++) {
				str = (String[][]) list.get(i);
				if (str != null && str.length > 0) {
					k = str.length;
					StringUtils.arrayCopy(str, 0, ret, j, k);
					j += k;
				}
			}
		}

		return ret;
	}

	/**
	 * 以DEBUG模式将数组内容输出.是否输出将视LOG设置而定.
	 * 
	 * @param s
	 *            要输出的数组.
	 */
	public static void print(Object[][] s) {
		if (s != null && s.length > 0) {
			log.debug("Below is the contents of Array:");
			for (int i = 0, len = s.length; i < len; i++) {
				for (int j = 0, jLen = s[i].length; j < jLen; j++) {
					log.debug("\t" + s[i][j]);
				}
				log.debug("\n");
			}
		}
	}

	/**
	 * 返回用指定分隔符隔开的字串.如分隔符为null或空串,抛出运行错误.
	 * 
	 * @param arr
	 *            要连接的数组,如长度为1,返回该元素,否则，返回用分隔符隔开而成的字串.
	 * @param delim
	 *            分隔符.
	 * @return 将数组转换成字串，以便可执行逆操作split().
	 * @exception AppException
	 *                当分隔符为null或空串时抛出.
	 */
	public static String join(String[] arr, String delim) throws AppException {
		// 首先判断是否为NULL
		if (arr == null || arr.length < 1) {
			return null;
		}

		// 判断参数是否有效
		delim = StringUtils.trim(delim);
		if (delim.length() < 1) {
			throw new AppException("参数非法，连接数组时，分隔符不可为null或空串");
		}

		// 取数组长度

		int len = arr.length;

		// 如为1，立刻返回第一个

		if (len < 2) {
			return arr[0];
		}

		// 构造用于返回的字符串对象

		StringBuilder sb = new StringBuilder(arr[0]);

		// 拼字串

		for (int i = 1; i < len; i++) {
			sb.append(delim).append(arr[i]);
		}

		return sb.toString();
	}

	public static String escape(String src) {
		int i;
		char j;
		StringBuilder tmp = new StringBuilder();
		tmp.ensureCapacity(src.length() * 6);
		for (i = 0; i < src.length(); i++) {
			j = src.charAt(i);
			if (Character.isDigit(j) || Character.isLowerCase(j)
					|| Character.isUpperCase(j))
				tmp.append(j);
			else if (j < 256) {
				tmp.append("%");
				if (j < 16)
					tmp.append("0");
				tmp.append(Integer.toString(j, 16));
			} else {
				tmp.append("%u");
				tmp.append(Integer.toString(j, 16));
			}
		}
		return tmp.toString();
	}

	private final static byte[] val = { 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x00, 0x01,
			0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F };

	public static String unescape(String s) {
		StringBuilder sbuf = new StringBuilder();
		int i = 0;
		int len = s.length();
		while (i < len) {
			int ch = s.charAt(i);
			if ('A' <= ch && ch <= 'Z') {
				sbuf.append((char) ch);
			} else if ('a' <= ch && ch <= 'z') {
				sbuf.append((char) ch);
			} else if ('0' <= ch && ch <= '9') {
				sbuf.append((char) ch);
			} else if (ch == '-' || ch == '_' || ch == '.' || ch == '!'
					|| ch == '~' || ch == '*' || ch == '\'' || ch == '('
					|| ch == ')') {
				sbuf.append((char) ch);
			} else if (ch == '%') {
				int cint = 0;
				if ('u' != s.charAt(i + 1)) {
					cint = (cint << 4) | val[s.charAt(i + 1)];
					cint = (cint << 4) | val[s.charAt(i + 2)];
					i += 2;
				} else {
					cint = (cint << 4) | val[s.charAt(i + 2)];
					cint = (cint << 4) | val[s.charAt(i + 3)];
					cint = (cint << 4) | val[s.charAt(i + 4)];
					cint = (cint << 4) | val[s.charAt(i + 5)];
					i += 5;
				}
				sbuf.append((char) cint);
			} else {
				sbuf.append((char) ch);
			}
			i++;
		}
		return sbuf.toString();
	}

	/**
	 * 取随机字符串
	 * 
	 * @param len
	 *            长度
	 * @return
	 */
	public static String randomString(int len) {
		StringBuilder RndData = new StringBuilder();
		char Upper = 'z';
		char Lower = 'a';
		Random r = new Random();
		for (int i = 0; i < len; i++) {
			int tempval = (int) ((int) Lower + (r.nextFloat() * ((int) (Upper - Lower))));
			RndData.append(new Character((char) tempval).toString());
		}
		return RndData.toString();
	}

	/**
	 * 测试用.
	 * 
	 * @param s
	 *            传入参数
	 */
	public static void main(String[] s) {

	}
}