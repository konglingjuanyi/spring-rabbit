package br.com.zup.spring.rabbit.infra;

import java.nio.charset.Charset;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMessage implements QMessage {

	private static final long serialVersionUID = -2339113523782040983L;

	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private String payload;

	public JsonMessage(String payload) {

		this.payload = payload;
	}

	public JsonMessage(Object pojo) {
	    try {
			this.payload = MAPPER.writeValueAsString(pojo);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    }

	public JsonMessage(byte[] body) {

		this.payload = new String(body, DEFAULT_CHARSET);
	}

	public String getPayload() {

		return payload;
	}

	public byte[] getBody() {

		return payload.getBytes(DEFAULT_CHARSET);
	}

	@Override
	public String toString() {

		return payload;
	}
}
