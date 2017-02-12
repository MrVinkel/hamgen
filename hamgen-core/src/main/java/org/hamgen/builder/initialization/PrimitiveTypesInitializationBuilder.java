package org.hamgen.builder.initialization;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpression;
import org.hamcrest.Matchers;

import java.util.ArrayList;
import java.util.List;

public class PrimitiveTypesInitializationBuilder extends MatcherInitializationBuilder {

    private static final String METHOD_NAME_IS = "is";

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

        JExpression assignmentExpression = matchersClazz.staticInvoke(METHOD_NAME_IS).arg(expected.invoke(matcherField.getGetterName()));

        return constructorBody.assign(matcher, assignmentExpression);
    }
}
