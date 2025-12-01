package br.com.fatec.modulo1.pessoa_api.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    private String resourceName;
    private Long resourceId;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long resourceId) {
        super(String.format("%s não encontrado com ID: %d", resourceName, resourceId));
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Long getResourceId() {
        return resourceId;
    }
}