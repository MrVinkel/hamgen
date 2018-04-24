package org.hamgen.builder.initialization;

import com.sun.codemodel.*;
import org.hamcrest.Matchers;
import org.hamgen.builder.PackageNameResolver;
import org.hamgen.util.StringUtil;

import java.util.Collections;
import java.util.List;


public class GeneratedClassesInitializationBuilder extends MatcherInitializationBuilder {

    private static final String METHOD_NAME_NULL_VALUE = "nullValue";

    @Override
    public List<Class<?>> getTypes() {
        return Collections.emptyList();
    }

    @Override
    public JBlock build() {
        JClass matchersClazz = codeModel.ref(Matchers.class);

        JExpression condition = expected.invoke(matcherField.getGetterName()).eq(JExpr._null());
        JInvocation invokeMatcherNullValue = matchersClazz.staticInvoke(METHOD_NAME_NULL_VALUE);

        Class<?> originalClazz = matcherField.getTypeClass();
        String packageName = new PackageNameResolver().resolvePackageName(originalClazz);

        String generatedMatcherName = packageName + packagePostFix + "." + originalClazz.getSimpleName() + matcherField.getFieldPostFix();
        String generatedMatcherFactoryName = matcherPreFix + StringUtil.capitalizeFirstLetter(originalClazz.getSimpleName());
        JClass generatedMatcherClass = codeModel.ref(generatedMatcherName);
        JInvocation invokeGeneratedMatcher = generatedMatcherClass.staticInvoke(generatedMatcherFactoryName).arg(expected.invoke(matcherField.getGetterName()));

        JExpression assignmentExpression = JOp.cond(condition, invokeMatcherNullValue, invokeGeneratedMatcher);

        return constructorBody.assign(matcher, assignmentExpression);
    }

}
