package jp.pon.saveelectric.model;

import java.util.Calendar;

import static jp.pon.saveelectric.Const.*;

/**
 * 電気キークラス
 *
 * @author pon
 *
 */
public class ElectricKey {

	private int ampere;			//契約アンペア
	private int year;				//西暦年
	private int month;			//対象月（請求は翌月）

	/**
	 * コンストラクタ
	 */
	public ElectricKey() {
		this.ampere	= DEFAULT_AMPERE;
		//今日の年、月を設定
		Calendar now = Calendar.getInstance();
		this.year  = now.get(Calendar.YEAR);
		this.month = now.get(Calendar.MONTH) + 1;
	}

	/**
	 * コンストラクタ
	 *
	 * @param ampere		//契約アンペア
	 */
	public ElectricKey(int ampere) {
		this.ampere	= ampere;
		//今日の年、月を設定
		Calendar now = Calendar.getInstance();
		this.year  = now.get(Calendar.YEAR);
		this.month = now.get(Calendar.MONTH) + 1;
	}

	/**
	 * コンストラクタ
	 *
	 * @param key		電気キー
	 */
//	public ElectricKey(ElectricKey key) {
//		this.ampere	= key.getAmpere();
//		this.year		= key.getYear();
//		this.month		= key.getMonth();
//	}

	/**
	 * コンストラクタ
	 *
	 * @param ampere		//契約アンペア
	 * @param year			//西暦年
	 * @param month		//対象月
	 */
	public ElectricKey(int ampere, int year, int month) {
		this.ampere	= ampere;
		this.year		= year;
		this.month		= month;
	}

	/**
	 * 前月の電気キーを取得する
	 *
	 * @return	電気キー
	 */
	public ElectricKey getPrevKey() {
		ElectricKey prevKey = copy();
		prevKey.decrement();

		return prevKey;
	}

	/**
	 * 翌月の電気キーを取得する
	 *
	 * @return	電気キー
	 */
	public ElectricKey getNextKey() {
		ElectricKey nextKey = copy();
		nextKey.increment();

		return nextKey;
	}

	/**
	 * 電気キーをコピーする
	 *
	 * @return	電気キー
	 */
	public ElectricKey copy() {
		return new ElectricKey(this.ampere, this.year, this.month);
	}

	/**
	 * 日付が将来か否かの判断
	 *
	 * @return		比較結果
	 */
	public boolean isFutureDate() {
		boolean bool = false;

		Calendar nowDate = Calendar.getInstance();
		nowDate.add(Calendar.MONTH, 2);		//補正：月は０オリジン＆請求月は翌月
		nowDate.set(Calendar.DATE, 1);		//補正：Dateは1日に合わせる
		Calendar targetDate = Calendar.getInstance();
		targetDate.set(year, month, 1);
		if (nowDate.compareTo(targetDate) <= 0) {
			bool = true;
		}

		return bool;
	}

	/**
	 * 電気キーの日付を比較する
	 *
	 * @param key		電気キー
	 * @return		比較結果
	 */
	public boolean isEqualsDate(ElectricKey key) {
		boolean bool = false;

		if (this.year == key.getYear() &&	this.month == key.getMonth()) {
			bool = true;
		}

		return bool;
	}

	/**
	 * 電気キーを比較する
	 *
	 * @param key		電気キー
	 * @return		比較結果
	 */
	public boolean isEquals(ElectricKey key) {
		boolean bool = false;

		if (this.ampere == key.getAmpere() &&
				this.year == key.getYear() &&
				this.month == key.getMonth()) {
			bool = true;
		}

		return bool;
	}

	/**
	 * 対象月をマイナス１する
	 */
	public void decrement() {
		this.month--;
		if (this.month <= 0) {
			this.month = 12;
			this.year--;
		}
	}

	/**
	 * 対象月をプラス１する
	 */
	public void increment() {
		this.month++;
		if (this.month > 12) {
			this.month = 1;
			this.year++;
		}
	}


	public int getAmpere() {
		return ampere;
	}
	public void setAmpere(int ampere) {
		this.ampere = ampere;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}

}
