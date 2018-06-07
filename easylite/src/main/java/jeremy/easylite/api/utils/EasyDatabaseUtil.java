package jeremy.easylite.api.utils;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import jeremy.easylite.api.config.Config;
import jeremy.easylite.api.dao.AbstractSQLOpenHelper;
import jeremy.easylite.api.dao.IUpdataSchema;
import jeremy.easylite.api.utils.LogUtils;
import jeremy.easylite.api.utils.Utils;

public class EasyDatabaseUtil {
    private static AbstractSQLOpenHelper sql;

    public static void init(Application application, boolean debug) {
        init(application, null, debug);
    }

    public static void init(Application application, IUpdataSchema iUpdataSchema, boolean debug) {
        sql = Utils.getEasySQLOpenHelper(Config.CLASSNAME_EasyDatabaseHelper,
                application, Config.getDatabaseName(application),
                new EasyCursorFactory(debug),
                Config.getDatabaseVersion(application), iUpdataSchema);
        getDB();
    }

    public static SQLiteDatabase getDB() {
        if (sql == null)
            try {
                throw new Exception("EasyLiteUtil not init.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        return sql.getWritableDatabase();
    }

    public static String[] getColumnNames(SQLiteDatabase db, String tableName) {
        String[] columns = new String[0];
        String query = "SELECT * FROM " + tableName;
        Cursor cr = db.rawQuery(query, null);
        try {
            // 获得所有列的数目及实际列数
            int count = cr.getColumnCount();
            if (count == 0)
                return null;
            columns = new String[count];
            for (int i = 0; i < cr.getColumnCount(); i++) {
                // 获得指定列的列名
                String columnName = cr.getColumnName(i);
                columns[i] = columnName;
                LogUtils.i("SQLiteDatabase getColumnNames:" + count + "," + columnName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cr.close();
            return columns;
        }
    }

    /**
     * 返回列的类型
     * <p>
     * 只有在有数据的时候才会返回类型
     *
     * @param db
     * @param tableName
     * @param columNname
     * @return
     */
    public static String getTypeByName(SQLiteDatabase db, String tableName, String columNname) {
        String query = "select typeof(" + columNname + ") from " + tableName;
        Cursor cr = null;
        try {
            cr = db.rawQuery(query, null);
            if (cr == null)
                return null;
            int count = cr.getColumnCount();
            if (count == 0)
                return null;
            if (cr.moveToFirst()) {
                String type = cr.getString(0).toUpperCase();
                return type;
            }
            return null;
        } finally {
            if (cr != null)
                cr.close();
        }
    }

    /**
     * 返回列的类型
     * <p>
     * 只有在有数据的时候才会返回类型
     *
     * @param db
     * @param tableName
     * @param columns
     * @return
     */
    public static Map<String, String> getTypeByNames(SQLiteDatabase db, String tableName, String[] columns) {
        if (columns == null || columns.length == 0)
            return null;
        Map<String, String> map = new HashMap<>();
        StringBuilder queryBuilder = new StringBuilder("select ");
        for (String colum : columns) {
            queryBuilder.append(" typeof(").append(colum).append(") as ").append(colum).append(",");
        }
        String query = queryBuilder.substring(0,queryBuilder.length()-1)+" from "+tableName;
        Cursor cr = null;
        try {
            cr = db.rawQuery(query, null);
            if (cr == null)
                return null;
            int count = cr.getColumnCount();
            if (count < columns.length)
                return null;
            if (cr.moveToFirst()) {
                for (String colum : columns) {
                    map.put(colum, cr.getString(cr.getColumnIndex(colum)).toUpperCase());
                }
                return map;
            }
            return null;
        } finally {
            if (cr != null)
                cr.close();
        }
    }

    public static String[] getAllTable(SQLiteDatabase db) {
        String query = "select name from sqlite_master where type='table' order by name";
        Cursor cr = null;
        try {
            cr = db.rawQuery(query, null);
            int count = cr.getCount();
            if (count == 0)
                return null;
            String[] tables = new String[count];
            int i = 0;
            if (cr.moveToFirst()) {
                do {
                    tables[i] = cr.getString(0);
                    LogUtils.i("SQLiteDatabase table:" + tables[i]);
                    i++;
                } while (cr.moveToNext());
            }
            return tables;
        } finally {
            if (cr != null)
                cr.close();
        }
    }

    public static long save(String table, ContentValues values) {
        SQLiteDatabase db = getDB();
        long i = db.insert(table, null, values);
        LogUtils.i("SQLiteDatabase insert:" + i);
        return i;
    }

    public static long delete(String table, String whereClause, String... whereArgs) {
        SQLiteDatabase db = getDB();
        return db.delete(table, whereClause, whereArgs);
    }

    public static long update(String table, ContentValues values, String whereClause, String... whereArgs) {
        SQLiteDatabase db = getDB();
        return db.update(table, values, whereClause, whereArgs);
    }

    public static Cursor find(String table, String[] columns, String selection,
                              String[] selectionArgs, String groupBy, String having,
                              String orderBy, String limit) {
        SQLiteDatabase sqLiteDatabase = getDB();
        Cursor c = sqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        return c;
    }

    public static long count(String table, String whereClause, String[] whereArgs) {
        SQLiteDatabase sqLiteDatabase = getDB();

        long toRet = -1;
        String filter = (!TextUtils.isEmpty(whereClause)) ? " where " + whereClause : "";
        SQLiteStatement sqLiteStatament = sqLiteDatabase.compileStatement("SELECT count(*) FROM " + table + filter);
        if (whereArgs != null) {
            for (int i = whereArgs.length; i != 0; i--) {
                sqLiteStatament.bindString(i, whereArgs[i - 1]);
            }
        }
        try {
            toRet = sqLiteStatament.simpleQueryForLong();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqLiteStatament.close();
        }
        return toRet;
    }

    static class EasyCursorFactory implements SQLiteDatabase.CursorFactory {

        private boolean debugEnabled;

        public EasyCursorFactory() {
            this.debugEnabled = false;
        }

        public EasyCursorFactory(boolean debugEnabled) {
            this.debugEnabled = debugEnabled;
        }

        @SuppressWarnings("deprecation")
        public Cursor newCursor(SQLiteDatabase sqLiteDatabase,
                                SQLiteCursorDriver sqLiteCursorDriver,
                                String editTable,
                                SQLiteQuery sqLiteQuery) {
            if (debugEnabled) {
                Log.d("EasyLiteFactory", sqLiteQuery.toString());
            }
            return new SQLiteCursor(sqLiteDatabase, sqLiteCursorDriver, editTable, sqLiteQuery);
        }
    }
}
