package com.pgw.mobilesafe.ui;

import com.pgw.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SetingItemView extends RelativeLayout {
	private TextView tv_title, tv_desc;
	private CheckBox cb_status;
	private String desc_on;
	private String desc_off;

	private void initView(Context context) {
		View.inflate(context, R.layout.seting_item_view, this);
		tv_title = (TextView) this.findViewById(R.id.tv_item_title);
		tv_desc = (TextView) this.findViewById(R.id.tv_item_desc);
		cb_status = (CheckBox) this.findViewById(R.id.cb_item_status);
	}

	public SetingItemView(Context context) {
		super(context);
		initView(context);
	}

	public SetingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		// 得到参数的值
		String namespace = "http://schemas.android.com/apk/res/com.pgw.mobilesafe";
		String title=attrs.getAttributeValue(namespace,"title");
		 desc_on = attrs.getAttributeValue(namespace, "desc_on");
		 desc_off = attrs.getAttributeValue(namespace,"desc_off");
		
//		 设置标题
		 tv_title.setText(title);
		 tv_desc.setText(desc_off);
	}

	public SetingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	/**
	 * 获取选中状态
	 * 
	 * @return
	 */
	public boolean isChecked() {
		return cb_status.isChecked();
	}

	/**
	 * 设置选中状态
	 * 
	 * @param b
	 */
	public void setChecked(boolean b) {
//		判断是否选中，并更改描述信息
		if (b) {
			setDesc(desc_on);
		}else {
			setDesc(desc_off);
		}
		cb_status.setChecked(b);
	}

	/**
	 * 设置描述信息
	 * 
	 * @param string
	 */
	public void setDesc(String desc) {
		tv_desc.setText(desc);
	}

}
