package com.fsck.k9;

import java.util.ArrayList;

import com.fsck.k9.mail.store.LockableDatabase;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AccountDatabase {
    protected static final int DB_VERSION = 42;
    private final Application mApplication;
    protected String uUid = null;
    private LockableDatabase database;
    private ArrayList<AccountDatabaseUpgradeListener> mOnUpgradeListeners = new ArrayList<AccountDatabaseUpgradeListener>();

    private class StoreSchemaDefinition implements LockableDatabase.SchemaDefinition {
        @Override
        public int getVersion() {
            return DB_VERSION;
        }

        @Override
        public void doDbUpgrade(final SQLiteDatabase db) {
            Log.i(K9.LOG_TAG, String.format("Upgrading database from version %d to version %d",
                                            db.getVersion(), DB_VERSION));

            for (AccountDatabaseUpgradeListener listener : mOnUpgradeListeners) {
                listener.onDatabaseUpgrade(AccountDatabase.this, db);
            }

            db.setVersion(getVersion());

            if (db.getVersion() != getVersion()) {
                throw new Error("Database upgrade failed!");
            }
        }
    }

    /**
     * Returns the latest database version - the one the app should try to upgrade
     * the existing one to.
     * @return Database version
     */
    public int getVersion() {
        return DB_VERSION;
    }

    public AccountDatabase(final String uuid, final Application application) {
        mApplication = application;
        uUid = uuid;
        database = new LockableDatabase(application, uuid, new StoreSchemaDefinition());
    }

    public LockableDatabase getDatabase() {
        return database;
    }

    public void addListener(AccountDatabaseUpgradeListener listener) {
        mOnUpgradeListeners.add(listener);
    }

    public Application getApplication() {
        return mApplication;
    }
}
