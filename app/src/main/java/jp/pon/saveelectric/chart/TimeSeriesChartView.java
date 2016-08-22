package jp.pon.saveelectric.chart;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static jp.pon.saveelectric.Const.*;

import jp.pon.saveelectric.R;
import jp.pon.saveelectric.model.EcoManager;
import jp.pon.saveelectric.model.ElectricKey;
import jp.pon.saveelectric.entity.TableEntity;

import org.afree.chart.AFreeChart;
import org.afree.chart.ChartFactory;
import org.afree.chart.axis.DateAxis;
import org.afree.chart.axis.ValueAxis;
import org.afree.chart.plot.XYPlot;
import org.afree.chart.renderer.xy.XYItemRenderer;
import org.afree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.afree.chart.title.LegendTitle;
import org.afree.chart.title.TextTitle;
import org.afree.data.time.Month;
import org.afree.data.time.TimeSeries;
import org.afree.data.time.TimeSeriesCollection;
import org.afree.data.xy.XYDataset;
import org.afree.graphics.SolidColor;
import org.afree.graphics.geom.Font;
import org.afree.ui.RectangleInsets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

/**
 * 折れ線グラフビュー
 *
 * @author pon
 *
 */
public class TimeSeriesChartView extends ChartView {
	Activity activity;			//アクティビティ

	EcoManager ecoMan;			//エコロジーマネージャー
    AFreeChart chart;			//チャート
	ElectricKey electKey;		//電気データ
	int chartId;         		//チャートID

	/**
	 * コンストラクター
	 *
	 * @param context		コンテキスト
	 */
	public TimeSeriesChartView(Context context) {
		super(context);
	}

   /**
	* コンストラクター
	*
	* @param activity		アクティビティ
	* @param electKey		電気キー
	* @param chartId		チャートID
	*/
	public TimeSeriesChartView(Activity activity,
							   ElectricKey electKey,
							   int chartId) {
		super(activity);
		this.activity = activity;
		this.ecoMan	= new EcoManager(activity);

		this.electKey = electKey.copy();
		this.chartId  = chartId;

		XYDataset dataSet = createDataSet();
		if (dataSet != null) {
			chart = createChart(dataSet);
			setChart(chart);
		}
	}

	/**
	 * 折れ線グラフを描画します。
	 *
	 * @param dataSet	データセット
	 * @return		チャート
	 */
    private AFreeChart createChart(XYDataset dataSet) {

        AFreeChart chart;
        if (chartId == CHART_USED_KWH) {
	        chart = ChartFactory.createTimeSeriesChart(
					activity.getString(R.string.word_consumption) +
					activity.getString(R.string.word_parenthesis_l) +
					electKey.getAmpere() + "A" +
					activity.getString(R.string.word_parenthesis_r) +
					activity.getString(R.string.space_zen_1) +
					activity.getString(R.string.word_transition),  		// title
		            "",            				// x-axis label
		            activity.getString(R.string.label_kwh_consumption),	// y-axis label
		            dataSet,            		// data
		            true,               		// create legend?
		            true,               		// generate tooltips?
		            false               		// generate URLs?
		        );
        } else if (chartId == CHART_ADJUST) {
	        chart = ChartFactory.createTimeSeriesChart(
					activity.getString(R.string.label_adjust) +
					activity.getString(R.string.space_zen_1) +
					activity.getString(R.string.word_transition),		// title
		            "",             			// x-axis label
					activity.getString(R.string.label_yen_kwh_unit),	// y-axis label
		            dataSet,            		// data
		            true,               		// create legend?
		            true,               		// generate tooltips?
		            false               		// generate URLs?
		        );
        } else {
        	return null;
        }

		//*********** フォント ***************
		// グラフタイトル
		TextTitle title = chart.getTitle();
//		title.setFont(new Font("ＭＳ 明朝", Typeface.BOLD | Typeface.ITALIC, 35));
		title.setFont(new Font(Typeface.SANS_SERIF, Typeface.BOLD, 40));

		// 横軸・縦軸
        XYPlot plot = (XYPlot) chart.getPlot();
		ValueAxis xAxis = plot.getDomainAxis();
		ValueAxis yAxis = plot.getRangeAxis();
		Font xyLabelFont = new Font(Typeface.SANS_SERIF, Typeface.NORMAL, 30);
		xAxis.setLabelFont(xyLabelFont);
		yAxis.setLabelFont(xyLabelFont);
		Font xyTickLabelFont = new Font(Typeface.SANS_SERIF, Typeface.NORMAL, 30);
		xAxis.setTickLabelFont(xyTickLabelFont);
		yAxis.setTickLabelFont(xyTickLabelFont);

		// 凡例
		LegendTitle legend = chart.getLegend();
		legend.setItemFont(new Font(Typeface.SANS_SERIF, Typeface.NORMAL, 30));

		//*********** 配色等 ***************
		if (activity instanceof ChartActivity) {
			chart.setBackgroundPaintType(new SolidColor(Color.WHITE));
		} else {
			chart.setBackgroundPaintType(new SolidColor(Color.rgb(190, 245, 245)));
		}

		if (chartId == CHART_USED_KWH) {
			if (activity instanceof ChartActivity) {
				plot.setBackgroundPaintType(new SolidColor(Color.rgb(210, 233, 255)));
			} else {
				plot.setBackgroundPaintType(new SolidColor(Color.WHITE));
			}
        } else if (chartId == CHART_ADJUST) {
        	plot.setBackgroundPaintType(new SolidColor(Color.rgb(255, 255, 200)));
        } else {
        	return null;
        }
        plot.setDomainGridlinePaintType(new SolidColor(Color.DKGRAY));
        plot.setRangeGridlinePaintType(new SolidColor(Color.DKGRAY));
        plot.setAxisOffset(new RectangleInsets(1d, 1d, 1d, 20d));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
            renderer.setDrawSeriesLineAsPath(true);
        }

        DateAxis dAxis = (DateAxis) plot.getDomainAxis();
		dAxis.setDateFormatOverride(new SimpleDateFormat("yyyy/MM", Locale.JAPAN));

        return chart;
    }

    /**
     * データセット（2年分）を作成します
     *
     * @return		データセット
     */
    private XYDataset createDataSet() {
		int year;
		int month;

		year  = electKey.getYear();
		electKey.setYear(year - 2);		//2年分
		electKey.increment();
		year = electKey.getYear();			//描画開始年
        month = electKey.getMonth();		//描画開始月

        TimeSeriesCollection dataSet;
        TimeSeries ts1 = new TimeSeries(activity.getString(R.string.word_last_year));
        TimeSeries ts2 = new TimeSeries(activity.getString(R.string.word_this_year));

        for (int i = 0; i < CHART_TERM_MONTH; i++) {	//2年分表示
        	//電気データを取得
        	TableEntity entity = ecoMan.findByKey(electKey.getAmpere(), year, month);
        	if (entity != null) {	//レコードがあった場合
                if (chartId == CHART_USED_KWH) {
            		//電気使用量をセット
                	int usedKWH = EcoManager.getProperMeter(entity.getLastUsed(), entity.getCurrUsed());
                	if ((i + 1) == CHART_TERM_MONTH) {
                		//当月の場合は使用量を予測する
						ecoMan.calc(entity);
                		usedKWH *= ecoMan.getConvertRate();
                	}
                	if (i < MONTHS_OF_YEAR) {
                		ts1.add(new Month(month, year + 1), usedKWH);
                	} else {
                		ts2.add(new Month(month, year), usedKWH);
                	}
                } else if (chartId == CHART_ADJUST) {
            		//燃料費調整をセット
                	if (i < MONTHS_OF_YEAR) {
                		ts1.add(new Month(month, year + 1), entity.getAdjust());
                	} else {
                		ts2.add(new Month(month, year), entity.getAdjust());
                	}
                } else {
                	return null;
                }
        	} else {	//レコードがなかった場合
        		//値が0のダミーデータをセット
            	if (i < MONTHS_OF_YEAR) {
            		ts1.add(new Month(month, year + 1), 0.0);
            	} else {
            		ts2.add(new Month(month, year), 0.0);
            	}
        	}
        	month++;
        	//年を補正
        	if (month > MONTHS_OF_YEAR) {
        		month = 1;
        		year++;
        	}
        }
        dataSet = new TimeSeriesCollection();
        dataSet.addSeries(ts1);
        dataSet.addSeries(ts2);

        return dataSet;
    }
}
