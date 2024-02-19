package com.github.sylvainlaurent.maven.swaggervalidator.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class Util {

    private Util() {
        // private constructor
    }

    public static <T> Set<T> findDuplicates(Collection<T> list) {
        Set<T> duplicates = new LinkedHashSet<>();
        Set<T> uniques = new HashSet<>();

        for (T elem : emptyIfNull(list)) {
            if (!uniques.add(elem)) {
                duplicates.add(elem);
            }
        }

        return duplicates;
    }

    public static <T> Set<T> createInstances(String packageName, Class<T> validatorClass) {
        Set<T> customValidators = new HashSet<>();
        Set<Class<? extends T>> customValidatorClasses = getClassesFromPackage(packageName, validatorClass);

        for (Class<? extends T> clazz : customValidatorClasses) {
            if (!Modifier.isAbstract(clazz.getModifiers())) {
                customValidators.add(createInstance(clazz));
            }
        }
        return customValidators;
    }

    private static <T> Set<Class<? extends T>> getClassesFromPackage(String packageName, final Class<T> superClass) {

        // Get the package directory
        String packagePath = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set<Class<? extends T>> subClasses = new HashSet<>();
        URL resource = classLoader.getResource(packagePath);
        if (resource != null) {
            File packageDir = new File(resource.getFile());
            if (packageDir.exists() && packageDir.isDirectory()) {
                collectSubClasses(packageDir, packageName, subClasses, superClass);
            }
        }

        return subClasses;
    }

    private static <T> void collectSubClasses(File directory, String packageName, Set<Class<? extends T>> subClasses, final Class<T> superClass) {
        // List all files in the directory
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursive call for subdirectories
                    collectSubClasses(file, packageName + "." + file.getName(), subClasses, superClass);
                } else if (file.getName().endsWith(".class")) {
                    // Add the class name to the list
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        Class cls = Class.forName(className);
                        if (superClass.isAssignableFrom(cls)) {
                            subClasses.add(cls);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
    private static <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new IllegalArgumentException("Cannot instantiate validator " + clazz.getCanonicalName(), e);
        }
    }
}
