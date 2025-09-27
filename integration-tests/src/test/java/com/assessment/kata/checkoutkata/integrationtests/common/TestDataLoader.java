package com.assessment.kata.checkoutkata.integrationtests.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TestDataLoader {

    public String loadRequest(String relativePath) {
        return loadJsonFile(relativePath);
    }

    public String loadResponse(String relativePath) {
        return loadJsonFile(relativePath);
    }

    public String processTemplate(String json, Map<String, String> variables) {
        String result = json;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return processBuiltInVariables(result);
    }

    private String processBuiltInVariables(String json) {
        String result = json;

        // Replace built-in template variables
        if (result.contains("${RANDOM_QUANTITY}")) {
            int randomQuantity = 1 + (int)(Math.random() * 10); // 1-10 items
            result = result.replace("${RANDOM_QUANTITY}", String.valueOf(randomQuantity));
        }

        if (result.contains("${RANDOM_PRICE}")) {
            int randomPrice = 10 + (int)(Math.random() * 190); // 10-199 cents
            result = result.replace("${RANDOM_PRICE}", String.valueOf(randomPrice));
        }

        if (result.contains("${UNIQUE_ITEM_NAME}")) {
            String[] items = {"apple", "banana", "peach", "kiwi"};
            result = result.replace("${UNIQUE_ITEM_NAME}", items[(int)(Math.random() * items.length)]);
        }

        return result;
    }

    private String loadJsonFile(String relativePath) {
        // Convert service/testdata/requests/file.json to /com/assessment/kata/checkoutservice/integrationtests/service/testdata/requests/file.json
        String packagePath = "/com/assessment/kata/checkoutservice/integrationtests/" + relativePath;

        try (InputStream inputStream = getClass().getResourceAsStream(packagePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Test data file not found: " + packagePath +
                    " (looking for " + relativePath + " in package structure)");
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test data file: " + packagePath, e);
        }
    }
}