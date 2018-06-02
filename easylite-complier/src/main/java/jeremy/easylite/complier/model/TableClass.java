package jeremy.easylite.complier.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import jeremy.easylite.annotation.EasyColumn;
import jeremy.easylite.annotation.EasyTable;
import jeremy.easylite.complier.TypeUtil;
import jeremy.easylite.complier.exception.FieldModifierException;

/**
 * Created by JIANGJIAN650 on 2018/5/25.
 * <p>
 * $T 进行映射，会自动import声明
 */
public class TableClass {
    final static String NAMEAPPEND = "EasyDao";
    private String KEY_ID = "_easy_id";

    public static TableClass create(TypeElement tableEle) {
        return new TableClass(tableEle);
    }

    private Name qualifiedName;//类全名（包名.名称）
    private Name simpleName;//类简单名
    private TypeMirror typeMirror;

    private String tableName;//注解获取的表名

    private List<ColumnField> columnFields = new ArrayList<>();
    private List<String> constructorParams = new ArrayList<>();

    private String CREATE_TABLE;

    private TypeName listOfQualified;

    private TableClass(TypeElement tableEle) {
        qualifiedName = tableEle.getQualifiedName();
        simpleName = tableEle.getSimpleName();
        typeMirror = tableEle.asType();
        EasyTable bindView = tableEle.getAnnotation(EasyTable.class);
        tableName = bindView.name();
        if ("".equals(tableName))
            tableName = simpleName.toString();
        initColumn(tableEle);
        CREATE_TABLE = createTableSQL();

        ClassName qualified = ClassName.bestGuess(qualifiedName.toString());
        listOfQualified = ParameterizedTypeName.get(TypeUtil.List, qualified);//List<T>
    }

    String getTableSQL() {
        return CREATE_TABLE;
    }

    /**
     * 构建类文件
     */
    public JavaFile generateFinder() {
        JavaFile.Builder javaFileBuilder = JavaFile.builder(getPackageName(), getFinderClass().build());
        return javaFileBuilder.build();
    }

    /**
     * 构建类
     */
    private TypeSpec.Builder getFinderClass() {
        TypeSpec.Builder finderClass = TypeSpec.classBuilder(getEasyDaoName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.IEasyDao, TypeName.get(typeMirror)))
                .addField(getClassName(), "ins", Modifier.PRIVATE, Modifier.STATIC)
                .addMethod(getStaticMethod())
                .addMethod(getSaveMethod())
                .addMethod(getUpdataMethod())
                .addMethod(getDeleteMethod())
                .addMethod(getFindMethod())
                .addMethod(getCountMethod())
                .addMethod(getContentValuesMethod());

        FieldSpec.Builder keyIdBuilder = FieldSpec.builder(String.class, "KEY" + KEY_ID.toUpperCase(),
                Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL)
                .initializer("\"" + KEY_ID + "\"")
                .addJavadoc(KEY_ID + "'s key!");
        finderClass.addField(keyIdBuilder.build());

        for (ColumnField columnField : columnFields) {//导入key值
            FieldSpec.Builder fieldBuilder = FieldSpec.builder(String.class, "KEY_" + columnField.getName().toUpperCase(),
                    Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("\"" + columnField.getName() + "\"")
                    .addJavadoc(columnField.getSimpleName() + "'s key!");
            finderClass.addField(fieldBuilder.build());
        }
        return finderClass;
    }

    /**
     * 构建Save方法
     */
    private MethodSpec getSaveMethod() {
        MethodSpec.Builder saveBuilder = MethodSpec.methodBuilder("save")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(typeMirror), "t")
                .returns(long.class)
                .addStatement("ContentValues v = getContentValues(t)")
                .addStatement("return $T.save(\"$N\",$N)", TypeUtil.EasyDatabaseUtil, tableName, "v");
        return saveBuilder.build();
    }

    /**
     * 构建Updata方法
     */
    private MethodSpec getUpdataMethod() {
        MethodSpec.Builder updataBuilder = MethodSpec.methodBuilder("updata")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(typeMirror), "t")
                .addParameter(String.class, "whereClause")
                .addParameter(String[].class, "whereArgs")
                .varargs(true)
                .returns(long.class)
                .addStatement("ContentValues v = getContentValues(t)")
                .addStatement("return $T.update(\"$N\",$N,$N,$N)", TypeUtil.EasyDatabaseUtil, tableName, "v", "whereClause", "whereArgs");
        return updataBuilder.build();
    }

    /**
     * 构建Delete方法
     */
    private MethodSpec getDeleteMethod() {
        MethodSpec.Builder deleteBuilder = MethodSpec.methodBuilder("delete")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(String.class, "whereClause")
                .addParameter(String[].class, "whereArgs")
                .varargs(true)
                .returns(long.class)
                .addStatement("return $T.delete(\"$N\",$N,$N)", TypeUtil.EasyDatabaseUtil, tableName, "whereClause", "whereArgs");
        return deleteBuilder.build();
    }

    /**
     * 构建Count方法
     */
    private MethodSpec getCountMethod() {
        MethodSpec.Builder deleteBuilder = MethodSpec.methodBuilder("count")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(String.class, "whereClause")
                .addParameter(String[].class, "whereArgs")
                .varargs(true)
                .returns(long.class)
                .addStatement("return $T.count(\"$N\",$N,$N)", TypeUtil.EasyDatabaseUtil, tableName, "whereClause", "whereArgs");
        return deleteBuilder.build();
    }

    /**
     * 构建Find方法
     */
    private MethodSpec getFindMethod() {
        MethodSpec.Builder findBuilder = MethodSpec.methodBuilder("find")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(String[].class, "columns")
                .addParameter(String.class, "selection")
                .addParameter(String[].class, "selectionArgs")
                .addParameter(String.class, "groupBy")
                .addParameter(String.class, "having")
                .addParameter(String.class, "orderBy")
                .addParameter(String.class, "limit")
                .returns(listOfQualified)
                .addStatement("$T list = new $T<>()", listOfQualified, TypeUtil.ArrayList)
                .addStatement("$T c = $T.find(\"$N\",$N,$N,$N,$N,$N,$N,$N)",
                        TypeUtil.Cursor, TypeUtil.EasyDatabaseUtil, getTableName(), "columns", "selection",
                        "selectionArgs", "groupBy", "having", "orderBy", "limit")
                .beginControlFlow("try")
                .beginControlFlow("if($N.moveToFirst())", "c")
                .beginControlFlow("do");
        StringBuilder params = new StringBuilder();
        for (int i = 0;i<constructorParams.size();i++){
            String param = constructorParams.get(i);
            findBuilder.addStatement("$N $N=$N",param,"o"+i,TypeUtil.getTypeInitParam(param));
            params.append("o").append(i);
            if (i!=constructorParams.size()-1){
                params.append(", ");
            }
        }
        findBuilder.addStatement("$N info = new $N($N)", simpleName, simpleName,params.toString());
        for (ColumnField columnField : columnFields) {
            String type = columnField.getTypeToCursor();
            if (type == null)
                continue;
            if (type.equals("Boolean")) {
                findBuilder.addStatement("info.$N=c.getInt(c.getColumnIndex(\"$N\"))>0", columnField.getSimpleName(), columnField.getName());
            } else {
                findBuilder.addStatement("info.$N=c.get$N(c.getColumnIndex(\"$N\"))", columnField.getSimpleName(), type, columnField.getName());
            }
        }
        findBuilder.addStatement("list.add(info)")
                .endControlFlow("while($N.moveToNext())", "c")
                .endControlFlow()
                .endControlFlow()
                .beginControlFlow("catch(Exception e)")
                .addStatement("e.printStackTrace()")
                .endControlFlow()
                .beginControlFlow("finally")
                .beginControlFlow("if(c != null)")
                .addStatement(" c.close()")
                .endControlFlow()
                .endControlFlow()
                .addStatement("return list");

        return findBuilder.build();
    }

    /**
     * 构建getContentValues方法
     */
    private MethodSpec getContentValuesMethod() {
        MethodSpec.Builder getContentValuesBuilder = MethodSpec.methodBuilder("getContentValues")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(typeMirror), "t")
                .returns(TypeUtil.ContentValues)
                .addStatement("ContentValues v = new ContentValues()");

        for (ColumnField columnField : columnFields) {
            getContentValuesBuilder.addStatement("v.put(\"$N\",t.$N)", columnField.getName(), columnField.getSimpleName());
        }
        getContentValuesBuilder.addStatement("return v");
        return getContentValuesBuilder.build();
    }

    /**
     * 构建单例方法
     */
    private MethodSpec getStaticMethod() {
        MethodSpec.Builder staticBuilder = MethodSpec.methodBuilder("getIns")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(getClassName())
                .beginControlFlow("if(ins==null)")
                .addStatement("ins = new $N()", getEasyDaoName())
                .endControlFlow()
                .addStatement("return ins");
        return staticBuilder.build();
    }

    private String getEasyDaoName() {
        return simpleName + NAMEAPPEND;
    }

    private ClassName getClassName() {
        return ClassName.bestGuess(getPackageName() + "." + getEasyDaoName());
    }

    private String getPackageName() {
        String quaName = qualifiedName.toString();
        return quaName.substring(0, quaName.lastIndexOf("."));
    }

    private void initColumn(TypeElement tableEle) {
        columnFields.clear();
        List<? extends Element> enclosedEle = tableEle.getEnclosedElements();
        for (Element enclosed : enclosedEle) {
            if (enclosed.getKind().isField()) {
                VariableElement columnEle = (VariableElement) enclosed;
                EasyColumn easyColumn = columnEle.getAnnotation(EasyColumn.class);
                if (!columnEle.getModifiers().contains(Modifier.PUBLIC)) {
                    throwNewException(simpleName + "/" + columnEle.getSimpleName() + ":Must use public field!!");
                    return;
                }
                if (easyColumn != null) {
                    columnFields.add(new ColumnField(columnEle.getSimpleName(), columnEle.asType(), easyColumn.name(), easyColumn.unique(), easyColumn.notNull()));
                }
            } else if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                constructorParams.clear();
                ExecutableElement constructorElement = (ExecutableElement) enclosed;
                List<? extends VariableElement> parameters = constructorElement.getParameters();
                for (VariableElement parameterElement : parameters) {
                    System.out.println("parameterElement:" + parameterElement.asType() + "");
                    constructorParams.add(parameterElement.asType().toString());
                }
            }
        }
    }

    private void throwNewException(String err) {
        try {
            throw new FieldModifierException(err);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String createTableSQL() {
        StringBuilder CREATE_TABLE = new StringBuilder("CREATE TABLE ")
                .append(tableName)
                .append("(")
                .append(KEY_ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
        for (ColumnField columnField : columnFields) {
            CREATE_TABLE.append(columnField.getColumnSQL());
        }
        String str = CREATE_TABLE.substring(0, CREATE_TABLE.length() - 1);
        str = str + ")";
        return str;
    }

    private String getTableName() {
        return tableName;
    }
}
