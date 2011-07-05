package com.fsck.k9.messagefilter;

import android.content.ContentValues;

import com.fsck.k9.mail.Message;
import com.fsck.k9.mail.MessagingException;

/**
 * A class for all criteria which take no arguments.
 *
 * They are all stored as boolean fields in a single database table.
 *
 * Currently the only implemented criterion is checking for a spam flag.
 */
public class SimpleCriteria extends FilteringCriterion {
	boolean mSpamFlag = false;

	@Override
	public boolean check(Message message) throws MessagingException {
		if (mSpamFlag && message.getSpamFlag() != null &&
				message.getSpamFlag().equalsIgnoreCase("yes")) {
			return true;
		}
		return false;
	}

	@Override
	public String getDatabaseTableName() {
		return "filter_criteria_simple";
	}

	@Override
	public ContentValues getDatabaseValues() {
		ContentValues result = new ContentValues();
		result.put("spam_flag", mSpamFlag);
		return result;
	}

	/**
	 * Turns checking for a spam flag on or off.
	 * @param on true if checking should be turned on, false if off
	 */
	public void turnSpamFlagOnOff(boolean on) {
		mSpamFlag = on;
	}

}
