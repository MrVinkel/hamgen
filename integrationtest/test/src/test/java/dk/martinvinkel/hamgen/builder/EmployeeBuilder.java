package dk.martinvinkel.hamgen.builder;

import dk.martinvinkel.hamgen.integrationtest.schemapackage.Address;
import dk.martinvinkel.hamgen.integrationtest.schemapackage.Employee;
import dk.martinvinkel.hamgen.integrationtest.schemapackage.PersonNameStruct;
import dk.martinvinkel.hamgen.integrationtest.schemapackage.Status;

import static dk.martinvinkel.hamgen.builder.AddressBuilder.aDefaultAddress;
import static dk.martinvinkel.hamgen.builder.PersonNameStructBuilder.aDefaultPersonNameStruct;
import static dk.martinvinkel.hamgen.integrationtest.schemapackage.Status.HIRED;

public class EmployeeBuilder {
    protected String jobPosition;
    protected Status status;
    protected double pay;
    protected PersonNameStruct personNameStruct;
    protected Address address;
    protected String email;

    private EmployeeBuilder() {
    }

    public static EmployeeBuilder anEmployee() {
        return new EmployeeBuilder();
    }

    public static EmployeeBuilder aDefaultEmployee() {
        return new EmployeeBuilder()
                .withAddress(aDefaultAddress().build())
                .withPersonNameStruct(aDefaultPersonNameStruct().build())
                .withEmail("default@email.com")
                .withJobPosition("Developer")
                .withPay(1000000.1)
                .withStatus(HIRED);
    }

    public EmployeeBuilder withJobPosition(String jobPosition) {
        this.jobPosition = jobPosition;
        return this;
    }

    public EmployeeBuilder withStatus(Status status) {
        this.status = status;
        return this;
    }

    public EmployeeBuilder withPay(double pay) {
        this.pay = pay;
        return this;
    }

    public EmployeeBuilder withPersonNameStruct(PersonNameStruct personNameStruct) {
        this.personNameStruct = personNameStruct;
        return this;
    }

    public EmployeeBuilder withAddress(Address address) {
        this.address = address;
        return this;
    }

    public EmployeeBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public Employee build() {
        Employee employee = new Employee();
        employee.setJobPosition(jobPosition);
        employee.setStatus(status);
        employee.setPay(pay);
        employee.setPersonNameStruct(personNameStruct);
        employee.setAddress(address);
        employee.setEmail(email);
        return employee;
    }
}
