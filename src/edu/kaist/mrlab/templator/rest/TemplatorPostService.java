package edu.kaist.mrlab.templator.rest;

import javax.ws.rs.Consumes;

import javax.ws.rs.FormParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;

@Path("/templator/ko")
public class TemplatorPostService {

	@POST
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	// @Produces("text/plain; charset=UTF-8")
	public Response getPost(String input) throws JSONException {

		// @FormParam("text")
		
		String result = input;

		return Response.ok(result).entity(result)
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept").build();

	}

	@OPTIONS
	@Consumes("application/x-www-form-urlencoded; charset=utf-8")
	@Produces("text/plain; charset=utf-8")
	public Response getFRDFOptions(@FormParam("text") String input) throws JSONException {

		return Response.ok().header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept").build();

	}

}
