package org.hamgen.builder;

import org.hamgen.test.data.ClassWithInnerClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PackageNameResolverTest {

    @Test
    public void t1401_normalClass() {
        //Arrange
        PackageNameResolver packageNameResolver = new PackageNameResolver();

        //Act
        String result = packageNameResolver.resolvePackageName(ClassWithInnerClass.class);

        //Assert
        assertEquals("org.hamgen.test.data", result);
    }

    @Test
    public void t1402_innerClass() {
        //Arrange
        PackageNameResolver packageNameResolver = new PackageNameResolver();

        //Act
        String result = packageNameResolver.resolvePackageName(ClassWithInnerClass.MyInnerClass.class);

        //Assert
        assertEquals("org.hamgen.test.data.classwithinnerclass", result);
    }

    @Test
    public void t1401_doubleInnerClass() {
        //Arrange
        PackageNameResolver packageNameResolver = new PackageNameResolver();

        //Act
        String result = packageNameResolver.resolvePackageName(ClassWithInnerClass.MyInnerClass.MyInnerInnerClass.class);

        //Assert
        assertEquals("org.hamgen.test.data.classwithinnerclass.myinnerclass", result);
    }
}
