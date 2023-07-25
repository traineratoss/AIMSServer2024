package com.atoss.idea.management.system.utils;

import com.atoss.idea.management.system.exception.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(value = CategoryAlreadyExistsException.class)
    public ResponseEntity<Object> categoryExistsException(CategoryAlreadyExistsException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = CategoryNotFoundException.class)
    public ResponseEntity<Object> categoryNotFoundException(CategoryNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = FieldValidationException.class)
    public ResponseEntity<Object> idNotValidException(FieldValidationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserAlreadyExistException.class)
    public ResponseEntity<Object> userAlreadyExistException(UserAlreadyExistException userAlreadyExistException) {
        return new ResponseEntity<>(userAlreadyExistException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<Object> userNotFoundException(UserNotFoundException userNotFoundException) {
        return new ResponseEntity<>(userNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = IOException.class)
    public ResponseEntity<Object> userNotFoundException(IOException ioException) {
        return new ResponseEntity<>(ioException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = AvatarNotFoundException.class)
    public ResponseEntity<Object> userNotFoundException(AvatarNotFoundException avatarNotFoundException) {
        return new ResponseEntity<>(avatarNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = IdeaNotFoundException.class)
    public ResponseEntity<Object> ideaNotFoundException(IdeaNotFoundException ideaNotFoundException) {
        return new ResponseEntity<>(ideaNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = CommentNotFoundException.class)
    public ResponseEntity<Object>  commentNotFoundException(CommentNotFoundException commentNotFoundException) {
        return new ResponseEntity<>("Comment not Found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = CommentTooLongException.class)
    public ResponseEntity<Object> commentTooLongException(CommentTooLongException commentTooLongException) {
        return new ResponseEntity<>("Comment Too Long", HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(value = UsernameAlreadyExistException.class)
    public ResponseEntity<Object> usernameAlreadyExistException(UsernameAlreadyExistException usernameAlreadyExistException) {
        return new ResponseEntity<>(usernameAlreadyExistException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = EmailAlreadyExistException.class)
    public ResponseEntity<Object> emailAlreadyExistException(EmailAlreadyExistException emailAlreadyExistException) {
        return new ResponseEntity<>(emailAlreadyExistException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = IncorrectPasswordException.class)
    public ResponseEntity<Object> incorrectPasswordException(IncorrectPasswordException incorrectPasswordException) {
        return new ResponseEntity<Object>(incorrectPasswordException.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
