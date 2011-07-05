package com.fsck.k9.messagefilter;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fsck.k9.Account;
import com.fsck.k9.AccountDatabase;
import com.fsck.k9.AccountDatabaseUpgradeListener;
import com.fsck.k9.K9;
import com.fsck.k9.controller.MessagingController;
import com.fsck.k9.mail.Message;
import com.fsck.k9.mail.MessagingException;
import com.fsck.k9.mail.store.LocalStore.LocalFolder;
import com.fsck.k9.mail.store.LocalStore.LocalMessage;
import com.fsck.k9.mail.store.LockableDatabase.DbCallback;
import com.fsck.k9.mail.store.LockableDatabase.WrappedException;
import com.fsck.k9.mail.store.UnavailableStorageException;

/**
 * Manager of multiple message filters for a particular account
 */
public class MessageFilterManager {
	public static final int FIELD_SUBJECT = 1;
	public static final int FIELD_FROM = 2;
	public static final int FIELD_TO = 3;

	public static final int OPERAND_STRING_IS = 1;
	public static final int OPERAND_STRING_CONTAINS = 2;

    private AccountDatabase mAccountDatabase;
    private ArrayList<MessageFilter> mFilters = new ArrayList<MessageFilter>();

    public class MessageFilterSchemaDefinition extends AccountDatabaseUpgradeListener {
        @Override
        public void onDatabaseUpgrade(final AccountDatabase accountDb, final SQLiteDatabase db) {
            Log.i(K9.LOG_TAG, String.format("Upgrading message filter database from version %d to version %d",
                                            db.getVersion(), accountDb.getVersion()));

            if (db.getVersion() < 44) {
            	//for debugging purposes
//            	db.execSQL("DROP TABLE IF EXISTS filter_criteria_subject");
//            	db.execSQL("DROP TABLE IF EXISTS filter_criteria_address");
//            	db.execSQL("DROP TABLE IF EXISTS filter_criteria_simple");
//            	db.execSQL("DROP TABLE IF EXISTS filters");

                db.execSQL("CREATE TABLE filters (id INTEGER PRIMARY KEY, name TEXT)");
                db.execSQL("CREATE TABLE filter_criteria_subject (id INTEGER PRIMARY KEY, filter_id INTEGER NULL, operand INTEGER NOT NULL, value TEXT NOT NULL)");
                db.execSQL("CREATE INDEX filter_criteria_subject_filter_id ON filter_criteria_subject (filter_id)");
                db.execSQL("CREATE TABLE filter_criteria_address (id INTEGER PRIMARY KEY, filter_id INTEGER NOT NULL, field INTEGER NOT NULL, value TEXT NOT NULL)");
                db.execSQL("CREATE INDEX filter_criteria_address_filter_id ON filter_criteria_address (filter_id)");
                db.execSQL("CREATE TABLE filter_criteria_simple (id INTEGER PRIMARY KEY, filter_id INTEGER NOT NULL, spam_flag BOOLEAN NOT NULL)");
                db.execSQL("CREATE INDEX filter_criteria_simple_filter_id ON filter_criteria_simple (filter_id)");

                db.execSQL("CREATE TRIGGER delete_filter BEFORE DELETE ON filters BEGIN " +
                		"DELETE FROM filter_criteria_subject WHERE filter_id=OLD.id; " +
                		"DELETE FROM filter_criteria_address WHERE filter_id=OLD.id; " +
                		"DELETE FROM filter_criteria_simple WHERE filter_id=OLD.id; END;");
            }
        }
    }

    public MessageFilterManager(Account account) {
        mAccountDatabase = account.getAccountDatabase();
        mAccountDatabase.addListener(new MessageFilterSchemaDefinition());
    }

    /**
     * Save all the filters to the database
     * @throws UnavailableStorageException
     */
    public void save() throws UnavailableStorageException {
    	mAccountDatabase.getDatabase().execute(false, new DbCallback<Void>() {
            @Override
            public Void doDbWork(final SQLiteDatabase db) throws WrappedException {
            	for (MessageFilter filter : mFilters) {
            		//db.execSQL("INSERT OR REPLACE INTO filters (name) VALUES (?)", new Object[]{ filter.getName() });
            		ContentValues filter_values = new ContentValues();
            		filter_values.put("name", filter.getName());

            		long filter_id = filter.getDatabaseId();
            		//insert of update
            		if (filter_id < 0) {
            			filter_id = db.insertOrThrow("filters", null, filter_values);
            			filter.setDatabaseId(filter_id);
            		} else {
            			//the filter is already in the database
            			db.update("filters", filter_values, "id=?", new String[]{Long.toString(filter_id)});
            		}

            		for (FilteringCriterion criterion : filter.getCriteria()) {
            			String table = criterion.getDatabaseTableName();
            			ContentValues values = criterion.getDatabaseValues();
            			values.put("filter_id", filter_id);

            			long criterion_id = criterion.getDatabaseId();
            			//insert or update
            			if (criterion_id < 0) {
            				criterion_id = db.insertOrThrow(table, null, values);
            				criterion.setDatabaseId(criterion_id);
            			} else {
            				db.update(table, values, "id=?", new String[]{Long.toString(criterion_id)});
            			}
            		}
            	}
            	return null;
            }
        });
    }

    public void addFilter(MessageFilter filter) {
    	mFilters.add(filter);
    }

    /**
     * Apply all the filters to a message, remembering actions to perform.
     *
     * @param message Remote message to be checked
     * @param localMessage Local version of the filtered message
     *
     * @return true if the new message should appear in the folder view.
     *         false means there's no point displaying or downloading the message
     * @throws MessagingException
     */
    public boolean applyToMessage(final Message message, final LocalMessage localMessage) throws MessagingException {
        return mFilters.get(0).applyToMessage(message, localMessage); //TODO
    }

    /**
     * Performs actions according to the results of the filtering.
     * @param controller Controller to deal with the message
     * @param folderUpdated Currently display folder. May be null.
     * @return How many messages in the current have been automatically marked as seen.
     */
    public int performActions(final MessagingController controller, final LocalFolder currentFolder) {
    	//TODO take into consideration there may be more filters than one
        return mFilters.get(0).performActions(controller, currentFolder); //TODO
    }
}
