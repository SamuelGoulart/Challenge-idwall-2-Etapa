package br.com.idwall.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.idwall.domain.ResponseBody;
import br.com.idwall.util.HttpResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@ControllerAdvice
public class RequestValidationException  {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseBody> handleValidationExceptions(MethodArgumentNotValidException ex) {
	    JSONArray errors = new JSONArray();

	    ex.getBindingResult().getAllErrors().forEach((error) -> {
	        String fieldName = ((FieldError) error).getField();
	        String errorMessage = error.getDefaultMessage();

	        JSONObject formatError = new JSONObject();
	        formatError.put("menssage", errorMessage);
	        formatError.put("param", fieldName);

	        errors.add(formatError);
	    });

	    return HttpResponse.badRequest(errors);
	}
	
}
