package jp.pon.saveelectric.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

import jp.pon.saveelectric.R;

import static jp.pon.saveelectric.Const.*;

/**
 * 電気係数一覧アクティビティ
 */
public class FactorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_factor);

		//引継ぎ情報を受け取る
		int 	targetYear	= getIntent().getIntExtra(INTENT_TARGET_YEAR, 2015);
		int 	targetMonth	= getIntent().getIntExtra(INTENT_TARGET_MONTH,   1);
		double baseCharge	= getIntent().getDoubleExtra(INTENT_BASE_CHARGE, 0);
		double unitPrice1	= getIntent().getDoubleExtra(INTENT_UNIT_PRICE1, 0);
		double unitPrice2	= getIntent().getDoubleExtra(INTENT_UNIT_PRICE2, 0);
		double unitPrice3	= getIntent().getDoubleExtra(INTENT_UNIT_PRICE3, 0);
		double recycleTax	= getIntent().getDoubleExtra(INTENT_RECYCLE_TAX, 0);
		double solarTax	= getIntent().getDoubleExtra(INTENT_SOLAR_TAX, 	0);
		double adjust		= getIntent().getDoubleExtra(INTENT_ADJUST,	 	0);

		TextView txFactorDate = (TextView) findViewById(R.id.factor_date);
		TextView txBaseCharge = (TextView) findViewById(R.id.base_charge);
		TextView txUnitPrice1 = (TextView) findViewById(R.id.unit_price1);
		TextView txUnitPrice2 = (TextView) findViewById(R.id.unit_price2);
		TextView txUnitPrice3 = (TextView) findViewById(R.id.unit_price3);
		TextView txRecycleTax = (TextView) findViewById(R.id.recycle_tax);
		TextView txSolarTax   = (TextView) findViewById(R.id.solar_tax);
		TextView txAdjust	  = (TextView) findViewById(R.id.adjust);

		//電気係数の表示
		String strDate = targetYear  + getString(R.string.word_year)
					   + targetMonth + getString(R.string.word_month);
		txFactorDate.setText(strDate);
		txBaseCharge.setText(String.format(Locale.getDefault(), "%.2f", baseCharge));
		txUnitPrice1.setText(String.format(Locale.getDefault(), "%.2f", unitPrice1));
		txUnitPrice2.setText(String.format(Locale.getDefault(), "%.2f", unitPrice2));
		txUnitPrice3.setText(String.format(Locale.getDefault(), "%.2f", unitPrice3));
		txRecycleTax.setText(String.format(Locale.getDefault(), "%.2f", recycleTax));
		txSolarTax.setText  (String.format(Locale.getDefault(), "%.2f", solarTax));
		txAdjust.setText    (String.format(Locale.getDefault(), "%.2f", adjust));
	}
}
