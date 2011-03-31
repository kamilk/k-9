package com.fsck.k9.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.fsck.k9.R;
import com.fsck.k9.activity.K9Activity;
import com.fsck.k9.messagefilter.AddressCriterion;
import com.fsck.k9.messagefilter.AddressCriterion.Field;
import com.fsck.k9.messagefilter.FilteringCriterion;
import com.fsck.k9.messagefilter.MessageFilter;
import com.fsck.k9.messagefilter.SubjectCriterion;

/**
 * Message filter editor
 */
public class MessageFilterEdit extends K9Activity {
    private LinearLayout mCriterionList;
    private ArrayAdapter<String> mFieldAdapter;
    private ArrayAdapter<String> mOperandAdapter;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        //fill the content
        setContentView(R.layout.message_filter_edit);

        //find the place where criterion fields will be placed
        mCriterionList = (LinearLayout)findViewById(R.id.criterion_list);

        //bind the addNewCriterionField function to the "add" button
        Button addButton = (Button)findViewById(R.id.add_new_criterion);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCriterionField();
            }
        });

        Resources res = getResources();

        //create an adapter for the field spinner
        String[] fields = new String[] {
        	//IMPORTANT If you add, remove an entry or change their order you
        	//also need to update the indices in createFilterFromInput()
        	res.getString(R.string.account_settings_filter_field_subject), //0
        	res.getString(R.string.account_settings_filter_field_from), //1
        	res.getString(R.string.account_settings_filter_field_to) //2
        };
        mFieldAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fields);

        String[] operands = new String[] {
    		//IMPORTANT If you add, remove an entry or change their order you
        	//also need to update the indices in createFilterFromInput()
        	res.getString(R.string.account_settings_filter_operand_contains),
        	res.getString(R.string.account_settings_filter_operand_is)
        };
        mOperandAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, operands);

        addNewCriterionField();
    }

    @Override
    public void onPause() {
    	createFilterFromInput();

    	super.onPause();
    }

    /**
     * Adds an extra row for a new criterion
     */
    public void addNewCriterionField() {
    	View root = getLayoutInflater().inflate(R.layout.message_filter_criterion, null);

    	Spinner field_spinner = (Spinner)root.findViewById(R.id.criterion_field);
    	field_spinner.setAdapter(mFieldAdapter);

    	Spinner operand_spinner = (Spinner)root.findViewById(R.id.criterion_operand);
    	operand_spinner.setAdapter(mOperandAdapter);

    	mCriterionList.addView(root);
    }

    public MessageFilter createFilterFromInput() {
    	EditText name_view = (EditText)findViewById(R.id.filter_name);
    	String name = name_view.getText().toString();

    	RadioButton all_button = (RadioButton)findViewById(R.id.criteria_all);
    	boolean all = all_button.isChecked();

    	MessageFilter filter = new MessageFilter(name, all);

    	LinearLayout criterion_list = (LinearLayout)findViewById(R.id.criterion_list);
    	for (int i = 0; i < criterion_list.getChildCount(); i++) {
    		RelativeLayout criterion_layout = (RelativeLayout)criterion_list.getChildAt(i);

    		Spinner field_spinner = (Spinner)criterion_layout.findViewById(R.id.criterion_field);
    		long field_id = field_spinner.getSelectedItemId();

    		Spinner operand_spinner = (Spinner)criterion_layout.findViewById(R.id.criterion_operand);
    		long operand_id = operand_spinner.getSelectedItemId();

    		EditText value_edittext = (EditText)criterion_layout.findViewById(R.id.criterion_value);
    		String value = value_edittext.getText().toString();

    		FilteringCriterion criterion = null;
    		if (field_id == 0) { //subject
    			SubjectCriterion.Operand operand;
    			if (operand_id == 0) { //contains
    				operand = SubjectCriterion.Operand.CONTAINS;
    			} else { //is
    				operand = SubjectCriterion.Operand.EQUALS;
    			}
    			criterion = new SubjectCriterion(operand, value);
    		} else if (field_id == 1 || field_id == 2) { //from or to
    			AddressCriterion.Field address_field;
    			if (field_id == 1) { //from
    				address_field = Field.FROM;
    			} else { //to
    				address_field = Field.TO;
    			}
    			criterion = new AddressCriterion(address_field, value);
    		}
    		if (criterion != null) {
    			filter.addCriterion(criterion);
    		}
    	}

    	return filter;
    }
}
