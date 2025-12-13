package com.nlshakal.web4.constants;

/**
 * Константы для валидации координат точек и радиусов области проверки.
 * Содержит сообщения об ошибках валидации для координат X, Y и радиуса R.
 */
public final class ValidationConstants {

    private ValidationConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }

    public static final String X_MIN_MESSAGE = "X должен быть >= -5";
    public static final String X_MAX_MESSAGE = "X должен быть <= 3";

    public static final String Y_MIN_MESSAGE = "Y должен быть >= -3";
    public static final String Y_MAX_MESSAGE = "Y должен быть <= 5";

    public static final String R_MIN_MESSAGE = "R должен быть > 0";
    public static final String R_MAX_MESSAGE = "R должен быть <= 3";
}

