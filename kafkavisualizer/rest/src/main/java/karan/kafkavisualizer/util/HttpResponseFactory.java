package karan.kafkavisualizer.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.ws.rs.core.Response;


public class HttpResponseFactory {

    private final Gson gson = new Gson();

    public <T> Response createOkResponse(T body) {
        return Response.ok().entity(gson.toJson(body)).build();
    }

    public Response createNotModifiedResponse() {
        return Response.notModified().build();
    }

    public Response createServerErrorResponse(Exception e) {
        return Response.serverError().entity(createErrorJsonString(e.getMessage())).build();
    }

    public Response createBadRequestResponse(String errorMessage) {
        return Response.status(Response.Status.BAD_REQUEST).entity(createErrorJsonString(errorMessage)).build();
    }

    public Response createNotFoundResponse(String errorMessage) {
        return Response.status(Response.Status.NOT_FOUND).entity(createErrorJsonString(errorMessage)).build();
    }

    private String createErrorJsonString(String errorMessage) {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("error", errorMessage);
        return gson.toJson(jsonObject);
    }
}
