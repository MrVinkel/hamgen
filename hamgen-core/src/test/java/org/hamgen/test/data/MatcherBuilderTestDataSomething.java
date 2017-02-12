package org.hamgen.test.data;

import org.hamgen.testdata.MatcherBuilderTestDataSomethingElse;

import javax.xml.bind.annotation.XmlType;

/**
 * DO NOT CHANGE THIS CLASS! it will brake tests
 */
@XmlType
public final class MatcherBuilderTestDataSomething {
    public MatcherBuilderTestDataSomethingElse getSomethingElse() {
        return new MatcherBuilderTestDataSomethingElse();
    }
}
