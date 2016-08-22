package jp.pon.saveelectric.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import jp.pon.saveelectric.model.ElectricKey;

import static jp.pon.saveelectric.Const.*;

/**
 * 電気テーブルDAOクラス
 */
public class TableDAO {
	private static DBConnection con;	//データベースコネクション

	/**
	 * コンストラクター
	 *
	 * @param ctx	Context
	 */
	public TableDAO(Context ctx) {
		if (con == null) con = DBConnection.getInstance(ctx);
	}

	/**
	 * 電気キーで電気データを取得する
	 *
	 * @param key	電気キー
	 * @return		電気データ
	 */
	public TableEntity findByKey(ElectricKey key) {
		return findByKey(key.getAmpere(), key.getYear(), key.getMonth());
	}

	/**
	 * 電気キーで電気データを取得する
	 *
	 * @param ampere	契約アンペア
	 * @param year		対象年
	 * @param month	対象月
	 * @return		電気データ
	 */
	public TableEntity findByKey(int ampere, int year, int month) {
		TableEntity entity = null;

		//取得列の列名を文字列で用意
		String[] columns = {
			COL_LAST_USED,
			COL_CURR_USED,
			COL_ADJUST,
			COL_METER_DAY
		};

		//テーブル情報の取得
		String where = getWhereClause(ampere, year, month);
		SQLiteDatabase db = con.getReadableDatabase();
		Cursor cursor;
		try {
			cursor = db.query(ELECTRIC_TABLE, columns, where, null, null, null, null);
		} catch (Exception e) {
			cursor = null;
		}
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				entity = new TableEntity();
				entity.setKey(ampere, year, month);
				while (cursor.moveToNext()) {
					entity.setLastUsed(cursor.getInt(0));
					entity.setCurrUsed(cursor.getInt(1));
					entity.setAdjust(cursor.getDouble(2));
					entity.setMeterDay(cursor.getInt(3));
				}
			}
			cursor.close();
		}
		return entity;
	}

	/**
	 * 電気データを保存する
	 *
	 * @param entity	電気データ
	 */
	public void save(TableEntity entity) {
		//引き渡しValuesを作成
		ContentValues cv = new ContentValues();
		cv.put(COL_AMPERE,		entity.getKey().getAmpere());
		cv.put(COL_YEAR,		entity.getKey().getYear());
		cv.put(COL_MONTH	,	entity.getKey().getMonth());
		cv.put(COL_LAST_USED,	entity.getLastUsed());
		cv.put(COL_CURR_USED,	entity.getCurrUsed());
		cv.put(COL_ADJUST,		entity.getAdjust());
		cv.put(COL_METER_DAY,	entity.getMeterDay());

		//テーブルに保管
		TableEntity record = findByKey(entity.getKey());
		SQLiteDatabase db = con.getWritableDatabase();
		if (record == null) {
			db.insert(ELECTRIC_TABLE, null, cv);
		} else {
			String where = getWhereClause(entity.getKey());
			db.update(ELECTRIC_TABLE, cv, where, null);
		}
	}

	/**
	 * where句をセットする
	 *
	 * @param key		電気キー
	 * @return		where句
	 */
	private String getWhereClause(ElectricKey key) {
		return getWhereClause(key.getAmpere(), key.getYear(), key.getMonth());
	}

	/**
	 * where句を取得する
	 *
	 * @param ampere		契約アンペア
	 * @param year			対象年
	 * @param month		対象月
	 * @return		where句
	 */
	private String getWhereClause(int ampere, int year, int month) {
		//検索キーをセット
		return COL_AMPERE	+ " = "	 + Integer.toString(ampere) +
				" and "   			+
				COL_YEAR			+ " = "	 + Integer.toString(year)	+
				" and "			+
				COL_MONTH			+ " = "	 + Integer.toString(month);
	}
}
