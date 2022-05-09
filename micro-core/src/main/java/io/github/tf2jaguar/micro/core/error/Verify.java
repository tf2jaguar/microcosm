package io.github.tf2jaguar.micro.core.error;

import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * @author zhangguodong
 */
public final class Verify {

    public static void verify(boolean expression) {
        if (expression) {
            throw new ServerException();
        }
    }

    public static void verify(boolean expression, Integer errorCode,@Nullable String errorMessageTemplate, @Nullable Object... errorMessageArgs) {
        if (expression) {
            throw new ServerException(errorCode, format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static void verify(boolean expression, ExceptionEnums exceptionEnums) {
        if (expression) {
            throw new ServerException(exceptionEnums.code(), exceptionEnums.message());
        }
    }

    public static <T> T verifyNotNull(Integer errorCode, @Nullable T reference) {
        return verifyNotNull(errorCode, reference, "expected a non-null reference");
    }

    public static <T> T verifyNotNull(Integer errorCode, @Nullable T reference, @Nullable String errorMessageTemplate, @Nullable Object... errorMessageArgs) {
        verify(reference == null, errorCode, errorMessageTemplate, errorMessageArgs);
        return reference;
    }

    private Verify() {
    }

    @Nullable
    static String format(@Nullable String template, @Nullable Object... args) {
        if (Objects.isNull(template)) {
            return null;
        }
        if (args == null || args.length == 0) {
            return template;
        }
        StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
        int templateStart = 0;

        int i;
        int placeholderStart;
        for (i = 0; i < args.length; templateStart = placeholderStart + 2) {
            placeholderStart = template.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }

            builder.append(template, templateStart, placeholderStart);
            builder.append(args[i++]);
        }

        builder.append(template, templateStart, template.length());
        if (i < args.length) {
            builder.append(" [");
            builder.append(args[i++]);

            while (i < args.length) {
                builder.append(", ");
                builder.append(args[i++]);
            }

            builder.append(']');
        }

        return builder.toString();
    }
}