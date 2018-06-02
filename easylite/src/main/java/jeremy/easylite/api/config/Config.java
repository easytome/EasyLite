package jeremy.easylite.api.config;

import android.content.Context;

import jeremy.easylite.api.utils.ManifestUtils;

/**
 * Created by JIANGJIAN650 on 2018/5/22.
 *
 * @param //example
 * @param //<meta-data android:name="database" android:value="test.db" />  //数据库名称
 * @param //<meta-data android:name="version" android:value="1" />         //数据库版本号
 * @param //<meta-data android:name="logcat" android:value="true" />       //log
 */
public class Config {
    public final static String NAMEAPPEND = "EasyDao";//生成的java文件添加的后缀
    public final static String CLASSNAME_EasyDatabaseHelper = "jeremy.easylite.api.EasyDatabaseHelper";//生成的数据库辅助类的全类名
    /**
     * key for database name
     */
    private final static String KEY_DATABASE = "database";
    /**
     * key for database verison
     */
    private final static String KEY_VERSION = "version";
    /**
     * key for logcat enable
     */
    private final static String KEY_LOG_ENABLE = "logcat";
    /**
     * default database name
     */
    private final static String DATABASE_DEFAULT_NAME = "easylite.db";

    public static String getDatabaseName(Context context) {
        String databaseName = ManifestUtils.getMetaDataString(context, KEY_DATABASE);

        if (databaseName == null) {
            databaseName = DATABASE_DEFAULT_NAME;
        }
        return databaseName;
    }

    public static boolean getDebugEnabled(Context context) {
        return ManifestUtils.getMetaDataBoolean(context, KEY_LOG_ENABLE);
    }

    public static int getDatabaseVersion(Context context) {
        Integer databaseVersion = ManifestUtils.getMetaDataInteger(context, KEY_VERSION);

        if ((databaseVersion == null) || (databaseVersion == 0)) {
            databaseVersion = 1;
        }
        return databaseVersion;
    }
}
