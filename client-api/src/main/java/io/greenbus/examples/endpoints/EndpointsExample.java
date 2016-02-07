/**
 * Copyright 2011 Green Energy Corp.
 *
 * Licensed to Green Energy Corp (www.greenenergycorp.com) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. Green Energy
 * Corp licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.greenbus.examples.endpoints;

import io.greenbus.msg.japi.Session;
import io.greenbus.client.service.proto.FrontEnd;
import io.greenbus.client.service.proto.Model;
import io.greenbus.client.service.proto.ModelRequests;
import io.greenbus.japi.client.service.FrontEndService;
import io.greenbus.japi.client.service.ModelService;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Example: Endpoints
 *
 * Endpoints manage the protocol connections to external devices and
 * systems. There are two central service objects: endpoints and endpoint connections.
 *
 * Endpoints themselves are the system-wide representations of communications to remote
 * devices/systems. Communications are not established until responsibility for an endpoint is
 * assigned to a front end processor (FEP).
 *
 * Endpoint connections represent actual communications on specific front end processors (FEP).
 */
public class EndpointsExample {

    /**
     * Get Endpoints
     *
     * Retrieves the list of Endpoints in the system.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getEndpoints(Session session) throws Exception {

        System.out.print("\n=== Get Endpoint Configurations ===\n\n");

        // Get service interface for endpoints
        final ModelService.Client client = ModelService.client(session);

        // Retrieve list of all endpoints
        final List<Model.Endpoint> endpoints = client.endpointQuery(ModelRequests.EndpointQuery.newBuilder().build()).get(5000, TimeUnit.MILLISECONDS);

        // Inspect a single endpoint
        final Model.Endpoint endpoint = endpoints.get(0);

        // Display properties of endpoint configuration
        System.out.println("Endpoint");
        System.out.println("-----------");
        System.out.println("Name: " + endpoint.getName());
        System.out.println("Protocol: " + endpoint.getProtocol());
        System.out.println("Disabled: " + endpoint.getDisabled());
        System.out.println("-----------");
    }

    /**
     * Get Connection Statuses
     *
     * Retrieves the list of Endpoints in the system.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getConnectionStatuses(Session session) throws Exception {

        System.out.print("\n=== Get Endpoint Connections ===\n\n");

        // Get service interface for endpoints
        final ModelService.Client client = ModelService.client(session);

        // Get service interface for frontends
        final FrontEndService.Client frontEndClient = FrontEndService.client(session);

        // Retrieve a list of all endpoints
        final List<Model.Endpoint> endpoints = client.endpointQuery(ModelRequests.EndpointQuery.newBuilder().build()).get(5000, TimeUnit.MILLISECONDS);

        // Retrieve list of front end connection statuses for endpoints
        final ModelRequests.EntityKeySet.Builder endpointKeySetBuilder = ModelRequests.EntityKeySet.newBuilder();
        for (Model.Endpoint endpoint: endpoints) {
            endpointKeySetBuilder.addUuids(endpoint.getUuid());
        }
        final ModelRequests.EntityKeySet endpointKeySet = endpointKeySetBuilder.build();

        final List<FrontEnd.FrontEndConnectionStatus> frontEndConnectionStatuses = frontEndClient.getFrontEndConnectionStatuses(endpointKeySet).get(5000, TimeUnit.MILLISECONDS);

        // Display list of frontend connection statuses, showing COMMS status
        for (FrontEnd.FrontEndConnectionStatus status : frontEndConnectionStatuses) {
            System.out.print("Endpoint: " + status.getEndpointName());
            System.out.print(", State: " + status.getState());
            System.out.print("\n");
        }

    }

    /**
     * Enable and Disable Endpoints
     *
     * Demonstrates setting Endpoints to enabled/disabled. This operation uses a separate (and separately authorized)
     * service from Endpoint model updates.
     *
     * @param session
     * @throws Exception
     */
    public static void enableDisableEndpoint(Session session) throws Exception {

        System.out.print("\n=== Enable/Disable Endpoint ===\n\n");

        // Get service interface for endpoints
        final ModelService.Client client = ModelService.client(session);

        // Inspect a single endpoint
        final Model.Endpoint endpoint = client.endpointQuery(ModelRequests.EndpointQuery.newBuilder()
                .setPagingParams(
                ModelRequests.EntityPagingParams.newBuilder()
                        .setPageSize(3)
                        .build())
                .build())
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        System.out.println("Original: " + endpoint.getName() + ", " + endpoint.getDisabled());

        // Disable endpoint
        final ModelRequests.EndpointDisabledUpdate endpointDisabledUpdate = ModelRequests.EndpointDisabledUpdate.newBuilder()
                .setEndpointUuid(endpoint.getUuid())
                .setDisabled(true)
                .build();

        final Model.Endpoint disabled = client.putEndpointDisabled(Arrays.asList(endpointDisabledUpdate))
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        System.out.println("Disabled: " + disabled.getName() + ", " + disabled.getDisabled());

        // Re-enable endpoint
        final ModelRequests.EndpointDisabledUpdate endpointEnableUpdate = ModelRequests.EndpointDisabledUpdate.newBuilder()
                .setEndpointUuid(endpoint.getUuid())
                .setDisabled(false)
                .build();

        final Model.Endpoint enabled = client.putEndpointDisabled(Arrays.asList(endpointEnableUpdate))
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        System.out.println("Re-enabled: " + enabled.getName() + ", " + enabled.getDisabled());

    }

}
