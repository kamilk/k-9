package com.fsck.k9.messagefilter;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fsck.k9.Account;
import com.fsck.k9.AccountDatabase;
import com.fsck.k9.AccountDatabaseUpgradeListener;
import com.fsck.k9.K9;

/**
 * Manager of multiple message filters for a particular account
 */
public class MessageFilterManager {
    private AccountDatabase mAccountDatabase;

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
}
