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
    public ResponseEntity<UserAlreadyExistException> userAlreadyExistException(UserAlreadyExistException userAlreadyExistException) {
        return new ResponseEntity<>(userAlreadyExistException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<UserNotFoundException> userNotFoundException(UserNotFoundException userNotFoundException) {
        return new ResponseEntity<>(userNotFoundException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = IOException.class)
    public ResponseEntity<IOException> userNotFoundException(IOException ioException) {
        return new ResponseEntity<>(ioException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = AvatarNotFoundException.class)
    public ResponseEntity<AvatarNotFoundException> userNotFoundException(AvatarNotFoundException avatarNotFoundException) {
        return new ResponseEntity<>(avatarNotFoundException, HttpStatus.NOT_FOUND);
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
    public ResponseEntity<UsernameAlreadyExistException> usernameAlreadyExistException(UsernameAlreadyExistException usernameAlreadyExistException) {
        return new ResponseEntity<>(usernameAlreadyExistException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = EmailAlreadyExistException.class)
    public ResponseEntity<EmailAlreadyExistException> emailAlreadyExistException(EmailAlreadyExistException emailAlreadyExistException) {
        return new ResponseEntity<>(emailAlreadyExistException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = IncorrectPasswordException.class)
    public ResponseEntity<IncorrectPasswordException> incorrectPasswordException(IncorrectPasswordException incorrectPasswordException) {
        return new ResponseEntity<>(incorrectPasswordException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = EmailFailedException.class)
    public ResponseEntity<EmailFailedException> emailFailedException(EmailFailedException emailFailedException) {
        return new ResponseEntity<>(emailFailedException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserStatusIsActiveException.class)
    public ResponseEntity<UserStatusIsActiveException> userStatusIsActiveException(UserStatusIsActiveException userStatusIsActiveException) {
        return new ResponseEntity<>(userStatusIsActiveException, HttpStatus.BAD_REQUEST);
    }
}
