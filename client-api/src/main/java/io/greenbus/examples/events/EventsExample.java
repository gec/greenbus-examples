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

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Examples: Events
 *
 *
 */
public class EventsExample {

    /**
     * Get Recent Events
     *
     * Gets a list of the most recent events.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getRecentEvents(Session session) throws Exception {

        System.out.print("\n=== Recent Events ===\n\n");

        // Get service interface for events
        final EventService.Client client = EventService.client(session);

        // Get list of five most recent events
        final EventRequests.EventQuery query = EventRequests.EventQuery.newBuilder()
                .setPageSize(5)
                .build();

        final List<Events.Event> events = client.eventQuery(query).get(5000, TimeUnit.MILLISECONDS);

        // Inspect a single event
        if (events.size() == 0) {
            System.out.println("No events found");
            return;
        }

        Events.Event firstEvent = events.get(0);

        // Display properties of the event
        System.out.println("Event");
        System.out.println("-----------");
        System.out.println("Uid: " + firstEvent.getId());
        System.out.println("User: " + firstEvent.getAgentName());
        System.out.println("Type: " + firstEvent.getEventType());
        System.out.println("Severity: " + firstEvent.getSeverity());
        System.out.println("Subsystem: " + firstEvent.getSubsystem());
        System.out.println("Message: " + firstEvent.getRendered());
        System.out.println("Is Alarm: " + firstEvent.getAlarm());
        System.out.println("Time: " + new Date(firstEvent.getTime()));
        System.out.println("-----------\n");

        // Display list of events
        for (Events.Event event : events) {
            System.out.println("Event: " + event.getRendered() + ", " + new Date(event.getTime()).toString());
        }
    }

    /**
     * Get Recent Events by Type
     *
     * Narrows the recent events returned by a specific type.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getRecentEventsByType(Session session) throws Exception {

        System.out.print("\n=== Recent Events By Type ===\n\n");

        // Get service interface for events
        final EventService.Client client = EventService.client(session);

        // Get list of five most recent events
        final EventRequests.EventQuery query = EventRequests.EventQuery.newBuilder()
                .setQueryParams(
                        EventRequests.EventQueryParams.newBuilder()
                            .addEventType("System.UserLogin"))
                .setPageSize(5)
                .build();

        final List<Events.Event> events = client.eventQuery(query).get(5000, TimeUnit.MILLISECONDS);

        // Display list of events
        for (Events.Event event : events) {
            System.out.println("Event: " + event.getRendered() + ", " + new Date(event.getTime()).toString());
        }
    }

    /**
     * Search for Events by Severity
     *
     * Uses the EventSelect object to create an advanced event search. Searches for
     * events of severity five or higher.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void searchForEventsBySeverity(Session session) throws Exception {

        System.out.print("\n=== Search For Events By Severity ===\n\n");

        // Get service interface for events
        final EventService.Client client = EventService.client(session);

        // Get list of five most recent events
        final EventRequests.EventQuery query = EventRequests.EventQuery.newBuilder()
                .setQueryParams(
                        EventRequests.EventQueryParams.newBuilder()
                                .setSeverityOrHigher(5))
                .setPageSize(5)
                .build();

        final List<Events.Event> events = client.eventQuery(query).get(5000, TimeUnit.MILLISECONDS);

        // Display list of events
        for (Events.Event event : events) {
            System.out.println("Event: " + event.getRendered() + ", " + new Date(event.getTime()).toString());
        }
    }

    /**
     * Search for Events by Interval
     *
     * Uses the EventSelect object to create an advanced event search. Searches for
     * events which occurred between twenty minutes ago and five minutes ago.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void searchForEventsByInterval(Session session) throws Exception {

        System.out.print("\n=== Search For Events By Interval ===\n\n");

        // Get service interface for events
        final EventService.Client client = EventService.client(session);

        // Set start time to twenty minutes ago
        long twentyMinutesAgo = System.currentTimeMillis() - (20 * 60 * 1000);

        // Set end time to five minutes ago
        long fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000);

        // Get list of five most recent events
        final EventRequests.EventQuery query = EventRequests.EventQuery.newBuilder()
                .setQueryParams(
                        EventRequests.EventQueryParams.newBuilder()
                                .setTimeFrom(twentyMinutesAgo)
                                .setTimeTo(fiveMinutesAgo))
                .setPageSize(5)
                .build();

        final List<Events.Event> events = client.eventQuery(query).get(5000, TimeUnit.MILLISECONDS);

        // Display list of events
        for (Events.Event event : events) {
            System.out.println("Event: " + event.getRendered() + ", " + new Date(event.getTime()).toString());
        }

    }

}
