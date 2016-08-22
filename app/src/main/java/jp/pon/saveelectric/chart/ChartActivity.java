package jp.pon.saveelectric.chart;

import static jp.pon.saveelectric.Const.*;

import jp.pon.saveelectric.model.ElectricKey;

import android.os.Bundle;
import android.view.Window;
import android.app.Activity;
import android.content.pm.ActivityInfo;

/**
 * チャートアクティビィティ
 * <p/>
 * 　受け取った年月から2年分の折れ線グラフを描画します
 *
 * @author pon
 */
public class ChartActivity extends Activity {
	//*************** 引継ぎ情報 ************************
	int ampere;           	//契約アンペア
	int targetYear;     	//対象年
	int targetMonth;   	//対象月
	int chartId;        	//チャートID
	//***************************************************

	ElectricKey electKey;	//電気データ

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		//引継ぎ情報を受け取る
		ampere			= getIntent().getIntExtra(INTENT_AMPERE, 			15);
		targetYear		= getIntent().getIntExtra(INTENT_TARGET_YEAR, 	2015);
		targetMonth	= getIntent().getIntExtra(INTENT_TARGET_MONTH, 	1);
		chartId		= getIntent().getIntExtra(INTENT_CHART_ID, CHART_USED_KWH);

		//電気キーをセット
		electKey = new ElectricKey(ampere, targetYear, targetMonth);
		//折れ線グラフの作成
		TimeSeriesChartView tscView = new TimeSeriesChartView(this, electKey, chartId);
		setContentView(tscView);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
}