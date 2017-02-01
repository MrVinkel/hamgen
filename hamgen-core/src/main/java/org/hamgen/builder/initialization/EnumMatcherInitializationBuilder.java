package org.hamgen.builder.initialization;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JInvocation;
import org.hamcrest.Matchers;

import java.util.Collections;
import java.util.List;

public class EnumMatcherInitializationBuilder extends MatcherInitializationBuilder {
    @Override
    public List<Class<?>> getTypes() {
        return Collections.emptyList();
    }

    @Override
    public JBlock build() {
        JClass matchersClazz = codeModel.ref(Matchers.class);

        JInvocation assignmentExpression = matchersClazz.staticInvoke("is").arg(expected.invoke(matcherField.getGetterName()));

        return constructorBody.assign(matcher, assignmentExpression);
    }
}
