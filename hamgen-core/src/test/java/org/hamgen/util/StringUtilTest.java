package org.hamgen.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilTest {

    @Test
    public void t0301_deCapitalizeFirstLetterSuccess() {
        // Arrange
        String input = "MyString";
        String expected = "myString";

        // Act
        String result = StringUtil.deCapitalizeFirstLetter(input);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void t0302_deCapitalizeFirstLetterOnlyOneChar() {
        // Arrange
        String input = "M";
        String expected = "m";

        // Act
        String result = StringUtil.deCapitalizeFirstLetter(input);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void t0303_deCapitalizeFirstLetterEmptyString() {
        // Arrange
        String input = "";
        String expected = "";

        // Act
        String result = StringUtil.deCapitalizeFirstLetter(input);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void t0304_deCapitalizeFirstLetterNull() {
        // Arrange
        String input = null;
        String expected = null;

        // Act
        String result = StringUtil.deCapitalizeFirstLetter(input);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void t0305_capitalizeFirstLetterSuccess() {
        // Arrange
        String input = "myString";
        String expected = "MyString";

        // Act
        String result = StringUtil.capitalizeFirstLetter(input);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void t0306_capitalizeFirstLetterOnlyOneChar() {
        // Arrange
        String input = "m";
        String expected = "M";

        // Act
        String result = StringUtil.capitalizeFirstLetter(input);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void t0307_capitalizeFirstLetterEmptyString() {
        // Arrange
        String input = "";
        String expected = "";

        // Act
        String result = StringUtil.capitalizeFirstLetter(input);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void t0308_capitalizeFirstLetterNull() {
        // Arrange
        String input = null;
        String expected = null;

        // Act
        String result = StringUtil.capitalizeFirstLetter(input);

        // Assert
        assertEquals(expected, result);
    }
}
