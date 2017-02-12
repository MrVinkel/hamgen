package org.hamgen.builder.initialization;

import org.hamgen.testdata.MatcherBuilderTestDataMyEnum;
import org.hamgen.testdata.MatcherBuilderTestDataSomething;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public class MatcherInitializationBuilderTest {

    private Class<?> inputClass;
    private Class<?> outputBuilderClass;

    @Parameters
    public static Collection typeToBuilderParameters() {
        return Arrays.asList(new Object[][]{

                // PrimitiveTypesInitializationBuilder
                {Double.class, PrimitiveTypesInitializationBuilder.class},
                {double.class, PrimitiveTypesInitializationBuilder.class},
                {Boolean.class, PrimitiveTypesInitializationBuilder.class},
                {boolean.class, PrimitiveTypesInitializationBuilder.class},
                {Character.class, PrimitiveTypesInitializationBuilder.class},
                {char.class, PrimitiveTypesInitializationBuilder.class},
                {Short.class, PrimitiveTypesInitializationBuilder.class},
                {short.class, PrimitiveTypesInitializationBuilder.class},
                {Integer.class, PrimitiveTypesInitializationBuilder.class},
                {int.class, PrimitiveTypesInitializationBuilder.class},
                {Long.class, PrimitiveTypesInitializationBuilder.class},
                {long.class, PrimitiveTypesInitializationBuilder.class},
                {Double.class, PrimitiveTypesInitializationBuilder.class},
                {double.class, PrimitiveTypesInitializationBuilder.class},
                {Float.class, PrimitiveTypesInitializationBuilder.class},
                {float.class, PrimitiveTypesInitializationBuilder.class},
                {Void.class, PrimitiveTypesInitializationBuilder.class},
                {void.class, PrimitiveTypesInitializationBuilder.class},

                // Arrays
                {double[].class, PrimitiveTypesInitializationBuilder.class},

                // EnumMatcherInitializationBuilder
                {MatcherBuilderTestDataMyEnum.class, EnumMatcherInitializationBuilder.class},

                // GeneratedClassesInitializationBuilder
                {MatcherBuilderTestDataSomething.class, GeneratedClassesInitializationBuilder.class},

                // NullCheckMatcherInitializationBuilder
                {BigDecimal.class, NullCheckMatcherInitializationBuilder.class},
                {BigInteger.class, NullCheckMatcherInitializationBuilder.class},
                {XMLGregorianCalendar.class, NullCheckMatcherInitializationBuilder.class},
                {Object.class, NullCheckMatcherInitializationBuilder.class},

                // StringMatcherInitializationBuilder
                {String.class, StringMatcherInitializationBuilder.class},

                // CollectionMatcherInitializationBuilder
                {Collection.class, CollectionMatcherInitializationBuilder.class},
                {List.class, CollectionMatcherInitializationBuilder.class},
                {ArrayList.class, CollectionMatcherInitializationBuilder.class},
                {Set.class, CollectionMatcherInitializationBuilder.class},

        });
    }

    public MatcherInitializationBuilderTest(Class<?> inputClass, Class<?> outputBuilderClass) {
        this.inputClass = inputClass;
        this.outputBuilderClass = outputBuilderClass;
    }

    @Test
    public void t0700_getBuilderForType() throws Exception {
        //Act
        MatcherInitializationBuilder builder = MatcherInitializationBuilder.getBuilder(inputClass);

        //Assert
        assertThat(builder, instanceOf(outputBuilderClass));
    }
}
