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
package io.greenbus.examples.keyvalues;

import io.greenbus.msg.japi.Session;
import io.greenbus.client.service.proto.Model;
import io.greenbus.client.service.proto.ModelRequests;
import io.greenbus.japi.client.service.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Example: Key Values
 *
 * Key values are used to store arbitrary data used by protocols and
 * applications.
 */
public class KeyValuesExample {

    /**
     * Get EntityKeyValue
     *
     * Retrieves the list of EntityKeyValues in the system.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getKeyValues(Session session) throws Exception {

        System.out.print("\n=== Get Key Values ===\n\n");

        // Get service interface for the model
        final ModelService.Client client = ModelService.client(session);

        // Get entities in the system
        List<Model.Entity> entities = client.entityQuery(ModelRequests.EntityQuery.newBuilder().build()).get(5000, TimeUnit.MILLISECONDS);

        List<Model.ModelUUID> entityUuids = new ArrayList<Model.ModelUUID>();
        for (Model.Entity entity : entities) {
            entityUuids.add(entity.getUuid());
        }

        // Find all UUID/key pairs for the specified entities
        List<ModelRequests.EntityKeyPair> entityKeyPairs = client.getEntityKeys(entityUuids).get(5000, TimeUnit.MILLISECONDS);

        // Find the key value for the UUID/key pairs
        List<Model.EntityKeyValue> entityKeyValues = client.getEntityKeyValues(entityKeyPairs).get(5000, TimeUnit.MILLISECONDS);


        for (Model.EntityKeyValue keyValue : entityKeyValues) {

            // Display properties of the EntityKeyValue object
            System.out.println("EntityKeyValue");
            System.out.println("-----------");
            System.out.println("Key: " + keyValue.getKey());
            System.out.println("Value: " + keyValue.getValue());
            System.out.println("-----------\n");
        }
    }

    /**
     * Create/Update/Remove
     *
     * Runs through the lifecycle of EntityKeyValue objects. Creates a new EntityKeyValue,
     * updates it to change the data payload, and finally deletes it from the system.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void createUpdateRemove(Session session) throws Exception {

        System.out.print("\n=== Create / Update / Remove EntityKeyValue ===\n\n");

        // Get service interface for the model
        final ModelService.Client client = ModelService.client(session);

        // Get an Entity to attach the key value to
        Model.Entity entity = client.entityQuery(ModelRequests.EntityQuery.newBuilder()
                .setPagingParams(ModelRequests.EntityPagingParams.newBuilder()
                        .setPageSize(1))
                .build())
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        Model.EntityKeyValue originalKeyValue = Model.EntityKeyValue.newBuilder()
                .setUuid(entity.getUuid())
                .setKey("exampleKey")
                .setValue(
                        Model.StoredValue.newBuilder()
                        .setStringValue("Example data")
                        .build())
                .build();

        // Create the EntityKeyValue with an original value
        Model.EntityKeyValue createdKeyValue = client.putEntityKeyValues(Arrays.asList(originalKeyValue))
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        // Display the result of creating
        System.out.println("Created - Key: " + createdKeyValue.getKey() + " - Value: " + createdKeyValue.getValue().getStringValue());

        Model.EntityKeyValue updateKeyValue = Model.EntityKeyValue.newBuilder()
                .setUuid(entity.getUuid())
                .setKey("exampleKey")
                .setValue(
                        Model.StoredValue.newBuilder()
                                .setStringValue("Example data updated")
                                .build())
                .build();


        // Update the EntityKeyValue with a new value
        Model.EntityKeyValue updatedKeyValue = client.putEntityKeyValues(Arrays.asList(updateKeyValue))
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        // Display the result of updating
        System.out.println("Updated - Key: " + updatedKeyValue.getKey() + " - Value: " + updatedKeyValue.getValue().getStringValue());

        ModelRequests.EntityKeyPair deleteKeyPair = ModelRequests.EntityKeyPair.newBuilder()
                .setUuid(entity.getUuid())
                .setKey("exampleKey")
                .build();

        // Delete the EntityKeyValue from the system
        Model.EntityKeyValue deletedKeyValue = client.deleteEntityKeyValues(Arrays.asList(deleteKeyPair))
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        // Display the result of deleting
        System.out.println("Deleted - Key: " + deletedKeyValue.getKey() + " - Value: " + deletedKeyValue.getValue().getStringValue());

    }

}
