package curso.api.rest;

import java.sql.SQLException;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@ControllerAdvice
public class ExceptionControl extends ResponseEntityExceptionHandler{

	// Tratamento da maioria dos erros: java language
	@Override
	@ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class})
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
		StringBuilder msgBuilder = new StringBuilder();
		msgBuilder.append(ex.getMessage());
		
		if (ex instanceof MethodArgumentNotValidException) {
			msgBuilder = new StringBuilder();
			List<ObjectError> errors = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();
			for (ObjectError error: errors) {
				   msgBuilder.append(error.getDefaultMessage() + "\n");
			   } 
			}
			
		ObjectOfError objectOfError = new ObjectOfError();
		objectOfError.setError(ex.getMessage());
		objectOfError.setCode(status.value() + " ==> " + status.getReasonPhrase());
	    
		return new ResponseEntity<Object>(objectOfError, headers,status);
	}
	
	// Tratamento da maioria dos erros: banco de dados
    @ExceptionHandler({DataIntegrityViolationException.class, ConstraintViolationException.class, PSQLException.class, SQLException.class})
	protected ResponseEntity<Object> handleExceptionDataIntegrity(Exception ex) {
			
    	    String messageErrorSimplified = ex.getMessage();
    	    
    	    if (ex instanceof DataIntegrityViolationException) {
    	    	messageErrorSimplified = ((DataIntegrityViolationException) ex).getCause().getCause().getMessage();	
    	    } 
    	    
    	    if (ex instanceof ConstraintViolationException) {
    	    	messageErrorSimplified = ((ConstraintViolationException) ex).getCause().getCause().getMessage();
    	    }  
    	    
    	    if (ex instanceof PSQLException) {
    	    	messageErrorSimplified = ((PSQLException) ex).getCause().getCause().getMessage();
    	    } 
    	    
    	    if (ex instanceof SQLException) {
    	    	messageErrorSimplified = ((SQLException) ex).getCause().getCause().getMessage();
    	    } 
    	    
			ObjectOfError objectOfError = new ObjectOfError();
			objectOfError.setError(messageErrorSimplified);
			objectOfError.setCode(HttpStatus.INTERNAL_SERVER_ERROR + " ==> " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		    
			return new ResponseEntity<Object>(objectOfError, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	
	
}
