package com.github.sylvainlaurent.maven.swaggervalidator.semantic.validator.path;

import com.github.sylvainlaurent.maven.swaggervalidator.ValidatorCodeInstrumentationExtension;
import com.github.sylvainlaurent.maven.swaggervalidator.semantic.validator.error.SemanticError;
import com.github.sylvainlaurent.maven.swaggervalidator.service.SemanticValidationService;
import io.swagger.parser.util.SwaggerDeserializationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.github.sylvainlaurent.maven.swaggervalidator.SemanticValidationServiceTest.RESOURCE_FOLDER;
import static com.github.sylvainlaurent.maven.swaggervalidator.SemanticValidationServiceTest.readDoc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(ValidatorCodeInstrumentationExtension.class)
public class FormDataValidatorTest {

    private static Logger logger = LoggerFactory.getLogger(FormDataValidatorTest.class);

    @Test
    public void form_data_file_with_invalid_consumes() {
        SwaggerDeserializationResult swaggerResult = readDoc(
                RESOURCE_FOLDER + "form-data-with-file-type-and-invalid-consumes-type.yml");
        List<SemanticError> errors = new SemanticValidationService(swaggerResult.getSwagger()).validate();
        logger.info(errors.toString());

        assertFalse(errors.isEmpty());
        assertEquals(2, errors.size());
        SemanticError error1 = errors.get(0);
        assertEquals("Operations with parameters of 'type: file' must include 'multipart/form-data' in their 'consumes' property.",
                error1.getMessage());
        assertEquals("paths./category.post", error1.getPath());
        SemanticError error2 = errors.get(1);
        assertEquals("Operations with parameters of 'type: file' must include 'multipart/form-data' in their 'consumes' property.",
                error2.getMessage());
        assertEquals("paths./category.post.name", error2.getPath());
    }
}
