package org.hamgen;

import org.reflections.Reflections;

import java.util.Collection;

public class ReflectiveClassFinder {

    public Collection<Class<?>> findClassesWithAnnotation(String packageName, String annotation) throws ClassNotFoundException {
        Class annotationClass = Class.forName(annotation);
        if(annotationClass.isAnnotation()) {
            Reflections reflections = new Reflections(packageName);
            return reflections.getTypesAnnotatedWith(annotationClass);
        } else {
            throw new IllegalArgumentException("<" + annotationClass.getName() + "> is not an annotation!");
        }
    }
}
