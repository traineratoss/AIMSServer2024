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
    /**
     * category already exists
     *
     * @param exception CategoryAlreadyExistsException
     * @return ResponseEntity - error BAD REQUEST
     */

    @ExceptionHandler(value = CategoryAlreadyExistsException.class)
    public ResponseEntity<Object> categoryExistsException(CategoryAlreadyExistsException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * category not found
     *
     * @param exception CategoryNotFoundException
     * @return ResponseEntity - error NOT FOUND
     */

    @ExceptionHandler(value = CategoryNotFoundException.class)
    public ResponseEntity<Object> categoryNotFoundException(CategoryNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * valid fields for new idea (title, text)
     *
     * @param exception FieldValidationException
     * @return ResponseEntity - error BAD REQUEST
     */

    @ExceptionHandler(value = FieldValidationException.class)
    public ResponseEntity<Object> idNotValidException(FieldValidationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * user already exists
     *
     * @param userAlreadyExistException - registration error
     * @return ResponseEntity - error BAD REQUEST
     */

    @ExceptionHandler(value = UserAlreadyExistException.class)
    public ResponseEntity<UserAlreadyExistException> userAlreadyExistException(UserAlreadyExistException userAlreadyExistException) {
        return new ResponseEntity<>(userAlreadyExistException, HttpStatus.BAD_REQUEST);
    }

    /**
     * user not found
     *
     * @param userNotFoundException - log in error
     * @return ResponseEntity - error NOT FOUND
     */

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<UserNotFoundException> userNotFoundException(UserNotFoundException userNotFoundException) {
        return new ResponseEntity<>(userNotFoundException, HttpStatus.NOT_FOUND);
    }

    /**
     * can't read input file
     *
     * @param ioException - log in error
     * @return ResponseEntity - error NOT FOUND
     */

    @ExceptionHandler(value = IOException.class)
    public ResponseEntity<IOException> userNotFoundException(IOException ioException) {
        return new ResponseEntity<>(ioException, HttpStatus.NOT_FOUND);
    }

    /**
     * avatar not found
     *
     * @param avatarNotFoundException - login error
     * @return ResponseEntity - error NOT FOUND
     */

    @ExceptionHandler(value = AvatarNotFoundException.class)
    public ResponseEntity<AvatarNotFoundException> userNotFoundException(AvatarNotFoundException avatarNotFoundException) {
        return new ResponseEntity<>(avatarNotFoundException, HttpStatus.NOT_FOUND);
    }

    /**
     * idea not found
     *
     * @param ideaNotFoundException - can't access idea content
     * @return ResponseEntity - error NOT FOUND
     */

    @ExceptionHandler(value = IdeaNotFoundException.class)
    public ResponseEntity<Object> ideaNotFoundException(IdeaNotFoundException ideaNotFoundException) {
        return new ResponseEntity<>(ideaNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * comment not found
     *
     * @param commentNotFoundException - can't access comment content
     * @return ResponseEntity - error message Comment not Found
     */

    @ExceptionHandler(value = CommentNotFoundException.class)
    public ResponseEntity<Object>  commentNotFoundException(CommentNotFoundException commentNotFoundException) {
        return new ResponseEntity<>("Comment not Found", HttpStatus.NOT_FOUND);
    }

    /**
     * too long comment (500 max)
     *
     * @param commentTooLongException - don't allow to write more than 500 characters
     * @return ResponseEntity - error message Comment Too Long
     */

    @ExceptionHandler(value = CommentTooLongException.class)
    public ResponseEntity<Object> commentTooLongException(CommentTooLongException commentTooLongException) {
        return new ResponseEntity<>("Comment Too Long", HttpStatus.PAYLOAD_TOO_LARGE);
    }

    /**
     * username already exists
     *
     * @param usernameAlreadyExistException - don't allow to log in with that username
     * @return ResponseEntity - error
     */

    @ExceptionHandler(value = UsernameAlreadyExistException.class)
    public ResponseEntity<UsernameAlreadyExistException> usernameAlreadyExistException(UsernameAlreadyExistException usernameAlreadyExistException) {
        return new ResponseEntity<>(usernameAlreadyExistException, HttpStatus.BAD_REQUEST);
    }

    /**
     * email already exists
     *
     * @param emailAlreadyExistException - don't allow to log in with that email
     * @return ResponseEntity - error
     */

    @ExceptionHandler(value = EmailAlreadyExistException.class)
    public ResponseEntity<EmailAlreadyExistException> emailAlreadyExistException(EmailAlreadyExistException emailAlreadyExistException) {
        return new ResponseEntity<>(emailAlreadyExistException, HttpStatus.BAD_REQUEST);
    }

    /**
     * password doesn't contain the mandatory fields
     *
     * @param incorrectPasswordException - don't allow to log in, need to change password
     * @return ResponseEntity - error
     */

    @ExceptionHandler(value = IncorrectPasswordException.class)
    public ResponseEntity<IncorrectPasswordException> incorrectPasswordException(IncorrectPasswordException incorrectPasswordException) {
        return new ResponseEntity<>(incorrectPasswordException, HttpStatus.BAD_REQUEST);
    }

    /**
     * email doesn't contain the mandatory fields
     *
     * @param emailFailedException - don't allow to log in, need to change email
     * @return ResponseEntity - error
     */

    @ExceptionHandler(value = EmailFailedException.class)
    public ResponseEntity<EmailFailedException> emailFailedException(EmailFailedException emailFailedException) {
        return new ResponseEntity<>(emailFailedException, HttpStatus.BAD_REQUEST);
    }

    /**
     * user has an activated account
     *
     * @param userStatusIsActiveException - necessary if we need to deactivate
     *                                    user that is already active
     * @return ResponseEntity - error
     */

    @ExceptionHandler(value = UserStatusIsActiveException.class)
    public ResponseEntity<UserStatusIsActiveException> userStatusIsActiveException(UserStatusIsActiveException userStatusIsActiveException) {
        return new ResponseEntity<>(userStatusIsActiveException, HttpStatus.BAD_REQUEST);
    }

    /**
     * user is already approved
     *
     * @param approveAlreadyGrantedException - unnecessary to approve user that is already approved
     * @return ResponseEntity - error
     */

    @ExceptionHandler(value = ApproveAlreadyGrantedException.class)
    public ResponseEntity<ApproveAlreadyGrantedException> approveAlreadyGrantedException(
            ApproveAlreadyGrantedException approveAlreadyGrantedException) {
        return new ResponseEntity<>(approveAlreadyGrantedException, HttpStatus.BAD_REQUEST);
    }

    /**
     * user is already deactivated
     *
     * @param userAlreadyDeactivatedException - unnecessary to deactivate user that is already deactivated
     * @return ResponseEntity - error
     */

    @ExceptionHandler(value = UserAlreadyDeactivatedException.class)
    public ResponseEntity<UserAlreadyDeactivatedException> userAlreadyDeactivateException(
            UserAlreadyDeactivatedException userAlreadyDeactivatedException) {
        return new ResponseEntity<>(userAlreadyDeactivatedException, HttpStatus.BAD_REQUEST);
    }

    /**
     * user is already activated
     *
     * @param userAlreadyActivatedException - unnecessary to activate user that is already activated
     * @return ResponseEntity - error
     */
    @ExceptionHandler(value = UserAlreadyActivatedException.class)
    public ResponseEntity<UserAlreadyActivatedException> userAlreadyActivatedException(
            UserAlreadyActivatedException userAlreadyActivatedException) {
        return new ResponseEntity<>(userAlreadyActivatedException, HttpStatus.BAD_REQUEST);
    }
}
