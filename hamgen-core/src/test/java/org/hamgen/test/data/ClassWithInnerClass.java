package org.hamgen.test.data;

public class ClassWithInnerClass {

    public MyInnerClass getInnerClass() {
        return new MyInnerClass();
    }

    public class MyInnerClass {

        public MyInnerInnerClass getInnerInnerClass() {
            return new MyInnerInnerClass();
        }

        public class MyInnerInnerClass {
            public int getAnswer() {
                return 42;
            }
        }
    }
}
