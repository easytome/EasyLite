package jeremy.easylite.api.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Constructor;

import jeremy.easylite.api.dao.AbstractSQLOpenHelper;
import jeremy.easylite.api.dao.IEasyDao;
import jeremy.easylite.api.config.Config;
import jeremy.easylite.api.dao.IUpdataSchema;

/**
 * Created by JIANGJIAN650 on 2018/5/26.
 */

public class Utils {
    public static AbstractSQLOpenHelper getEasySQLOpenHelper(String className, Context context, String name,
                                                             SQLiteDatabase.CursorFactory factory, int version, IUpdataSchema iUpdataSchema) {
        try {
            Class<?> finderClass = Class.forName(className);
            Constructor constructor = finderClass.getConstructor(Context.class, String.class, SQLiteDatabase.CursorFactory.class, int.class, IUpdataSchema.class);
            return (AbstractSQLOpenHelper) constructor.newInstance(context, name, factory, version, iUpdataSchema);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IEasyDao getDaoByName(String className) {
        try {
            Class<?> finderClass = Class.forName(className + Config.NAMEAPPEND);
            return (IEasyDao) finderClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
