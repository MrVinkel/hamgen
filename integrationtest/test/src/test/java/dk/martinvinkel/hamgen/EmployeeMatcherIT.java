package dk.martinvinkel.hamgen;

import dk.martinvinkel.hamgen.integrationtest.schemapackage.Address;
import dk.martinvinkel.hamgen.integrationtest.schemapackage.Employee;
import dk.martinvinkel.hamgen.integrationtest.schemapackage.Status;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

import static dk.martinvinkel.hamgen.builder.AddressBuilder.aDefaultAddress;
import static dk.martinvinkel.hamgen.builder.EmployeeBuilder.aDefaultEmployee;
import static dk.martinvinkel.hamgen.integrationtest.schemapackage.Status.FIRED;
import static dk.martinvinkel.hamgen.integrationtest.schemapackage.Status.HIRED;
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

        //Act
        Matcher employeeMatcher = isEmployee(expected);
        StringDescription description = new StringDescription();
        employeeMatcher.describeTo(description);

        //Assert
        //The matchers are generated with random order each time, so we can't check on the exact description..
        String descriptionResult = description.toString();
        assertTrue(descriptionResult, descriptionResult.contains("address {"));
        assertTrue(descriptionResult, descriptionResult.contains("cityName is \"New York\""));
        assertTrue(descriptionResult, descriptionResult.contains("postCode is \"8453\""));
        assertTrue(descriptionResult, descriptionResult.contains("streetName is \"Peace Road\""));
        assertTrue(descriptionResult, descriptionResult.contains("floor is <42>"));
        assertTrue(descriptionResult, descriptionResult.contains("streetNumber is \"101010\""));
        assertTrue(descriptionResult, descriptionResult.contains("email is \"default@email.com\""));
        assertTrue(descriptionResult, descriptionResult.contains("status is <HIRED>"));
        assertTrue(descriptionResult, descriptionResult.contains("pay is <1000000.1>"));
        assertTrue(descriptionResult, descriptionResult.contains("jobPosition is \"Developer\""));
        assertTrue(descriptionResult, descriptionResult.contains("personNameStruct {"));
        assertTrue(descriptionResult, descriptionResult.contains("firstName is \"Bjarne\""));
        assertTrue(descriptionResult, descriptionResult.contains("middleName is \"Nerd\""));
        assertTrue(descriptionResult, descriptionResult.contains("lastName is \"Stroustrup\""));
    }

    @Test
    public void t1102_describeMismatch() throws Exception {
        //Arrange
        Employee expected = aDefaultEmployee().withPay(1000000).build();
        Employee actual = aDefaultEmployee().withPay(15).build();

        String expectedDescription = "{pay was <15>}";

        //Act
        Matcher employeeMatcher = isEmployee(expected);
        StringDescription mismatchDescription = new StringDescription();
        employeeMatcher.describeMismatch(actual, mismatchDescription);

        //Assert
        assertEquals(expectedDescription, mismatchDescription.toString());
    }

    @Test
    public void t1103_describeMismatchNested() throws Exception {
        //Arrange
        Employee expected = aDefaultEmployee().build();
        Address address = aDefaultAddress().withCityName("London").build();
        Employee actual = aDefaultEmployee().withAddress(address).build();

        String expectedDescription = "{address {cityName was \"London\"}}";

        //Act
        Matcher employeeMatcher = isEmployee(expected);
        StringDescription mismatchDescription = new StringDescription();
        employeeMatcher.describeMismatch(actual, mismatchDescription);

        //Assert
        assertEquals(expectedDescription, mismatchDescription.toString());
    }

    @Test
    public void t1104_describeMismatchEnum() throws Exception {
        //Arrange
        Employee expected = aDefaultEmployee().withStatus(HIRED).build();
        Employee actual = aDefaultEmployee().withStatus(FIRED).build();

        String expectedDescription = "{status was <FIRED>}";

        //Act
        Matcher employeeMatcher = isEmployee(expected);
        StringDescription mismatchDescription = new StringDescription();
        employeeMatcher.describeMismatch(actual, mismatchDescription);

        //Assert
        assertEquals(expectedDescription, mismatchDescription.toString());
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
