package jeremy.easylite.api.entity;

import android.text.TextUtils;

/**
 * 列
 */
public class ColumnEntity {
    public String name;
    public String type;

    public ColumnEntity() {
    }

    public ColumnEntity(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (TextUtils.isEmpty(name) || obj == null)
            return false;
        ColumnEntity o = (ColumnEntity) obj;
        return name.equals(o.name) && (TextUtils.isEmpty(type) || TextUtils.isEmpty(o.type) || type.equals(o.type));
    }

    public boolean equalsNameAndType(Object obj) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(type) || obj == null)
            return false;
        ColumnEntity o = (ColumnEntity) obj;
        if (type.equals(o.type) && name.equals(o.name)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "\n\t\tColumnEntity{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
