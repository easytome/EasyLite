package jeremy.easylite.api.utils;

import android.database.sqlite.SQLiteDatabase;


public class EasyTransactionHelper {
    private final static String TAG = "EasyTransactionHelper";

    public static void doInTransaction(Callback callback) {
        if (callback==null)
            return;
        SQLiteDatabase database = EasyDatabaseUtil.getDB();
        database.beginTransaction();
        try {
            LogUtils.d(TAG, "Callback executing within transaction");
            callback.manipulateInTransaction();
            database.setTransactionSuccessful();
            LogUtils.d(TAG, "Callback successfully executed within transaction");
        } catch (Throwable e) {
            LogUtils.d(TAG, "Could execute callback within transaction" + e);
        } finally {
            database.endTransaction();
        }
    }

    public static interface Callback {
        void manipulateInTransaction();
    }
}
