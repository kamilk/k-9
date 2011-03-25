package com.fsck.k9.messagefilter;

import com.fsck.k9.mail.Message;
import com.fsck.k9.mail.MessagingException;

/**
 * Filtering criteria based on comparision of a specified string field with
 * a given value
 */
public abstract class FilteringCriterion {
    protected String mReferenceValue;

    /**
     * Constructor creating a criteria performing operation: [field] [operation] [value]
     *   (for example "subject contains 'spam'"
     * @param operation Operation to be performed on the field
     * @param value Reference value for the criteria
     */
    public FilteringCriterion(String value) {
        mReferenceValue = value;
    }

    public abstract boolean check(final Message message) throws MessagingException;
}
