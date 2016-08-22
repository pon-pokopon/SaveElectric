package jp.pon.saveelectric.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.view.MotionEvent;
import android.view.Window;

import jp.pon.saveelectric.R;
import jp.pon.saveelectric.activity.MainActivity;

import static jp.pon.saveelectric.Const.PREF_NAME;

/**
 * タイトル画面
 *
 * @author pon
 *
 */
public class TitleActivity extends Activity {

	public static final String ID_SHOW_TITLE 	= "show_title";
	public static final int SHOW_TITLE 		= 0;
	public static final int DO_NOT_SHOW_TITLE = 1;

	private boolean bActivityCalled = false;

	SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

		pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		int showTitle = pref.getInt(ID_SHOW_TITLE, SHOW_TITLE);

		if (showTitle == DO_NOT_SHOW_TITLE) {
			callActivity();
		} else {
			setContentView(R.layout.layout_title);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		CheckBox cb = (CheckBox) findViewById(R.id.check_no_title);

		if (cb.isChecked()) {
			SharedPreferences.Editor editor = pref.edit();
			editor.putInt(ID_SHOW_TITLE, DO_NOT_SHOW_TITLE);
			editor.apply();
		}

		if (!bActivityCalled) {
			bActivityCalled = true;
			callActivity();
		}

		return true;
	}

	/**
	 * アクティビティの呼び出し
	 */
	private void callActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}
}
