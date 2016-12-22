package dk.martinvinkel.hamgen.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilTest {

    @Test
    public void t0301_Success() {
        // Arrange
        String input = "MyString";
        String expected = "myString";

        // Act
        String result = StringUtil.deCapitalizeFirstLetter(input);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void t0302_OnlyOneChar() {
        // Arrange
        String input = "M";
        String expected = "m";

        // Act
        String result = StringUtil.deCapitalizeFirstLetter(input);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void t0303_EmptyString() {
        // Arrange
        String input = "";
        String expected = "";

        // Act
        String result = StringUtil.deCapitalizeFirstLetter(input);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void t0304_Null() {
        // Arrange
        String input = null;
        String expected = null;

        // Act
        String result = StringUtil.deCapitalizeFirstLetter(input);

        // Assert
        assertEquals(expected, result);
    }
}
