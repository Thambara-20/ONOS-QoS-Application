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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onlab.util.Bandwidth;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.behaviour.DefaultQueueDescription;
import org.onosproject.net.behaviour.QueueConfigBehaviour;
import org.onosproject.net.behaviour.QueueDescription;
import org.onosproject.net.behaviour.QueueId;
import org.onosproject.net.device.DeviceService;
import org.onosproject.rest.AbstractWebResource;
// import org.onosproject.ovsdb

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.DELETE;
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
import java.util.EnumSet;
import java.util.Optional;



@Path("api")
public class AppWebResource extends AbstractWebResource {
    // Assuming you have references to the ONOS services


// ...

    @POST
    @Path("addQueue")
    @Consumes("application/json")
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
            String Id = input.get("deviceId").asText();
            int portNumber = input.get("portNumber").asInt();
            JsonNode queueDescriptionNode = input.get("queueDescription");

            // Get the DeviceId
            DeviceId deviceId = DeviceId.deviceId(Id);

            DeviceService deviceService = get(DeviceService.class);

            if (deviceService.getDevice(deviceId) == null) {
                responseNode.put("error", "Device not found.");
                return Response.serverError().entity(responseNode).build();
            }


            // Create a QueueId
            QueueId queueId = QueueId.queueId(queueDescriptionNode.get("queueId").asText());

            // Create QueueDescription
            QueueDescription queueDescription = DefaultQueueDescription.builder()
                    .queueId(queueId)
                    .type(EnumSet.of(QueueDescription.Type.MAX))
                    .maxRate(Bandwidth.bps(queueDescriptionNode.get("maxRate").asLong()))
                    .minRate(Bandwidth.bps(queueDescriptionNode.get("minRate").asLong()))
                    .burst(queueDescriptionNode.get("burst").asLong())
                    .priority(queueDescriptionNode.get("priority").asLong())
                    .build();


            Device device = deviceService.getDevice(deviceId);
            QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);

            queueConfig.addQueue(queueDescription);


        } catch (Exception e) {
            return Response.serverError().entity("Error processing JSON input: " + e.getMessage()).build();
        }

        return Response.ok().build();
    }

    @DELETE
    @Path("deleteQueue")
    public Response deleteQueue(@QueryParam("deviceId") String deviceId,
                                @QueryParam("queueId") String queueId) {


        return Response.ok().build();
    }

    @GET
    @Path("getQueue")
    public Response getQueue(@QueryParam("deviceId") String deviceId,
                             @QueryParam("queueId") String queueId) {


        return Response.ok().build();
    }
}
