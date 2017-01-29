package org.hamgen.testtools;

import com.sun.codemodel.*;

import static java.lang.reflect.Modifier.PUBLIC;

public class CodeModelTestDataBuilder {

    private JCodeModel codeModel;

    private JDefinedClass dummyClass;
    private JMethod dummyMethod;

    public CodeModelTestDataBuilder withCodeModel(JCodeModel codeModel) {
        this.codeModel = codeModel;
        return this;
    }

    public JVar buildJVar(Class<?> type, String varName) throws Exception {
        if(dummyClass == null) {
            dummyClass = codeModel._class("dummy");
        }
        if(dummyMethod == null) {
            dummyMethod = dummyClass.method(PUBLIC, codeModel.VOID, "dummy");
        }
        return dummyMethod.param(type, varName);
    }
}
