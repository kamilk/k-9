package com.fsck.k9.messagefilter;

import android.content.ContentValues;

import com.fsck.k9.mail.Address;
import com.fsck.k9.mail.Message;
import com.fsck.k9.mail.Message.RecipientType;
import com.fsck.k9.mail.MessagingException;

/**
 * A criterion checking a field of a message containing addresses
 *   (like From and To)
 */
public class AddressCriterion extends StringCriterion {
    public enum Field {
        FROM, TO
    }

    Field mField;

    public AddressCriterion(Field field, String value) {
        super(value);
        mField = field;
    }

    @Override
    public boolean check(Message message) throws MessagingException {
        Address[] addresses = null;

        switch (mField) {
        case FROM:
            addresses = message.getFrom();
            break;
        case TO:
            addresses = message.getRecipients(RecipientType.TO);
            break;
        }

        if (addresses == null)
            return false;

        for (Address address : addresses) {
            if (address.getAddress().equalsIgnoreCase(mReferenceValue))
                return true;
        }
        return false;
    }

    public String getDatabaseTableName() {
    	return "filter_criteria_address";
    }

	@Override
	public ContentValues getDatabaseValues() {
		ContentValues result = new ContentValues();
		switch(mField) {
		case FROM:
			result.put("field", MessageFilterManager.FIELD_FROM);
			break;
		case TO:
			result.put("field", MessageFilterManager.FIELD_TO);
			break;
		}
		result.put("value", mReferenceValue);
		return result;
	}

}
