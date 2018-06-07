package jeremy.easylite.api;

import android.app.Application;

import jeremy.easylite.api.config.Config;
import jeremy.easylite.api.dao.IUpdataSchema;
import jeremy.easylite.api.utils.EasyDatabaseUtil;
import jeremy.easylite.api.utils.LogUtils;

/**
 * Created by JIANGJIAN650 on 2018/5/22.
 */

public class EasyLiteUtil {
    public static void init(Application application) {
        init(application, null);
    }

    public static void init(Application application, IUpdataSchema iUpdataSchema) {
        boolean boo = Config.getDebugEnabled(application);
        LogUtils.setDebug(boo);
        LogUtils.d("getDatabaseName:" + Config.getDatabaseName(application));
        LogUtils.d("getDatabaseVersion:" + Config.getDatabaseVersion(application));
        EasyDatabaseUtil.init(application, iUpdataSchema, boo);
    }
}
