package jeremy.easylite.demo.bean;

import jeremy.easylite.annotation.EasyColumn;
import jeremy.easylite.annotation.EasyId;
import jeremy.easylite.annotation.EasyTable;

/**
 * Created by JIANGJIAN650 on 2018/5/29.
 */
@EasyTable(name = "test_tb")
public class TestBean {

    @EasyColumn(unique = true)//设置唯一，不设置默认为false，注意这不是主键，默认有一个主键id，发生冲突会插入失败
    public String name;//必须用public修饰

    @EasyColumn(name = "age")//设置数据库列名不设置默认为参数名
    public int mAge;

    @EasyColumn(notNull = true)//设置不能为空，不设置默认为false
    public float shengao;
    @EasyColumn(notNull = true)//设置不能为空，不设置默认为false
    public float tiZhong;

    public TestBean() {
    }

    @Override
    public String toString() {
        return "TestBean{" +
                ", name='" + name + '\'' +
                ", mAge=" + mAge +
                ", shengao=" + shengao +
                ", tizhong=" + tiZhong +
                '}';
    }
}
