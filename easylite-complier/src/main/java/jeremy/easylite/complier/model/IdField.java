package jeremy.easylite.complier.model;

import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;

import jeremy.easylite.complier.TypeUtil;

/**
 * Created by JIANGJIAN650 on 2018/5/25.
 * 数据库列
 */
public class IdField {
    public static String DEFAULT_KEY_ID = "_easy_id";

    private String simpleName;//参数名
    private String typeName;//参数的类型 String、int...

    private String name;//注解获取的KEY
    private boolean autoincrement;//注解获取的是否唯一

    IdField(String simpleName, String typeName, String name, boolean autoincrement) {
        this.simpleName = simpleName;
        this.typeName = typeName;
        this.name = name;
        if (name == null || "".equals(name))
            this.name = simpleName.toString();
        this.autoincrement = autoincrement;
    }

    String getColumnSQL() {
        String typeToSQL = getTypeToSQL();
        if (typeToSQL == null)
            return "";
        String sqlStr = name + " " + typeToSQL + " PRIMARY KEY";
        if (autoincrement && "INTEGER".equals(typeToSQL))
            sqlStr = sqlStr + " AUTOINCREMENT";
        sqlStr = sqlStr + ",";
        return sqlStr;
    }

    String getSimpleName() {
        return simpleName;
    }


    String getName() {
        return name;
    }

    String getTypeToCursor() {
        if (typeName == null || "".equals(typeName))
            return null;
        return TypeUtil.getTypeToCursor(typeName);
    }

    String getTypeToSQL() {
        if (typeName == null || "".equals(typeName))
            return null;
        return TypeUtil.getTypeToSQL(typeName);
    }

    public static String getDefaultSQL() {
        return DEFAULT_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,";
    }

    @Override
    public String toString() {
        return "\nIdField{" +
                "simpleName=" + simpleName +
                ", typeName=" + typeName +
                ", name='" + name + '\'' +
                ", autoincrement=" + autoincrement +
                '}';
    }
}
