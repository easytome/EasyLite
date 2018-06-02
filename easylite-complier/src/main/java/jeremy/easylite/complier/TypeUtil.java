package jeremy.easylite.complier;

import com.squareup.javapoet.ClassName;

/**
 * 类型
 */
public class TypeUtil {
    public static final ClassName IEasyDao = ClassName.bestGuess("jeremy.easylite.api.IEasyDao");
    public static final ClassName SQLiteOpenHelper = ClassName.bestGuess("android.database.sqlite.SQLiteOpenHelper");
    public static final ClassName Context = ClassName.bestGuess("android.content.Context");
    public static final ClassName CursorFactory = ClassName.bestGuess("android.database.sqlite.SQLiteDatabase.CursorFactory");
    public static final ClassName SQLiteDatabase = ClassName.bestGuess("android.database.sqlite.SQLiteDatabase");
    public static final ClassName ContentValues = ClassName.bestGuess("android.content.ContentValues");
    public static final ClassName EasyDatabaseUtil = ClassName.bestGuess("jeremy.easylite.api.EasyDatabaseUtil");

    public static final ClassName List = ClassName.get("java.util", "List");
    public static final ClassName ArrayList = ClassName.get("java.util", "ArrayList");
    public static final ClassName Cursor = ClassName.bestGuess("android.database.Cursor");

    public static final String getType(String typeName) {
        if (typeName == null)
            return null;
        String typeStr = typeName.toLowerCase();
        if (typeStr.contains("byte")) {
            return "byte";
        } else if (typeStr.contains("short")) {
            return "short";
        } else if (typeStr.contains("int")) {
            return "int";
        } else if (typeStr.contains("long")) {
            return "long";
        } else if (typeStr.contains("float")) {
            return "float";
        } else if (typeStr.contains("double")) {
            return "double";
        } else if (typeStr.contains("boolean")) {
            return "boolean";
        } else if (typeStr.contains("char")) {
            return "char";
        } else if (typeStr.contains("string"))
            return "string";
        return null;
    }

    public static final String getTypeInitParam(String typeName) {
        String typeStr = getType(typeName);
        if (typeStr == null)
            return "null";
        if (typeStr.contains("byte")) {
            return "0";
        } else if (typeStr.contains("short")) {
            return "0";
        } else if (typeStr.contains("int")) {
            return "0";
        } else if (typeStr.contains("long")) {
            return "0L";
        } else if (typeStr.contains("float")) {
            return "0F";
        } else if (typeStr.contains("double")) {
            return "0D";
        } else if (typeStr.contains("boolean")) {
            return "false";
        } else if (typeStr.contains("char")) {
            return "0";
        } else if (typeStr.contains("string")) {
            return "null";
        }
        return "null";
    }
}
