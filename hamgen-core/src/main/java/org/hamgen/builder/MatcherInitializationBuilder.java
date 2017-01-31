package org.hamgen.builder;

import com.sun.codemodel.*;
import org.hamgen.log.Logger;
import org.hamgen.model.MatcherField;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public abstract class MatcherInitializationBuilder {

    private static final Logger LOGGER = Logger.getLogger();

    private static Map<Class<?>, MatcherInitializationBuilder> builders = new HashMap<Class<?>, MatcherInitializationBuilder>();

    protected JCodeModel codeModel;
    protected JBlock constructorBody;
    protected JVar expected;
    protected JVar matcher;
    protected MatcherField matcherField;
    protected String packagePostFix;
    protected String matcherPreFix;

    static {
        try {
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forPackage(MatcherInitializationBuilder.class.getPackage().getName()))
                    .setScanners(new SubTypesScanner()));
            Set<Class<? extends MatcherInitializationBuilder>> builders = reflections.getSubTypesOf(MatcherInitializationBuilder.class);
            for(Class<? extends MatcherInitializationBuilder> builder : builders) {
                MatcherInitializationBuilder builderInstance = builder.newInstance();
                Class<?>[] types = builderInstance.getTypes().toArray(new Class<?>[builderInstance.getTypes().size()]);
                register(builderInstance, types);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to initialize builders");
        }
    }

    protected MatcherInitializationBuilder() {
        // use getBuilder
    }

    protected static void register(MatcherInitializationBuilder builder, Class<?>... types) {
        for (Class<?> type : types) {
            LOGGER.debug("Registering initialization builder " + Arrays.toString(types));
            builders.put(type, builder);
        }
    }

    public static MatcherInitializationBuilder getBuilder(Type type) {
        if(type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }

        if(builders.containsKey(type)) {
            return builders.get(type);
        } else if (((Class<?>) type).isEnum()) {
            return new EnumMatcherInitializationBuilder();
        } else if(((Class<?>) type).isArray()) {
            // NOT TESTED
            return new PrimitiveTypesInitializationBuilder();
        } else {
            return new GeneratedClassesInitializationBuilder();
        }
    }

    public MatcherInitializationBuilder withCodeModel(JCodeModel codeModel) {
        this.codeModel = codeModel;
        return this;
    }

    public MatcherInitializationBuilder withConstructorBody(JBlock constructorBody) {
        this.constructorBody = constructorBody;
        return this;
    }

    public MatcherInitializationBuilder withExpected(JVar expected) {
        this.expected = expected;
        return this;
    }

    public MatcherInitializationBuilder withMatcher(JVar matcher) {
        this.matcher = matcher;
        return this;
    }

    public MatcherInitializationBuilder withMatcherField(MatcherField matcherField) {
        this.matcherField = matcherField;
        return this;
    }

    public MatcherInitializationBuilder withPackagePostFix(String packagePostFix) {
        this.packagePostFix = packagePostFix;
        return this;
    }

    public MatcherInitializationBuilder withMatcherPreFix(String matcherPreFix) {
        this.matcherPreFix = matcherPreFix;
        return this;
    }

    public abstract List<Class<?>> getTypes();

    public abstract JBlock build();
}
