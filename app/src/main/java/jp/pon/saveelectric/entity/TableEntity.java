package jp.pon.saveelectric.entity;

import jp.pon.saveelectric.model.ElectricKey;

import static jp.pon.saveelectric.Const.CSV_SEPARATOR;

/**
 * 電気情報クラス
 */
public class TableEntity {

	//データ用フィールド
	private ElectricKey key = new ElectricKey();		//電気キー
	private int		lastUsed;						//前月メータ
	private int		currUsed;						//今月メータ
	private double	adjust;						//燃料費調整単価
	private int		meterDay;						//検針日

	/**
	 * コンストラクター
	 */
	public TableEntity() {}

	@Override
	/**
	 * CSV形式に変換
	 */
	public String toString() {
		return (Integer.toString(key.getAmpere()))	+ (CSV_SEPARATOR) +
				(Integer.toString(key.getYear()))	+ (CSV_SEPARATOR) +
				(Integer.toString(key.getMonth()))	+ (CSV_SEPARATOR) +
				(Integer.toString(lastUsed))		+ (CSV_SEPARATOR) +
				(Integer.toString(currUsed))		+ (CSV_SEPARATOR) +
				(Double.toString(adjust))			+ (CSV_SEPARATOR) +
				(Integer.toString(meterDay))		+ (CSV_SEPARATOR);
	}


	public ElectricKey getKey() {
		return key;
	}
	public void setKey(ElectricKey key) {
		setKey(key.getAmpere(), key.getYear(), key.getMonth());
	}
	public void setKey(int ampere, int year, int month) {
		this.key.setAmpere(ampere);
		this.key.setYear(year);
		this.key.setMonth(month);
	}
	public int getLastUsed() {
		return lastUsed;
	}
	public void setLastUsed(int lastUsed) {
		this.lastUsed = lastUsed;
	}
	public int getCurrUsed() {
		return currUsed;
	}
	public void setCurrUsed(int currUsed) {
		this.currUsed = currUsed;
	}
	public double getAdjust() {
		return adjust;
	}
	public void setAdjust(double adjust) {
		this.adjust = adjust;
	}
	public int getMeterDay() {
		return meterDay;
	}
	public void setMeterDay(int meterDay) {
		this.meterDay = meterDay;
	}

}
