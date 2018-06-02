package jeremy.easylite.demo;

import android.app.Application;

import jeremy.easylite.api.EasyLiteUtil;

/**
 * Created by JIANGJIAN650 on 2018/5/29.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyLiteUtil.init(this);
    }
}
