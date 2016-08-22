package jp.pon.saveelectric;

/**
 * 定数クラス
 *
 * @author pon
 *
 */
public class Const {

	/**
	 * コンストラクタ
	 */
	private Const() {}

//******************************************************************************
//	定数
//******************************************************************************
	// 一般
	/** 文字体系名称 */
	public static final String CHARSET_NAME			= "UTF-8";
	/** 区切り符号：カンマ */
	public static final String CSV_SEPARATOR		= ",";
	/** 確認：OK */
	public static final String CHAR_OK				= "OK";
	/** 確認：キャンセル */
	public static final String CHAR_CANCEL			= "Cancel";
	/** 一年 */
	public static final int 	  MONTHS_OF_YEAR 		= 12;

	// CSV関連
	/** CSVディレクトリ名 */
	public static final String CSV_DIR  			= "/backup";
	/** CSVフルパス名 */
	public static final String CSV_TABLE_NAME 		= CSV_DIR + "/electric.csv";
	/** 電気係数CSV */
	public static final String CSV_MASTER  			= "master.csv";

	// 設定ファイル
	/** 設定ファイル名 */
	public static final String PREF_NAME			= "ecoEle";
	/** 設定キー名：　マスタ更新 */
	public static final String PREF_KEY_UPDATE		= "update";
	/** 設定値：　マスタ更新する　：0 */
	public static final int	  PREF_UPDATE_MASTER	= 0;
	/** 設定値：　マスタ更新しない：1 */
	public static final int	  PREF_DO_NOT_UPDATE_MASTER	= 1;
	/** 設定キー名：　アンペア */
	public static final String PREF_KEY_AMPERE		= "ampere";

	// Activity引継ぎ情報
	/** 引継ぎ名：契約アンペア */
	public static final String INTENT_AMPERE		= "ampere";
	/** 引継ぎ名：対象年 */
	public static final String INTENT_TARGET_YEAR	= "targetYear";
	/** 引継ぎ名：対象月 */
	public static final String INTENT_TARGET_MONTH= "targetMonth";
	/** 引継ぎ名：チャートID */
	public static final String INTENT_CHART_ID		= "chartId";

	/** 引継ぎ名：基本料金 */
	public static final String INTENT_BASE_CHARGE	= "baseCharge";
	/** 引継ぎ名：消費電力単価1 */
	public static final String INTENT_UNIT_PRICE1	= "unitPrice1";
	/** 引継ぎ名：消費電力単価2 */
	public static final String INTENT_UNIT_PRICE2	= "unitPrice2";
	/** 引継ぎ名：消費電力単価3 */
	public static final String INTENT_UNIT_PRICE3	= "unitPrice3";
	/** 引継ぎ名：再エネ発電賦課単価 */
	public static final String INTENT_RECYCLE_TAX	= "recycleTax";
	/** 引継ぎ名：太陽光発電促進付加金単価 */
	public static final String INTENT_SOLAR_TAX	= "solarTax";
	/** 引継ぎ名：燃料費調整単価 */
	public static final String INTENT_ADJUST		= "adjust";

	// DB関連
	/** DB名 */
	public static final String DB_NAME				= "myEcoDB";
	/** テーブル名：エコ電気テーブル */
	public static final String ELECTRIC_TABLE		= "eco_electric_tb";
	/** テーブル名：電気マスター */
	public static final String ELECTRIC_MASTER		= "electric_master";

	// Table列名
	// エコ電気テーブル
	/** 契約アンペア */
	public static final String COL_AMPERE			= "ampere";
	/** 対象年（西暦） */
	public static final String COL_YEAR				= "year";
	/** 対象月 */
	public static final String COL_MONTH			= "month";
	/** 前月使用量 */
	public static final String COL_LAST_USED		= "last_used";
	/** 今月使用量 */
	public static final String COL_CURR_USED		= "curr_used";
	/** 燃料費調整単価 */
	public static final String COL_ADJUST			= "adjust";
	/** 検針日 */
	public static final String COL_METER_DAY		= "meter_day";

	// 電気マスター
	/** 単価種別 */
	public static final String COL_UNIT_TYPE		= "unit_type";
	/** マーク */
	public static final String COL_MARK				= "mark";
	/** 単価 */
	public static final String COL_UNIT_PRICE		= "unit_price";
	/** 単価2 */
	public static final String COL_UNIT_PRICE2		= "unit_price2";
	/** 単価3 */
	public static final String COL_UNIT_PRICE3		= "unit_price3";

	/** 基本料金単価 */
	public static final String ID_BASE_CHARGE		=  "1";
	/** 消費電力単価 */
	public static final String ID_UNIT_PRICE		=  "2";
	/** 太陽光発電促進付加金単価 */
	public static final String ID_SOLAR_TAX			=  "3";
	/** 再エネ発電賦課単価 */
	public static final String ID_RECYCLE_TAX		=  "4";

	// チャート関連
	/** 電気使用量チャート */
	public static final int CHART_USED_KWH			= 0;
	/** 燃料費調整単価チャート */
	public static final int CHART_ADJUST  			= 1;
	/** 出力期間（月） */
	public static final int CHART_TERM_MONTH		= MONTHS_OF_YEAR * 2;		//24か月

	// メニュー関連
//	/** 設定 */
	/** 電気係数の更新 */
	public static final int MENU_MASTER			= 0,
	/** データ復旧 */
							   MENU_IMPORT 	  		= 1,
	/** データ退避 */
							   MENU_EXPORT 	  		= 2;

	// 業務関連
	public static final int BACKUP_TERM_YEAR		= 10;
	/** 燃料費調整単価規定値 */
	public static final double DEFAULT_ADJUST		= 0;
	/** 検針日規定値 */
	public static final int DEFAULT_METER_DAY		= 5;
	/** 契約アンペア規定値：20A */
	public static final int DEFAULT_AMPERE 		= 20;
	/** 契約アンペアスピナー指標規定値：15A */
	public static final int DEFAULT_AMPERE_INDEX	= 1;
	/** 開始時刻：8時 */
	public static final int START_HOUR_OF_DAY 	= 8;
	/** 使用量最大値 */
	public static final int MAX_USED_KWH 	  		= 10000;	//メータが4桁のとき

//******************************************************************************
//	東京電力の定数
//******************************************************************************

	/** 契約アンペア */
	public static final int[] ARRAY_AMPERE = { 10, 15, 20, 30, 40, 50, 60 };

	/** 消費電力レベル */
	public static final int[] LEVEL_KWH = {
			120,	//120KWH
			300,	//300KWH
			-1		//
	};

}
