package org.hamgen.builder.initialization;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JInvocation;
import org.hamcrest.Matchers;

import java.util.Collections;
import java.util.List;

public class EnumMatcherInitializationBuilder extends MatcherInitializationBuilder {

    private static final String METHOD_NAME_IS = "is";

    @Override
    public List<Class<?>> getTypes() {
        return Collections.emptyList();
    }

    @Override
    public JBlock build() {
        JClass matchersClazz = codeModel.ref(Matchers.class);

        JInvocation assignmentExpression = matchersClazz.staticInvoke(METHOD_NAME_IS).arg(expected.invoke(matcherField.getGetterName()));

        return constructorBody.assign(matcher, assignmentExpression);
    }
}
