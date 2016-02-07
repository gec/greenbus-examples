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
import io.greenbus.client.service.proto.MeasurementRequests;
import io.greenbus.client.service.proto.Measurements;
import io.greenbus.client.service.proto.Measurements.Measurement;
import io.greenbus.client.service.proto.Model;
import io.greenbus.client.service.proto.ModelRequests;
import io.greenbus.japi.client.service.MeasurementService;
import io.greenbus.japi.client.service.ModelService;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Example: Measurement History
 *
 *
 */
public class MeasurementHistoryExample {

    /**
     * Get Measurement History
     *
     * Gets five most recent measurements for a point.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getMeasurementHistory(Session session) throws Exception {

        System.out.print("\n=== Measurement History ===\n\n");

        // Get service interface for points
        final ModelService.Client frontEndClient = ModelService.client(session);

        // Select a specific point
        final Model.Point point = frontEndClient.pointQuery(ModelRequests.PointQuery.newBuilder()
                .setPagingParams(
                        ModelRequests.EntityPagingParams.newBuilder()
                                .setPageSize(1)).build())
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        // Get service interface for measurements
        final MeasurementService.Client measurementClient = MeasurementService.client(session);

        // Limit the results to five; there are a potentially large number of measurements in the history
        int limit = 5;

        // Retrieve a list of the last five measurements for the point
        final MeasurementRequests.MeasurementHistoryQuery historyQuery = MeasurementRequests.MeasurementHistoryQuery.newBuilder()
                .setPointUuid(point.getUuid())
                .setLimit(limit)
                .build();

        final Measurements.PointMeasurementValues pointMeasurementValues = measurementClient.getHistory(historyQuery).get(5000, TimeUnit.MILLISECONDS);

        // Display measurement history
        for (Measurement measurement : pointMeasurementValues.getValueList()) {
            System.out.println("Measurement: " + pointMeasurementValues.getPointUuid().getValue() +
                    ", Value: " + buildValueString(measurement) +
                    ", Time: " + new Date(measurement.getTime()));
        }

    }

    /**
     * Get Measurement History Since
     *
     * Gets measurement history for the last five minutes (limited to five results).
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getMeasurementHistorySince(Session session) throws Exception {

        System.out.print("\n=== Measurement History (Last 5 Minutes) ===\n\n");

        // Get service interface for points
        final ModelService.Client frontEndClient = ModelService.client(session);

        // Select a specific point
        final Model.Point point = frontEndClient.pointQuery(ModelRequests.PointQuery.newBuilder()
                .setPagingParams(
                        ModelRequests.EntityPagingParams.newBuilder()
                                .setPageSize(1)).build())
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        // Get service interface for measurements
        final MeasurementService.Client measurementClient = MeasurementService.client(session);

        // Specify the time as five minutes ago
        long fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000);

        // Limit the results to five; there are a potentially large number of measurements in the history
        int limit = 5;

        // Retrieve a list of the last five measurements for the point
        final MeasurementRequests.MeasurementHistoryQuery historyQuery = MeasurementRequests.MeasurementHistoryQuery.newBuilder()
                .setPointUuid(point.getUuid())
                .setTimeTo(fiveMinutesAgo)
                .setLimit(limit)
                .build();

        final Measurements.PointMeasurementValues pointMeasurementValues = measurementClient.getHistory(historyQuery).get(5000, TimeUnit.MILLISECONDS);

        // Display measurement history
        for (Measurement measurement : pointMeasurementValues.getValueList()) {
            System.out.println("Measurement: " + pointMeasurementValues.getPointUuid().getValue() +
                    ", Value: " + buildValueString(measurement) +
                    ", Time: " + new Date(measurement.getTime()));
        }

    }

    /**
     * Get Measurement History Interval
     *
     * Gets measurement history for the time period of twenty minutes ago to five minutes ago (limited to ten results).
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getMeasurementHistoryInterval(Session session) throws Exception {

        System.out.print("\n=== Measurement History (Interval: 20 Minutes Ago to 5 Minutes Ago) ===\n\n");

        // Get service interface for points
        final ModelService.Client frontEndClient = ModelService.client(session);

        // Select a specific point
        final Model.Point point = frontEndClient.pointQuery(ModelRequests.PointQuery.newBuilder()
                .setPagingParams(
                        ModelRequests.EntityPagingParams.newBuilder()
                                .setPageSize(1)).build())
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        // Get service interface for measurements
        final MeasurementService.Client measurementClient = MeasurementService.client(session);

        // Specify the start time as twenty minutes ago
        long twentyMinutesAgo = System.currentTimeMillis() - (20 * 60 * 1000);

        // Specify the end time as five minutes ago
        long fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000);

        // Specify that the newest measurements in the interval should be returned
        boolean returnNewest = true;

        // Limit the results to five; there are a potentially large number of measurements in the history
        int limit = 5;

        // Retrieve a list of the last five measurements for the point
        final MeasurementRequests.MeasurementHistoryQuery historyQuery = MeasurementRequests.MeasurementHistoryQuery.newBuilder()
                .setPointUuid(point.getUuid())
                .setTimeFrom(twentyMinutesAgo)
                .setTimeTo(fiveMinutesAgo)
                .setLatest(returnNewest)
                .setLimit(limit)
                .build();

        final Measurements.PointMeasurementValues pointMeasurementValues = measurementClient.getHistory(historyQuery).get(5000, TimeUnit.MILLISECONDS);

        // Display measurement history
        for (Measurement measurement : pointMeasurementValues.getValueList()) {
            System.out.println("Measurement: " + pointMeasurementValues.getPointUuid().getValue() +
                    ", Value: " + buildValueString(measurement) +
                    ", Time: " + new Date(measurement.getTime()));
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
