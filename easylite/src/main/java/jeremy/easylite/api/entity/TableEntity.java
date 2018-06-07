package jeremy.easylite.api.entity;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import jeremy.easylite.api.utils.LogUtils;

/**
 * 表
 */
public class TableEntity {
    public String tableName;
    public int columnNum = 0;
    public List<ColumnEntity> columns = new ArrayList<>();

    public String createSQL;

    public TableEntity() {
    }

    public TableEntity(String tableName, int columnNum, List<ColumnEntity> columns) {
        this.tableName = tableName;
        this.columnNum = columnNum;
        this.columns = columns;
    }

    public TableEntity(String tableName, int columnNum, List<ColumnEntity> columns, String createSQL) {
        this.tableName = tableName;
        this.columnNum = columnNum;
        this.columns = columns;
        this.createSQL = createSQL;
    }

    @Override
    public boolean equals(Object obj) {
        if (TextUtils.isEmpty(tableName) || obj == null)
            return false;
        TableEntity o = (TableEntity) obj;
        if (tableName.equals(o.tableName)) {
            if (columnNum != o.columnNum) {
                LogUtils.d("TableEntity:" + tableName + "column number is change！");
                return false;
            }
            for (ColumnEntity columnEntity : columns) {
                if (!o.columns.contains(columnEntity)) {
                    LogUtils.d("TableEntity:" + columnEntity + " is change！");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "\nTableEntity{" +
                "\n\ttableName='" + tableName + '\'' +
                ", \n\tcolumnNum=" + columnNum +
                ", \n\tcolumns=" + columns +
                ", \n\tcreateSQL='" + createSQL + '\'' +
                "\n}";
    }
}
