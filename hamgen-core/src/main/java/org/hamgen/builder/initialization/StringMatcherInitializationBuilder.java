package org.hamgen.builder.initialization;

import com.sun.codemodel.*;
import org.hamcrest.Matchers;

import java.util.ArrayList;
import java.util.List;

public class StringMatcherInitializationBuilder extends MatcherInitializationBuilder {

    private static final String METHOD_NAME_IS_EMPTY = "isEmpty";
    private static final String METHOD_NAME_IS_EMPTY_OR_NULL_STRING = "isEmptyOrNullString";
    private static final String METHOD_NAME_IS = "is";

    @Override
    public List<Class<?>> getTypes() {
        List<Class<?>> types = new ArrayList<Class<?>>();
        types.add(String.class);
        return types;
    }

    @Override
    public JBlock build() {
        JClass matchersClazz = codeModel.ref(Matchers.class);

        JExpression condition = expected.invoke(matcherField.getGetterName()).eq(JExpr._null()).cor(expected.invoke(matcherField.getGetterName()).invoke(METHOD_NAME_IS_EMPTY));
        JInvocation invokeMatcherIsEmptyOrNullString = matchersClazz.staticInvoke(METHOD_NAME_IS_EMPTY_OR_NULL_STRING);
        JInvocation invokeMatcherIs = matchersClazz.staticInvoke(METHOD_NAME_IS).arg(expected.invoke(matcherField.getGetterName()));
        JExpression assignmentExpression = JOp.cond(condition, invokeMatcherIsEmptyOrNullString, invokeMatcherIs);

        return constructorBody.assign(matcher, assignmentExpression);
    }
}
