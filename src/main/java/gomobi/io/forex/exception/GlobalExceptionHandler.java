package gomobi.io.forex.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static gomobi.io.forex.exception.CustomExceptions.*;

//makes the class a global exception handler for the application
@ControllerAdvice
public class GlobalExceptionHandler {

    // Exceptions thrown by me!
    @ExceptionHandler({
    	CustomExceptions.DuplicateEmailException.class, 
    	CustomExceptions.DuplicateUsernameException.class,
    	CustomExceptions.IllegalArgumentException.class,
    	CustomExceptions.InvalidCredentialsException.class
    })
    public ResponseEntity<ErrorResponse> handleMultipleExceptions(Exception ex) {

        //ErrorResponse obj with status code, message, and timestamp
    	ErrorResponse responseBody = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),ex.getMessage());

        // Return the error response with the bad req status
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }
    
    // Handle all other exceptions (Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        // Create ErrorResponse object for any other exceptions
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),  //500
                ex.getMessage()  //custom message!
        );

        // Return the error response with the internal server error status
        // return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}