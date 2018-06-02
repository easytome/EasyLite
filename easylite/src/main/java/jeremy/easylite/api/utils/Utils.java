package jeremy.easylite.api.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Constructor;

import jeremy.easylite.api.IEasyDao;
import jeremy.easylite.api.config.Config;

/**
 * Created by JIANGJIAN650 on 2018/5/26.
 */

public class Utils {
    public static SQLiteOpenHelper getEasySQLOpenHelper(String className, Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        try {
            Class<?> finderClass = Class.forName(className);
            Constructor constructor = finderClass.getConstructor(Context.class, String.class, SQLiteDatabase.CursorFactory.class, int.class);
            return (SQLiteOpenHelper) constructor.newInstance(context, name, factory, version);
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
