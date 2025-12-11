package com.nlshakal.web4.resource;

import com.nlshakal.web4.dto.PointRequest;
import com.nlshakal.web4.dto.ResultResponse;
import com.nlshakal.web4.service.ResultService;
import com.nlshakal.web4.util.TokenExtractor;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Path("/results")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class ResultResource {

    private final ResultService resultService;
    private final TokenExtractor tokenExtractor;

    @POST
    @Path("/check")
    public Response checkPoint(@Valid PointRequest request, @Context HttpHeaders headers) {
        try {
            Long userId = tokenExtractor.extractUserId(headers);
            ResultResponse response = resultService.checkPoint(request, userId);
            return Response.ok(response)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (WebApplicationException e) {
            throw e;
        } catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(createErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @GET
    public Response getUserResults(@Context HttpHeaders headers) {
        try {
            Long userId = tokenExtractor.extractUserId(headers);
            List<ResultResponse> results = resultService.getUserResults(userId);
            return Response.ok(results)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(createErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @DELETE
    public Response clearResults(@Context HttpHeaders headers) {
        try {
            Long userId = tokenExtractor.extractUserId(headers);
            resultService.clearUserResults(userId);
            return Response.ok(createSuccessResponse("Results cleared successfully"))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(createErrorResponse(e.getMessage()))
                    .build();
        }
    }
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> success = new HashMap<>();
        success.put("message", message);
        return success;
    }
}

