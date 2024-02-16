
package com.github.sylvainlaurent.maven.swaggervalidator;

import com.github.sylvainlaurent.maven.swaggervalidator.service.ValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ValidatorCodeInstrumentationExtension.class)
public class ValidationServiceTest {
    private final ValidationService service = new ValidationService();

    @Test
    public void test_malformed_file_yml() {
        final ValidationResult result = service.validate(new File("src/test/resources/swagger-doc-malformed.yml"));
        assertTrue(result.hasError());
        assertEquals(1, result.getMessages().size());
    }

    @Test
    public void test_malformed_file_json() {
        final ValidationResult result = service.validate(new File("src/test/resources/swagger-doc-malformed.json"));
        assertTrue(result.hasError());
        assertEquals(1, result.getMessages().size());
    }

    @Test
    public void test_not_swagger_yml() {
        final ValidationResult result = service.validate(new File("src/test/resources/swagger-doc-not-swagger.yml"));
        assertTrue(result.hasError());
        assertFalse(result.getMessages().isEmpty());
    }

    @Test
    public void test_not_swagger_json() {
        final ValidationResult result = service.validate(new File("src/test/resources/swagger-doc-not-swagger.json"));
        assertTrue(result.hasError());
        assertFalse(result.getMessages().isEmpty());
    }

    @Test
    public void test_not_valid_swagger_yml() {
        final ValidationResult result = service.validate(new File("src/test/resources/swagger-doc-not-valid.yml"));
        assertTrue(result.hasError());
    }

    @Test
    public void test_not_valid_swagger_json() {
        final ValidationResult result = service.validate(new File("src/test/resources/swagger-doc-not-valid.json"));
        assertTrue(result.hasError());
    }

    @Test
    public void test_valid_swagger() {
        final ValidationResult result = service.validate(new File("src/test/resources/swagger-editor-example.yml"));
        assertFalse(result.hasError());
        assertTrue(result.getMessages().isEmpty());
    }

    @Test
    public void test_valid_swagger_json() {
        final ValidationResult result = service.validate(new File("src/test/resources/swagger-editor-example.json"));
        assertFalse(result.hasError());
        assertTrue(result.getMessages().isEmpty());
    }

    @Test
    public void test_external_references_in_definition_yaml() {
        final ValidationResult result = service.validate(new File("src/test/resources/ext-ref/swagger-doc-external-ref-definition.yml"));
        assertFalse(result.hasError());
        assertTrue(result.getMessages().isEmpty());
    }

    @Test
    public void test_external_references_in_definition_json() {
        final ValidationResult result = service.validate(new File("src/test/resources/ext-ref/swagger-doc-external-ref-definition.json"));
        assertFalse(result.hasError());
        assertTrue(result.getMessages().isEmpty());
    }

    @Test
    public void test_external_references_in_path_yaml() {
        final ValidationResult result = service.validate(new File("src/test/resources/ext-ref/swagger-doc-external-ref-path.yml"));
        assertFalse(result.hasError());
        assertTrue(result.getMessages().isEmpty());
    }

}
