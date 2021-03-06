package org.hamgen;

import org.hamgen.integrationtest.schemapackage.Address;
import org.hamgen.integrationtest.schemapackage.Employee;
import org.hamgen.integrationtest.schemapackage.PersonNameStruct;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

import static org.hamgen.builder.AddressBuilder.aDefaultAddress;
import static org.hamgen.builder.EmployeeBuilder.aDefaultEmployee;
import static org.hamgen.integrationtest.schemapackage.Status.FIRED;
import static org.hamgen.integrationtest.schemapackage.Status.HIRED;
import static org.hamgen.integrationtest.schemapackage.matcher.EmployeeMatcher.isEmployee;
import static org.hamgen.integrationtest.schemapackage.matcher.PersonNameStructMatcher.isPersonNameStruct;
import static org.hamcrest.MatcherAssert.assertThat;
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

    @Test
    public void testGetAPerson() throws Exception {
        //Arrange
        PersonNameStruct expected = new PersonNameStruct();
        expected.setFirstName("John");
        expected.setMiddleName("Poul");
        expected.setLastName("Einstein");

        //Act
        PersonNameStruct actual = getAPerson();

        //Assert
        assertThat(actual, isPersonNameStruct(expected));
    }

    private PersonNameStruct getAPerson() {
        PersonNameStruct expected = new PersonNameStruct();
        expected.setFirstName("John");
        expected.setMiddleName("Huggo");
        expected.setLastName("Einstein");
        return expected;
    }

}
