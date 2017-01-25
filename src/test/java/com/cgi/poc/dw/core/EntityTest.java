package com.cgi.poc.dw.core;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.BeforeClass;

public abstract class EntityTest {

    /**
     * Validation error message if value is null.
     */
    protected static final String ERROR_NOT_NULL = "may not be null";
    /**
     * Validation error message if string argument length is incorrect.
     */
    protected static final String ERROR_LENGTH
            = "size must be between 1 and 255";
    /**
     * Validator used for testing purposes.
     */
    protected static Validator validator;

    /**
     * Do initialization.
     */
    @BeforeClass
    public static void setUpClass() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
}
