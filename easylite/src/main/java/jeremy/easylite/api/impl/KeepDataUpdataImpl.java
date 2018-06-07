package jeremy.easylite.api.impl;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import jeremy.easylite.api.dao.IUpdataSchema;
import jeremy.easylite.api.entity.ColumnEntity;
import jeremy.easylite.api.entity.TableEntity;
import jeremy.easylite.api.utils.LogUtils;

/**
 * Created by JIANGJIAN650 on 2018/6/4.
 */

public class KeepDataUpdataImpl implements IUpdataSchema {
    @Override
    public void onUpgrade(SQLiteDatabase db, List<TableEntity> oldTables, List<TableEntity> newTables, int oldVersion, int newVersion) {
        LogUtils.d("KeepDataUpdataImpl oldTables:" + oldTables + "\nnewTables:" + newTables);
        for (TableEntity newTable : newTables) {
            if (!oldTables.contains(newTable)) {
                checkUpdata(db, newTable, oldTables);
            }
        }
    }

    private void checkUpdata(SQLiteDatabase db, TableEntity newTable, List<TableEntity> oldTables) {
        for (TableEntity oldTable : oldTables) {
            if (oldTable.tableName.equals(newTable.tableName)) {
                updataNewTable(db, newTable, oldTable);
                break;
            }
        }
    }

    private void updataNewTable(SQLiteDatabase db, TableEntity newTable, TableEntity oldTable) {
        List<ColumnEntity> keeps = new ArrayList<>();
        List<ColumnEntity> changes = new ArrayList<>();
        for (ColumnEntity columnEntity : newTable.columns) {
            if (oldTable.columns.contains(columnEntity))
                keeps.add(columnEntity);
            else {
                changes.add(columnEntity);
            }
        }

        if (keeps.size() > 0) {
            StringBuilder oldColumnB = new StringBuilder();
            db.execSQL("ALTER TABLE " + oldTable.tableName + " RENAME TO " + oldTable.tableName + "Old");//先将表重命名
            db.execSQL(newTable.createSQL);//重新创建表
            for (ColumnEntity columnEntity : keeps) {
                oldColumnB.append(columnEntity.name).append(",");
            }
            String oldColumns = oldColumnB.substring(0, oldColumnB.length() - 1);
            db.execSQL("INSERT INTO " + newTable.tableName + " (" + oldColumns + ") SELECT " +
                    oldColumns + " FROM " + oldTable.tableName + "Old");//将旧表的内容插入到新表中
            db.execSQL("DROP TABLE " + oldTable.tableName + "Old"); //删除旧表
        } else {
            String drop = "DROP TABLE IF EXISTS " + newTable.tableName;
            db.execSQL(drop);
            db.execSQL(newTable.createSQL);
        }
    }
}
