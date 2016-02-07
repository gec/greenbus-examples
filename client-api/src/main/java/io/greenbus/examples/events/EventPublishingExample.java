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
package io.greenbus.examples.events;

import io.greenbus.msg.japi.Session;
import io.greenbus.client.service.proto.EventRequests;
import io.greenbus.client.service.proto.Events;
import io.greenbus.japi.client.service.EventService;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Examples: Event Publishing
 *
 */
public class EventPublishingExample {

    /**
     * Publish Event
     *
     * Publish a user login event.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void publishEvent(Session session) throws Exception {

        System.out.print("\n=== Publish Event ===\n\n");

        // Get service interface for events
        final EventService.Client client = EventService.client(session);

        // Set event type to user login
        String eventType = "System.UserLogin";

        // Set subsystem to generic "system"
        String subsystem = "system";

        final EventRequests.EventTemplate eventTemplate = EventRequests.EventTemplate.newBuilder()
                .setEventType(eventType)
                .setSubsystem(subsystem)
                .build();

        final Events.Event published = client.postEvents(Arrays.asList(eventTemplate))
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        // Display properties of published Event
        System.out.println("Event");
        System.out.println("-----------");
        System.out.println("Uid: " + published.getId());
        System.out.println("User: " + published.getAgentName());
        System.out.println("Type: " + published.getEventType());
        System.out.println("Severity: " + published.getSeverity());
        System.out.println("Subsystem: " + published.getSubsystem());
        System.out.println("Message: " + published.getRendered());
        System.out.println("Is Alarm: " + published.getAlarm());
        System.out.println("Time: " + new Date(published.getTime()));
        System.out.println("-----------\n");
    }

    /**
     * Publish Event
     *
     * Publish a user login event with arguments. Arguments are used to provide event-specific
     * details. Their structure is determined by the event type.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void publishEventWithArguments(Session session) throws Exception {

        System.out.print("\n=== Publish Event With Arguments ===\n\n");

        // Get service interface for events
        final EventService.Client client = EventService.client(session);

        // Set event type to user login
        String eventType = "System.UserLogin";

        // Set subsystem to generic "system"
        String subsystem = "system";

        final EventRequests.EventTemplate eventTemplate = EventRequests.EventTemplate.newBuilder()
                .setEventType(eventType)
                .setSubsystem(subsystem)
                .addArgs(Events.Attribute.newBuilder()
                        .setName("status")
                        .setValueString("StatusArg"))
                .addArgs(Events.Attribute.newBuilder()
                        .setName("reason")
                        .setValueString("ReasonArg"))
                .build();

        final Events.Event published = client.postEvents(Arrays.asList(eventTemplate))
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        // Display properties of published Event
        System.out.println("Event");
        System.out.println("-----------");
        System.out.println("Uid: " + published.getId());
        System.out.println("User: " + published.getAgentName());
        System.out.println("Type: " + published.getEventType());
        System.out.println("Severity: " + published.getSeverity());
        System.out.println("Subsystem: " + published.getSubsystem());
        System.out.println("Message: " + published.getRendered());
        System.out.println("Is Alarm: " + published.getAlarm());
        System.out.println("Time: " + new Date(published.getTime()));
        System.out.println("-----------\n");

    }

}
