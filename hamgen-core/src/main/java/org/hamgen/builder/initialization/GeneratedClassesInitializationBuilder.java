package org.hamgen.builder.initialization;

import com.sun.codemodel.*;
import org.hamcrest.Matchers;
import org.hamgen.util.StringUtil;

import java.util.Collections;
import java.util.List;


public class GeneratedClassesInitializationBuilder extends MatcherInitializationBuilder {

    @Override
    public List<Class<?>> getTypes() {
        return Collections.emptyList();
    }

    @Override
    public JBlock build() {
        JClass matchersClazz = codeModel.ref(Matchers.class);

        JExpression condition = expected.invoke(matcherField.getGetterName()).eq(JExpr._null());
        JInvocation invokeMatcherNullValue = matchersClazz.staticInvoke("nullValue");

        String generatedMatcherName = matcherField.getTypeClass().getPackage().getName() + packagePostFix + "." + matcherField.getTypeClass().getSimpleName() + matcherField.getFieldPostFix();
        String generatedMatcherFactoryName = matcherPreFix + StringUtil.capitalizeFirstLetter(matcherField.getTypeClass().getSimpleName());
        JClass generatedMatcherClass = codeModel.ref(generatedMatcherName);
        JInvocation invokeGeneratedMatcher = generatedMatcherClass.staticInvoke(generatedMatcherFactoryName).arg(expected.invoke(matcherField.getGetterName()));

        JExpression assignmentExpression = JOp.cond(condition, invokeMatcherNullValue, invokeGeneratedMatcher);

        return constructorBody.assign(matcher, assignmentExpression);
    }
}
