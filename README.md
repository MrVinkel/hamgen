# HamGen

## Introduction
------------
HamGen is a plugin based on Hamcrest to generate matchers for model classes. It should be used together with Hamcrest.
The generated matchers are based on the TypeSafeDiagnosingMatcher which makes a nice output when a mismatch occurs.
The generated matchers supports nesting of JAXB object and collections.

I made this plugin due to being bored by writing my own matchers every time I had to test some code that returned large JAXB objects.

Why not just use assertEquals you ask? Consider a generated class from a 3rd party without a equals method. You'll have to write multiple assertEquals or make your own equals method which would clutter the test code.
Even when a model class has a equals method the TypeSafeDiagnosingMatcher produces a very nice 'easy to see what went wrong' output where it explicit teels you what was different in your multi layered model class with nested collections.

Inspired by [How Hamcrest can save your soul](http://blogs.atlassian.com/2009/06/how_hamcrest_can_save_your_sou/)

## Usage
------------

```xml
...
<dependency>
    <groupId>org.hamgen</groupId>
    <artifactId>hamgen-core</artifactId>
    <version>1.2</version>
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
    <version>1.2</version>
    <executions>
        <execution>
            <id>generate-matchers</id>
            <phase>generate-test-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <!-- Output directory of generated matchers -->
                <outputDirectory>${project.build.directory}/generated-test-sources/hamgen</outputDirectory>

                <!-- package names to scan for classes -->
                <packageNames>
                    <packageName>org.hamgen.integrationtest.schemapackage</packageName>
                    <packageName>org.hamgen.other.package</packageName>
                </packageNames>

                <!-- Annotation scanned classes must have -->
                <annotation>javax.xml.bind.annotation.XmlType</annotation>

                <!-- Classes to include that don't have the annotation -->
                <classIncludes>
                    <classInclude>my.custom.class.without.annotation</classInclude>
                </classIncludes>

                <!-- Classes to exclude/ignore when generated matchers -->
                <classExcludes>
                    <classExclude>a.class.i.do.not.want.to.match</classExclude>
                </classExcludes>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>my.dependency.to.scan</groupId>
            <artifactId>schemas</artifactId>
            <version>1.2.3</version>
        </dependency>
    </dependencies>
</plugin>
...
<!-- Add generated sources to jar -->
<plugin>
    <!-- https://mvnrepository.com/artifact/org.codehaus.mojo/build-helper-maven-plugin -->
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <version>1.12</version>
    <executions>
        <execution>
            <id>add-test-sources</id>
            <phase>process-test-resources</phase>
            <goals>
                <goal>add-test-source</goal>
            </goals>
            <configuration>
                <sources>
                    <source>${project.build.directory}/generated-test-sources/hamgen</source>
                </sources>
            </configuration>
        </execution>
    </executions>
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

This has been generated without an equals method from some 3rd party. Normally we would need three asserts to verify we had the correct person. Instead we kan now do this:
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

* 1.2 Rewrite to CodeModel - Replaced JavaPoet with CodeModel to support Java 1.6
* 1.1 POJO matchers - Generate Hamcrest matchers for any POJO with getter methods
* 1.0 Initial - Generate Hamcrest matchers for JAXB classes

## TODO
-----------

* Support for matching collection lax
* Verification of all nested matchers are generated
* List of classes to exclude (and rename classNames to includeClasses/excludeClasses) - Added in v1.3

### Bugs:

* Missing support for BigDecimal - Fixed in v1.3
* Missing support for BigInteger - Fixed in v1.3
* Missing support for XMLGregorianCalendar - Fixed in v1.3
* Missing support for byte arrays byte[] - Hacked in v1.3, needs proper fix and testing
* Wrong generation for inner classes - Fixed in v1.3
* Multiple collections initialization gives compile error - Fixed in v1.3
* Collections initialization don't look at excluded classes (Might be RarameterType)

## License
-----------

Copyright © 2017 Martin Vinkel <info@vinkel.email>
This work is free. You can redistribute it and/or modify it under the
terms of the Do What The Fuck You Want To Public License, Version 2,
as published by Sam Hocevar. See the LICENSE file for more details.

## Maintainers
-----------
* Martin Vinkel - Initial work - (mavvi)

