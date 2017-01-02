package org.hamgen.builder;

import org.hamgen.integrationtest.schemapackage.PersonNameStruct;

public class PersonNameStructBuilder {
    protected String firstName;
    protected String middleName;
    protected String lastName;

    private PersonNameStructBuilder() {
    }

    public static PersonNameStructBuilder aPersonNameStruct() {
        return new PersonNameStructBuilder();
    }

    public static PersonNameStructBuilder aDefaultPersonNameStruct() {
        return new PersonNameStructBuilder()
                .withFirstName("Bjarne")
                .withMiddleName("Nerd")
                .withLastName("Stroustrup");
    }

    public PersonNameStructBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public PersonNameStructBuilder withMiddleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public PersonNameStructBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public PersonNameStruct build() {
        PersonNameStruct personNameStruct = new PersonNameStruct();
        personNameStruct.setFirstName(firstName);
        personNameStruct.setMiddleName(middleName);
        personNameStruct.setLastName(lastName);
        return personNameStruct;
    }
}
