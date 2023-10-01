package br.com.idwall.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.com.idwall.domain.FbiPerson;
import br.com.idwall.domain.ResponseBody;
import br.com.idwall.exception.Exceptions;
import br.com.idwall.service.impl.FbiWantedPersonServiceImpl;
import br.com.idwall.util.HttpResponse;

@RestController
@RequestMapping("/api/v1")
public class FbiWantedPersonController {

	@Autowired
	private FbiWantedPersonServiceImpl fbiWantedPersonServiceImpl;

	@GetMapping("/fbi/persons/wanted")
	public ResponseEntity<ResponseBody> getAllPersons() {
		try {
			List<FbiPerson> persons = fbiWantedPersonServiceImpl.getAll();
			ObjectMapper objectMapper = new ObjectMapper();
			ArrayNode responseArray = objectMapper.createArrayNode();

			for (FbiPerson person : persons) {
				String jsonPerson = objectMapper.writeValueAsString(person);
				JsonNode personNode = objectMapper.readTree(jsonPerson);

				if (personNode.has("images")) {
					String imagesJsonString = personNode.get("images").asText();
					JsonNode imagesJsonNode = objectMapper.readTree(imagesJsonString);
					((ObjectNode) personNode).set("images", imagesJsonNode);
				}

				if (personNode.has("createdAt")) {
					long createdAtTimestamp = personNode.get("createdAt").asLong();
					String formattedCreatedAt = formatDate(createdAtTimestamp);
					((ObjectNode) personNode).put("createdAt", formattedCreatedAt);
				}

				responseArray.add(personNode);
			}

			return HttpResponse.ok("Pessoas procuradas pelo FBI listadas com sucesso",
					convertKeysToSnakeCase(responseArray));
		} catch (Exception error) {
			switch (error.getMessage()) {
			case Exceptions.FBI_WANTED_PERSON_NOT_FOUND:
				return HttpResponse.notFound(Exceptions.FBI_WANTED_PERSON_NOT_FOUND);
			default:
				return HttpResponse.serverError();
			}
		}
	}

	@GetMapping("/fbi/persons/{id}/wanted")
	public ResponseEntity<ResponseBody> getPersonById(@PathVariable int id) {
		try {
			FbiPerson person = fbiWantedPersonServiceImpl.getById(id);

			ObjectMapper objectMapper = new ObjectMapper();
			String jsonPerson = objectMapper.writeValueAsString(person);
			JsonNode response = objectMapper.readTree(jsonPerson);

			if (response.has("images")) {
				String imagesJsonString = response.get("images").asText();
				JsonNode imagesJsonNode = objectMapper.readTree(imagesJsonString);
				((ObjectNode) response).set("images", imagesJsonNode);
			}

			if (response.has("createdAt")) {
				long createdAtTimestamp = response.get("createdAt").asLong();
				String formattedCreatedAt = formatDate(createdAtTimestamp);
				((ObjectNode) response).put("createdAt", formattedCreatedAt);
			}

			return HttpResponse.ok("Pessoas procuradas pelo FBI listado com sucesso", convertKeysToSnakeCase(response));
		} catch (Exception error) {
			switch (error.getMessage()) {
			case Exceptions.WANTED_BY_FBI_NOT_FOUND:
				return HttpResponse.notFound(Exceptions.WANTED_BY_FBI_NOT_FOUND);
			default:
				return HttpResponse.serverError();
			}
		}
	}

	private String formatDate(long timestamp) {
		Date date = new Date(timestamp);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		return sdf.format(date);
	}

	private JsonNode convertKeysToSnakeCase(JsonNode node) {
		if (node.isArray()) {
			return convertKeysToSnakeCaseArray((ArrayNode) node);
		} else if (node.isObject()) {
			return convertKeysToSnakeCaseObject((ObjectNode) node);
		} else {
			return node;
		}
	}

	private ArrayNode convertKeysToSnakeCaseArray(ArrayNode arrayNode) {
		ObjectMapper objectMapper = new ObjectMapper();
		ArrayNode snakeCaseArray = objectMapper.createArrayNode();

		for (JsonNode element : arrayNode) {
			snakeCaseArray.add(convertKeysToSnakeCase(element));
		}

		return snakeCaseArray;
	}

	private ObjectNode convertKeysToSnakeCaseObject(ObjectNode objectNode) {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode snakeCaseNode = objectMapper.createObjectNode();

		objectNode.fields().forEachRemaining(entry -> {
			String snakeKey = camelToSnake(entry.getKey());
			snakeCaseNode.set(snakeKey, convertKeysToSnakeCase(entry.getValue()));
		});

		return snakeCaseNode;
	}

	private String camelToSnake(String camelCase) {
		return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
	}

}
