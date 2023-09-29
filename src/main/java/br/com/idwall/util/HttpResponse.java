package br.com.idwall.util;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.idwall.domain.ResponseBody;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class HttpResponse {
	
	public static ResponseEntity<ResponseBody> created(String message, Object payload) {
		ResponseBody responseBody = new ResponseBody();
		
		responseBody.setMessage(message);
		responseBody.setPayload(payload);
		responseBody.setError(new ArrayList<>());
		
	    return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
	}
	
	public static ResponseEntity<ResponseBody> serverError() {
		ResponseBody responseBody = new ResponseBody();

	    JSONObject error = new JSONObject();
	    JSONArray array = new JSONArray();

	    error.put("message", "Ocorreu um erro em nosso servidores");

	    array.add(error);

	    responseBody.setMessage("Ops, parece que ocorreu um erro dentro dos nossos servidores");
	    responseBody.setPayload(new JSONObject());
	    responseBody.setError(array);

	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
	}

	
	public static ResponseEntity<ResponseBody> ok(String message, Object payload) {
		ResponseBody responseBody = new ResponseBody();

		
		responseBody.setMessage(message);
		responseBody.setError(new ArrayList<>());
		responseBody.setPayload(payload);
		
		return ResponseEntity.status(HttpStatus.OK).body(responseBody);
	}
	
	public static ResponseEntity<ResponseBody> notFound(String message, ArrayList<Object> error) {
		ResponseBody responseBody = new ResponseBody();

		responseBody.setMessage(message);
		responseBody.setError(error);
		responseBody.setPayload(new JSONObject());
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
	}
	
	
	public static ResponseEntity<ResponseBody> notFound(String message) {
		ResponseBody responseBody = new ResponseBody();

		responseBody.setMessage(message);
		responseBody.setPayload(new JSONObject());
		responseBody.setError(new ArrayList<>());
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
	}
	
	public static ResponseEntity<ResponseBody> badRequest(JSONArray errors) {
		ResponseBody responseBody = new ResponseBody();

	    responseBody.setMessage("Ops! Ocorreram alguns erros de validação");
	    responseBody.setPayload(new JSONObject());
		responseBody.setError(errors);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
	}
}
