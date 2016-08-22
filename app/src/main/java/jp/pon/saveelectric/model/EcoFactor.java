package jp.pon.saveelectric.model;

import android.content.Context;

import jp.pon.saveelectric.entity.MasterDAO;
import jp.pon.saveelectric.entity.MasterEntity;

import static jp.pon.saveelectric.Const.*;

/**
 * 電気係数クラス
 *
 * @author pon
 */
public class EcoFactor {

	MasterDAO masterDao;				//電気マスタDAO

	private double baseCharge;		//基本料金
	private double[] unitPrice = new double[3];		//消費電力単価
	private double solarTax;			//太陽光発電促進付加金単価
	private double recycleTax;		//再エネ発電賦課単価

	/**
	 * コンストラクタ
	 *
	 * @param ctx		コンテキスト
	 */
	public EcoFactor(Context ctx) {
		//電気データDAOの生成
		masterDao = new MasterDAO(ctx);
	}

	/**
	 * 電気係数のセットアップ
	 *
	 * @param key		電気キー
	 */
	public void setupFactors(ElectricKey key) {
		MasterEntity entity;

		entity = getTargetEntity(key, ID_BASE_CHARGE);
		setBaseCharge(entity.getUnitPrice());

		entity = getTargetEntity(key, ID_UNIT_PRICE);
		setUnitPrice(entity.getUnitPrice(), entity.getUnitPrice2(), entity.getUnitPrice3());

		entity = getTargetEntity(key, ID_SOLAR_TAX);
		setSolarTax(entity.getUnitPrice());

		entity = getTargetEntity(key, ID_RECYCLE_TAX);
		setRecycleTax(entity.getUnitPrice());
	}

	/**
	 * ターゲット電気マスタ情報の取得
	 *
	 * @param key		電気キー
	 * @param id		係数ID
	 * @return		電気マスタ情報
	 */
	private MasterEntity getTargetEntity(ElectricKey key, String id) {
		MasterEntity defaultEntity = getDefaultEntity(id);
		ElectricKey defaultKey = new ElectricKey();
		defaultKey.setYear(defaultEntity.getYear());
		defaultKey.setMonth(defaultEntity.getMonth());

		MasterEntity targetEntity = new MasterEntity();
		ElectricKey wKey = key.copy();
		targetEntity.setUnitType(id);
		MasterEntity entity = null;
		while (true) {
			targetEntity.setYear(wKey.getYear());
			targetEntity.setMonth(wKey.getMonth());
			entity = masterDao.findEntity(targetEntity);
			if (entity != null) {
				break;
			} else {
				if (defaultKey.isEqualsDate(wKey)) {
					entity = defaultEntity;
					break;
				}
			}
			wKey.decrement();
		}

		return entity;
	}

	/**
	 * 最古の電気マスタ情報を取得
	 *
	 * @param id		係数ID
	 * @return		電気マスタ情報
	 */
	private MasterEntity getDefaultEntity(String id) {
		MasterEntity entity = new MasterEntity();
		entity.setUnitType(id);
		entity.setMark("*");

		return masterDao.findEntity(entity);
	}

	/**
	 * 電気マスタ情報を保存する
	 *
	 * @param entity		電気マスタ情報
	 */
	public void save(MasterEntity entity) {
		this.masterDao.save(entity);
	}

	public double getBaseCharge() {
		return baseCharge;
	}

	public void setBaseCharge(double baseCharge) {
		this.baseCharge = baseCharge;
	}

	public double[] getUnitPrice() {
		return unitPrice;
	}

	public double getUnitPrice1() {
		return unitPrice[0];
	}
	public double getUnitPrice2() {
		return unitPrice[1];
	}
	public double getUnitPrice3() {
		return unitPrice[2];
	}

	public void setUnitPrice(double price1, double price2, double price3) {
		this.unitPrice[0] = price1;
		this.unitPrice[1] = price2;
		this.unitPrice[2] = price3;
	}

	public double getSolarTax() {
		return solarTax;
	}

	public void setSolarTax(double solarTax) {
		this.solarTax = solarTax;
	}

	public double getRecycleTax() {
		return recycleTax;
	}

	public void setRecycleTax(double recycleTax) {
		this.recycleTax = recycleTax;
	}
}
