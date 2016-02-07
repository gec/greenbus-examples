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
package io.greenbus.examples.subscriptions;

import io.greenbus.msg.japi.Session;
import io.greenbus.msg.japi.SubscriptionHandler;
import io.greenbus.msg.japi.SubscriptionResult;
import io.greenbus.client.service.proto.Measurements;
import io.greenbus.client.service.proto.Measurements.Measurement;
import io.greenbus.client.service.proto.Model;
import io.greenbus.client.service.proto.ModelRequests;
import io.greenbus.japi.client.service.MeasurementService;
import io.greenbus.japi.client.service.ModelService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Example: Subscriptions
 *
 */
public class SubscriptionsExample {

    /**
     * Implements the SubscriptionEventAcceptor, which provides a callback to be notified
     * of new subscription events, in this case for measurements.
     *
     */
    public static class MeasurementSubscriber implements SubscriptionHandler<Measurements.MeasurementNotification> {

        /**
         * Receives notifications when subscription events (measurements) happen
         * in the system.
         *
         * @param event
         */
        @Override
        public void handle(Measurements.MeasurementNotification event) {

            // Measurement associated with the event
            Measurement measurement = event.getValue();

            System.out.println("Measurement: " + event.getPointName() + ", " + buildValueString(measurement));
        }
    }

    /**
     * Subscribe to Measurements
     *
     * Subscribes to measurement updates for all points.
     *
     * @param session Logged-in Session object
     * @throws Exception
     * @throws InterruptedException
     */
    public static void subscribeToMeasurements(Session session) throws Exception  {

        System.out.print("\n=== Measurement Subscription ===\n\n");

        // Get service interface for points
        final ModelService.Client modelClient = ModelService.client(session);

        // Select four points to get the measurements
        final ModelRequests.PointQuery query = ModelRequests.PointQuery.newBuilder().build();

        final List<Model.Point> points = modelClient.pointQuery(query).get(5000, TimeUnit.MILLISECONDS);

        // Get service interface for measurements
        final MeasurementService.Client measurementClient = MeasurementService.client(session);

        // Get the latest measurements for the list of points
        final ArrayList<Model.ModelUUID> pointUuids = new ArrayList<Model.ModelUUID>();
        for (Model.Point point: points) {
            pointUuids.add(point.getUuid());
        }

        final SubscriptionResult<List<Measurements.PointMeasurementValue>, Measurements.MeasurementNotification> subscriptionResult = measurementClient.getCurrentValuesAndSubscribe(pointUuids).get(5000, TimeUnit.MILLISECONDS);

        // Display latest measurements for the points
        for (Measurements.PointMeasurementValue pointMeasurementValue : subscriptionResult.getResult()) {
            System.out.println("Measurement: " + pointMeasurementValue.getPointUuid().getValue() +
                    ", Value: " + buildValueString(pointMeasurementValue.getValue()) +
                    ", Time: " + new Date(pointMeasurementValue.getValue().getTime()));
        }

        final MeasurementSubscriber measurementSubscriber = new MeasurementSubscriber();

        subscriptionResult.getSubscription().start(measurementSubscriber);

        // Receive new measurements for fifteen seconds
        Thread.sleep(15 * 1000);

        // Cancel subscription to clean up resources in broker
        subscriptionResult.getSubscription().cancel();

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
