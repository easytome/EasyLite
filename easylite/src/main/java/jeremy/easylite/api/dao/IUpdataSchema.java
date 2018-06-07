package jeremy.easylite.api.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import jeremy.easylite.api.entity.TableEntity;

/**
 * 数据库升级模式
 */
public interface IUpdataSchema {
    public void onUpgrade(SQLiteDatabase db, List<TableEntity> oldTables, List<TableEntity> newTables, int oldVersion, int newVersion);
}
