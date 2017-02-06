package org.hamgen.builder.initialization;

import com.sun.codemodel.*;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamgen.util.ClassUtil;
import org.hamgen.util.StringUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CollectionMatcherInitializationBuilder extends MatcherInitializationBuilder {

    @Override
    public List<Class<?>> getTypes() {
        List<Class<?>> types = new ArrayList<Class<?>>();
        types.add(Collection.class);
        types.add(List.class);
        types.add(ArrayList.class);
        types.add(Set.class);
        return types;
    }

    @Override
    public JBlock build() {
        JClass matchersClazz = codeModel.ref(Matchers.class);
        JClass matcherClazz = codeModel.ref(Matcher.class);

        ParameterizedType parameterizedType = (ParameterizedType) matcherField.getType();
        Type collectionType = parameterizedType.getActualTypeArguments()[0];
        Class<?> collectionClass = (Class<?>) collectionType;

        JClass rawListClazz = codeModel.ref(List.class);
        JClass genericClazz = codeModel.ref(collectionClass);
        JClass itemsListClazz = rawListClazz.narrow(genericClazz);
        JVar itemsList = constructorBody.decl(itemsListClazz, matcherField.getName() + "Items", expected.invoke(matcherField.getGetterName()));

        JConditional isListNull = constructorBody._if(itemsList.eq(JExpr._null()));
        isListNull._then().assign(matcher, matchersClazz.staticInvoke("nullValue"));
        JBlock listNotEmptyBlock = isListNull._else();

        JClass matcherListClazz = rawListClazz.narrow(matcherClazz);
        JClass arrayListClass = codeModel.ref(ArrayList.class).narrow(matcherClazz);
        JVar matchersList = listNotEmptyBlock.decl(matcherListClazz, "matchers", JExpr._new(arrayListClass));

        JForEach itemLoop = listNotEmptyBlock.forEach(genericClazz, "item", itemsList);
        JVar item = itemLoop.var();
        JBlock itemLoopBlock = itemLoop.body();
        JInvocation invokeMatcherIs = matchersClazz.staticInvoke("is").arg(item);

        JExpression matcherInitialization;
        if (collectionType == String.class) {
            JExpression condition = item.eq(JExpr._null()).cor(item.invoke("isEmpty"));
            JInvocation invokeMatcherIsEmptyOrNullString = matchersClazz.staticInvoke("isEmptyOrNullString");
            matcherInitialization = JOp.cond(condition, invokeMatcherIsEmptyOrNullString, invokeMatcherIs);
        } else if (collectionClass.isPrimitive() || ClassUtil.isPrimitiveWrapper(collectionClass) || collectionClass.isEnum()) {
            matcherInitialization = invokeMatcherIs;
        } else {
            JExpression condition = item.eq(JExpr._null());
            JInvocation invokeMatcherNullValue = matchersClazz.staticInvoke("nullValue");

            String generatedMatcherName = collectionClass.getPackage().getName() + packagePostFix + "." + collectionClass.getSimpleName() + matcherField.getFieldPostFix();
            String generatedMatcherFactoryName = matcherPreFix + StringUtil.capitalizeFirstLetter(collectionClass.getSimpleName());
            JClass generatedMatcherClass = codeModel.ref(generatedMatcherName);
            JInvocation invokeGeneratedMatcher = generatedMatcherClass.staticInvoke(generatedMatcherFactoryName).arg(item);

            matcherInitialization = JOp.cond(condition, invokeMatcherNullValue, invokeGeneratedMatcher);
        }
        JVar itemMatcher = itemLoopBlock.decl(matcherClazz, "itemMatcher", matcherInitialization);
        itemLoopBlock.add(matchersList.invoke("add").arg(itemMatcher));

        listNotEmptyBlock.assign(matcher, matchersClazz.staticInvoke("contains").arg(matchersList.invoke("toArray").arg(JExpr.newArray(matcherClazz, matchersList.invoke("size")))));

        return constructorBody;
    }
}
