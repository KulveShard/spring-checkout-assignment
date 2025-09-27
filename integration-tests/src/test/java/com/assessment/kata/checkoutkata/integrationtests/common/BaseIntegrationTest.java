package com.assessment.kata.checkoutkata.integrationtests.common;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseIntegrationTest {
    protected static TestDataLoader dataLoader;
    protected static ResponseValidator validator;

    @BeforeAll
    static void baseSetUp() {
        RestAssured.baseURI = TestConstants.BASE_URL;
        dataLoader = new TestDataLoader();
        validator = new ResponseValidator();
    }
}