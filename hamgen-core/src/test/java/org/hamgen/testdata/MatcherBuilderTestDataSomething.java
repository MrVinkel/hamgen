package org.hamgen.testdata;

import javax.xml.bind.annotation.XmlType;

/**
 * DO NOT CHANGE THIS CLASS! it will brake MatcherBuildTest
 */
@XmlType
public final class MatcherBuilderTestDataSomething {
    public MatcherBuilderTestDataSomethingElse getSomethingElse() {
        return new MatcherBuilderTestDataSomethingElse();
    }
}
