package jp.pon.saveelectric.entity;

import static jp.pon.saveelectric.Const.*;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database接続クラス(DataBase Open Helper)<p>
 * serve as a singleton instance.
 *
 * @author pon
 *
 */
public class DBConnection extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;

	private static DBConnection dbConnection;
	//singleton
	public static DBConnection getInstance(Context ctx){
		if(dbConnection == null) dbConnection = new DBConnection(ctx);
		return dbConnection ;
	}

	public DBConnection(Context ctx) {
		super(ctx, DB_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists " +
				ELECTRIC_TABLE 		+
				"("						+
					COL_AMPERE			+ " int,"		+		//契約アンペア
					COL_YEAR			+ " int,"		+	 	//対象年（西暦）
					COL_MONTH			+ " int,"		+		//対象月
					COL_LAST_USED		+ " int,"		+		//前月メータ
					COL_CURR_USED		+ " int,"		+		//今月メータ
					COL_ADJUST			+ " double,"	+		//燃料費調整単価
					COL_METER_DAY		+ " int"		+		//検針日
				")"
		);
		db.execSQL("create table if not exists " +
				ELECTRIC_MASTER 		+
				"("						+
					COL_UNIT_TYPE		+ " String,"	+		//単価種別
					COL_YEAR			+ " int,"		+		//年（西暦）
					COL_MONTH			+ " int,"		+		//月
					COL_MARK			+ " String,"	+		//マーク
					COL_UNIT_PRICE	+ " double,"	+		//単価
					COL_UNIT_PRICE2	+ " double,"	+		//単価2
					COL_UNIT_PRICE3	+ " double"	+		//単価3
				")"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
