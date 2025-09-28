package work.chncyl.base.global.tools.classTool;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Slf4j
public class ClassUtils {
    // 项目根路径
    public static String rootPath = new File("").getAbsolutePath();
    //class路径
    public static String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();

    private static List<Class> getAllClasses(File file, List<Class> classes) throws ClassNotFoundException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) return null;
            for (File f : files) getAllClasses(f, classes);
        } else {
            if (file.getName().endsWith(".class")) {
                String className = file.getPath().replace(".class", "").replace(classPath.replace("/", "\\").substring(1), "").replace("\\", ".");
                classes.add(Class.forName(className));
            }
        }
        return classes;
    }

    public static List<Class> getAllClasses() throws ClassNotFoundException {
        return getAllClasses(classPath);
    }

    public static List<Class> getAllClasses(String path) throws ClassNotFoundException {
        List<Class> list = new ArrayList<>();
        list = getAllClasses(new File(path), list);
        return list;
    }

    /**
     * 在指定的包中获取所有 继承 指定接口的类
     *
     * @param clz
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static List<Class<?>> getManualScanner(Class<?> clz, ClassType resultType, String... packageNames) throws IOException, ClassNotFoundException {
        List<Class<?>> allClasses = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for (String packageName : packageNames) {
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }
            for (File directory : dirs) {
                allClasses.addAll(findManualScanner(directory, packageName, clz, resultType));
            }
        }
        return allClasses;
    }

    /**
     * 从指定文件夹中获取所有 继承 指定接口的类
     *
     * @param directory   文件夹
     * @param packageName 类所在的包，用于拼接全限域名
     * @param clz         基础的接口
     * @param resultType  需要的类型
     */
    public static List<Class<?>> findManualScanner(File directory, String packageName, Class<?> clz, ClassType resultType) throws IOException, ClassNotFoundException {
        List<Class<?>> manual = new ArrayList<>();
        if (!directory.exists()) {
            return manual;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    manual.addAll(findManualScanner(file, packageName + "." + file.getName(), clz, resultType));
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz = Class.forName(className);
                    if (clz.isAssignableFrom(clazz)) {
                        if (resultType == ClassType.ANY) {
                            manual.add(clazz);
                        } else if (resultType == ClassType.CLASS && !clazz.isInterface()) {
                            manual.add(clazz);
                        } else if (resultType == ClassType.INTERFACE && clazz.isInterface()) {
                            manual.add(clazz);
                        } else if (resultType == ClassType.ENUM && clazz.isEnum()) {
                            manual.add(clazz);
                        } else if (resultType == ClassType.ANNOTATION && clazz.isAnnotation()) {
                            manual.add(clazz);
                        } else if (resultType == ClassType.PRIMITIVE && clazz.isPrimitive()) {
                            manual.add(clazz);
                        }
                    }
                }
            }
        }
        return manual;
    }

    /**
     * 在指定的包中获取所有被指定注解标注的类
     *
     * @param clz          注解
     * @param resultType   要获取的类的类型 ，默认ANY
     * @param packageNames 扫描的包（会递归向下扫描）
     * @return 扫描到的所有class
     */
    public static List<Class<?>> getAnnotatedClass(Class<? extends Annotation> clz, ClassType resultType, String... packageNames) throws IOException, ClassNotFoundException {
        if (resultType == null) {
            resultType = ClassType.ANY;
        }
        List<Class<?>> annotatedEnums = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for (String packageName : packageNames) {
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }
            for (File directory : dirs) {
                annotatedEnums.addAll(findAnnotatedClass(directory, packageName, clz, resultType));
            }
        }
        return annotatedEnums;
    }

    private static List<Class<?>> findAnnotatedClass(File directory, String packageName, Class<? extends Annotation> clz, ClassType resultType) throws ClassNotFoundException {
        List<Class<?>> annotatedEnums = new ArrayList<>();
        if (!directory.exists()) {
            return annotatedEnums;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    annotatedEnums.addAll(findAnnotatedClass(file, packageName + "." + file.getName(), clz, resultType));
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(clz)) {
                        if (resultType == ClassType.ANY) {
                            annotatedEnums.add(clazz);
                        } else if (resultType == ClassType.CLASS && !clazz.isInterface()) {
                            annotatedEnums.add(clazz);
                        } else if (resultType == ClassType.INTERFACE && clazz.isInterface()) {
                            annotatedEnums.add(clazz);
                        } else if (resultType == ClassType.ENUM && clazz.isEnum()) {
                            annotatedEnums.add(clazz);
                        } else if (resultType == ClassType.ANNOTATION && clazz.isAnnotation()) {
                            annotatedEnums.add(clazz);
                        } else if (resultType == ClassType.PRIMITIVE && clazz.isPrimitive()) {
                            annotatedEnums.add(clazz);
                        }
                    }
                }
            }
        }
        return annotatedEnums;
    }

    /**
     * 判断一个实体类对象实例的所有成员变量是否为空
     *
     * @param obj 校验的类对象实例
     * @return List 值为空的成员变量名称
     * @throws Exception
     */
    public static List<String> isObjectFieldEmpty(Object obj) throws Exception {
        Class<?> clazz = obj.getClass();  //得到类对象
        Field[] fs = clazz.getDeclaredFields(); //得到属性集合
        List<String> list = new ArrayList<>();
        for (Field field : fs) {            //遍历属性
            field.setAccessible(true); //设置属性是可以访问的（私有的也可以）
            if (field.get(obj) == null || field.get(obj) == "" || "null".equalsIgnoreCase((String) field.get(obj))) {
                String name = field.getName();
                list.add(name);
            }
        }
        return list;
    }
}