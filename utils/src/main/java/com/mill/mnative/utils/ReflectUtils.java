package com.mill.mnative.utils;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/2/23.
 */

public class ReflectUtils {

    public static final String CLASSNAME_IPACKAGEMANAGER_STUB = "android.content.pm.IPackageManager$Stub";

    public static Context getApplicationContext() {
        Context context = null;
        try {
            Class<?> clazz = Class.forName("android.app.ActivityThread");
            Method method = clazz.getDeclaredMethod("currentApplication", new Class<?>[]{});
            context = (Context) method.invoke(null, new Object[]{});
        } catch (ClassNotFoundException | SecurityException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {

        }
        return context;
    }

    // Hide Constants Helper
    public static int getStaticIntField(String className, String fieldName) {
        try {
            Field field = Class.forName(className).getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getInt(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invokeStaticMethod(ClassLoader classloader, String className, String methodName, Class<?>[] parameterTypes, Object... parameters) {
        return invokeStaticMethod(classloader, className, false, methodName, parameterTypes, parameters);
    }

    public static Object getObjectNewInstance(Class clazz, Class[] paramsTypes, Object... args) {
        try {
            return clazz.getConstructor(paramsTypes).newInstance(args);
        } catch (Exception e) {
            Constructor<?>[] aa = clazz.getConstructors();
            throw new RuntimeException(e);
        }
    }

    public static Object invokeStaticMethod(ClassLoader classloader, String className, boolean shouldInitialize, String methodName, Class<?>[] parameterTypes, Object... parameters) {
        try {
            Class<?> clazz;
            clazz = classForName(className, shouldInitialize, classloader);
            if (clazz != null) {
                Method method = getDeclaredMethod(clazz, methodName, parameterTypes);
                method.setAccessible(true);
                return method.invoke(parameterTypes, parameters);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Method getMethod(Class<?> cls, String methodName, Class<?> paramTypes[]) {
        Method method = null;
        if (methodName != null && methodName.length() > 0) {
            try {
                method = cls.getMethod(methodName, paramTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return method;
    }


    public static Class classForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("getClass exception, className = " + className, e);
        }
    }

    public static Class classForName(String className, boolean shouldInitialize, ClassLoader classLoader) {
        try {
            return Class.forName(className, shouldInitialize, classLoader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("getClass exception, className = " + className, e);
        }
    }

    public static Object getStaticFieldVal(String className, String fieldName, ClassLoader classLoader) {
        try {
            Class clazz = classForName(className, false, classLoader);
            if (clazz != null) {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(null);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Field getDeclaredField(Object object, String fieldName) {
        Class<?> clazz = object.getClass();
        while (clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (Exception e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new RuntimeException("getDeclaredField exception, object = " + object + ", fieldName = " + fieldName);
    }

    public static Object getFieldValue(Object object, String fieldName) {
        try {
            Field field = getDeclaredField(object, fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException("getFieldValue exception, object = " + object + ", fieldName = " + fieldName, e);
        }
    }

    public static Object invokeStaticMethod(String className, String methodName, Class<?>[] parameterTypes, Object... parameters) {
        try {
            Method method = getDeclaredMethod(classForName(className), methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(null, parameters);
        } catch (Exception e) {
            throw new RuntimeException("invokeStaticMethod exception, className = " + className + ", methodName = " + methodName, e);
        }
    }

    public static Object invokeMethod(Object obj, String methodName, Class<?>[] parameterTypes, Object... parameters) {
        try {
            Method method = getDeclaredMethod(obj, methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(obj, parameters);
        } catch (Exception e) {
            throw new RuntimeException("invokeMethod exception, receiver = " + obj + ", methodName = " + methodName, e);
        }
    }

    public static Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
        Class<?> clazz = object instanceof Class ? (Class) object : object.getClass();
        while (clazz != Object.class) {
            try {
                return clazz.getDeclaredMethod(methodName, parameterTypes);
            } catch (Exception e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new RuntimeException("getDeclaredMethod exception, object = " + object + ", methodName = " + methodName);
    }

    static boolean hookSimpleDraweeViewed = false;

    public static void hookSimpleDraweeView(ClassLoader hostClassLoader, ClassLoader pluginClassLoader, String className) {
        if (hookSimpleDraweeViewed) {
            return;
        }

        hookSimpleDraweeViewed = true;

        Log.e("debugtest ", "hookSimpleDraweeView() a " + hostClassLoader);
        Log.e("debugtest ", "hookSimpleDraweeView() a " + pluginClassLoader);

        Class clazzTemp = classForName("android.view.LayoutInflater");
        if (clazzTemp != null) {
            Log.e("debugtest ", "hookSimpleDraweeView() a " + clazzTemp.getClassLoader());
        } else {
            Log.e("debugtest ", "hookSimpleDraweeView() xxxxxxxxxx " + clazzTemp.getClassLoader());
        }

        HashMap<String, Constructor<? extends View>> sConstructorMap = (HashMap<String, Constructor<? extends View>>) ReflectUtils.getStaticFieldVal("android.view.LayoutInflater", "sConstructorMap", clazzTemp.getClassLoader());
        Class<?>[] mConstructorSignature = (Class<?>[]) ReflectUtils.getStaticFieldVal("android.view.LayoutInflater", "mConstructorSignature", clazzTemp.getClassLoader());
        if (sConstructorMap != null && mConstructorSignature != null) {
            Log.e("debugtest ", "hookSimpleDraweeView() b " + className);
            Class<? extends View> clazz;
            try {
                Log.e("debugtest ", "hookSimpleDraweeView() c " + className);
                clazz = pluginClassLoader.loadClass(className).asSubclass(View.class);
                Constructor<? extends View> constructor = clazz.getConstructor(mConstructorSignature);
                constructor.setAccessible(true);
                constructor = clazz.getConstructor(mConstructorSignature);
                constructor.setAccessible(true);
                Log.e("debugtest ", "hookSimpleDraweeView() d " + className);
                sConstructorMap.put(className, constructor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setField(Object obj, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        prepareField(obj.getClass(), fieldName).set(obj, value);
    }

    public static Field prepareField(Class<?> c, String fieldName) throws NoSuchFieldException {
        while (c != null) {
            try {
                Field f = c.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            } catch (Exception e) {
                //这里必须catch住，不然循环不会执行
            } finally {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException();
    }

    /**
     * 直接设置对象属性值, 忽略 private/protected 修饰符, 也不经过 setter
     *
     * @param object    : 子类对象
     * @param fieldName : 父类中的属性名
     * @param value     : 将要设置的值
     */
    public static void setFieldValue(Object object, String fieldName, Object value) {
        try {
            Field field = getDeclaredField(object, fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("setFieldValue exception, object = " + object + ", fieldName = " + fieldName, e);
        }
    }

    public static Object getObjectNewInstance(String className, Class[] paramsTypes, Object... args) {
        try {
            Class clazz =  Class.forName(className);
            Constructor constructor = clazz.getConstructor(paramsTypes);
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void windowDismissed(InputMethodManager imm, IBinder iBinder) {
        try {
            imm.getClass().getMethod("windowDismissed", IBinder.class).invoke(imm, iBinder);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object stubAsInterface(String clazz, IBinder binder) {
        return stubAsInterface(classForName(clazz), binder);
    }

    public static Object stubAsInterface(Class clazz, IBinder binder) {
        try {
            return clazz.getDeclaredMethod("asInterface", IBinder.class)
                    .invoke(null, binder);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    public static Object getObjectField(Object object, String fieldName) {
        try {
            return object.getClass()
                    .getDeclaredField(fieldName)
                    .get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    public static int getIntField(Object object, String fieldName) {
        try {
            return object.getClass()
                    .getDeclaredField(fieldName)
                    .getInt(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }


    public static Object getStaticObjectField(String className, String fieldName) {
        try {
            Field field = Class.forName(className).getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}