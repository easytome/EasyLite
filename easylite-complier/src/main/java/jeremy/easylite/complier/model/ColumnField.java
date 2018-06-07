package jeremy.easylite.complier.model;

import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;

import jeremy.easylite.complier.TypeUtil;

/**
 * Created by JIANGJIAN650 on 2018/5/25.
 * 数据库列
 */
public class ColumnField {
    private Name simpleName;//参数名
    private TypeMirror typeMirror;//参数的类型 String、int...

    private String name;//注解获取的KEY
    private boolean unique;//注解获取的是否唯一
    private boolean notNull;//注解获取的是否可以为空

    ColumnField(Name simpleName, TypeMirror typeMirror, String name, boolean unique, boolean notNull) {
        this.simpleName = simpleName;
        this.typeMirror = typeMirror;
        this.name = name;
        if (name == null || "".equals(name))
            this.name = simpleName.toString();
        this.unique = unique;
        this.notNull = notNull;
    }

    String getColumnSQL() {
        if (getTypeToSQL() == null)
            return "";
        String sqlStr = name + " " + getTypeToSQL();
        if (notNull)
            sqlStr = sqlStr + " NOT NULL";
        if (unique)
            sqlStr = sqlStr + " UNIQUE";
        sqlStr = sqlStr + ",";
        return sqlStr;
    }

    Name getSimpleName() {
        return simpleName;
    }


    String getName() {
        return name;
    }

    String getTypeToCursor() {
        if (typeMirror == null || "".equals(typeMirror.toString()))
            return null;
        return TypeUtil.getTypeToCursor(typeMirror.toString());
    }

    String getTypeToSQL() {
        if (typeMirror == null || "".equals(typeMirror.toString()))
            return null;
        return TypeUtil.getTypeToSQL(typeMirror.toString());
    }

    @Override
    public String toString() {
        return "\nColumnField{" +
                "simpleName=" + simpleName +
                ", typeMirror=" + typeMirror +
                ", typeMirror.getKind()=" + typeMirror.getKind() +
                ", name='" + name + '\'' +
                ", unique=" + unique +
                ", notNull=" + notNull +
                '}';
    }
}
