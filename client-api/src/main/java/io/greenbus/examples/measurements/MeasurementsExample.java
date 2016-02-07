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
package io.greenbus.examples.measurements;

import io.greenbus.msg.japi.Session;
import io.greenbus.client.service.proto.Measurements;
import io.greenbus.client.service.proto.Measurements.Measurement;
import io.greenbus.client.service.proto.Model;
import io.greenbus.client.service.proto.ModelRequests;
import io.greenbus.japi.client.service.MeasurementService;
import io.greenbus.japi.client.service.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Example: Measurements
 *
 */
public class MeasurementsExample {

    /**
     * Get Measurement by Point
     *
     * Finds latest measurement value for a specific point.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getMeasurementByPoint(Session session) throws Exception {

        System.out.print("\n=== Measurement By Point ===\n\n");

        // Get service interface for points
        final ModelService.Client frontEndClient = ModelService.client(session);

        // Select a specific point
        final Model.Point examplePoint = frontEndClient.pointQuery(ModelRequests.PointQuery.newBuilder()
                .setPagingParams(ModelRequests.EntityPagingParams.newBuilder()
                        .setPageSize(1))
                .build())
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        // Get service interface for measurements
        final MeasurementService.Client measurementClient = MeasurementService.client(session);

        // Get latest measurement for the point
        //Measurement measurement = measurementService.getMeasurementByPoint(examplePoint);
        final Measurements.PointMeasurementValue measurement = measurementClient.getCurrentValues(Arrays.asList(examplePoint.getUuid()))
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        // Display measurement properties
        System.out.println("Found Measurement by Point: \n" + measurement);
    }

    /**
     * Get Multiple Measurements
     *
     * Finds latest measurement value for multiple points.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getMultipleMeasurements(Session session) throws Exception {

        System.out.print("\n=== Multiple Measurements ===\n\n");

        // Get service interface for points
        final ModelService.Client modelClient = ModelService.client(session);

        // Select four points to get the measurements
        final ModelRequests.PointQuery query = ModelRequests.PointQuery.newBuilder()
                .setPagingParams(ModelRequests.EntityPagingParams.newBuilder()
                        .setPageSize(1))
                .build();

        final List<Model.Point> points = modelClient.pointQuery(query).get(5000, TimeUnit.MILLISECONDS);

        // Get service interface for measurements
        final MeasurementService.Client measurementClient = MeasurementService.client(session);

        // Get the latest measurements for the list of points
        final ArrayList<Model.ModelUUID> pointUuids = new ArrayList<Model.ModelUUID>();
        for (Model.Point point: points) {
            pointUuids.add(point.getUuid());
        }

        final List<Measurements.PointMeasurementValue> pointMeasurementValues = measurementClient.getCurrentValues(pointUuids).get(5000, TimeUnit.MILLISECONDS);

        // Display latest measurements for the points
        for (Measurements.PointMeasurementValue pointMeasurementValue : pointMeasurementValues) {
            System.out.println("Measurement: " + pointMeasurementValue.getPointUuid().getValue() +
                    ", Value: " + buildValueString(pointMeasurementValue.getValue()) +
                    ", Time: " + new Date(pointMeasurementValue.getValue().getTime()));
        }
    }

    private static String buildValueString(Measurement measurement) {
        if(measurement.getType() == Measurement.Type.BOOL) {
            return Boolean.toString(measurement.getBoolVal());
        } else if(measurement.getType() == Measurement.Type.INT) {
            return Long.toString(measurement.getIntVal());
        } else if(measurement.getType() == Measurement.Type.DOUBLE) {
            return Double.toString(measurement.getDoubleVal());
        } else if(measurement.getType() == Measurement.Type.STRING) {
            return measurement.getStringVal();
        } else {
            return "";
        }
    }

}
