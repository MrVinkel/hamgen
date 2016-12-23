package dk.martinvinkel.hamgen;

import dk.martinvinkel.hamgen.integrationtest.schemapackage.Employee;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

import static dk.martinvinkel.hamgen.builder.EmployeeBuilder.aDefaultEmployee;
import static dk.martinvinkel.hamgen.integrationtest.schemapackage.matcher.EmployeeMatcher.isEmployee;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EmployeeMatcherIT {

    @Test
    public void t1101_success() throws Exception {
        //Arrange
        Employee expected = aDefaultEmployee().build();
        Employee actual = aDefaultEmployee().build();

        //Act
        Matcher employeeMatcher = isEmployee(expected);
        boolean result = employeeMatcher.matches(actual);

        //Assert
        String errorReason = getDescription("Not the same :(", employeeMatcher, actual).toString();
        assertTrue(errorReason, result);
    }

    @Test
    public void t1102_describeTo() throws Exception {
        //Arrange
        Employee expected = aDefaultEmployee().build();

        String expectedDescription =
                "{address " +
                    "{cityName New York, postCode 8453, streetName Peace Road, floor 42, streetNumber 101010}, " +
                "email default@email.com, " +
                "status HIRED, " +
                "pay 1000000.1, " +
                "jobPosition Developer, " +
                "personNameStruct " +
                    "{firstName Bjarne, middleName Nerd, lastName Stroustrup}}";

        //Act
        Matcher employeeMatcher = isEmployee(expected);
        StringDescription description = new StringDescription();
        employeeMatcher.describeTo(description);

        //Assert
        assertEquals(expectedDescription, description.toString());
    }

    @Test
    public void t1102_describeMismatch() throws Exception {
        //Arrange
        Employee expected = aDefaultEmployee().withPay(1000000).build();
        Employee actual = aDefaultEmployee().withPay(15).build();

        String expectedDescription = "{pay was 15}";

        //Act
        Matcher employeeMatcher = isEmployee(expected);
        StringDescription mismatchDescription = new StringDescription();
        employeeMatcher.describeMismatch(actual, mismatchDescription);

        //Assert
        assertEquals(expectedDescription, mismatchDescription.toString());
    }

    private BufferDescription getDescription(String reason, Matcher matcher, Object actual) {
        BufferDescription description = new BufferDescription();
        // Do the same magic as assertThat
        description.appendText(reason)
                .appendText("\nExpected: ")
                .appendDescriptionOf(matcher)
                .appendText("\n     but: ");
        matcher.describeMismatch(actual, description);
        return description;
    }


}
