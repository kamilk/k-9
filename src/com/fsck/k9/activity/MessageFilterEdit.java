package com.fsck.k9.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.FrameLayout;

import com.fsck.k9.R;
import com.fsck.k9.activity.K9Activity;

/**
 * An activity for editing a message filter
 */
public class MessageFilterEdit extends K9Activity {
    private LinearLayout mCriterionList;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        //fill the content
        setContentView(R.layout.message_filter_edit);
        mCriterionList = (LinearLayout)findViewById(R.id.criterion_list);

        Button addButton = (Button)findViewById(R.id.add_new_criterion);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCriterionField();
            }
        });

        addNewCriterionField();
    }

    public void addNewCriterionField() {
        FrameLayout criterion_frame = new FrameLayout(this);
        criterion_frame.setClipChildren(false);
        getLayoutInflater().inflate(R.layout.message_filter_criterion, criterion_frame);
        mCriterionList.addView(criterion_frame);
    }
}
