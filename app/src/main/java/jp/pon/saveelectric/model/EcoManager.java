package jp.pon.saveelectric.model;

import android.content.Context;

import java.util.Calendar;

import jp.pon.saveelectric.entity.TableDAO;
import jp.pon.saveelectric.entity.TableEntity;

import static jp.pon.saveelectric.Const.*;
import static jp.pon.saveelectric.util.Utility.*;

/**
 * エコロジーマネージャークラス
 *
 * @author pon
 *
 */
public class EcoManager {

	TableDAO tableDao;			//電気データDAO
	EcoFactor factor;			//電気係数インスタンス

	//計算用フィールド
	private int		usedKWH;						//実績：使用量
	private int		amountKWH;						//　　　請求額
	private double	averageUsed;					//平均：使用量
	private int		averageAmount;				//　　　請求額
	private int 		estimateUsed;					//予測：使用量
	private int 		estimateAmount;				//　　　請求額
	private double	convertRate;					//換算レート


	/**
	 * コンストラクター
	 *
	 * @param ctx	Context
	 */
	public EcoManager(Context ctx) {
		//電気データDAOの生成
		tableDao = new TableDAO(ctx);
		//電気係数インスタンスの生成
		factor   = new EcoFactor(ctx);
	}


	/**
	 * 電気キーで電気データを検索する
	 *
	 * @param key		電気キー
	 * @return		電気データ
	 */
	public TableEntity findByKey(ElectricKey key) {
		return this.tableDao.findByKey(key);
	}

	/**
	 * 電気キーで電気データを検索する
	 *
	 * @param ampere	契約アンペア
	 * @param year		対象年
	 * @param month	対象月
	 * @return		電気データ
	 */
	public TableEntity findByKey(int ampere, int year, int month) {
		return this.tableDao.findByKey(ampere, year, month);
	}

	/**
	 * 電気データを保存する
	 *
	 * @param entity		電気データ
	 */
	public void save(TableEntity entity) {
		tableDao.save(entity);
	}

	/**
	 * 前月データの有無チェック
	 *
	 * @param entity		電気データ
	 * @return		判定結果
	 */
//	public boolean isPrevExists(TableEntity entity) {
//		boolean bool = false;
//
//		ElectricKey keyWk = key.copy();
//		keyWk.decrement();
//		if (findEntity(keyWk) != null) {
//			bool = true;
//		}
//
//		return bool;
//	}

	/**
	 * 翌月データの有無チェック
	 *
	 * @param entity		電気データ
	 * @return		判定結果
	 */
	public boolean isNextExists(TableEntity entity) {
		boolean bool = false;

		//過去のデータは対象外
		if (entity.getKey().isFutureDate()) {
			ElectricKey keyWk = entity.getKey().copy();
			keyWk.increment();
			if (findByKey(keyWk) != null) {
				bool = true;
			}
		} else {
			bool = true;
		}

		return bool;
	}

	/**
	 * 検針日になったか（翌月処理が必要か）
	 *
	 * @param year			チェック年
	 * @param month		チェック月
	 * @param meterDay	検針日
	 * @return		判定結果
	 */
	public boolean isLatestInfo(int year, int month, int meterDay) {
		boolean bool = false;

		Calendar nowDate = Calendar.getInstance();
		nowDate.add(Calendar.MONTH, 1);			//月の補正
		Calendar meterDate = Calendar.getInstance();
		meterDate.set(year, month, meterDay);
		if (nowDate.compareTo(meterDate) < 0) {
			bool = true;
		} else {
			int nowHour = nowDate.get(Calendar.HOUR_OF_DAY);
			if (nowDate.compareTo(meterDate) == 0 && nowHour < START_HOUR_OF_DAY) {
				bool = true;
			}
		}

		return bool;
	}

	/**
	 * 請求金額を計算する<BR>
	 *   ・当日における消費電力を求め、請求金額を計算する<BR>
	 *   ・締め日における推定額を算出する<BR>
	 *
	 * @param entity		電気データ
	 * @return 	経過日数
	 */
	public double calc(TableEntity entity) {
		double daysPast;

		//*********** 対象月の経過日数計算 ************
		//対象月の総日数を計算
		Calendar targetDate = Calendar.getInstance();
		targetDate.set(entity.getKey().getYear(), entity.getKey().getMonth(), 1);
		//month is 0-based. so need to minus 1.
		targetDate.add(Calendar.MONTH, -1);
		int daysOfMonth = targetDate.getActualMaximum(Calendar.DATE);
		//前月データの検針日を取得
		int meterDayPre	= entity.getKey().getPrevKey().getMeterDay(this);
		//請求日数を計算
		int billingDates = daysOfMonth + entity.getMeterDay() - meterDayPre;

		//*********** 経過日数と換算率の計算 ************
		Calendar now = Calendar.getInstance();
		int nowDay = now.get(Calendar.DATE);
		Calendar meterDate = Calendar.getInstance();
		int year     = entity.getKey().getYear();
		int month    = entity.getKey().getMonth();
		int meterDay = entity.getMeterDay();
		meterDate.set(year, month, meterDay);
		if (isLatestInfo(year, month, meterDay)) {
			//スピナー選択月が最新分の場合
			if (isMonthChanged(now, meterDate)) {
				if (nowDay < entity.getMeterDay()) {
					//月が変わり検針日より前の場合
					daysPast = daysOfMonth - meterDayPre + nowDay;
				} else {
					daysPast = billingDates;
				}
			} else {
				//今日が検針日以降の場合
				daysPast =  nowDay - meterDayPre;
				//経過時間を加算
				daysPast += cnvTime2Days();
			}
		} else {
			//スピナー選択月が過去の場合
			daysPast = billingDates;	//経過日数＝請求対象日数
		}
		//換算率の決定
		convertRate = (double)billingDates / daysPast;

		//*********** 各種計算 ******************
		//実績値の計算
		usedKWH = getProperMeter(entity.getLastUsed(), entity.getCurrUsed());
		amountKWH = calcFee(entity, usedKWH);

		//予測値の計算
		double dEstimateUsed = usedKWH * convertRate;
		estimateUsed = (int) Math.round(dEstimateUsed);
		estimateAmount = calcFee(entity, estimateUsed);

		//平均値の計算
		averageUsed = (double) usedKWH / daysPast;
		double dAverageAmount = (double) estimateAmount / billingDates;
		averageAmount = (int) Math.round(dAverageAmount);

		return daysPast;
	}

	/**
	 * 電気料金の計算
	 *
	 * @param entity		電気データ
	 * @param usedKWH		電気使用量
	 * @return		請求金額
	 */
	public int calcFee(TableEntity entity, int usedKWH) {
		//電気係数のセットアップ
		factor.setupFactors(entity.getKey());

		int[] usedKWHPerLevel = new int[3];
		if (usedKWH <= LEVEL_KWH[0]) {
			//第一段階
			usedKWHPerLevel[0] = usedKWH;
		} else if (usedKWH <= LEVEL_KWH[1]) {
			//第二段階
			usedKWHPerLevel[1] = usedKWH - LEVEL_KWH[0];
			usedKWHPerLevel[0] = LEVEL_KWH[0];
		} else if (usedKWH > LEVEL_KWH[1]) {
			//第三段階
			usedKWHPerLevel[2] = usedKWH - LEVEL_KWH[1];
			usedKWHPerLevel[1] = LEVEL_KWH[1];
		}

		//各段階の料金を合計する
		double dUsedAmount = 0;
		double[] arrUnitPrice = factor.getUnitPrice();
		for (int i = 0; i < arrUnitPrice.length; i++) {
			dUsedAmount += arrUnitPrice[i] * usedKWHPerLevel[i];
		}

		//基本料金を計算する
		double dBasicFee = factor.getBaseCharge() * entity.getKey().getAmpere();

		//その他調整金や賦課金を加算する
		double dAdjustFee  = entity.getAdjust() * usedKWH;
		double dRecycleTax = factor.getRecycleTax();
		double dSolarTax   = factor.getSolarTax();
		double dEcoFee     = Math.floor(dRecycleTax * usedKWH) + Math.floor(dSolarTax * usedKWH);

		//各料金を合計し請求金額を求める
		double dTotalAmount = dBasicFee + dUsedAmount + dAdjustFee + dEcoFee;

		return (int) dTotalAmount;
	}

	/**
	 * 計器のメータが一回りしたときの補正処理
	 *
	 * @param value1	値１
	 * @param value2	値２
	 * @return		補正した値
	 */
	public static int getProperMeter(int value1, int value2) {
		int proper;

		if (value1 <= value2) {	//通常の場合
			proper = value2 - value1;
		} else {	//メータがオーバフローした場合
			//メータの桁数を求める
			int digit = (int) Math.log10((double) value1) + 1;
			//補正する
			proper = (int) Math.pow(10, digit) - value1 + value2;
		}

		return proper;
	}

	public int getUsedKWH() {
		return usedKWH;
	}
	public int getAmountKWH() {
		return amountKWH;
	}
	public int getEstimateUsed() {
		return estimateUsed;
	}
	public int getEstimateAmount() {
		return estimateAmount;
	}
	public double getAverageUsed() {
		return averageUsed;
	}
	public int getAverageAmount() {
		return averageAmount;
	}
	public double getConvertRate() {
		return convertRate;
	}
}
