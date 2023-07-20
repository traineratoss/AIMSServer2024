package com.atoss.idea.management.system.utils;

import com.atoss.idea.management.system.exception.CategoryAlreadyExistsException;
import com.atoss.idea.management.system.exception.CategoryNotFoundException;
import com.atoss.idea.management.system.exception.UserAlreadyExistException;
import com.atoss.idea.management.system.exception.ImageNotFoundException;
import com.atoss.idea.management.system.exception.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(value = CategoryAlreadyExistsException.class)
    public ResponseEntity<Object> categoryExistsException(CategoryAlreadyExistsException exception) {
        return new ResponseEntity<>("Category already exist", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = CategoryNotFoundException.class)
    public ResponseEntity<Object> categoryNotFoundException(CategoryNotFoundException exception) {
        return new ResponseEntity<>("Category not found!", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ValidationException.class)
    public ResponseEntity<Object> idException(ValidationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserAlreadyExistException.class)
    public ResponseEntity<Object> userAlreadyExistException(UserAlreadyExistException userAlreadyExistException) {
        return new ResponseEntity<>(userAlreadyExistException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = ImageNotFoundException.class)
    public ResponseEntity<Object> imageNotFoundException(ImageNotFoundException exception) {
        return new ResponseEntity<>("Category not found!", HttpStatus.NOT_FOUND);
    }
}