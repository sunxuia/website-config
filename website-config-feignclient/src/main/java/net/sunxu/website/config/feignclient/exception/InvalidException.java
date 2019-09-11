package net.sunxu.website.config.feignclient.exception;

import java.util.Objects;

public class InvalidException extends ServiceException {

    protected InvalidException(String message, Throwable cause) {
        super(DEFAULT_HTTP_STATUS, message, cause);
    }

    public static InvalidException newException(String message, Object... paras) {
        return new InvalidException(format(message, paras), null);
    }

    public static InvalidException wrapException(Throwable cause) {
        return new InvalidException(null, cause);
    }

    public static InvalidException wrapException(Throwable cause, String message, Object... paras) {
        return new InvalidException(format(message, paras), cause);
    }

    public static void assertTrue(boolean success, String message, Object... paras) {
        if (!success) {
            throw newException(message, paras);
        }
    }

    public static void assertFalse(boolean success, String message, Object... paras) {
        assertTrue(!success, message, paras);
    }

    public static <T> void assertEquals(T a, T b, String message, Object... paras) {
        if (Objects.equals(a, b)) {
            throw newException(message, paras);
        }
    }

    public static void assertNull(Object resource, String message, Object... paras) {
        assertTrue(resource == null, message, paras);
    }

    public static void assertNotNull(Object resource, String message, Object... paras) {
        assertTrue(resource != null, message, paras);
    }
}
