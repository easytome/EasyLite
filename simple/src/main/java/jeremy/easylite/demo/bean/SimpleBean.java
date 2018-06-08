package jeremy.easylite.demo.bean;

import jeremy.easylite.annotation.*;

/**
 * Created by JIANGJIAN650 on 2018/5/29.
 */
@EasyTable(name = "simple_tb")//设置表名 如果不设置name直接用@EasyTable默认为类名
public class SimpleBean {
    @EasyId(name = "simple_id", autoincrement = false)//设置主键
    public Long id;

    @EasyColumn(unique = true)//设置唯一，不设置默认为false，注意这不是主键，默认有一个主键id，发生冲突会插入失败
    public String name;//必须用public修饰

    @EasyColumn(name = "MINT")//设置数据库列名不设置默认为参数名
    public int mInt;

    @EasyColumn(notNull = true)//设置不能为空，不设置默认为false
    public boolean boo;

    @EasyColumn
    public String desc;

    public SimpleBean(String name, int mInt, boolean boo) {
        this.name = name;
        this.mInt = mInt;
        this.boo = boo;
    }

    @Override
    public String toString() {
        return "SimpleBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mInt=" + mInt +
                ", boo=" + boo +
                ", desc=" + desc +
                '}';
    }
}
