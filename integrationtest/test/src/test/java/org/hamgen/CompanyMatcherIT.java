package org.hamgen;

import org.hamgen.integrationtest.schemapackage.Company;
import org.hamgen.integrationtest.schemapackage.Employee;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamgen.builder.CompanyBuilder;
import org.hamgen.builder.EmployeeBuilder;
import org.hamgen.builder.PersonNameStructBuilder;
import org.junit.Test;

import static org.hamgen.integrationtest.schemapackage.matcher.CompanyMatcher.isCompany;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompanyMatcherIT {

    @Test
    public void t1201_success() throws Exception {
        //Arrange
        Employee expectedEmployee1 = EmployeeBuilder.aDefaultEmployee().withPay(123).build();
        Employee expectedEmployee2 = EmployeeBuilder.aDefaultEmployee().withPay(321).build();
        Company expected = CompanyBuilder.aDefaultCompany().withEmployees(expectedEmployee1, expectedEmployee2).build();

        Employee actualEmployee1 = EmployeeBuilder.aDefaultEmployee().withPay(123).build();
        Employee actualEmployee2 = EmployeeBuilder.aDefaultEmployee().withPay(321).build();
        Company actual = CompanyBuilder.aDefaultCompany().withEmployees(actualEmployee1, actualEmployee2).build();


        //Act
        Matcher companyMatcher = isCompany(expected);
        boolean result = companyMatcher.matches(actual);

        //Assert
        String errorReason = getDescription("Not the same :(", companyMatcher, actual).toString();
        assertTrue(errorReason, result);
    }

    @Test
    public void t1202_describeMismatchInList() throws Exception {
        //Arrange
        Employee expectedEmployee1 = EmployeeBuilder.aDefaultEmployee().withPay(123).build();
        Employee expectedEmployee2 = EmployeeBuilder.aDefaultEmployee().withPay(321).build();
        Company expected = CompanyBuilder.aDefaultCompany().withEmployees(expectedEmployee1, expectedEmployee2).build();

        Employee actualEmployee1 = EmployeeBuilder.aDefaultEmployee().withPay(123).withPersonNameStruct(PersonNameStructBuilder.aDefaultPersonNameStruct().withFirstName("LOL").build()).build();
        Employee actualEmployee2 = EmployeeBuilder.aDefaultEmployee().withPay(321).build();
        Company actual = CompanyBuilder.aDefaultCompany().withEmployees(actualEmployee1, actualEmployee2).build();


        //Act
        Matcher companyMatcher = isCompany(expected);
        StringDescription mismatchDescription = new StringDescription();
        companyMatcher.describeMismatch(actual, mismatchDescription);

        //Assert
        String descriptionResult = mismatchDescription.toString();
        assertTrue(descriptionResult, descriptionResult.equals("{employeeCollection {employee item 0: {personNameStruct {firstName was \"LOL\"}}}}"));
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
