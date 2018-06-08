## EasyLite是什么？
EasyLite是一个便于使用的编译时生成代码的Orm框架，它刚出生，它还需要成长
## EasyLite有哪些功能？
* ORM对象映射
* 编译时生成代码

## 快速使用
### 需要Java 7或以上

```java
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_7
    targetCompatibility JavaVersion.VERSION_1_7
}
```
### 导入依赖
```java
dependencies {
    implementation project(':easylite')
    annotationProcessor project(':easylite-complier')
}
```

### 配置参数
```java

//数据库名称
<meta-data
    android:name="database"
    android:value="test.db" />
//数据库版本号
<meta-data
    android:name="version"
    android:value="1" />
//log
<meta-data
    android:name="logcat"
    android:value="true" />

```
### 初始化
```java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyLiteUtil.init(this,new KeepDataUpdataImpl());//KeepDataUpdataImpl是一种升级策略（数据库升级时保持数据）另外一种是SimpleUpdataImpl升级表时删除数据 有特殊需要可以自己实现IUpdataSchema接口来自定义升级策略
    }
}
```
### 使用注解
```java
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

```
### 调用数据库
根据以上步骤配置好之后需要MakeProject以生成EasyDao，命名规则是你（配置的类名+EasyDao），就可以直接调用数据库

* 注意：如果不MakeProject，代码中会提示找不到SimpleBeanEasyDao

```java
SimpleBeanEasyDao.getIns().save(new SimpleBean("name", 111, true));
```

##有问题反馈
在使用中有任何问题，欢迎反馈给我，可以用以下联系方式跟我交流

* 邮件(785436663#qq.com, 把#换成@)
* QQ: 785436663