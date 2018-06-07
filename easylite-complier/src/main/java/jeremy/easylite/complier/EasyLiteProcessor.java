package jeremy.easylite.complier;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import jeremy.easylite.annotation.EasyColumn;
import jeremy.easylite.annotation.EasyId;
import jeremy.easylite.annotation.EasyTable;
import jeremy.easylite.complier.model.EasyDBClass;
import jeremy.easylite.complier.model.TableClass;

@AutoService(Processor.class)
public class EasyLiteProcessor extends AbstractProcessor {
    /**
     * 文件相关的辅助类
     */
    private Filer mFiler;
    /**
     * 元素相关的辅助类
     */
    private Elements mElementUtils;
    /**
     * 日志相关的辅助类
     */
    private Messager mMessager;

    private List<TableClass> sqlList = new ArrayList<TableClass>();

    /**
     * 解析的目标注解集合
     */
//    private Map<String, AnnotatedClass> mAnnotatedClassMap = new HashMap<>();
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(EasyTable.class.getCanonicalName());
        types.add(EasyColumn.class.getCanonicalName());
        types.add(EasyId.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        sqlList.clear();
        try {
            for (Element element : roundEnv.getElementsAnnotatedWith(EasyTable.class)) {
                TypeElement tableEle = (TypeElement) element;
                TableClass tableClass = TableClass.create(tableEle);
                sqlList.add(tableClass);
                tableClass.generateFinder().writeTo(mFiler);
            }
            EasyDBClass easyDBClass = new EasyDBClass(sqlList);
            easyDBClass.generateFinder().writeTo(mFiler);
        } catch (IOException e) {
        }
        return true;
    }


    private void error(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    private void info(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }
}