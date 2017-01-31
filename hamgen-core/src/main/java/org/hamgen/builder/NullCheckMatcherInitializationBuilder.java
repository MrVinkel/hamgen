package org.hamgen.builder;

import com.sun.codemodel.*;
import org.hamcrest.Matchers;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class NullCheckMatcherInitializationBuilder extends MatcherInitializationBuilder {

    @Override
    public List<Class<?>> getTypes() {
        List<Class<?>> types = new ArrayList<Class<?>>();
        types.add(BigDecimal.class);
        types.add(BigInteger.class);
        types.add(XMLGregorianCalendar.class);
        return types;
    }

    @Override
    public JBlock build() {
        JClass matchersClazz = codeModel.ref(Matchers.class);

        JExpression condition = expected.eq(JExpr._null());
        JInvocation invokeMatcherNullValue = matchersClazz.staticInvoke("nullValue");
        JInvocation invokeMatcherIs = matchersClazz.staticInvoke("is").arg(expected);

        JExpression assignmentExpression = JOp.cond(condition, invokeMatcherNullValue, invokeMatcherIs);

        return constructorBody.assign(matcher, assignmentExpression);
    }
}
