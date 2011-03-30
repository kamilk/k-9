package com.fsck.k9.messagefilter;

import java.util.ArrayList;

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
    private AccountDatabase mAccountDatabase;
    private ArrayList<MessageFilter> mFilters = new ArrayList<MessageFilter>();

    public class MessageFilterSchemaDefinition extends AccountDatabaseUpgradeListener {
        @Override
        public void onDatabaseUpgrade(final AccountDatabase accountDb, final SQLiteDatabase db) {
            Log.i(K9.LOG_TAG, String.format("Upgrading message filter database from version %d to version %d",
                                            db.getVersion(), accountDb.getVersion()));

            if (db.getVersion() < 43) {
                db.execSQL("CREATE TABLE filters (id INTEGER PRIMARY KEY, name TEXT)");
                db.execSQL("CREATE TABLE filter_criteria_string (id INTEGER PRIMARY KEY, filter_id INTEGER, field INTEGER, operand INTEGER, reference_string TEXT)");
                db.execSQL("CREATE TRIGGER delete_fiter BEFORE DELETE ON filters BEGIN DELETE FROM filter_criteria_string WHERE old.id=filter_criteria_string.id; END");
            }
        }
    }

    public MessageFilterManager(Account account) {
        mAccountDatabase = account.getAccountDatabase();
        mAccountDatabase.addListener(new MessageFilterSchemaDefinition());
    }

    public void save() throws UnavailableStorageException {
    	mAccountDatabase.getDatabase().execute(false, new DbCallback<Void>() {
            @Override
            public Void doDbWork(final SQLiteDatabase db) throws WrappedException {
            	for (MessageFilter filter : mFilters) {
            		//TODO
            		db.execSQL("INSERT INTO filters (name) VALUES (?)", new Object[]{ filter.getName() });
            	}
            	return null;
            }
        });
    }

    public void addFilter(MessageFilter filter) {
    	mFilters.add(filter);
    }

    /**
     * Apply all the filters to a message, performing appropriate actions if
     * necessary.
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
        return mFilters.get(0).performActions(controller, currentFolder); //TODO
    }
}
