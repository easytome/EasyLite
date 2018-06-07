package jeremy.easylite.api.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JIANGJIAN650 on 2018/6/4.
 */
public abstract class AbstractSQLOpenHelper extends SQLiteOpenHelper {
    public AbstractSQLOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                                 int version) {
        super(context, name, factory, version);
    }

    public abstract void setUpdataSchema(IUpdataSchema iUpdataSchema);
}
