package jp.pon.saveelectric.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static jp.pon.saveelectric.Const.*;

/**
 * 電気マスタDAOクラス
 *
 * @author pon
 */
public class MasterDAO {
	private static DBConnection con;	//データベースコネクション

	/**
	 * コンストラクター
	 *
	 * @param ctx	Context
	 */
	public MasterDAO(Context ctx) {
		if (con == null) con = DBConnection.getInstance(ctx);
	}

	/**
	 * 電気マスタ情報を取得する
	 *
	 * @param entity		電気マスタ情報
	 * @return		電気マスタ情報
	 */
	public MasterEntity findEntity(MasterEntity entity) {
		MasterEntity gotEntity = null;

		//取得列の列名を文字列で用意
		String[] columns = {
			COL_MARK,
			COL_UNIT_PRICE,
			COL_UNIT_PRICE2,
			COL_UNIT_PRICE3
		};

		//テーブル情報の取得
		String where = getWhereClause(entity);
		SQLiteDatabase db = con.getReadableDatabase();
		Cursor cursor;
		try {
			cursor = db.query(ELECTRIC_MASTER, columns, where, null, null, null, null);
		} catch (Exception e) {
			cursor = null;
		}
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				gotEntity = new MasterEntity();
				gotEntity.setUnitType(entity.getUnitType());
				gotEntity.setYear(entity.getYear());
				gotEntity.setMonth(entity.getMonth());
				while (cursor.moveToNext()) {
					gotEntity.setMark(cursor.getString(0));
					gotEntity.setUnitPrice(cursor.getDouble(1));
					gotEntity.setUnitPrice2(cursor.getDouble(2));
					gotEntity.setUnitPrice3(cursor.getDouble(3));
				}
			}
			cursor.close();
		}

		return gotEntity;
	}

	/**
	 * 電気マスタ情報を保存する
	 *
	 * @param entity		電気マスタ情報
	 */
	public void save(MasterEntity entity) {

		//引き渡しValuesを作成
		ContentValues cv = new ContentValues();
		cv.put(COL_UNIT_TYPE,		entity.getUnitType());
		cv.put(COL_YEAR,			entity.getYear());
		cv.put(COL_MONTH,			entity.getMonth());
		cv.put(COL_MARK,			entity.getMark());
		cv.put(COL_UNIT_PRICE,	entity.getUnitPrice());
		cv.put(COL_UNIT_PRICE2,	entity.getUnitPrice2());
		cv.put(COL_UNIT_PRICE3,	entity.getUnitPrice3());

		//テーブルに保管
		MasterEntity record = findEntity(entity);
		SQLiteDatabase db = con.getWritableDatabase();
		if (record == null) {
			db.insert(ELECTRIC_MASTER, null, cv);
		} else {
			String where = getWhereClause(entity);
			db.update(ELECTRIC_MASTER, cv, where, null);
		}
	}

	/**
	 * where句を取得する
	 *
	 * @param entity		電気マスタ情報
	 * @return		where句
	 */
	private String getWhereClause(MasterEntity entity) {
		String where;

		if ("*".equals(entity.getMark())) {
			where = COL_UNIT_TYPE		+ " = "	 	+ entity.getUnitType()
					+ 	" and "
					+ 	COL_MARK		+ " = '" 	+ entity.getMark() + "'";
		} else {
			where = COL_UNIT_TYPE		+ " = "	 	+ entity.getUnitType()
					+ 	" and "
					+ 	COL_YEAR		+ " = "		+ Integer.toString(entity.getYear())
					+	 " and "
					+ 	COL_MONTH		+ " = "		+ Integer.toString(entity.getMonth());
		}

		//検索キーをセット
		return where;
	}
}
