package jeremy.easylite.demo.bean;

import jeremy.easylite.annotation.*;

/**
 * Created by JIANGJIAN650 on 2018/5/29.
 */
@EasyTable(name = "simple_tb")
public class SimpleBean {
    @EasyColumn(unique = true)//设置唯一，不设置默认为false，注意这不是主键，默认有一个主键id，发生冲突会插入失败
    public String name;//必须用public修饰

    @EasyColumn(name = "MINT")//设置数据库列名不设置默认为参数名
    public int mInt;

    @EasyColumn(notNull = true)//设置不能为空，不设置默认为false
    public boolean isWhat;

    public SimpleBean(String name, int mInt, boolean isWhat) {
        this.name = name;
        this.mInt = mInt;
        this.isWhat = isWhat;
    }

    @Override
    public String toString() {
        return "\nSimpleBean{" +
                "name='" + name + '\'' +
                ", mInt=" + mInt +
                ", isWhat=" + isWhat +
                '}';
    }
}
