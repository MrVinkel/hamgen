package org.hamgen;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamgen.integrationtest.schemapackage.NestedParentOne;
import org.junit.Test;


import static org.hamgen.integrationtest.schemapackage.matcher.NestedParentOneMatcher.isNestedParentOne;
import static org.junit.Assert.assertFalse;

public class NestedMatcherIT {

    @Test
    public void t1501() {
        //Arrange
        NestedParentOne expected = createNestedParent("");
        NestedParentOne actual = createNestedParent("wrong");

        //Act
        Matcher employeeMatcher = isNestedParentOne(expected);
        boolean result = employeeMatcher.matches(actual);

        //Assert
        String errorReason = getDescription("This should fail", employeeMatcher, actual).toString();
        System.out.println(errorReason);
        assertFalse(errorReason, result);

    }

    private NestedParentOne createNestedParent(String customFieldValue) {
        NestedParentOne.NestedElement.DoubleNestedElement doubleNestedElement = new NestedParentOne.NestedElement.DoubleNestedElement();
        doubleNestedElement.setItemOne("Double Nested One " + customFieldValue);

        NestedParentOne.NestedElement nestedElement = new NestedParentOne.NestedElement();
        nestedElement.setItemOne("Nested One " + customFieldValue);
        nestedElement.setDoubleNestedElement(doubleNestedElement);

        NestedParentOne parentOne = new NestedParentOne();
        parentOne.setElementOne("Parent One " + customFieldValue);
        parentOne.setNestedElement(nestedElement);
        return parentOne;
    }

    private Description getDescription(String reason, Matcher matcher, Object actual) {
        Description description = new StringDescription();
        // Do the same magic as assertThat
        description.appendText(reason)
                .appendText("\nExpected: ")
                .appendDescriptionOf(matcher)
                .appendText("\n     but: ");
        matcher.describeMismatch(actual, description);
        return description;
    }
}
