package jeremy.easylite.demo;

import android.app.Application;

import jeremy.easylite.api.EasyLiteUtil;
import jeremy.easylite.api.impl.KeepDataUpdataImpl;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyLiteUtil.init(this,new KeepDataUpdataImpl());
    }
}
