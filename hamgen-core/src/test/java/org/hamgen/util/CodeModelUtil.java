package org.hamgen.util;

import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JGenerable;

import java.io.StringWriter;

public class CodeModelUtil {

    private CodeModelUtil() {
        // Util class
    }

    public static String generableToString(JGenerable result) {
        StringWriter stringWriter = new StringWriter();
        JFormatter formatter = new JFormatter(stringWriter);
        result.generate(formatter);
        return stringWriter.toString();
    }
}
