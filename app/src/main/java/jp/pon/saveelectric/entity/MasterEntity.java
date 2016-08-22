package jp.pon.saveelectric.entity;

/**
 * 電気マスタ情報クラス
 *
 * @author pon
 */
public class MasterEntity {

	//フィールド
	private String	 unitType;		//単価種別
	private int 	 year;			//年（西暦）
	private int 	 month;			//月
	private String  mark;			//マーク
	private double unitPrice;	//単価
	private double unitPrice2;	//単価2
	private double unitPrice3;	//単価3

	/**
	 * コンストラクター
	 */
	public MasterEntity() {}


	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
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

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public double getUnitPrice2() {
		return unitPrice2;
	}

	public void setUnitPrice2(double unitPrice2) {
		this.unitPrice2 = unitPrice2;
	}

	public double getUnitPrice3() {
		return unitPrice3;
	}

	public void setUnitPrice3(double unitPrice3) {
		this.unitPrice3 = unitPrice3;
	}
}
