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
package io.greenbus.examples;

import com.google.common.util.concurrent.ListenableFuture;
import io.greenbus.japi.client.ServiceConnection;
import io.greenbus.japi.client.ServiceConnectionFactory;
import io.greenbus.msg.amqp.japi.AmqpSettings;
import io.greenbus.msg.japi.Session;
import io.greenbus.msg.qpid.QpidBroker;
import io.greenbus.examples.alarms.AlarmsExample;
import io.greenbus.examples.commands.CommandsExample;
import io.greenbus.examples.endpoints.EndpointsExample;
import io.greenbus.examples.entities.EntitiesExample;
import io.greenbus.examples.events.EventPublishingExample;
import io.greenbus.examples.events.EventsExample;
import io.greenbus.examples.keyvalues.KeyValuesExample;
import io.greenbus.examples.measurements.MeasurementHistoryExample;
import io.greenbus.examples.measurements.MeasurementsExample;
import io.greenbus.examples.points.PointsExample;
import io.greenbus.examples.subscriptions.SubscriptionsExample;
import io.greenbus.util.UserSettings;

import java.util.concurrent.TimeUnit;

public class Examples {
    /**
     * Java entry-point for running examples.
     *
     * Starts a client connection to GreenBus, logs in, and executes example code.
     * This is a "single shot" connection, if an application plans on running for extended periods it should use a
     * ConnectedApplicationManagers to be informed of the connection to the server is acquired or lost.
     *
     * @param args Command line arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // Load configuration files from paths provided in environment variables or in default locations
        final String configBaseDir = System.getProperty("io.greenbus.config.base", "");
        final String amqpConfigPath = System.getProperty("io.greenbus.config.amqp", configBaseDir + "io.greenbus.msg.amqp.cfg");
        final String userConfigPath = System.getProperty("io.greenbus.config.user", configBaseDir + "io.greenbus.user.cfg");

        // Load broker settings from config file
        final AmqpSettings amqpSettings = new AmqpSettings(amqpConfigPath);

        // Load user settings (login credentials) from config file
        final UserSettings userSettings = UserSettings.load(userConfigPath);

        // Create ServiceConnection to the Qpid broker
        final ServiceConnection connection = ServiceConnectionFactory.create(amqpSettings, QpidBroker.instance(), 10000);

        // Get a Session object that has a valid auth token. Causes a service call to login
        final ListenableFuture<Session> loginFuture = connection.login(userSettings.user(), userSettings.password());

        final Session session = loginFuture.get(5000, TimeUnit.MILLISECONDS);

        try {
            // Run Examples
            runAllExamples(session);
        } finally {

            // Disconnect from AMQP and shut down thread pools
            connection.disconnect();
        }

        System.exit(0);
    }

    public static void runAllExamples(Session session) throws Exception{

        AlarmsExample.getActiveAlarms(session);
        AlarmsExample.alarmLifecycle(session);

        CommandsExample.getCommands(session);
        CommandsExample.executionLock(session);
        CommandsExample.multipleExecutionLock(session);
        CommandsExample.commandBlocking(session);
        CommandsExample.executeControl(session);
        CommandsExample.executeSetpoint(session);

        KeyValuesExample.getKeyValues(session);
        KeyValuesExample.createUpdateRemove(session);

        EndpointsExample.getEndpoints(session);
        EndpointsExample.getConnectionStatuses(session);
        EndpointsExample.enableDisableEndpoint(session);

        EntitiesExample.getEntities(session);
        EntitiesExample.getByType(session);
        EntitiesExample.getImmediateChildren(session);

        EventsExample.getRecentEvents(session);
        EventsExample.getRecentEventsByType(session);
        EventsExample.searchForEventsBySeverity(session);
        EventsExample.searchForEventsByInterval(session);

        EventPublishingExample.publishEvent(session);
        EventPublishingExample.publishEventWithArguments(session);

        MeasurementsExample.getMeasurementByPoint(session);
        MeasurementsExample.getMultipleMeasurements(session);

        MeasurementHistoryExample.getMeasurementHistory(session);
        MeasurementHistoryExample.getMeasurementHistorySince(session);
        MeasurementHistoryExample.getMeasurementHistoryInterval(session);

        PointsExample.getPoints(session);
        PointsExample.getPointByName(session);
        PointsExample.getPointByUuid(session);

        SubscriptionsExample.subscribeToMeasurements(session);
    }
}
