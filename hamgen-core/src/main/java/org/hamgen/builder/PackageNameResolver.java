package org.hamgen.builder;

public class PackageNameResolver {

    public String resolvePackageName(Class<?> originalClazz) {
        if(originalClazz.getDeclaringClass() != null) {
            String declaredClasses = getDeclaredClassPostfix(originalClazz, "");
            return originalClazz.getPackage().getName().trim() + declaredClasses;
        } else {
            return originalClazz.getPackage().getName().trim();
        }
    }

    private String getDeclaredClassPostfix(Class<?> clazz, String child) {
        Class<?> declaringClass = clazz.getDeclaringClass();
        if(declaringClass != null) {
            String declaredClassPostfix = child.isEmpty() ? declaringClass.getSimpleName() : declaringClass.getSimpleName() + "." + child;
            return getDeclaredClassPostfix(declaringClass, declaredClassPostfix);
        }
        return "." + child.toLowerCase();
    }
}
