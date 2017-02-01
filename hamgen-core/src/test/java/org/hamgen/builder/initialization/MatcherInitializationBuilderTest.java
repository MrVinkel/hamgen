package org.hamgen.builder.initialization;

import org.hamgen.builder.initialization.*;
import org.hamgen.testdata.MatcherBuilderTestDataMyEnum;
import org.hamgen.testdata.MatcherBuilderTestDataSomething;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

public class MatcherInitializationBuilderTest {

    @Test
    public void t0700_getBuilderPrimitiveType() throws Exception {
        //Act
        MatcherInitializationBuilder builder = MatcherInitializationBuilder.getBuilder(Double.class);

        //Assert
        assertThat(builder, instanceOf(PrimitiveTypesInitializationBuilder.class));
    }

    @Test
    public void t0701_getBuilderEnum() throws Exception {
        //Act
        MatcherInitializationBuilder builder = MatcherInitializationBuilder.getBuilder(MatcherBuilderTestDataMyEnum.class);

        //Assert
        assertThat(builder, instanceOf(EnumMatcherInitializationBuilder.class));
    }

    @Test
    public void t0702_getBuilderArray() throws Exception {
        //Act
        MatcherInitializationBuilder builder = MatcherInitializationBuilder.getBuilder(Double[].class);

        //Assert
        assertThat(builder, instanceOf(PrimitiveTypesInitializationBuilder.class));
    }

    @Test
    public void t0703_getBuilderGeneratedClass() throws Exception {
        //Act
        MatcherInitializationBuilder builder = MatcherInitializationBuilder.getBuilder(MatcherBuilderTestDataSomething.class);

        //Assert
        assertThat(builder, instanceOf(GeneratedClassesInitializationBuilder.class));
    }

    @Test
    public void t0704_getBuilderString() throws Exception {
        //Act
        MatcherInitializationBuilder builder = MatcherInitializationBuilder.getBuilder(String.class);

        //Assert
        assertThat(builder, instanceOf(StringMatcherInitializationBuilder.class));
    }

    @Test
    public void t0705_getBuilderCollection() throws Exception {
        //Act
        MatcherInitializationBuilder builder = MatcherInitializationBuilder.getBuilder(List.class);

        //Assert
        assertThat(builder, instanceOf(CollectionMatcherInitializationBuilder.class));
    }

    @Test
    public void t0706_getBuilderCollection() throws Exception {
        //Act
        MatcherInitializationBuilder builder = MatcherInitializationBuilder.getBuilder(BigInteger.class);

        //Assert
        assertThat(builder, instanceOf(NullCheckMatcherInitializationBuilder.class));
    }
}
