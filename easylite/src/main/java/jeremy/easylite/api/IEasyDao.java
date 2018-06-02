package jeremy.easylite.api;

import android.content.ContentValues;

import java.util.List;

/**
 * Created by JIANGJIAN650 on 2018/5/25.
 */

public interface IEasyDao<T> {
    public long save(T t);

    public long updata(T t, String whereClause, String... whereArgs);

    public long delete(String whereClause, String... whereArgs);

    public ContentValues getContentValues(T t);

    public List<T> find(String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy, String limit);

    public long count(String whereClause, String[] whereArgs);
}
