package org.hamgen;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.util.Collection;
import java.util.List;

public class ReflectiveClassFinder {

    public Collection<Class<?>> findClassesWithAnnotation(List<String> packageNames, String annotation) throws ClassNotFoundException {

        if(annotation == null ||annotation.isEmpty()) {
            Reflections reflections = new Reflections(packageNames.toArray(), new SubTypesScanner(false));
            return reflections.getSubTypesOf(Object.class);
        }

        Class annotationClass = Class.forName(annotation);
        if(annotationClass.isAnnotation()) {
            Reflections reflections = new Reflections(packageNames.toArray());
            return reflections.getTypesAnnotatedWith(annotationClass);
        } else {
            throw new IllegalArgumentException("<" + annotationClass.getName() + "> is not an annotation!");
        }
    }
}
