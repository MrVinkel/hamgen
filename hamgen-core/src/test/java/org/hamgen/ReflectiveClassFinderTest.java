package org.hamgen;

import org.hamcrest.Matcher;
import org.hamgen.testdata.MatcherBuilderTestDataListSomething;
import org.hamgen.testdata.MatcherBuilderTestDataSomething;
import org.hamgen.testdata.MatcherBuilderTestDataSomethingElse;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class ReflectiveClassFinderTest {

    @Test
    public void t0601_findAllClassesInPackage() throws Exception {
        //Arrange
        ReflectiveClassFinder classFinder = new ReflectiveClassFinder();

        List<String> packages = new ArrayList<String>();
        packages.add("org.hamgen.testdata");

        //Act
        Collection<Class<?>> result = classFinder.findClassesWithAnnotation(packages, null);


        //Assert
        Assert.assertEquals(3, result.size());
        assertThat(result, (Matcher)hasItem(hasProperty("name", is(MatcherBuilderTestDataListSomething.class.getName()))));
        assertThat(result, (Matcher)hasItem(hasProperty("name", is(MatcherBuilderTestDataSomething.class.getName()))));
        assertThat(result, (Matcher)hasItem(hasProperty("name", is(MatcherBuilderTestDataSomethingElse.class.getName()))));
    }

    @Test
    public void t0602_findAllClassesInPackageButWithAnnotation() throws Exception {
        //Arrange
        ReflectiveClassFinder classFinder = new ReflectiveClassFinder();

        List<String> packages = new ArrayList<String>();
        packages.add("org.hamgen.testdata");

        //Act
        Collection<Class<?>> result = classFinder.findClassesWithAnnotation(packages, XmlType.class.getName());

        //Assert
        Assert.assertEquals(1, result.size());
        assertThat(result, (Matcher)hasItem(hasProperty("name", is(MatcherBuilderTestDataSomething.class.getName()))));
    }

    @Test
    public void t0603_unknownPackageNameYieldsNothing() throws Exception {
        //Arrange
        ReflectiveClassFinder classFinder = new ReflectiveClassFinder();

        List<String> packages = new ArrayList<String>();
        packages.add("my.random.package.name.potato");

        //Act
        Collection<Class<?>> result = classFinder.findClassesWithAnnotation(packages, null);

        //Assert
        Assert.assertEquals(0, result.size());
    }

    @Test(expected = ClassNotFoundException.class)
    public void t0604_unknownAnnotationThrowsException() throws Exception {
        //Arrange
        ReflectiveClassFinder classFinder = new ReflectiveClassFinder();

        List<String> packages = new ArrayList<String>();
        packages.add("org.hamgen.testdata");

        //Act
        classFinder.findClassesWithAnnotation(packages, "my.random.annotation.name.potato");
    }

    @Test
    public void t0605_multiplePackages() throws Exception {
        //Arrange
        ReflectiveClassFinder classFinder = new ReflectiveClassFinder();

        List<String> packages = new ArrayList<String>();
        packages.add("org.hamgen.testdata");
        packages.add("org.hamgen.test.data");

        //Act
        Collection<Class<?>> result = classFinder.findClassesWithAnnotation(packages, null);

        //Assert
        Assert.assertEquals(4, result.size());
        assertThat(result, (Matcher)hasItem(hasProperty("name", is(MatcherBuilderTestDataListSomething.class.getName()))));
        assertThat(result, (Matcher)hasItem(hasProperty("name", is(MatcherBuilderTestDataSomething.class.getName()))));
        assertThat(result, (Matcher)hasItem(hasProperty("name", is(MatcherBuilderTestDataSomethingElse.class.getName()))));
        assertThat(result, (Matcher)hasItem(hasProperty("name", is(org.hamgen.test.data.MatcherBuilderTestDataSomething.class.getName()))));
    }

    @Test
    public void t0606_multiplePackagesAnnotation() throws Exception {
        //Arrange
        ReflectiveClassFinder classFinder = new ReflectiveClassFinder();

        List<String> packages = new ArrayList<String>();
        packages.add("org.hamgen.testdata");
        packages.add("org.hamgen.test.data");

        //Act
        Collection<Class<?>> result = classFinder.findClassesWithAnnotation(packages, XmlType.class.getName());

        //Assert
        Assert.assertEquals(2, result.size());
        assertThat(result, (Matcher)hasItem(hasProperty("name", is(MatcherBuilderTestDataSomething.class.getName()))));
        assertThat(result, (Matcher)hasItem(hasProperty("name", is(org.hamgen.test.data.MatcherBuilderTestDataSomething.class.getName()))));
    }
}
