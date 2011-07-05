package com.fsck.k9.messagefilter;

import android.content.ContentValues;

import com.fsck.k9.mail.Message;
import com.fsck.k9.mail.MessagingException;

/**
 * Filtering criteria based on comparision of a specified string field with
 * a given value
 */
public abstract class FilteringCriterion {
    protected long mDatabaseId;

    /**
     * Constructor
     */
    public FilteringCriterion() {
        mDatabaseId = -1;
    }

    public abstract boolean check(final Message message) throws MessagingException;

    /**
     * Get the name of the database table where the criterion should be stored.
     * @return
     */
    abstract public String getDatabaseTableName();

    /**
     * Get the map binding column names with values which should be stored in the columns.
     * @return
     */
    public abstract ContentValues getDatabaseValues();

    /**
     * Get the id of the current criterion in the database.
     * -1 if the criterion is not yet in the database.
     * @return
     */
    public long getDatabaseId() {
    	return mDatabaseId;
    }

    public void setDatabaseId(long id) {
    	mDatabaseId = id;
    }
}
