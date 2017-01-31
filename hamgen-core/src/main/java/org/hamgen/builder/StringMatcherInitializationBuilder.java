package org.hamgen.builder;

import com.sun.codemodel.*;
import org.hamcrest.Matchers;

import java.util.ArrayList;
import java.util.List;

public class StringMatcherInitializationBuilder extends MatcherInitializationBuilder {

    @Override
    public List<Class<?>> getTypes() {
        List<Class<?>> types = new ArrayList<Class<?>>();
        types.add(String.class);
        return types;
    }

    @Override
    public JBlock build() {
        JClass matchersClazz = codeModel.ref(Matchers.class);

        JExpression condition = expected.invoke(matcherField.getGetterName()).eq(JExpr._null()).cor(expected.invoke(matcherField.getGetterName()).invoke("isEmpty"));
        JInvocation invokeMatcherIsEmptyOrNullString = matchersClazz.staticInvoke("isEmptyOrNullString");
        JInvocation invokeMatcherIs = matchersClazz.staticInvoke("is").arg(expected.invoke(matcherField.getGetterName()));
        JExpression assignmentExpression = JOp.cond(condition, invokeMatcherIsEmptyOrNullString, invokeMatcherIs);

        return constructorBody.assign(matcher, assignmentExpression);
    }
}
