/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class Classes {
    public static void runMethod(String className,String methodName) {
        try {
               Class<?> clazz = Class.forName(className);
            // Create an instance of the class
            Object obj = clazz.getDeclaredConstructor().newInstance();

            // Call a method of the class dynamically
            Method method = clazz.getMethod(methodName);
            method.invoke(obj);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
}