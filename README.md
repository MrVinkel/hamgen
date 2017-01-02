CONTENTS OF THIS FILE
---------------------

 * Introduction
 * Usage
 * Examples
 * Changelog
 * License
 * Maintainers

INTRODUCTION
------------

USAGE
------------

```xml
<dependencies>
    <dependency>
        <groupId>dk.martinvinkel</groupId>
        <artifactId>hamgen-core</artifactId>
        <version>1.0</version>
    </dependency>
    <dependency>
        <groupId>org.hamcrest</groupId>
        <artifactId>hamcrest-all</artifactId>
        <version>1.3</version>
    </dependency>
</dependencies>
...
<build>
    <plugins>
        <plugin>
            <groupId>dk.martinvinkel</groupId>
            <artifactId>hamgen-maven-plugin</artifactId>
            <version>1.0</version>
            <executions>
                <execution>
                    <id>generate-jaxb-matchers</id>
                    <phase>generate-test-sources</phase>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                    <configuration>
                        <packageName>org.hamgen.integrationtest.schemapackage</packageName>
                        <outputDir>${project.build.directory}/generated-test-sources/hamgen</outputDir>
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
    </plugins>
</build>
```

EXAMPLES
------------

CHANGELOG
-----------

 * 1.0 Initial - Generate Hamcrest matchers for JAXB classes

 Not yet implemented:
 * Support for matching collections exact and lax
 * Support for generating matchers for any POJO model class

LICENSE
-----------

MAINTAINERS
-----------

 Current maintainers:
 * Martin V. Vinkel (mavvi)

