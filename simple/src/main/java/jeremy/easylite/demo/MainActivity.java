package jeremy.easylite.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jeremy.easylite.api.entity.ColumnEntity;
import jeremy.easylite.api.entity.TableEntity;
import jeremy.easylite.api.utils.EasyDatabaseUtil;
import jeremy.easylite.demo.bean.SimpleBean;
import jeremy.easylite.demo.bean.SimpleBeanEasyDao;

public class MainActivity extends AppCompatActivity {
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = findViewById(R.id.txt);
    }

    public void insert(View view) {
        SimpleBeanEasyDao.getIns().save(new SimpleBean("name", 111, true));
    }

    public void delete(View view) {
        SimpleBeanEasyDao.getIns().delete(null);
    }

    public void find(View view) {
        List<SimpleBean> list = SimpleBeanEasyDao.getIns().find(null, null, null, null, null, null);
        txt.setText(list.toString());
    }

    public void updata(View view) {
        SimpleBeanEasyDao.getIns().updata(new SimpleBean("newName", 111, true), SimpleBeanEasyDao.KEY_NAME + "=?", "name");
    }

    public void upTest(View view) {
    }
}