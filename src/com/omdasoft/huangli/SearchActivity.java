package com.omdasoft.huangli;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import net.yihabits.huangli.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class SearchActivity extends Activity {

	private static final String[] types = { "", "嫁娶", "订盟", "纳采", "裁衣", "合帐",
			"挽面", "纳婿", "祭祀", "祈福", "斋醮", "安香", "求嗣", "开光", "塑绘", "谢土", "解除",
			"造庙", "造桥", "拆卸", "修造", "动土", "破土", "安门", "作厕", "移徙", "筑堤", "入宅",
			"出火", "作灶", "开池", "伐木", "冠笄", "问名", "安床", "开容", "启钻", "起基", "修坟",
			"安葬", "立碑", "入殓", "移柩", "成服", "除服", "交易", "开市", "纳财", "立券", "作染",
			"鼓铸", "酝酿", "造船", "买车", "挂匾", "分居", "出行", "入学", "沐浴", "理发", "赴任",
			"求医", "捕捉", "割蜜", "取渔", "栽种", "结网", "牧养", "纳畜", "破屋", "坏垣", "竖柱",
			"上梁", "开渠", "掘井", "扫舍", "探病", "酬神", "雕刻", "见贵", "开仓", "习艺", "雇庸",
			"置产", "造屋", "合脊", "架马", "定磉", "作梁", "补垣", "塞穴", "针灸", "经络", "治病",
			"词讼", "畋猎", "造仓", "乘船", "放水", "渡水", "修门", "扫舍", "进人口", "会亲友",
			"安碓?", "开生坟", "教牛马", "出货财", "开柱眼", "安机械", "造车器", "修饰垣墙", "平治道涂",
			"整手足甲", "造畜?栖" };

	private int fromYear;
	private int fromMonth;
	private int fromDay;
	private int toYear;
	private int toMonth;
	private int toDay;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		Button saveBtn = (Button) findViewById(R.id.searchBtn);
		saveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search();

			}
		});

		DatePicker fromDate = (DatePicker) findViewById(R.id.fromDate);
		Calendar now = Calendar.getInstance();
		this.fromYear = now.get(Calendar.YEAR);
		this.fromMonth = now.get(Calendar.MONDAY);
		this.fromDay = now.get(Calendar.DAY_OF_MONTH);
		this.toYear = this.fromYear;
		this.toMonth = this.fromMonth + 1;
		this.toDay = this.fromDay;
		fromDate.init(now.get(Calendar.YEAR), now.get(Calendar.MONDAY), now.get(Calendar.DAY_OF_MONTH),
				new MyListener());
		DatePicker toDate = (DatePicker) findViewById(R.id.toDate);
		toDate.init(now.get(Calendar.YEAR), now.get(Calendar.MONDAY)+1, now.get(Calendar.DAY_OF_MONTH),
				new MyListener());

		Spinner spinner = (Spinner) findViewById(R.id.typeSpinner);
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner, types);
		mAdapter.setDropDownViewResource(R.layout.spinner);
		spinner.setAdapter(mAdapter);
	}

	private class MyListener implements DatePicker.OnDateChangedListener {

		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			if (year > 2018 || year < 2010) {
				SearchActivity.this.toastMsg(SearchActivity.this
						.getString(R.string.yearValidate));
			} else {
				if (view.getId() == R.id.fromDate) {
					SearchActivity.this.fromYear = year;
					SearchActivity.this.fromMonth = monthOfYear;
					SearchActivity.this.fromDay = dayOfMonth;
				} else {
					SearchActivity.this.toYear = year;
					SearchActivity.this.toMonth = monthOfYear;
					SearchActivity.this.toDay = dayOfMonth;
				}
			}

		}

	}

	private void search() {
		String type = "1";
		RadioGroup typeRadio = (RadioGroup) findViewById(R.id.typeRadio);
		if (typeRadio.getCheckedRadioButtonId() == R.id.badRadio) {
			type = "0";
		}

		Spinner spinner = (Spinner) findViewById(R.id.typeSpinner);
		String contentType = (String) spinner.getSelectedItem();
		try {
			contentType = URLEncoder.encode(contentType, "gb2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = "http://www.99wed.com/tools/huangli_search.php?year_from="
				+ this.fromYear + "&month_from=" + this.fromMonth
				+ "&day_from=" + this.fromDay + "&year_to=" + this.toYear
				+ "&month_to=" + this.toMonth + "&day_to=" + this.toDay
				+ "&sty=" + type + "&key=" + contentType;

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setClass(this, DailyHuangliActivity.class);
		intent.putExtra("url", url);
		startActivity(intent);
	}

	public void toastMsg(final String msg) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG)
						.show();
			}
		});
	}
}