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

    private static final String PARAM_NAME_ITEMS = "Items";
    private static final String PARAM_NAME_ITEM = "item";
    private static final String PARAM_NAME_MATCHERS = "matchers";
    private static final String PARAM_NAME_ITEM_MATCHER = "itemMatcher";

    private static final String METHOD_NAME_NULL_VALUE = "nullValue";
    private static final String METHOD_NAME_IS = "is";
    private static final String METHOD_NAME_IS_EMPTY = "isEmpty";
    private static final String METHOD_NAME_IS_EMPTY_OR_NULL_STRING = "isEmptyOrNullString";
    private static final String METHOD_NAME_ADD = "add";
    private static final String METHOD_NAME_CONTAINS = "contains";
    private static final String METHOD_NAME_TO_ARRAY = "toArray";
    private static final String METHOD_NAME_SIZE = "size";

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
        JVar itemsList = constructorBody.decl(itemsListClazz, matcherField.getName() + PARAM_NAME_ITEMS, expected.invoke(matcherField.getGetterName()));

        JConditional isListNull = constructorBody._if(itemsList.eq(JExpr._null()));
        isListNull._then().assign(matcher, matchersClazz.staticInvoke(METHOD_NAME_NULL_VALUE));
        JBlock listNotEmptyBlock = isListNull._else();

        JClass matcherListClazz = rawListClazz.narrow(matcherClazz);
        JClass arrayListClass = codeModel.ref(ArrayList.class).narrow(matcherClazz);
        JVar matchersList = listNotEmptyBlock.decl(matcherListClazz, PARAM_NAME_MATCHERS, JExpr._new(arrayListClass));

        JForEach itemLoop = listNotEmptyBlock.forEach(genericClazz, PARAM_NAME_ITEM, itemsList);
        JVar item = itemLoop.var();
        JBlock itemLoopBlock = itemLoop.body();
        JInvocation invokeMatcherIs = matchersClazz.staticInvoke(METHOD_NAME_IS).arg(item);

        JExpression matcherInitialization;
        if (collectionType == String.class) {
            JExpression condition = item.eq(JExpr._null()).cor(item.invoke(METHOD_NAME_IS_EMPTY));
            JInvocation invokeMatcherIsEmptyOrNullString = matchersClazz.staticInvoke(METHOD_NAME_IS_EMPTY_OR_NULL_STRING);
            matcherInitialization = JOp.cond(condition, invokeMatcherIsEmptyOrNullString, invokeMatcherIs);
        } else if (collectionClass.isPrimitive() || ClassUtil.isPrimitiveWrapper(collectionClass) || collectionClass.isEnum()) {
            matcherInitialization = invokeMatcherIs;
        } else {
            JExpression condition = item.eq(JExpr._null());
            JInvocation invokeMatcherNullValue = matchersClazz.staticInvoke(METHOD_NAME_NULL_VALUE);

            String generatedMatcherName = collectionClass.getPackage().getName() + packagePostFix + "." + collectionClass.getSimpleName() + matcherField.getFieldPostFix();
            String generatedMatcherFactoryName = matcherPreFix + StringUtil.capitalizeFirstLetter(collectionClass.getSimpleName());
            JClass generatedMatcherClass = codeModel.ref(generatedMatcherName);
            JInvocation invokeGeneratedMatcher = generatedMatcherClass.staticInvoke(generatedMatcherFactoryName).arg(item);

            matcherInitialization = JOp.cond(condition, invokeMatcherNullValue, invokeGeneratedMatcher);
        }
        JVar itemMatcher = itemLoopBlock.decl(matcherClazz, PARAM_NAME_ITEM_MATCHER, matcherInitialization);
        itemLoopBlock.add(matchersList.invoke(METHOD_NAME_ADD).arg(itemMatcher));

        listNotEmptyBlock.assign(matcher, matchersClazz.staticInvoke(METHOD_NAME_CONTAINS).arg(matchersList.invoke(METHOD_NAME_TO_ARRAY).arg(JExpr.newArray(matcherClazz, matchersList.invoke(METHOD_NAME_SIZE)))));

        return constructorBody;
    }
}
