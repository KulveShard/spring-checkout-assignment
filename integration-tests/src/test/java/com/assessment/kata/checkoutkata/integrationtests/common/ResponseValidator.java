package com.assessment.kata.checkoutkata.integrationtests.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResponseValidator {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void validateResponse(Response actual, String expectedJsonPath) {
        validateResponse(actual, expectedJsonPath, null);
    }

    public void validateResponse(Response actual, String expectedJsonPath, Map<String, String> variables) {
        try {
            TestDataLoader dataLoader = new TestDataLoader();
            String expectedJson = dataLoader.loadResponse(expectedJsonPath);

            if (variables != null) {
                expectedJson = dataLoader.processTemplate(expectedJson, variables);
            }

            JsonNode expectedNode = objectMapper.readTree(expectedJson);
            JsonNode actualNode = objectMapper.readTree(actual.asString());

            validateJsonNode(actualNode, expectedNode, "");

        } catch (Exception e) {
            throw new RuntimeException("Failed to validate response against " + expectedJsonPath, e);
        }
    }

    private void validateJsonNode(JsonNode actual, JsonNode expected, String path) {
        if (expected.isObject()) {
            assertTrue(actual.isObject(), "Expected object at path: " + path);

            expected.fieldNames().forEachRemaining(fieldName -> {
                String currentPath = path.isEmpty() ? fieldName : path + "." + fieldName;
                JsonNode expectedValue = expected.get(fieldName);
                JsonNode actualValue = actual.get(fieldName);

                assertNotNull(actualValue, "Missing field at path: " + currentPath);
                validateJsonNode(actualValue, expectedValue, currentPath);
            });

        } else if (expected.isArray()) {
            assertTrue(actual.isArray(), "Expected array at path: " + path);
            assertEquals(expected.size(), actual.size(), "Array size mismatch at path: " + path);

            for (int i = 0; i < expected.size(); i++) {
                validateJsonNode(actual.get(i), expected.get(i), path + "[" + i + "]");
            }

        } else {
            // Handle special validation tokens
            String expectedText = expected.asText();

            if ("${NOT_NULL}".equals(expectedText)) {
                assertNotNull(actual, "Expected non-null value at path: " + path);
                assertFalse(actual.isNull(), "Expected non-null value at path: " + path);

            } else if ("${ANY}".equals(expectedText)) {
                // Any value is acceptable, no validation needed

            } else if ("${NUMBER}".equals(expectedText)) {
                assertTrue(actual.isNumber(), "Expected number at path: " + path);

            } else if ("${POSITIVE_NUMBER}".equals(expectedText)) {
                assertTrue(actual.isNumber(), "Expected number at path: " + path);
                assertTrue(actual.asDouble() > 0, "Expected positive number at path: " + path);

            } else if ("${TIMESTAMP}".equals(expectedText)) {
                assertTrue(actual.isNumber(), "Expected timestamp number at path: " + path);
                assertTrue(actual.asDouble() > 0, "Expected positive timestamp at path: " + path);
                // Accept any numeric timestamp format (long, double, scientific notation)

            } else {
                // Direct value comparison
                assertEquals(expected, actual, "Value mismatch at path: " + path);
            }
        }
    }
}