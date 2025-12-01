package br.com.fatec.modulo1.pessoa_api.exceptions;

import br.com.fatec.modulo1.pessoa_api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ConstraintViolation;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata ResourceNotFoundException - 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        MDC.put("errorType", "RESOURCE_NOT_FOUND");

        logger.warn("Recurso não encontrado: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        error.setRequestId(MDC.get("requestId"));

        MDC.remove("errorType");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Trata ValidationException - 400
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex,
            HttpServletRequest request) {

        MDC.put("errorType", "VALIDATION_ERROR");

        logger.warn("Erro de validação: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                ex.getMessage(),
                request.getRequestURI()
        );
        error.setRequestId(MDC.get("requestId"));

        if (ex.getField() != null) {
            error.addFieldError(ex.getField(), ex.getRejectedValue(), ex.getMessage());
        }

        MDC.remove("errorType");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata validações do Bean Validation (@Valid) - 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        MDC.put("errorType", "VALIDATION_ERROR");

        logger.warn("Erro de validação: {} campo(s) inválido(s)",
                ex.getBindingResult().getFieldErrorCount());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "Erro de validação nos campos fornecidos",
                request.getRequestURI()
        );
        error.setRequestId(MDC.get("requestId"));

        // Adiciona erros de cada campo
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.addFieldError(
                    fieldError.getField(),
                    fieldError.getRejectedValue(),
                    fieldError.getDefaultMessage()
            );
            logger.debug("Campo inválido: {} = {} ({})",
                    fieldError.getField(),
                    fieldError.getRejectedValue(),
                    fieldError.getDefaultMessage());
        }

        MDC.remove("errorType");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata ConstraintViolationException - 400
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        MDC.put("errorType", "CONSTRAINT_VIOLATION");

        logger.warn("Violação de constraint: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Constraint Violation",
                "Erro de validação",
                request.getRequestURI()
        );
        error.setRequestId(MDC.get("requestId"));

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            error.addFieldError(
                    fieldName,
                    violation.getInvalidValue(),
                    violation.getMessage()
            );
        }

        MDC.remove("errorType");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata IllegalArgumentException - 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        MDC.put("errorType", "ILLEGAL_ARGUMENT");

        logger.warn("Argumento ilegal: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        error.setRequestId(MDC.get("requestId"));

        MDC.remove("errorType");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata erro de tipo de argumento - 400
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        MDC.put("errorType", "TYPE_MISMATCH");

        logger.warn("Tipo de argumento incompatível: {} para o parâmetro {}",
                ex.getValue(), ex.getName());

        ErrorResponse error = getErrorResponse(ex, request);
        error.setRequestId(MDC.get("requestId"));

        MDC.remove("errorType");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private static ErrorResponse getErrorResponse(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = String.format(
                "O parâmetro '%s' com valor '%s' não pôde ser convertido para o tipo %s",
                ex.getName(),
                ex.getValue(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconhecido"
        );

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Type Mismatch",
                message,
                request.getRequestURI()
        );
        return error;
    }

    /**
     * Trata JSON malformado - 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        MDC.put("errorType", "MALFORMED_JSON");

        logger.warn("JSON malformado na requisição");

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Malformed JSON",
                "Requisição contém JSON inválido ou malformado",
                request.getRequestURI()
        );
        error.setRequestId(MDC.get("requestId"));

        MDC.remove("errorType");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata todas as outras exceções não tratadas - 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        MDC.put("errorType", "INTERNAL_ERROR");

        logger.error("Erro interno não tratado", ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ocorreu um erro interno no servidor. Por favor, tente novamente mais tarde.",
                request.getRequestURI()
        );
        error.setRequestId(MDC.get("requestId"));

        MDC.remove("errorType");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
