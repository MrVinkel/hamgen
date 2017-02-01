package org.hamgen.builder.initialization;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JVar;
import org.hamgen.HamProperties;
import org.hamgen.builder.initialization.GeneratedClassesInitializationBuilder;
import org.hamgen.model.MatcherField;
import org.hamgen.test.data.ClassWithInnerClass;
import org.hamgen.testtools.CodeModelTestDataBuilder;
import org.junit.Test;

import java.util.regex.Matcher;

import static org.hamgen.testtools.CodeModelUtil.generableToString;

public class GeneratedClassesInitializationBuilderTest {

    private static final String MATCHER_PRE_FIX = HamProperties.Key.MATCHER_PRE_FIX.getDefaultValue();
    private static final String PACKAGE_POST_FIX = HamProperties.Key.PACKAGE_POST_FIX.getDefaultValue();

    @Test
    public void t0900() throws Exception {
        //Arrange
        JCodeModel codeModel = new JCodeModel();
        CodeModelTestDataBuilder testDataBuilder = new CodeModelTestDataBuilder().withCodeModel(codeModel);
        MatcherField matcherField = new MatcherField("getInnerClass", "innerClass", "Matcher", ClassWithInnerClass.MyInnerClass.class);

        GeneratedClassesInitializationBuilder builder = new GeneratedClassesInitializationBuilder();
        JVar matcherParam = testDataBuilder.buildJVar(Matcher.class, "innerClassMatcher");
        JVar expectedParam = testDataBuilder.buildJVar(ClassWithInnerClass.class, "expected");

        builder.withMatcher(matcherParam)
                .withExpected(expectedParam)
                .withMatcherField(matcherField)
                .withPackagePostFix(PACKAGE_POST_FIX)
                .withMatcherPreFix(MATCHER_PRE_FIX)
                .withConstructorBody(new JBlock())
                .withCodeModel(codeModel);

        //Act
        JBlock result = builder.build();

        //Assert
        System.out.println(generableToString(result));
    }
}
