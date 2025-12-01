package br.com.fatec.modulo1.pessoa_api.exceptions;

public class ValidationException extends RuntimeException {
    private String field;
    private Object rejectedValue;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String field, Object rejectedValue, String message) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public String getField() {
        return field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }
}