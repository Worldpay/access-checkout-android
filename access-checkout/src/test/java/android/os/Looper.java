package android.os;

import static org.mockito.Mockito.mock;

public class Looper {
    static private Looper myLooper;
    static private Looper mainLooper;

    public static Looper myLooper() {
        return myLooper;
    }

    public static Looper getMainLooper() {
        return mainLooper;
    }

    public static void resetAnyLooperToNull() {
        myLooper = null;
        mainLooper = null;
    }

    public static void prepareMainLooper() {
        myLooper = mock(Looper.class);
    }
}