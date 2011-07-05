package com.fsck.k9.messagefilter;

import android.content.ContentValues;

import com.fsck.k9.mail.Message;

/**
 * A criterion checking the subject of a message
 */
public class SubjectCriterion extends StringCriterion {
    /**
     * Enumaration type representing operations which can be used in this criteria
     */
    public enum Operand {
        EQUALS, CONTAINS
    }

    Operand mOperand;

    public SubjectCriterion(Operand operation, String value) {
        super(value);
        mOperand = operation;
    }

    @Override
    public boolean check(final Message message) {
        String subject = message.getSubject();
        if (subject != null) {
            switch (mOperand) {
            case CONTAINS:
                return subject.contains(mReferenceValue);
            case EQUALS:
                return subject.equals(mReferenceValue);
            }
        }
        return false;
    }

    public String getDatabaseTableName() {
    	return "filter_criteria_subject";
    }

	@Override
	public ContentValues getDatabaseValues() {
		ContentValues result = new ContentValues();
		switch(mOperand) {
		case CONTAINS:
			result.put("operand", MessageFilterManager.OPERAND_STRING_CONTAINS);
			break;
		case EQUALS:
			result.put("operand", MessageFilterManager.OPERAND_STRING_IS);
			break;
		}
		result.put("value", mReferenceValue);
		return result;
	}
}
