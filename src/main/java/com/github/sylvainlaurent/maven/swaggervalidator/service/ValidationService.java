package com.github.sylvainlaurent.maven.swaggervalidator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sylvainlaurent.maven.swaggervalidator.ValidationResult;
import com.github.sylvainlaurent.maven.swaggervalidator.semantic.validator.error.SemanticError;
import io.swagger.models.Swagger;
import io.swagger.parser.Swagger20Parser;
import io.swagger.parser.SwaggerResolver;
import io.swagger.parser.util.SwaggerDeserializationResult;
import io.swagger.util.Json;
import io.swagger.util.Yaml;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ValidationService {

    private static final String SCHEMA_FILE = "swagger-schema.json";

    private final Schema schema;

    private final ObjectMapper jsonMapper = Json.mapper();
    private final ObjectMapper yamlMapper = Yaml.mapper();

    private String customModelValidatorsPackage;
    private String customPathValidatorsPackage;
    private String[] customMimeTypes;

    public ValidationService() {

        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(SCHEMA_FILE)) {
            assert inputStream != null;
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            schema = SchemaLoader
                    .builder()
                    .useDefaults(true)
                    .schemaJson(rawSchema)
                    .build()
                    .load()
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ValidationResult validate(final File file) {
        final ValidationResult validationResult = new ValidationResult();

        JsonNode spec;
        try {
            spec = readFileContent(file);
        } catch (final Exception e) {
            validationResult.addMessage("Error while parsing file " + file + ": " + e.getMessage());
            validationResult.encounteredError();
            return validationResult;
        }

        Swagger swagger = readSwaggerSpec(spec, file, validationResult);
        validateSwagger(spec, validationResult, swagger);

        return validationResult;
    }

    private Swagger readSwaggerSpec(final JsonNode spec, File specFile, final ValidationResult validationResult) {
        // use the swagger deserializer to get human-friendly messages
        final SwaggerDeserializationResult swaggerResult = new Swagger20Parser().readWithInfo(spec);
        Swagger swagger = new SwaggerResolver(swaggerResult.getSwagger(), new ArrayList<>(), specFile.getPath()).resolve();
        swaggerResult.setSwagger(swagger);

        validationResult.addMessages(swaggerResult.getMessages());
        return swagger;
    }

    private void validateSwagger(final JsonNode spec, final ValidationResult validationResult, final Swagger swagger) {

        try {
            schema.validate(new JSONObject(jsonMapper.writeValueAsString(spec)));
        } catch (ValidationException e) {
            validationResult.addMessage(e.getMessage());
            validationResult.encounteredError();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<SemanticError> semanticValidationResult = new SemanticValidationService(swagger,
                customModelValidatorsPackage, customPathValidatorsPackage, customMimeTypes).validate();
        if (!semanticValidationResult.isEmpty()) {
            for (SemanticError error : semanticValidationResult) {
                validationResult.addMessage(error.toString());
            }
            validationResult.encounteredError();
        }
    }

    private JsonNode readFileContent(final File file) throws IOException {
        final String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".yml") || fileName.endsWith(".yaml")) {
            return yamlMapper.readTree(file);
        } else {
            return jsonMapper.readTree(file);
        }
    }

    public void setCustomModelValidatorsPackage(String customModelValidatorsPackage) {
        this.customModelValidatorsPackage = customModelValidatorsPackage;
    }

    public void setCustomPathValidatorsPackage(String customPathValidatorsPackage) {
        this.customPathValidatorsPackage = customPathValidatorsPackage;
    }

    public void setCustomMimeTypes(String[] customMimeTypes) {
        this.customMimeTypes = customMimeTypes;
    }
}
