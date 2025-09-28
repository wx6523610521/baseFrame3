package work.chncyl.base.global.tools;

public class InvocationInfoUtil {
    /**
     * 获取当前类名
     *
     * @return 类名
     */
    public static String getCurrentClassName() {
        return Thread.currentThread().getStackTrace()[2].getClassName();
    }

    /**
     * 获取当前方法名
     *
     * @return 方法名
     */
    public static String getCurrentMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    /**
     * 获取当前行号
     *
     * @return 行号
     */
    public static int getCurrentLineNumber() {
        return Thread.currentThread().getStackTrace()[2].getLineNumber();
    }

    /**
     * 获取调用类名
     *
     * @return 类名
     */
    public static String getInvocationClassName() {
        return Thread.currentThread().getStackTrace()[3].getClassName();
    }

    /**
     * 获取当前方法名
     *
     * @return 方法名
     */
    public static String getInvocationMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    /**
     * 获取当前行号
     *
     * @return 行号
     */
    public static int getInvocationLineNumber() {
        return Thread.currentThread().getStackTrace()[3].getLineNumber();
    }

    /**
     * 获取指定堆栈类名
     *
     * @return 类名
     */
    public static String getClassNameByStack(int deepSize) {
        return Thread.currentThread().getStackTrace()[deepSize].getClassName();
    }

    /**
     * 获取指定堆栈方法名
     *
     * @return 方法名
     */
    public static String getMethodNameByStack(int deepSize) {
        return Thread.currentThread().getStackTrace()[deepSize].getMethodName();
    }

    /**
     * 获取指定堆栈行号
     *
     * @return 行号
     */
    public static int getLineNumberByStack(int deepSize) {
        return Thread.currentThread().getStackTrace()[deepSize].getLineNumber();
    }
}
