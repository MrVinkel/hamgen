package org.hamgen.testtools;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JGenerable;
import com.sun.codemodel.writer.FilterCodeWriter;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
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

    public static String codeModelToString(JCodeModel result) throws Exception {
        OutputStream outputStream = new ByteArrayOutputStream();
        CodeWriter codeWriter = new SingleStreamCodeWriter(outputStream);
        result.build(codeWriter);
        return outputStream.toString();
    }
}
