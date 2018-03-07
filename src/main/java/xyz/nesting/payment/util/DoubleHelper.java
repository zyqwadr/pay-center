/**
 * 
 */
package xyz.nesting.payment.util;

import java.math.RoundingMode;
import java.text.NumberFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 
 * @Description: TODO(添加描述)
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2016年6月3日-上午10:47:10
 *
 */
public class DoubleHelper {

	private static final int DEFAULT_SCALAE = 10;

	public static int toInt(String str) {
		return toInt(str, 0);
	}

	/**
	 * 计算百分比
	 * 
	 * @param num
	 * @param total
	 * @param scale
	 * @return
	 */
	public static String accuracy(double num, double total, int scale) {
		NumberFormat df = NumberFormat.getInstance();
		// 可以设置精确几位小数
		df.setMaximumFractionDigits(scale);
		df.setMinimumFractionDigits(scale);
		// 模式 例如四舍五入
		df.setRoundingMode(RoundingMode.HALF_UP);
		double accuracy_num = (total == 0 || num == 0) ? 0 : num / total * 100;
		String str = df.format(accuracy_num);
		return str;
	}

	/**
	 * 计算百分比
	 * 
	 * @param num
	 *            分子
	 * @param total
	 *            分母
	 * @param scale
	 *            保留小数位
	 * @return
	 */
	public static double accuracyN(double num, double total, int scale) {
		return NumberUtils.toDouble(accuracy(num, total, scale));
	}

	public static int toInt(String str, int defaultValue) {
		if (str == null)
			return defaultValue;
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException nfe) {
		}
		return defaultValue;
	}

	public static double feeToYuan(int fee) {
		if (fee == 0) {
			return 0;
		}
		return div(fee, 100, 2);
	}

	/**
	 * 字符串转化为double
	 * 
	 * @param str
	 * @return
	 */
	public static double toDouble(String str) {
		return toDouble(str, 0.0D);
	}

	/**
	 * 
	 * @param str
	 * @param scale
	 *            精度
	 * @return
	 */
	public static double toDoubleRound(String str, int scale) {
		return round(toDouble(str, 0.0D), scale);
	}

	/**
	 * 字符串转化为double
	 * 
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static double toDouble(String str, double defaultValue) {
		if (str == null)
			return defaultValue;
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
		}
		return defaultValue;
	}

	/**
	 * 转化为百分数,保留2位小数
	 * 
	 * @param number
	 * @return
	 */
	public static String formatPercentage(double number) {
		return formatPercentage(number, 2);
	}

	/**
	 * 转化为百分数
	 * 
	 * @param number
	 * @param scale
	 *            保留几位
	 * @return
	 */
	public static String formatPercentage(double number, int scale) {
		NumberFormat numberFormat = NumberFormat.getPercentInstance();
		numberFormat.setMinimumFractionDigits(scale);
		numberFormat.setMaximumFractionDigits(scale);
		return numberFormat.format(number);
	}

	/**
	 * 
	 * @param number
	 * @param scale
	 * @return
	 */
	public static String formatNumber(double number, int scale) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMinimumFractionDigits(scale);
		numberFormat.setMaximumFractionDigits(scale);
		return numberFormat.format(number);
	}

	/**
	 * 提供精确的加法运算。
	 * 
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
	 */
	public static double add(double v1, double v2) {
		java.math.BigDecimal b1 = new java.math.BigDecimal(Double.toString(v1));
		java.math.BigDecimal b2 = new java.math.BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 提供精确的减法运算。
	 * 
	 * @param v1
	 *            被减数
	 * @param v2
	 *            减数
	 * @return 两个参数的差
	 */
	public static double sub(double v1, double v2) {
		java.math.BigDecimal b1 = new java.math.BigDecimal(Double.toString(v1));
		java.math.BigDecimal b2 = new java.math.BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 提供精确的乘法运算。
	 * 
	 * @param v1
	 *            被乘数
	 * @param v2
	 *            乘数
	 * @return 两个参数的积
	 */
	public static double mul(double v1, double v2) {
		java.math.BigDecimal b1 = new java.math.BigDecimal(Double.toString(v1));
		java.math.BigDecimal b2 = new java.math.BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2) {
		return div(v1, v2, DEFAULT_SCALAE);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		java.math.BigDecimal b1 = new java.math.BigDecimal(Double.toString(v1));
		java.math.BigDecimal b2 = new java.math.BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, java.math.BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static boolean eq(double v1, double v2, int scale) {
		return StringUtils.equals(formatNumber(v1, scale), formatNumber(v2, scale));
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		java.math.BigDecimal b = new java.math.BigDecimal(Double.toString(v));
		java.math.BigDecimal one = new java.math.BigDecimal("1");
		return b.divide(one, scale, java.math.BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 向上取整
	 * 
	 * @param v
	 * @return
	 */
	public static double ceil(double v) {
		return Math.ceil(v);
	}

	/**
	 * 向下取整
	 * 
	 * @param v
	 * @return
	 */
	public static double floor(double v) {
		return Math.floor(v);
	}

}
