package com.fsck.k9.activity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.fsck.k9.R;
import com.fsck.k9.activity.K9Activity;

public class MessageFilterEdit extends K9Activity {
	private LinearLayout mCriterionList;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.message_filter_edit);

		mCriterionList = (LinearLayout)findViewById(R.id.criterion_list);
		getLayoutInflater().inflate(R.layout.message_filter_criterion, mCriterionList);
	}
}
