package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;


@Component
public class JdbcValidator<T> {
    private final Validator validator;

    @Autowired
    public JdbcValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(T entity) {
        var validationResult = validator.validate(entity);
        validationResult.removeIf(constraint -> "user".equals(constraint.getPropertyPath().toString()));
        if (!validationResult.isEmpty()) {
            throw new ConstraintViolationException(validationResult);
        }
    }
}
