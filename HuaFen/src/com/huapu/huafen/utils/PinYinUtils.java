package com.huapu.huafen.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.text.TextUtils;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

/** 
 * @ClassName: PinYinUtils 
 * @Description: TODO
 * @author liang_xs
 * @date 2014-8-25
 */
public class PinYinUtils {
	public static String getPinYin(String str) {
		if (TextUtils.isEmpty(str))
			return "";
		String src = str.replace(" ", "");
		StringBuilder pinyinBuf = new StringBuilder();
		HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
		outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

		try {
			for (int i = 0; i < src.length(); i++) {
				String[] pinYins = PinyinHelper.toHanyuPinyinStringArray(src.charAt(i), outputFormat);
				if (pinYins != null && pinYins.length > 0) {
					pinyinBuf.append(pinYins[0]);
					pinyinBuf.append("_");
				}
				else {
					pinyinBuf.append(src.charAt(i));
				}
			}
		}
		catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		String pinyin = pinyinBuf.toString();
		if(pinyin.contains("sha_men_")) { // 判断是否是厦门如果是，则改为xia
			pinyin = pinyin.replace("sha", "xia");
		}
		if(pinyin.contains("zhang_zhi_")) { // 判断是否是常治如果是，则改为chang
			pinyin = pinyin.replace("zhang", "chang");
		}
		if(pinyin.contains("zhang_chun_")) { // 判断是否是长春如果是，则改为chang
			pinyin = pinyin.replace("zhang", "chang");
		}
		if(pinyin.contains("zhong_qing_")) { // 判断是否是重庆如果是，则改为chong
			pinyin = pinyin.replace("zhong", "chong");
		}
		if(pinyin.contains("zhang_sha_")) { // 判断是否是长沙如果是，则改为chang
			pinyin = pinyin.replace("zhang", "chang");
		}
		return pinyin;
	}

    private Comparator<String> stringComparator = new Comparator<String>() {
        public int compare(String c1, String c2) {
            return compareName(c1, c2);
        }
    };

    /**
     * @Title: compareName
     * @Description: TODO
     * @return 比较排序
     * @author liang_xs
     */
    public static int compareName(String var1, String var2) {


        if (TextUtils.isEmpty(var1) && !TextUtils.isEmpty(var2)) {
            return 1;
        } else if (!TextUtils.isEmpty(var1) && TextUtils.isEmpty(var2)) {
            return -1;
        } else if (!TextUtils.isEmpty(var1) && !TextUtils.isEmpty(var2)) {
            if (!isLetter(var1.substring(0, 1)) && isLetter(var2.substring(0, 1)))
                return 1;
            else if (isLetter(var1.substring(0, 1)) && !isLetter(var2.substring(0, 1)))
                return -1;
            else if (0 == var1.compareToIgnoreCase(var2))
                return 0;
            else if ((var1.compareToIgnoreCase(var2) < 0))
                return -1;
            else if ((var1.compareToIgnoreCase(var2) > 0))
                return 1;
        }
        return 0;
    }

    public static boolean isLetter(String str) {
        if (TextUtils.isEmpty(str))
            return false;
        for (int i = 0; i < str.length(); i++) {
            int chr = str.charAt(i);
            if ((chr < 'A' || chr > 'Z') && (chr < 'a' || chr > 'z'))
                return false;
        }
        return true;
    }
	/**
	 * 名字转拼音,取首字母
	 * @param name
	 * @return
	 */
	public static String getSortLetter(String name,String pinyin) {
		String letter = "#";
		if (name == null) {
			return letter;
		}
		String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINESE);

		// 正则表达式，判断首字母是否是英文字母
		if (sortString.matches("[A-Z]")) {
			letter = sortString.toUpperCase(Locale.CHINESE);
		}
		return letter;
	}

	private static final String chReg = "[\\u4E00-\\u9FA5]+";//中文字符串匹配
	//String chReg="[^\\u4E00-\\u9FA5]";//除中文外的字符匹配
	/**
	 * 解析sort_key,封装简拼,全拼
	 * @param sortKey
	 * @return
	 */
	public static SortToken parseSortKey(String sortKey) {
		SortToken token = new SortToken();
		if (sortKey != null && sortKey.length() > 0) {
			//其中包含的中文字符
			String[] enStr = sortKey.replace(" ", "").split(chReg);
			for (int i = 0, length = enStr.length; i < length; i++) {
				if (enStr[i].length() > 0) {
					//拼接简拼
					token.simpleSpell += enStr[i].charAt(0);
					//拼接全拼
					token.wholeSpell += enStr[i];
				}
			}
		}
		return token;
	}

}
