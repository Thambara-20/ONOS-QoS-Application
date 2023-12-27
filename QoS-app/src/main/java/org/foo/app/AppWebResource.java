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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.rest.AbstractWebResource;
//import org.onosproject.ovsdb.controller.



import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static org.onlab.util.Tools.nullIsNotFound;

/**
 * Sample web resource.
 */
@Path("api")
public class AppWebResource extends AbstractWebResource {

    /**
     * Get hello world greeting.
     *
     * @return 200 OK
     */
    @GET
    @Path("getQueue")
    public Response getQueue() {
        ObjectNode node = mapper().createObjectNode().put("getQueue","getQueue");
        return ok(node).build();
    }

    @POST
    @Path("updateQueue")
    public Response updateQueue() {
        ObjectNode node = mapper().createObjectNode().put("updateQueue","updateQueue");
        return ok(node).build();
    }

    @DELETE
    @Path("deleteQueue")
    public Response deleteQueue() {
        ObjectNode node = mapper().createObjectNode().put("deleteQueue","deleteQueue");
        return ok(node).build();
    }

}