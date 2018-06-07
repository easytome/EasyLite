package jeremy.easylite.complier.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
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
        ParameterSpec paramsCursorIUpdataSchema = ParameterSpec.builder(TypeUtil.IUpdataSchema, "iUpdataSchema").build();
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(paramsContext)
                .addParameter(String.class, "name")
                .addParameter(paramsCursorFactory)
                .addParameter(TypeName.INT, "version")
                .addParameter(paramsCursorIUpdataSchema)
                .addStatement("super($N, $N, $N, $N)", "context", "name", "factory", "version")
                .addStatement("setUpdataSchema($N)", "iUpdataSchema");

        ParameterSpec paramsSQLiteDatabase = ParameterSpec.builder(TypeUtil.SQLiteDatabase, "db").build();
        //构建onCreate方法
        MethodSpec.Builder methodonCreate = MethodSpec.methodBuilder("onCreate")
                .addParameter(paramsSQLiteDatabase)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("int total = 0")
                .addStatement("$T.d(\"$N\")", TypeUtil.LogUtils, "EasyDatabaseHelper onCreate()")
                .beginControlFlow("for (int i = 0; i < createList.length; i++)")
                .addStatement("db.execSQL(" + "createList[i]" + ")")
                .addStatement("$T.d($N)", TypeUtil.LogUtils, "\"EasyDatabaseHelper:\"+createList[i]")
                .endControlFlow();
        //构建onUpgrade方法
        MethodSpec.Builder methodUpgrade = MethodSpec.methodBuilder("onUpgrade")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(paramsSQLiteDatabase)
                .addParameter(TypeName.INT, "oldVersion")
                .addParameter(TypeName.INT, "newVersion")
                .addStatement("$T.d(\"$N\")", TypeUtil.LogUtils, "EasyDatabaseHelper onUpgrade()")
                .beginControlFlow("if(mIUpdataSchema==null)")
                .addStatement("mIUpdataSchema = new $T()", TypeUtil.SimpleUpdataImpl)
                .endControlFlow()
                .addStatement("mIUpdataSchema.onUpgrade($N, $N, $N, $N, $N)", "db", "getOldTable(db)", "getNewTable()", "oldVersion", "newVersion");

        //构建getUpdataSchema方法
        MethodSpec.Builder methodGetUpdataSchema = MethodSpec.methodBuilder("setUpdataSchema")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeUtil.IUpdataSchema, "iUpdataSchema")
                .addStatement("mIUpdataSchema = iUpdataSchema");

        //构建EasyDatabaseHelper类
        TypeSpec.Builder finderClassBuilder = TypeSpec.classBuilder("EasyDatabaseHelper")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constructorBuilder.build())
                .superclass(TypeUtil.AbstractSQLOpenHelper);

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
        FieldSpec.Builder createListBuilder = FieldSpec.builder(String[].class, "createList", Modifier.PRIVATE)
                .initializer(createListStr);

        FieldSpec.Builder iUpdataSchemaBuilder = FieldSpec.builder(TypeUtil.IUpdataSchema, "mIUpdataSchema", Modifier.PRIVATE);

        finderClassBuilder.addField(iUpdataSchemaBuilder.build())
                .addField(createListBuilder.build())
                .addMethod(methodonCreate.build())
                .addMethod(methodUpgrade.build())
                .addMethod(methodGetUpdataSchema.build())
                .addMethod(createGetNewTable().build())
                .addMethod(createGetOldTable().build());
        String packageName = "jeremy.easylite.api";
        return JavaFile.builder(packageName, finderClassBuilder.build()).build();
    }

    MethodSpec.Builder createGetNewTable() {
        ClassName TableEntity = ClassName.bestGuess("jeremy.easylite.api.entity.TableEntity");
        ClassName ColumnEntity = ClassName.bestGuess("jeremy.easylite.api.entity.ColumnEntity");
        ParameterizedTypeName listOfTableEntity = ParameterizedTypeName.get(TypeUtil.List, TableEntity);//List<T>
        //构建getUpdataSchema方法
        MethodSpec.Builder method = MethodSpec.methodBuilder("getNewTable")
                .addModifiers(Modifier.PRIVATE)
                .returns(listOfTableEntity)
                .addStatement("$T list = new $T()", listOfTableEntity, TypeUtil.ArrayList);
        for (TableClass tableClass : sqlList) {
            List<ColumnField> columnFieldList = tableClass.getColumnFields();
            method.addStatement("$T $NTable = new $T()", TableEntity, tableClass.getSimpleName(), TableEntity)
                    .addStatement("$NTable.tableName=\"$N\"", tableClass.getSimpleName(), tableClass.getTableName())
                    .addStatement("$NTable.columnNum=$N", tableClass.getSimpleName(), (columnFieldList.size() + 1) + "")
                    .addStatement("$NTable.createSQL=\"$N\"", tableClass.getSimpleName(), tableClass.getTableSQL() + "");
            IdField idField = tableClass.getEasyIdField();
            method.addStatement("$T $N_$NCol = new $T()", ColumnEntity, idField.getSimpleName(), tableClass.getSimpleName(), ColumnEntity)
                    .addStatement("$N_$NCol.name=\"$N\"", idField.getSimpleName(), tableClass.getSimpleName(), idField.getName())
                    .addStatement("$N_$NCol.type=\"$N\"", idField.getSimpleName(), tableClass.getSimpleName(), idField.getTypeToSQL().toUpperCase())
                    .addStatement("$NTable.columns.add($N_$NCol)", tableClass.getSimpleName(), idField.getSimpleName(), tableClass.getSimpleName());
            for (ColumnField columnField : columnFieldList) {
                method.addStatement("$T $N_$NCol = new $T()", ColumnEntity, columnField.getSimpleName(), tableClass.getSimpleName(), ColumnEntity)
                        .addStatement("$N_$NCol.name=\"$N\"", columnField.getSimpleName(), tableClass.getSimpleName(), columnField.getName())
                        .addStatement("$N_$NCol.type=\"$N\"", columnField.getSimpleName(), tableClass.getSimpleName(), columnField.getTypeToSQL().toUpperCase())
                        .addStatement("$NTable.columns.add($N_$NCol)", tableClass.getSimpleName(), columnField.getSimpleName(), tableClass.getSimpleName());
            }
            method.addStatement("list.add($NTable)", tableClass.getSimpleName());
        }
        method.addStatement("return list");
        return method;
    }

    MethodSpec.Builder createGetOldTable() {
        ClassName TableEntity = ClassName.bestGuess("jeremy.easylite.api.entity.TableEntity");
        ClassName ColumnEntity = ClassName.bestGuess("jeremy.easylite.api.entity.ColumnEntity");
        ParameterizedTypeName listOfTableEntity = ParameterizedTypeName.get(TypeUtil.List, TableEntity);//List<T>
        //构建getUpdataSchema方法
        MethodSpec.Builder method = MethodSpec.methodBuilder("getOldTable")
                .addParameter(TypeUtil.SQLiteDatabase, "db")
                .addModifiers(Modifier.PRIVATE)
                .returns(listOfTableEntity)
                .addStatement("db.beginTransaction()")
                .beginControlFlow("try")
                .addStatement("$T list = new $T()", listOfTableEntity, TypeUtil.ArrayList)
                .addStatement("String[] tables = $T.getAllTable(db)", TypeUtil.EasyDatabaseUtil)
                .beginControlFlow("if(tables == null)")
                .addStatement("return list")
                .endControlFlow()
                .beginControlFlow("for (String table : tables)")
                .addStatement("$T tableEntity = new $T()", TableEntity, TableEntity)
                .addStatement("String[] columns = EasyDatabaseUtil.getColumnNames(db, table)")
                .addStatement("int count = 0")
                .beginControlFlow("if (columns != null)")
                .addStatement("count = columns.length")
                .beginControlFlow("for (String column : columns)")
                .addStatement("String type = EasyDatabaseUtil.getTypeByName(db, table, column)")
                .addStatement("$T columnEntity = new $T()", ColumnEntity, ColumnEntity)
                .addStatement("columnEntity.name = column")
                .addStatement("columnEntity.type = type")
                .addStatement("tableEntity.columns.add(columnEntity)")
                .endControlFlow()
                .endControlFlow()
                .addStatement("tableEntity.tableName = table")
                .addStatement("tableEntity.columnNum = count")
                .addStatement("list.add(tableEntity)")
                .endControlFlow()
                .addStatement("db.setTransactionSuccessful()")
                .addStatement("return list")
                .nextControlFlow("finally")
                .addStatement("db.endTransaction()")
                .endControlFlow();
        return method;
    }
}
