package gomobi.io.forex.exception;

public class CustomExceptions extends RuntimeException{
	public static class DuplicateEmailException extends RuntimeException {
	    public DuplicateEmailException(String message) {
	        super(message);
	    }
	}
	
	public static class DuplicateUsernameException extends RuntimeException {
	    public DuplicateUsernameException(String message) {
	        super(message);
	    }
	}

	public static class IllegalArgumentException extends RuntimeException {
	    public IllegalArgumentException(String message) {
	        super(message);
	    }
	}
	
	public static class InvalidCredentialsException extends RuntimeException {
	    public InvalidCredentialsException(String message) {
	        super(message);
	    }
	}

	public static class ResourceNotFoundException extends RuntimeException {
	    public ResourceNotFoundException(String message) {
	        super(message);
	    }
	}
	
	public static class WeakPasswordException extends RuntimeException {
	    public WeakPasswordException(String message) {
	        super(message);
	    }
	}
}
