package org.hamgen.test.data;

/**
 * DO NOT CHANGE THIS CLASS! it will brake tests
 */
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
