package org.hamgen.builder;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;
import org.hamcrest.Matchers;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PrimitiveTypesInitializationBuilder extends MatcherInitializationBuilder {

    static {
        register(new PrimitiveTypesInitializationBuilder(),
                Boolean.class, boolean.class,
                Byte.class, byte.class,
                Character.class, char.class,
                Short.class, short.class,
                Integer.class, int.class,
                Long.class, long.class,
                Double.class, double.class,
                Float.class, float.class,
                Void.class, void.class);
    }

    @Override
    public List<Class<?>> getTypes() {
        List<Class<?>> types = new ArrayList<Class<?>>();
        types.add(Boolean.class);
        types.add(boolean.class);
        types.add(Character.class);
        types.add(char.class);
        types.add(Short.class);
        types.add(short.class);
        types.add(Integer.class);
        types.add(int.class);
        types.add(Long.class);
        types.add(long.class);
        types.add(Double.class);
        types.add(double.class);
        types.add(Float.class);
        types.add(float.class);
        types.add(Void.class);
        types.add(void.class);
        return types;
    }

    @Override
    public JBlock build() {
        JClass matchersClazz = codeModel.ref(Matchers.class);

        JExpression assignmentExpression = matchersClazz.staticInvoke("is").arg(expected.invoke(matcherField.getGetterName()));

        return constructorBody.assign(matcher, assignmentExpression);
    }
}
