/*
 * Copyright 2023-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.foo.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.net.behaviour.QueueDescription;
import org.onosproject.rest.AbstractWebResource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;

import static org.onlab.util.Tools.nullIsNotFound;

/**
 * Sample web resource.
 */
@Path("api")
public class AppWebResource extends AbstractWebResource {


//    private final OvsdbQueueConfig ovsdbQueueConfig = new OvsdbQueueConfig();
@POST
@Path("addQueue")
@Consumes("application/json")
@Produces("application/json")
public Response addQueue(InputStream stream) {

    try {
        ObjectNode responseNode = mapper().createObjectNode();

        // Read JSON input
        JsonNode input = mapper().readTree(stream);

        // Validate JSON input
        if (!input.has("deviceId") || !input.has("portNumber") || !input.has("queueDescription")) {
            responseNode.put("error", "'deviceId', 'portNumber', and 'queueDescription' must be provided in the JSON input.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseNode).build();
        }

        // Extract values from JSON input
        String deviceId = input.get("deviceId").asText();
        int portNumber = input.get("portNumber").asInt();
        JsonNode queueDescriptionNode = input.get("queueDescription");





        // Call the existing addQueue method from OvsdbQueueConfig
//        boolean success = ovsdbQueueConfig.addQueue(queueDescription);

        responseNode.put("success", true);
        return Response.ok(responseNode).build();
    } catch (Exception e) {
        return Response.serverError().entity("Error processing JSON input: " + e.getMessage()).build();
    }
}



    @GET
    @Path("getQueue")
    public Response getQueue() {
        ObjectNode node = mapper().createObjectNode().put("getQueue", "getQueue");
        return ok(node).build();
    }


}
