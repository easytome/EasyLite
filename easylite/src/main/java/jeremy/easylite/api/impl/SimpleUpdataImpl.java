package jeremy.easylite.api.impl;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import jeremy.easylite.api.dao.IUpdataSchema;
import jeremy.easylite.api.entity.TableEntity;
import jeremy.easylite.api.utils.LogUtils;

/**
 * Created by JIANGJIAN650 on 2018/6/4.
 */

public class SimpleUpdataImpl implements IUpdataSchema {
    @Override
    public void onUpgrade(SQLiteDatabase db, List<TableEntity> oldTables, List<TableEntity> newTables, int oldVersion, int newVersion) {
        LogUtils.d("SimpleUpdataImpl oldTables:" + oldTables + "\nnewTables:" + newTables);
        List<TableEntity> needTables = new ArrayList<>();
        for (TableEntity newTable : newTables) {
            if (!oldTables.contains(newTable)) {
                needTables.add(newTable);
            }
        }
        for (TableEntity needTable : needTables) {
            String drop = "DROP TABLE IF EXISTS " + needTable.tableName;
            db.execSQL(drop);
            db.execSQL(needTable.createSQL);
            LogUtils.d("SimpleUpdataImpl:" + drop);
            LogUtils.d("SimpleUpdataImpl:" + needTable.createSQL);
        }
    }
}
