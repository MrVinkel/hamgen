package org.hamgen.builder;

import org.hamgen.integrationtest.schemapackage.Address;
import org.hamgen.integrationtest.schemapackage.Company;
import org.hamgen.integrationtest.schemapackage.Employee;
import org.hamgen.integrationtest.schemapackage.EmployeeCollection;

import java.util.Collections;

import static org.hamgen.builder.AddressBuilder.aDefaultAddress;

public final class CompanyBuilder {
    protected String name;
    protected Address address;
    protected EmployeeCollection employeeCollection;

    private CompanyBuilder() {
    }

    public static CompanyBuilder aCompany() {
        return new CompanyBuilder();
    }

    public static CompanyBuilder aDefaultCompany() {
        return new CompanyBuilder()
                .withAddress(aDefaultAddress().build())
                .withEmployeeCollection(new EmployeeCollection())
                .withName("My Company");
    }

    public CompanyBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CompanyBuilder withAddress(Address address) {
        this.address = address;
        return this;
    }

    public CompanyBuilder withEmployee(Employee employee) {
        this.employeeCollection.getEmployee().add(employee);
        return this;
    }

    public CompanyBuilder withEmployees(Employee ...employees) {
        Collections.addAll(this.employeeCollection.getEmployee(), employees);
        return this;
    }

    public CompanyBuilder withEmployeeCollection(EmployeeCollection employeeCollection) {
        this.employeeCollection = employeeCollection;
        return this;
    }

    public Company build() {
        Company company = new Company();
        company.setName(name);
        company.setAddress(address);
        company.setEmployeeCollection(employeeCollection);
        return company;
    }
}
