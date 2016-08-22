package jp.pon.saveelectric.util;

import android.widget.EditText;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jp.pon.saveelectric.Const.*;

/**
 * ユーティリティ　クラス
 */
public class Utility {

	/**
	 * 文字列内の指定文字列を取り除く
	 *
	 * @param strSrc		文字列
	 * @param strRemove	削除文字
	 * @return		削除後の文字列
	 */
	public static String removeString(String strSrc, String strRemove) {
		Pattern pattern = Pattern.compile(strRemove);
		Matcher matcher = pattern.matcher(strSrc);

		return matcher.replaceAll("");
	}

	/**
	 * 配列内のインデックスを返します
	 *
	 * @param array	配列
	 * @param value	値
	 * @return		インデックス
	 */
	public static int getArrayIndex(int[] array, int value) {
		int index = -1;

		for (int i = 0; i < array.length; i++) {
			if (array[i] == value) {
				index = i;
				break;
			}
		}

		return index;
	}

	/**
	 * 入力値をint型にパースします
	 *
	 * @param text		入力欄
	 * @return		変換値
	 */
	public static int parseInt(EditText text) {
		int iNum;

		try {
			iNum = Integer.parseInt(text.getText().toString());
		} catch (NumberFormatException e) {
			iNum = 0;
		}

		return iNum;
	}

	/**
	 * 入力値をdouble型にパースします
	 *
	 * @param text		入力欄
	 * @return		変換値
	 */
	public static double parseDouble(EditText text) {
		double dNum;

		try {
			dNum = Double.parseDouble(text.getText().toString());
		} catch (NumberFormatException e) {
			dNum = 0;
		}

		return dNum;
	}

	/**
	 * 引数の値が日付かどうかのチェック
	 *
	 * @param 	month	月
	 * @param 	day		日
	 * @return		true:日付、false:日付ではない
	 */
	public static boolean isDayOfMonth(int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		int max = cal.getActualMaximum(Calendar.DATE);

		return day >= 1 && day <= max;
	}

	/**
	 * 経過時間を日に換算
	 *
	 * @return		換算結果
	 */
	public static double cnvTime2Days() {
		Calendar now = Calendar.getInstance();
		double dHour = now.get(Calendar.HOUR_OF_DAY);
		dHour = dHour - START_HOUR_OF_DAY;
		double hourRate = dHour / 24.0;
		double minRate  = now.get(Calendar.MINUTE) / 60.0;

		return hourRate + (minRate / 24);
	}

	/**
	 * 月が変わったかのチェック
	 *
	 * @param now			対象日
	 * @param meterDate	検針日
	 * @return		有無
	 */
	public static boolean isMonthChanged(Calendar now, Calendar meterDate) {
		now.add(Calendar.MONTH, 1);
		return now.compareTo(meterDate) >= 0;
	}
}
