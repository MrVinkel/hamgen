package org.hamgen.builder.initialization;

import com.sun.codemodel.*;
import org.hamcrest.Matchers;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class NullCheckMatcherInitializationBuilder extends MatcherInitializationBuilder {

    private static final String METHOD_NAME_NULL_VALUE = "nullValue";
    private static final String METHOD_NAME_IS = "is";

    @Override
    public List<Class<?>> getTypes() {
        List<Class<?>> types = new ArrayList<Class<?>>();
        types.add(BigDecimal.class);
        types.add(BigInteger.class);
        types.add(XMLGregorianCalendar.class);
        types.add(Object.class);
        return types;
    }

    @Override
    public JBlock build() {
        JClass matchersClazz = codeModel.ref(Matchers.class);

        JExpression condition = expected.invoke(matcherField.getGetterName()).eq(JExpr._null());
        JInvocation invokeMatcherNullValue = matchersClazz.staticInvoke(METHOD_NAME_NULL_VALUE);
        JInvocation invokeMatcherIs = matchersClazz.staticInvoke(METHOD_NAME_IS).arg(expected.invoke(matcherField.getGetterName()));

        JExpression assignmentExpression = JOp.cond(condition, invokeMatcherNullValue, invokeMatcherIs);

        return constructorBody.assign(matcher, assignmentExpression);
    }
}
