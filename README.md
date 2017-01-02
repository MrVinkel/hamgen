# HamGen

## Introduction
------------
HamGen is a plugin based on Hamcrest to generate matchers for model classes. It should be used together with Hamcrest.
The generated matchers are based on the TypeSafeDiagnosingMatcher which makes a nice output when a mismatch occurs.
The generated matchers supports nesting of JAXB object and collections.

I made this plugin due to being bored by writing my own matchers every time I had to test some code that returned large JAXB objects.

Inspired by [How Hamcrest can save your soul](http://blogs.atlassian.com/2009/06/how_hamcrest_can_save_your_sou/)

## Usage
------------

```xml
...
<dependency>
    <groupId>org.hamgen</groupId>
    <artifactId>hamgen-core</artifactId>
    <version>1.1</version>
</dependency>
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest-all</artifactId>
    <version>1.3</version>
</dependency>
...
<plugin>
    <groupId>org.hamgen</groupId>
    <artifactId>hamgen-maven-plugin</artifactId>
    <version>1.1</version>
    <executions>
        <execution>
            <id>generate-matchers</id>
            <phase>generate-test-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <outputDir>${project.build.directory}/generated-test-sources/hamgen</outputDir>
                <packageName>org.hamgen.integrationtest.schemapackage</packageName>
                <annotation>javax.xml.bind.annotation.XmlType</annotation>
                <classNames>
                    <className>my.custom.class.without.annotation</className>
                </classNames>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>my.dependency.to.scan</groupId>
            <artifactId>schemas</artifactId>
            <version>1.0</version>
        </dependency>
    </dependencies>
</plugin>
...
```

## Examples
-----------

Consider the following JAXB class PersonNameStruct:
```java
package my.package

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "firstName",
    "middleName",
    "lastName"
})
@XmlRootElement(name = "PersonNameStruct")
public class PersonNameStruct {

    @XmlElement(name = "FirstName", required = true)
    protected String firstName;
    @XmlElement(name = "MiddleName")
    protected String middleName;
    @XmlElement(name = "LastName")
    protected String lastName;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String value) {
        this.firstName = value;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String value) {
        this.middleName = value;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String value) {
        this.lastName = value;
    }

}
```

This has been generated without an equals method from some third party. Normally we would need three asserts to verify we had the correct person. Instead we kan now do this:
```java
@Test
public void testGetAPerson() throws Exception {
    //Arrange
    PersonNameStruct expected = new PersonNameStruct();
    expected.setFirstName("John");
    expected.setMiddleName("Poul");
    expected.setLastName("Einstein");

    //Act
    PersonNameStruct actual = someBusiness.getAPerson();

    //Assert
    assertThat(actual, isPersonNameStruct(expected));
}
```

If this fails we get an output telling precisely what went wrong:
```
java.lang.AssertionError:
Expected: {middleName is "Poul", firstName is "John", lastName is "Einstein"}
     but: {middleName was "Huggo"}

	at org.hamcrest.MatcherAssert.assertThat(MatcherAssert.java:20)
	...
```

## Changelog
-----------

* 1.1 POJO matchers - Generate Hamcrest matchers for any POJO with getter methods
* 1.0 Initial - Generate Hamcrest matchers for JAXB classes

## TODO
-----------

* Support for matching collection lax
* Verification of all nested matchers are generated

## License
-----------

Copyright © 2017 Martin Vinkel <info@vinkel.email>
This work is free. You can redistribute it and/or modify it under the
terms of the Do What The Fuck You Want To Public License, Version 2,
as published by Sam Hocevar. See the LICENSE file for more details.

## Maintainers
-----------
* Martin Vinkel - Initial work - (mavvi)

