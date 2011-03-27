package com.fsck.k9;

import android.database.sqlite.SQLiteDatabase;

/**
 * A listener which will be responsible for updating a part of the account database
 */
public abstract class AccountDatabaseUpgradeListener {
    public void onDatabaseUpgrade(final AccountDatabase accountDb, final SQLiteDatabase db) {
    }
}