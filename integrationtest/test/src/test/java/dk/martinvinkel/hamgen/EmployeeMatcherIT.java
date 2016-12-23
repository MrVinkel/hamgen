package dk.martinvinkel.hamgen;

import dk.martinvinkel.hamgen.integrationtest.schemapackage.Employee;
import org.hamcrest.Matcher;
import org.junit.Test;

import static dk.martinvinkel.hamgen.builder.EmployeeBuilder.aDefaultEmployee;
import static dk.martinvinkel.hamgen.integrationtest.schemapackage.matcher.EmployeeMatcher.isEmployee;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EmployeeMatcherIT {

    @Test
    public void t1101_success() throws Exception {
        //Arrange
        Employee employee = aDefaultEmployee().build();

        //Act
        boolean result = isEmployee(employee).matches(employee);

        //Assert
        assertTrue(result);
    }

    @Test
    public void t1102_description() throws Exception {
        //Arrange
        Employee expected = aDefaultEmployee().build();
        Employee actual = aDefaultEmployee().withPay(123).build();

        //Act
        Matcher employeeMatcher = isEmployee(expected);
        boolean result = employeeMatcher.matches(actual);

        // do the same string description magic as assertThat does
        BufferDescription description = new BufferDescription();
        description.appendText("My Reason")
                .appendText("\nExpected: ")
                .appendDescriptionOf(employeeMatcher)
                .appendText("\n     but: ");
        employeeMatcher.describeMismatch(actual, description);

        //Assert
        assertFalse(result);
        System.out.println(description.toString());


    }


}
