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
package io.greenbus.examples.entities;

import io.greenbus.msg.japi.Session;
import io.greenbus.client.service.proto.Model.Entity;
import io.greenbus.client.service.proto.Model.ModelUUID;
import io.greenbus.client.service.proto.ModelRequests;
import io.greenbus.japi.client.service.ModelService;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Example: Entities
 *
 */
public class EntitiesExample {

    /**
     * Get Entities
     *
     * Retrieves the list of Entity objects in the system.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getEntities(Session session) throws Exception {

        System.out.print("\n=== Get Entities ===\n\n");

        // Get service interface for entities
        final ModelService.Client client = ModelService.client(session);

        // Retrieve list of all entities in the system
        final ModelRequests.EntityQuery entityQuery = ModelRequests.EntityQuery.newBuilder().setPagingParams(
                ModelRequests.EntityPagingParams.newBuilder()
                        .setPageSize(3)
                        .build())
                .build();

        final List<Entity> entityList = client.entityQuery(entityQuery).get(5000, TimeUnit.MILLISECONDS);

        // Inspect a single Entity object
        final Entity first = entityList.get(0);

        // Display properties of Entity object
        System.out.println("Entity");
        System.out.println("-----------");
        System.out.println("Uuid: " + first.getUuid().getValue());
        System.out.println("Name: " + first.getName());
        System.out.println("Types: " + first.getTypesList());
        System.out.println("-----------\n");

        System.out.println("Entity count: " + entityList.size());

        // Display list of (first 10) Entity objects
        for (Entity entity : entityList.subList(0, 10)) {
            System.out.println("Entity: " + entity.getName());
        }

        System.out.println("...");
    }

    /**
     * Get By Type
     *
     * Retrieves Entity objects of a certain type.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getByType(Session session) throws Exception {

        System.out.print("\n=== Get Entities With Type ===\n\n");

        // Get service interface for entities
        final ModelService.Client client = ModelService.client(session);

        // Get Entity objects with the type "Breaker"
        final ModelRequests.EntityQuery entityQuery = ModelRequests.EntityQuery.newBuilder()
                .setTypeParams(
                        ModelRequests.EntityTypeParams.newBuilder()
                                .addIncludeTypes("CapBank")
                                .build())
                .setPagingParams(
                        ModelRequests.EntityPagingParams.newBuilder()
                                .setPageSize(3)
                                .build())
                .build();

        final List<Entity> entityList = client.entityQuery(entityQuery).get(5000, TimeUnit.MILLISECONDS);

        System.out.println("Entity count: " + entityList.size());

        // Display list of entities of type
        for (Entity entity : entityList) {
            System.out.println("Entity: " + entity.getName() + ", Types: " + entity.getTypesList());
        }

    }

    /**
     * Get Immediate Children
     *
     * Finds the immediate children of an Entity.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getImmediateChildren(Session session) throws Exception {

        System.out.print("\n=== Get Immediate Children ===\n\n");

        // Get service interface for entities
        final ModelService.Client client = ModelService.client(session);

        // Select an Entity of type "Equipment"
        final ModelRequests.EntityQuery entityQuery = ModelRequests.EntityQuery.newBuilder()
                .setTypeParams(
                        ModelRequests.EntityTypeParams.newBuilder()
                                .addIncludeTypes("Equipment")
                                .build())
                .setPagingParams(
                        ModelRequests.EntityPagingParams.newBuilder()
                                .setPageSize(1)
                                .build())
                .build();

        final Entity equipment = client.entityQuery(entityQuery)
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        System.out.println("Parent: " + equipment.getName() + ", Types: " + equipment.getTypesList());

        // Get UUID of equipment entity
        ModelUUID equipUuid = equipment.getUuid();

        // Get immediate children (relationship "owns") of equipment entity
        final ModelRequests.EntityRelationshipFlatQuery ownsQuery = ModelRequests.EntityRelationshipFlatQuery.newBuilder()
                .addStartUuids(equipUuid)
                .setRelationship("owns")
                .setDescendantOf(true)
                .setDepthLimit(1)
                .build();

        final List<Entity> children = client.relationshipFlatQuery(ownsQuery).get(5000, TimeUnit.MILLISECONDS);

        // Display list of the children of the equipment entity
        for (Entity entity : children) {
            System.out.println("Children: " + entity.getName() + ", Types: " + entity.getTypesList());
        }

    }

}
