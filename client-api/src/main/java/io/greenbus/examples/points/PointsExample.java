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
package io.greenbus.examples.points;

import io.greenbus.msg.japi.Session;
import io.greenbus.client.service.proto.Model;
import io.greenbus.client.service.proto.ModelRequests;
import io.greenbus.japi.client.service.ModelService;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Example: Points
 *
 */
public class PointsExample {

    /**
     * Get All Points
     *
     * Get all points configured in the system.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getPoints(Session session) throws Exception {

        System.out.print("\n=== Get All Points ===\n\n");

        // Get service interface for points
        final ModelService.Client client = ModelService.client(session);

        // Retrieve list of all points
        //List<Model.Point> pointList = client.pointQuery(ModelRequests.PointQuery.newBuilder().build());
        final List<Model.Point> pointList = client.pointQuery(ModelRequests.PointQuery.newBuilder().build()).get(5000, TimeUnit.MILLISECONDS);

        System.out.println("Found points: " + pointList.size());

        // Inspect a single point
        Model.Point point = pointList.get(0);

        // Display properties of the point
        System.out.println("Point");
        System.out.println("-----------");
        System.out.println("Uuid: " + point.getUuid().getValue());
        System.out.println("Name: " + point.getName());
        System.out.println("Category: " + point.getPointCategory());
        System.out.println("Unit: " + point.getUnit());
        System.out.println("-----------");
    }

    /**
     * Get Point by Name
     *
     * Get a particular point by providing the point name.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getPointByName(Session session) throws Exception {

        System.out.print("\n=== Get Point By Name ===\n\n");

        // Get service interface for points
        final ModelService.Client client = ModelService.client(session);

        // Select a single example point
        final Model.Point examplePoint = client.pointQuery(ModelRequests.PointQuery.newBuilder().build())
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        // Get the name of the example point
        String name = examplePoint.getName();

        // Find the point again using the point name
        Model.Point point = client.getPoints(ModelRequests.EntityKeySet.newBuilder().addNames(name).build())
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        System.out.println("Found point by name: " + point.getName());
    }

    /**
     * Get Point by UUID
     *
     * Get a particular point by providing the point UUID.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getPointByUuid(Session session) throws Exception {

        System.out.print("\n=== Get Point By UUID ===\n\n");

        // Get service interface for points
        final ModelService.Client client = ModelService.client(session);

        // Select a single example point
        final Model.Point examplePoint = client.pointQuery(ModelRequests.PointQuery.newBuilder().build())
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        // Get the name of the example point
        Model.ModelUUID uuid = examplePoint.getUuid();

        // Find the point again using the point name
        Model.Point point = client.getPoints(ModelRequests.EntityKeySet.newBuilder().addUuids(uuid).build())
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        System.out.println("Found point by UUID: " + point.getName() + ", " + point.getUuid());
    }
}
