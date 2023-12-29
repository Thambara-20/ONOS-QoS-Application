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
import javax.ws.rs.PUT;
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
import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;



@Path("api")
public class AppWebResource extends AbstractWebResource {
    // Assuming you have references to the ONOS services


//----------------------------------------------------------------add Queue---------------------------------------------------------------------------

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

//    --------------------------------------------------get Queues---------------------------------------------------------------------------
    @GET
    @Path("getQueues")
    @Produces("application/json")
    public Response getQueues(@QueryParam("deviceId") String deviceId) {
        try {
            ObjectNode responseNode = mapper().createObjectNode();

            // Validate input parameters
            if (deviceId == null || deviceId.isEmpty()) {
                responseNode.put("error", "'deviceId' must be provided as a query parameter.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseNode).build();
            }

            DeviceId deviceIdentifier = DeviceId.deviceId(deviceId);

            DeviceService deviceService = get(DeviceService.class);

            // Check if the device exists
            Device device = deviceService.getDevice(deviceIdentifier);
            if (device == null) {
                responseNode.put("error", "Device not found.");
                return Response.status(Response.Status.NOT_FOUND).entity(responseNode).build();
            }

            // Get queues for the specified device
            QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);
            Collection<QueueDescription> queues = queueConfig.getQueues();

            // Prepare JSON response
            ArrayNode queuesArray = mapper().createArrayNode();
            for (QueueDescription queue : queues) {
                ObjectNode queueNode = mapper().createObjectNode();
                queueNode.put("queueId", queue.queueId().toString());
                queueNode.put("type", queue.type().toString());
                queueNode.put("maxRate", queue.maxRate().get().bps());
                queueNode.put("minRate", queue.minRate().get().bps());
                queueNode.put("burst", queue.burst().toString());
                queueNode.put("priority", queue.priority().toString());
                queuesArray.add(queueNode);
            }

            responseNode.set("queues", queuesArray);

            return Response.ok().entity(responseNode).build();

        } catch (Exception e) {
            return Response.serverError().entity("Error processing request: " + e.getMessage()).build();
        }
    }
//-------------------------------------------------------delete Queue----------------------------------------------------------------------------
    @DELETE
    @Path("deleteQueue")
    @Produces("application/json")
    public Response deleteQueue(@QueryParam("deviceId") String deviceId,
                                @QueryParam("queueId") String queueId) {
        try {
            ObjectNode responseNode = mapper().createObjectNode();

            // Validate input parameters
            if (deviceId == null || deviceId.isEmpty() || queueId == null || queueId.isEmpty()) {
                responseNode.put("error", "'deviceId' and 'queueId' must be provided as query parameters.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseNode).build();
            }

            // Get the DeviceId
            DeviceId deviceIdentifier = DeviceId.deviceId(deviceId);

            // Get the DeviceService
            DeviceService deviceService = get(DeviceService.class);

            // Check if the device exists
            Device device = deviceService.getDevice(deviceIdentifier);
            if (device == null) {
                responseNode.put("error", "Device not found.");
                return Response.status(Response.Status.NOT_FOUND).entity(responseNode).build();
            }

            // Get the QueueConfigBehaviour
            QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);

            // Create a QueueId for deletion
            QueueId queueToDelete = QueueId.queueId(queueId);

            // Check if the queue exists before attempting to delete
            if (!queueConfig.getQueues().stream().anyMatch(q -> q.queueId().equals(queueToDelete))) {
                responseNode.put("error", "Queue not found for deletion.");
                return Response.status(Response.Status.NOT_FOUND).entity(responseNode).build();
            }

            // Delete the queue
            queueConfig.deleteQueue(queueToDelete);

            responseNode.put("message", "Queue deleted successfully.");
            return Response.ok().entity(responseNode).build();

        } catch (Exception e) {
            return Response.serverError().entity("Error processing request: " + e.getMessage()).build();
        }
    }

//    ---------------------------------------------------Update Queue----------------------------------------------------------------------
    @PUT
    @Path("updateQueue")
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateQueue(InputStream stream) {
        try {
            ObjectNode responseNode = mapper().createObjectNode();

            // Read JSON input
            JsonNode input = mapper().readTree(stream);

            // Validate JSON input
            if (!input.has("deviceId") || !input.has("queueDescription")) {
                responseNode.put("error", "'deviceId' and 'queueDescription' must be provided in the JSON input.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseNode).build();
            }

            // Extract values from JSON input
            String deviceId = input.get("deviceId").asText();
            JsonNode queueDescriptionNode = input.get("queueDescription");

            // Get the DeviceId
            DeviceId deviceIdentifier = DeviceId.deviceId(deviceId);

            // Get the DeviceService
            DeviceService deviceService = get(DeviceService.class);

            // Check if the device exists
            Device device = deviceService.getDevice(deviceIdentifier);
            if (device == null) {
                responseNode.put("error", "Device not found.");
                return Response.status(Response.Status.NOT_FOUND).entity(responseNode).build();
            }

            // Get the QueueConfigBehaviour
            QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);

            // Create a QueueId for updating
            String queueId = queueDescriptionNode.get("queueId").asText();
            QueueId queueToUpdate = QueueId.queueId(queueId);

            // Check if the queue exists before attempting to update
            if (!queueConfig.getQueues().stream().anyMatch(q -> q.queueId().equals(queueToUpdate))) {
                responseNode.put("error", "Queue not found for updating.");
                return Response.status(Response.Status.NOT_FOUND).entity(responseNode).build();
            }

            // Create updated QueueDescription
            QueueDescription updatedQueueDescription = DefaultQueueDescription.builder()
                    .queueId(queueToUpdate)
                    .type(EnumSet.of(QueueDescription.Type.MAX))
                    .maxRate(Bandwidth.bps(queueDescriptionNode.get("maxRate").asLong()))
                    .minRate(Bandwidth.bps(queueDescriptionNode.get("minRate").asLong()))
                    .burst(queueDescriptionNode.get("burst").asLong())
                    .priority(queueDescriptionNode.get("priority").asLong())
                    .build();

            // Update the queue
//            queueConfig.updateQueue(updatedQueueDescription);

            responseNode.put("message", "Queue updated successfully.");
            return Response.ok().entity(responseNode).build();

        } catch (Exception e) {
            return Response.serverError().entity("Error processing request: " + e.getMessage()).build();
        }
    }



}
