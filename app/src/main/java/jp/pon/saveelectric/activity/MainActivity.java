package jp.pon.saveelectric.activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static jp.pon.saveelectric.Const.*;
import static jp.pon.saveelectric.util.Utility.*;
import jp.pon.saveelectric.R;
import jp.pon.saveelectric.chart.ChartActivity;
import jp.pon.saveelectric.chart.TimeSeriesChartView;
import jp.pon.saveelectric.model.EcoFactor;
import jp.pon.saveelectric.entity.MasterEntity;
import jp.pon.saveelectric.entity.TableEntity;
import jp.pon.saveelectric.model.EcoManager;
import jp.pon.saveelectric.model.ElectricKey;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * 「省エレ君」画面のActivityクラス<p>
 *
 * 【制約】<br>
 *   ・東京電力のみ対応<br>
 *   ・個人契約のみ対応<br>
 *   ・メータは4桁で計器1台のみ対応<br>
 *   ・太陽光発電促進付加金は、平成26年9月で終了<br>
 *
 *	【注意】<br>
 *	電気係数に変更がある場合（例えば電気料金の値上げ）は、<br>
 *  本アプリのバージョンアップでの対応となります。<br>
 *
 * @author	pon
 *
 * @version 0.1.1 2013/05/01	bug fixed.
 * @version 0.1.2 2013/05/10	月ごとに賦課金を求める機能追加（暫定対応）
 * @version 1.0.0 2015/06/22	リリースV1.0
 * @version 1.2.0 2016/08/10	大幅にリファクタリング
 * @version 1.3.0 2016/08/21	電気係数のDB化
 * @version 2.0.0 2016/08/22	リリースV2.0
 */
public class MainActivity extends Activity implements View.OnClickListener {

//******************************************************************************
//	UI変数
//******************************************************************************
	//Spinner
	Spinner spinnerAmpere;		//スピナー契約アンペア
	Spinner spinnerMonth;			//スピナー対象月

	//EditText
	EditText edLastUsed;			//前月メータ
	EditText edCurrUsed;			//今月メータ
	EditText edAdjust;				//燃料費調整単価
	EditText edMeterDay;			//検針日
	EditText edUsedKWH;			//実績：使用量
	EditText edAmountKWH;			//　　　請求額
	EditText edAverageUsed;		//平均：使用量
	EditText edAverageAmount;		//　　　請求額
	EditText edEstimateUsed;		//予測：使用量
	EditText edEstimateAmount;	//　　　請求額

	//TextView
	TextView tvMessage;			//メッセージ

	//Button
	Button btnPrev;				//前月ボタン
	Button btnNext;				//翌月ボタン
	Button btnFactor;				//電気係数ボタン
	Button btnSave;				//保存ボタン
	Button btnToggle;				//図切替ボタン
	Button btnExpand;				//図拡大ボタン

	//グラフレイアウト
	LinearLayout graphLayout;

//******************************************************************************
//	変数
//******************************************************************************
	boolean bInitFlag 	= true;				//初期フラグ
	boolean bStopEvent 	= false;			//イベント停止フラグ
	boolean bChartToggle = false;			//チャートトグルフラグ

	TimeSeriesChartView tscView;				//折れ線グラフビュー

	ArrayAdapter<String> arrAdapter_month;	//スピナー（年月）要素
	ArrayList<ElectricKey> electKeyList;      //電気キーリスト
	EcoManager ecoMan;							//エコロジーマネージャー
	TableEntity entity;    	                //電気データ

	DecimalFormat formatDouble;               //少数表示用フォーマット
	DecimalFormat formatAmount;               //金額表示用フォーマット
	AlertDialog.Builder dialog;				//OK、Cancel確認ダイアログ

//******************************************************************************
//	メソッド
//******************************************************************************
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_save_electric);

		//少数表示用フォーマットの準備
		formatDouble = new DecimalFormat("0.00");
		//金額表示用フォーマットの準備
		formatAmount = new DecimalFormat("#,###");
		//OK、Cancel確認ダイアログ作成
		dialog = new AlertDialog.Builder(this);

		ecoMan = new EcoManager(this);
		//電気データインスタンスの生成
		entity = new TableEntity();

		//Viewの取得
		spinnerAmpere		= (Spinner) findViewById(R.id.spinner_ampere);
		spinnerMonth		= (Spinner) findViewById(R.id.spinner_month);
		edLastUsed			= (EditText) findViewById(R.id.last_used);
		edCurrUsed			= (EditText) findViewById(R.id.curr_used);
		edAdjust			= (EditText) findViewById(R.id.adjust);
		edMeterDay			= (EditText) findViewById(R.id.meter_day);
		edUsedKWH			= (EditText) findViewById(R.id.used_kwh);
		edAmountKWH		= (EditText) findViewById(R.id.amount_kwh);
		edAverageUsed		= (EditText) findViewById(R.id.average_used);
		edAverageAmount	= (EditText) findViewById(R.id.average_amount);
		edEstimateUsed 	= (EditText) findViewById(R.id.estimat_used);
		edEstimateAmount	= (EditText) findViewById(R.id.estimat_amount);
		tvMessage			= (TextView) findViewById(R.id.message);
		graphLayout		= (LinearLayout) findViewById(R.id.graph_layout);

		//設定ファイルから値を取得
		SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		//初回は電気マスタを更新する
		int updateMaster = pref.getInt(PREF_KEY_UPDATE, PREF_UPDATE_MASTER);
		if (updateMaster == PREF_UPDATE_MASTER) {
			updateMasterData();
			SharedPreferences.Editor editor = pref.edit();
			editor.putInt(PREF_KEY_UPDATE, PREF_DO_NOT_UPDATE_MASTER);
			editor.apply();
		}
		//現行電気キーに契約アンペアの規定値を設定
		int ampere = pref.getInt(PREF_KEY_AMPERE, DEFAULT_AMPERE);
		entity.getKey().setAmpere(ampere);

		int spinnerAmpereIndex = getArrayIndex(ARRAY_AMPERE, ampere);
		if (spinnerAmpereIndex < 0) {
			spinnerAmpereIndex = DEFAULT_AMPERE_INDEX;
		}

		//***** 契約アンペアスピナーの初期表示 *****
		//契約アンペアスピナー登録
		ArrayAdapter<String> arrAdapter_contract =
				new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
		for (int iAmpere : ARRAY_AMPERE) {
			arrAdapter_contract.add(iAmpere + "A");
		}
		arrAdapter_contract.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerAmpere.setAdapter(arrAdapter_contract);
		spinnerAmpere.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		spinnerAmpere.setSelection(spinnerAmpereIndex);
		spinnerAmpere.setOnItemSelectedListener(
			new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					//現行電気キーの契約アンペアを更新する
					entity.getKey().setAmpere(ARRAY_AMPERE[position]);
					//DBからデータを再表示する
					showDataArea();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			}
		);

		//***** 対象月スピナーの初期表示 *****
		arrAdapter_month = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
		arrAdapter_month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		electKeyList = new ArrayList<>();

		//今日の日を取得
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		//現行電気キーの初期値を設定
		int year  = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		entity.getKey().setYear(year);
		entity.getKey().setMonth(month);
		//スピナーに過去1年分を表示する
		int meterDay = new ElectricKey(ampere).getMeterDay(ecoMan);
		if (!ecoMan.isLatestInfo(year, month, meterDay)) {
			entity.getKey().increment();
		}

		//スピナー要素の変更
		replaceSpinnerMonthElement();
		spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (bStopEvent) {
					bStopEvent = false;
					return;
				}
				//選択された要素で更新
				entity.setKey(electKeyList.get(position));
				if (bInitFlag) {
					//スピナー要素の変更
					replaceSpinnerMonthElement();
					bStopEvent = true;
				}
				//DBデータを再表示する
				showDataArea();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		edAdjust.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					//マイナス値の赤表示
					setMinusColor(edAdjust);
				}
			}
		});

		//ボタンのリスナーをセット
		btnPrev = (Button) findViewById(R.id.button_prev);
		btnPrev.setOnClickListener(this);

		btnNext = (Button) findViewById(R.id.button_next);
		btnNext.setOnClickListener(this);

		btnFactor = (Button) findViewById(R.id.button_factor);
		btnFactor.setOnClickListener(this);

		btnSave = (Button) findViewById(R.id.button_save);
		btnSave.setOnClickListener(this);

		btnToggle = (Button) findViewById(R.id.button_toggle);
		btnToggle.setOnClickListener(this);

		btnExpand = (Button) findViewById(R.id.button_expand);
		btnExpand.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		//オプションメニューへのアイテム0の追加
		MenuItem item0 = menu.add(0, MENU_MASTER, 0, R.string.menu_master);
		item0.setIcon(android.R.drawable.ic_menu_gallery);

		MenuItem item1 = menu.add(0, MENU_IMPORT,	  0, R.string.menu_import);
		item1.setIcon(android.R.drawable.ic_menu_upload);

		MenuItem item2 = menu.add(0, MENU_EXPORT,	  0, R.string.menu_export);
		item2.setIcon(android.R.drawable.ic_menu_save);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//メニューイベントの処理
		switch (item.getItemId()) {
			case MENU_IMPORT:
				//***** 「データ復元」の処理 *****
				dialog.setTitle("");
				dialog.setMessage(R.string.message_import);
				dialog.setPositiveButton(CHAR_OK, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//データを復元
						importData();
						showDataArea();
						tvMessage.setText(getString(R.string.message_imported));
					}

				});
				dialog.setNeutralButton(CHAR_CANCEL, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//キャンセル処理
						tvMessage.setText("");
					}
				});
				dialog.show();

				return true;
			case MENU_EXPORT:
				//***** 「データ退避」の処理 *****
				dialog.setTitle("");
				dialog.setMessage(R.string.message_export);
				dialog.setPositiveButton(CHAR_OK, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//データをバックアップ
						exportData();
						tvMessage.setText(getString(R.string.message_exported));
					}

				});
				dialog.setNeutralButton(CHAR_CANCEL, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//キャンセル処理
						tvMessage.setText("");
					}
				});
				dialog.show();

				return true;
			case MENU_MASTER:
				//***** 「電気係数の更新」の処理 *****
				dialog.setTitle("");
				dialog.setMessage(R.string.message_update);
				dialog.setPositiveButton(CHAR_OK, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//電気係数の更新
						updateMasterData();
						tvMessage.setText(getString(R.string.message_updated));
					}

				});
				dialog.setNeutralButton(CHAR_CANCEL, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//キャンセル処理
						tvMessage.setText("");
					}
				});
				dialog.show();

				return true;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v == btnPrev) {
			//***** 前月ボタンの押下時の処理 *****
			entity.getKey().decrement();
			//スピナー要素の変更
			replaceSpinnerMonthElement();
			//DBデータを再表示する
			showDataArea();
		} else if (v == btnNext) {
			//***** 翌月ボタンの押下時の処理 *****
			entity.getKey().increment();
			//スピナー要素の変更
			replaceSpinnerMonthElement();
			//DBデータを再表示する
			showDataArea();
		} else if (v == btnFactor) {
			//***** 電気係数ボタンの押下時の処理 *****
			callFactorActivity();
		} else if (v == btnSave) {
			//***** データ保存ボタンの押下時の処理 *****
			if (validation()) {
				dialog.setTitle("");
				dialog.setMessage(R.string.message_save);
				dialog.setPositiveButton(CHAR_OK, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					//計算領域の表示
					showCalcArea();
					//データを保存
					saveTableInfo();
					String str = String.format(Locale.getDefault(), "%d", entity.getKey().getMonth());
					str += getString(R.string.word_month_for);
					str += getString(R.string.message_saved);
					tvMessage.setText(str);
					}

				});
				dialog.setNeutralButton(CHAR_CANCEL, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					//キャンセル処理
					tvMessage.setText("");
					}
				});
				dialog.show();
			}
		} else if (v == btnToggle) {
			//***** 図切替ボタンの押下時の処理 *****
			bChartToggle = !bChartToggle;    //トグルフラグ
			//DBデータを再表示する
			showDataArea();
		} else if (v == btnExpand) {
			//***** 推移表ボタンの押下時の処理 *****
			//グラフ表示アクティビティの呼び出し
			callChartActivity();
		}
	}

	/**
	 * 画面表示クリア処理
	 */
	private void clear() {
		edLastUsed.setText("");
		edCurrUsed.setText("");
		edAdjust.setText("");
		edAdjust.setTextColor(Color.BLACK);
		edMeterDay.setText("");
		edUsedKWH.setText("");
		edAmountKWH.setText("");
		edAverageUsed.setText("");
		edAverageAmount.setText("");
		edEstimateUsed.setText("");
		edEstimateAmount.setText("");
		tvMessage.setText("");
		tvMessage.setTextColor(Color.BLUE);
	}

	/**
	 * ボタン有効・無効の制御
	 */
	private void ctrlButtonEnable() {
//		if (ecoMan.isPrevExists(entity)) {
//			btnPrev.setEnabled(true);
//		} else {
//			btnPrev.setEnabled(false);
//		}
		if (ecoMan.isNextExists(entity)) {
			btnNext.setEnabled(true);
		} else {
			btnNext.setEnabled(false);
		}
	}

	/**
	 * 符号付入力欄の色を設定します。
	 * （正：黒、負：赤）
	 *
	 * @param edit エディットテキスト
	 */
	private void setMinusColor(EditText edit) {
		String strEdit = edit.getText().toString();
		edit.setTextColor(Color.BLACK);
		if (strEdit.equals("-")) {
			edit.setText("");
		} else if (!strEdit.equals("")) {
			try {
				if (Double.parseDouble(strEdit) < 0) {
					edit.setTextColor(Color.RED);
				}
			} catch (NumberFormatException e) {
				Toast.makeText(this, R.string.error_invalid_data, Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * スピナー（年月）の要素を変更する
	 */
	private void replaceSpinnerMonthElement() {
		arrAdapter_month.clear();
		electKeyList.clear();

		//カレンダーを0.5年前にセット
		ElectricKey baseKey = entity.getKey().copy();
		int iTerm = MONTHS_OF_YEAR / 2;
		for (int i = 0; i < iTerm; i++) {
			baseKey.decrement();
		}
		int ampere = baseKey.getAmpere();
		int year   = baseKey.getYear();
		int month  = baseKey.getMonth();
		int spinnerMonthIndex = 0;
		for (int i = 0; i < MONTHS_OF_YEAR; i++) {
			ElectricKey key = new ElectricKey(ampere, year, month);
			if (key.isFutureDate()) {
				break;
			}
			key.increment();
			year = key.getYear();
			month = key.getMonth();
			if (key.isEqualsDate(entity.getKey())) {
				spinnerMonthIndex = i;
			}
			//スピナーに追加
			arrAdapter_month.add(Integer.toString(year) + getString(R.string.word_year)
					+ Integer.toString(month) + getString(R.string.word_month_for));
			//電気キーを電気キーリストに追加
			electKeyList.add(key);
		}
		spinnerMonth.setAdapter(arrAdapter_month);
		try {
			spinnerMonth.setSelection(spinnerMonthIndex, false);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 計算領域の表示<P>
	 * 　使用量、請求金額等を計算し、表示する
	 */
	private void showCalcArea() {
		//画面の値を電気データにセット
		TableEntity entity = makeTableInfoByScreen();
		//計算処理
		double daysPast = ecoMan.calc(entity);

		//計算結果を表示
		edUsedKWH.setText(String.format(Locale.getDefault(), "%d", ecoMan.getUsedKWH()));
		edAmountKWH.setText(formatAmount.format(ecoMan.getAmountKWH()));
		edEstimateUsed.setText(String.format(Locale.getDefault(), "%d", ecoMan.getEstimateUsed()));
		edEstimateAmount.setText(formatAmount.format(ecoMan.getEstimateAmount()));
		edAverageUsed.setText(formatDouble.format(ecoMan.getAverageUsed()));
		edAverageAmount.setText(formatAmount.format(ecoMan.getAverageAmount()));

		//経過日数の表示
		String strMessage = tvMessage.getText().toString();
		if (strMessage.equals("")) {
			DecimalFormat formatDouble = new DecimalFormat("0.##");
			double days = daysPast * 100.0;
			String strDaysPast = formatDouble.format(Math.round(days) / 100.0);
			String str = getString(R.string.word_past_days) + "："
					+ strDaysPast
					+ getString(R.string.word_day);
			tvMessage.setTextColor(Color.BLUE);
			tvMessage.setText(str);
		}
	}

	/**
	 * DBの電気データを表示する
	 */
	private void showDataArea() {
		//画面表示クリア
		clear();

		//前月のデータを取得
		TableEntity entityPrev = ecoMan.findByKey(entity.getKey().getPrevKey());
		//当月のデータを取得
		TableEntity entityCurr = ecoMan.findByKey(entity.getKey());
		if (entityCurr == null) {	//DBにデータがない場合
			//前月のメータを使用（前月最終メータ＝今月開始メータ）
			if (entityPrev != null && entityPrev.getCurrUsed() != 0) {
				edLastUsed.setText(String.format(Locale.getDefault(), "%d", entityPrev.getCurrUsed()));
			}
			edAdjust.setText(String.format(Locale.getDefault(), "%.2f", DEFAULT_ADJUST));
			edMeterDay.setText(String.format(Locale.getDefault(), "%d", DEFAULT_METER_DAY));
		} else {
			if (entityPrev != null && entityPrev.getCurrUsed() != 0) {
				entityCurr.setLastUsed(entityPrev.getCurrUsed());
			}
			if (entityCurr.getLastUsed() != 0) {
				edLastUsed.setText(String.format(Locale.getDefault(), "%d", entityCurr.getLastUsed()));
			}
			if (entityCurr.getCurrUsed() != 0) {
				edCurrUsed.setText(String.format(Locale.getDefault(), "%d", entityCurr.getCurrUsed()));
			}
			edAdjust.setText(String.format(Locale.getDefault(), "%.2f", entityCurr.getAdjust()));
			if (entityCurr.getMeterDay() > 0) {
				edMeterDay.setText(String.format(Locale.getDefault(), "%d", entityCurr.getMeterDay()));
			} else {
				edMeterDay.setText(String.format(Locale.getDefault(), "%d", DEFAULT_METER_DAY));
			}
		}
		//マイナス値の赤表示
		setMinusColor(edAdjust);
		if (validation()) {
			//計算領域の表示
			showCalcArea();
		}

		//ミニ折れ線グラフの描画
		if (entityCurr != null) {
			graphLayout.removeAllViews();
			tscView = new TimeSeriesChartView(this, entityCurr.getKey(), getChartId());
			graphLayout.addView(tscView);
		}

		//ボタン有効・無効の制御
		ctrlButtonEnable();
	}

	/**
	 * 入力値のバリデーションチェック<p>
	 *
	 * @return		true:正常、false:不正
	 */
	private boolean validation() {
		boolean bool = true;

		String strLastUsed	= edLastUsed.getText().toString();
		String strCurrUsed	= edCurrUsed.getText().toString();
//		String strAdjust	= edAdjust.getText().toString();
		String strMeterDay	= edMeterDay.getText().toString();

		tvMessage.setTextColor(Color.RED);
		if (strLastUsed.equals("")) {
			String str =getString(R.string.label_last_used);
			str += getString(R.string.error_no_input);
			tvMessage.setText(str);
			bool = false;
		}
		if (strCurrUsed.equals("")) {
			String str = getString(R.string.label_curr_used);
			str += getString(R.string.error_no_input);
			tvMessage.setText(str);
			bool = false;
		}
		if (strMeterDay.equals("")) {
			strMeterDay = String.format(Locale.getDefault(), "%d", DEFAULT_METER_DAY);
		}

		if (bool) {
			int lastUsed = Integer.parseInt(strLastUsed);
			int currUsed = Integer.parseInt(strCurrUsed);
			int usedKWH = EcoManager.getProperMeter(lastUsed, currUsed);
			int meterDay = Integer.parseInt(strMeterDay);
			ElectricKey key = entity.getKey().copy();	//請求月の値は翌々月
			key.decrement();								//月の補正（対象は翌月）
			if (usedKWH >= MAX_USED_KWH) {
				String str = getString(R.string.error_used_too_big) + "："
						+ String.format(Locale.getDefault(), "%d", usedKWH)
						+ getString(R.string.label_kwh);
				tvMessage.setText(str);
				bool = false;
			} else if (!isDayOfMonth(key.getMonth(), meterDay)) {
				tvMessage.setText(getString(R.string.error_invalid_meter_date));
				bool = false;
			}
		}

		if (bool) {
			tvMessage.setTextColor(Color.BLUE);
			tvMessage.setText("");
		}

		return bool;
	}

	/**
	 * 電気データを保存する
	 */
	private void saveTableInfo() {
		//電気データを保存
		TableEntity entity = makeTableInfoByScreen();
		ecoMan.save(entity);

		//契約アンペアの規定値の更新処理（現行データのときのみ処理）
		int year     = entity.getKey().getYear();
		int month    = entity.getKey().getMonth();
		int meterDay = entity.getMeterDay();
		if (ecoMan.isLatestInfo(year, month, meterDay)) {
			SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit();
			editor.putInt(PREF_KEY_AMPERE, entity.getKey().getAmpere());
			editor.apply();
		}
	}

	/**
	 * 電気データに画面表示値をセットする
	 *
	 * @return	電気データ
	 */
	private TableEntity makeTableInfoByScreen() {
		entity.setLastUsed(parseInt(edLastUsed));
		entity.setCurrUsed(parseInt(edCurrUsed));
		entity.setAdjust(parseDouble(edAdjust));
		entity.setMeterDay(parseInt(edMeterDay));

		return entity;
	}

	/**
	 * 電気データにCSV値をセットする
	 *
	 * @param record	CSVレコード
	 * @return		電気データ
	 */
	private TableEntity makeTableInfoByCSV(String[] record) {
		TableEntity entity = new TableEntity();

		try {
			entity.setKey     (Integer.parseInt(record[0]),
							 Integer.parseInt(record[1]),
							 Integer.parseInt(record[2]));
			entity.setLastUsed(Integer.parseInt(record[3]));
			entity.setCurrUsed(Integer.parseInt(record[4]));
			entity.setAdjust  (Double.parseDouble(record[5]));
			entity.setMeterDay(Integer.parseInt(record[6]));
		} catch (NumberFormatException e) {
			Toast.makeText(this, R.string.error_invalid_data, Toast.LENGTH_LONG).show();
			return null;
		}

		return entity;
	}

	/**
	 * 電気係数一覧画面の呼び出し
	 */
	private void callFactorActivity() {
		Intent intent = new Intent(this, FactorActivity.class);

		EcoFactor factor = new EcoFactor(this);
		factor.setupFactors(entity.getKey());
		//出力は対象月
		ElectricKey key = entity.getKey();
		intent.putExtra(INTENT_TARGET_YEAR,  key.getYear());
		intent.putExtra(INTENT_TARGET_MONTH, key.getMonth());
		intent.putExtra(INTENT_BASE_CHARGE,	factor.getBaseCharge());
		intent.putExtra(INTENT_UNIT_PRICE1,	factor.getUnitPrice1());
		intent.putExtra(INTENT_UNIT_PRICE2,	factor.getUnitPrice2());
		intent.putExtra(INTENT_UNIT_PRICE3,	factor.getUnitPrice3());
		intent.putExtra(INTENT_RECYCLE_TAX,	factor.getRecycleTax());
		intent.putExtra(INTENT_SOLAR_TAX,		factor.getSolarTax());
		intent.putExtra(INTENT_ADJUST,		entity.getAdjust());

		startActivity(intent);
	}

	/**
	 * チャート画面の呼び出し
	 */
	private void callChartActivity() {
		Intent intent = new Intent(this, ChartActivity.class);
		//出力は対象月
		ElectricKey key = entity.getKey();
		intent.putExtra(INTENT_AMPERE, 		key.getAmpere());
		intent.putExtra(INTENT_TARGET_YEAR,  key.getYear());
		intent.putExtra(INTENT_TARGET_MONTH, key.getMonth());
		intent.putExtra(INTENT_CHART_ID,	    getChartId());

		startActivity(intent);
	}

	/**
	 * チャートIDの取得<P>
	 *     チャートは2種類とし、トグルさせる。
	 *
	 * @return		チャートID
	 */
	private int getChartId() {
		int chartId = CHART_USED_KWH;
		if (bChartToggle) {
			chartId = CHART_ADJUST;
		}

		return chartId;
	}

	/**
	 * CSVデータでマスタデータを更新
	 */
	private void updateMasterData() {
		InputStream is = null;
		BufferedReader br = null;

		try {
			is = getAssets().open(CSV_MASTER);
			br = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null) {
				MasterEntity entity = makeMasterInfoByCSV(line.split(CSV_SEPARATOR));
				if (entity != null) {
					new EcoFactor(this).save(entity);
				}
			}
		} catch (IOException e) {
			Toast.makeText(this, R.string.error_occured + "：open", Toast.LENGTH_LONG).show();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (is != null) {
					is.close();
				}
			} catch(IOException e) {
				Toast.makeText(this, R.string.error_occured + "：close", Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * マスタ情報をセット
	 *
	 * @param record		CSVデータ
	 * @return		マスタ情報
	 */
	private MasterEntity makeMasterInfoByCSV(String[] record) {
		MasterEntity entity = new MasterEntity();

		try {
			entity.setUnitType(record[0]);
			entity.setYear(Integer.parseInt(record[1]));
			entity.setMonth(Integer.parseInt(record[2]));
			entity.setMark(record[3]);
			entity.setUnitPrice(Double.parseDouble(record[4]));
			entity.setUnitPrice2(Double.parseDouble(record[5]));
			entity.setUnitPrice3(Double.parseDouble(record[6]));
		} catch (NumberFormatException e) {
			Toast.makeText(this, R.string.error_invalid_data, Toast.LENGTH_LONG).show();
			return null;
		}

		return entity;
	}

	/**
     * CSVファイルからDBにデータを復元します
     */
    private void importData() {
		//「/sdcard」を取得
		File directory = Environment.getExternalStorageDirectory();

		//フルパスを取得し、ファイルをオープンします
		String filepath = directory.getAbsolutePath() + CSV_TABLE_NAME;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					 new FileInputStream(filepath), CHARSET_NAME));

			//CSVファイルを読み、DBを更新します
			String line;
			while((line = reader.readLine()) != null) {
				//DBの更新
				TableEntity entity = makeTableInfoByCSV(line.split(CSV_SEPARATOR));
				if (entity != null) {
					new EcoManager(this).save(entity);
				}
			}
		} catch(FileNotFoundException e) {
			Toast.makeText(this, R.string.error_file_not_found + "："
					+ CSV_TABLE_NAME, Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Toast.makeText(this, R.string.error_occured + "：write", Toast.LENGTH_LONG).show();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch(IOException e) {
				Toast.makeText(this, R.string.error_occured + "：write", Toast.LENGTH_LONG).show();
			}
		}
    }

    /**
     * DBからデータをCSV出力します
     */
    private void exportData() {

		//「/sdcard」を取得
		File directory = Environment.getExternalStorageDirectory();
		//sdcardに書き込める状態かチェック
		if (directory.exists()){
			if(directory.canWrite()){
			//⇒書き込める状態の場合
				//フォルダを作成します。
				File path = new File(directory.getAbsolutePath() + CSV_DIR);
				if (!path.exists()) {
					boolean isDirectoryCreated = path.mkdir();
					if (isDirectoryCreated) {
						Toast.makeText(this, R.string.error_cannot_write + "：" + CSV_DIR,
								Toast.LENGTH_LONG).show();
						return;
					}
				}
			} else {
				Toast.makeText(this, R.string.error_cannot_write + "：" + CSV_DIR,
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		//フルパスを取得し、CSVファイルをオープンします
		String filepath = directory.getAbsolutePath() + CSV_TABLE_NAME;
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filepath, false), CHARSET_NAME));
		} catch (FileNotFoundException e) {
			Toast.makeText(this, R.string.error_file_not_found + "："
					+ CSV_TABLE_NAME, Toast.LENGTH_LONG).show();
			return;
		} catch (UnsupportedEncodingException e) {
			Toast.makeText(this, R.string.error_occured + "："
					+ CSV_TABLE_NAME, Toast.LENGTH_LONG).show();
			return;
		}

		ElectricKey baseKey = new ElectricKey();
		baseKey.increment();
		ElectricKey key = baseKey.copy();
		for (int ampere : ARRAY_AMPERE) {
			baseKey.setAmpere(ampere);
			key.setAmpere(ampere);
			key.setYear(baseKey.getYear() - BACKUP_TERM_YEAR);
			key.setMonth(baseKey.getMonth());

			while (true) {
				TableEntity entity = ecoMan.findByKey(key);

				if (entity != null) {
					try {
						writer.write(entity.toString());
						writer.newLine();
						writer.flush();
					} catch (IOException e) {
						Toast.makeText(this, R.string.error_occured + "：write",
								Toast.LENGTH_LONG).show();
					}
				}
				if (key.isEquals(baseKey)) {
					break;
				} else {
					key.increment();
				}
			}
		}
		try {
			writer.close();
		} catch (IOException e) {
			Toast.makeText(this, R.string.error_occured + "：close", Toast.LENGTH_LONG).show();
		}
    }
}
