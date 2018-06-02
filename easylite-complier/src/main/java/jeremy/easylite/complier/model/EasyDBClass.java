package jeremy.easylite.complier.model;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.lang.model.element.Modifier;

import jeremy.easylite.complier.TypeUtil;

/**
 * Created by JIANGJIAN650 on 2018/5/26.
 * 创建数据库Helper
 */
public class EasyDBClass {
    private List<TableClass> sqlList;

    public EasyDBClass(List<TableClass> sqlList) {
        this.sqlList = sqlList;
    }

    public JavaFile generateFinder() {
        ParameterSpec paramsContext = ParameterSpec.builder(TypeUtil.Context, "context").build();
        ParameterSpec paramsCursorFactory = ParameterSpec.builder(TypeUtil.CursorFactory, "factory").build();
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(paramsContext)
                .addParameter(String.class, "name")
                .addParameter(paramsCursorFactory)
                .addParameter(TypeName.INT, "version")
                .addStatement("super($N, $N, $N, $N)", "context", "name", "factory", "version");

        ParameterSpec paramsSQLiteDatabase = ParameterSpec.builder(TypeUtil.SQLiteDatabase, "db").build();
        //构建onCreate方法
        MethodSpec.Builder methodonCreate = MethodSpec.methodBuilder("onCreate")
                .addParameter(paramsSQLiteDatabase)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("int total = 0")
                .beginControlFlow("for (int i = 0; i < createList.length; i++)")
                .addStatement("db.execSQL(" + "createList[i]" + ")")
                .endControlFlow();
        //构建onUpgrade方法
        MethodSpec.Builder methodUpgrade = MethodSpec.methodBuilder("onUpgrade")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(paramsSQLiteDatabase)
                .addParameter(TypeName.INT, "oldVersion")
                .addParameter(TypeName.INT, "newVersion");
        //构建EasyDatabaseHelper类
        TypeSpec.Builder finderClassBuilder = TypeSpec.classBuilder("EasyDatabaseHelper")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constructorBuilder.build())
                .superclass(TypeUtil.SQLiteOpenHelper);

        StringBuilder createListSB = new StringBuilder("{");
        for (TableClass sql : sqlList) {
            createListSB.append("\n\"").append(sql.getTableSQL()).append("\",");
        }
        int lastIndexOf = createListSB.lastIndexOf(",");
        String createListStr = createListSB.toString();
        if (lastIndexOf > 0)
            createListStr = createListSB.substring(0, lastIndexOf);
        createListStr += "}";
        //构建数组
        FieldSpec.Builder fieldBuilder = FieldSpec.builder(String[].class, "createList", Modifier.PRIVATE)
                .initializer(createListStr);

        finderClassBuilder.addField(fieldBuilder.build());
        finderClassBuilder.addMethod(methodonCreate.build());
        finderClassBuilder.addMethod(methodUpgrade.build());
        String packageName = "jeremy.easylite.api";
        return JavaFile.builder(packageName, finderClassBuilder.build()).build();
    }
}
