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
package io.greenbus.examples.alarms;

import io.greenbus.msg.japi.Session;
import io.greenbus.client.service.proto.EventRequests;
import io.greenbus.client.service.proto.Events;
import io.greenbus.client.service.proto.Events.Event;
import io.greenbus.japi.client.service.EventService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *  Example: Alarms
 *
 *
 */
public class AlarmsExample {

    /**
     * Get Active Alarms
     *
     * Simple query for
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getActiveAlarms(Session session) throws Exception {

        System.out.print("\n=== Active Alarms ===\n\n");

        // Get service interface for alarms
        final EventService.Client client = EventService.client(session);

        // Limit the number of objects returned to a manageable amount
        int limit = 5;

        // Call the alarm service to get a list of active alarms
        final EventRequests.AlarmQuery query = EventRequests.AlarmQuery.newBuilder()
                .addAlarmStates(Events.Alarm.State.UNACK_AUDIBLE)
                .addAlarmStates(Events.Alarm.State.UNACK_SILENT)
                .setPageSize(limit)
                .build();

        final List<Events.Alarm> alarmList = client.alarmQuery(query).get(5000, TimeUnit.MILLISECONDS);

        // Inspect the first Alarm
        Events.Alarm firstAlarm = alarmList.get(0);

        // Alarms are associated with a single Event
        Event firstEvent = firstAlarm.getEvent();

        // Display the properties of the Alarm and Event objects
        System.out.println("Alarm");
        System.out.println("-----------");
        System.out.println("Alarm Uid: " + firstAlarm.getId());
        System.out.println("State: " + firstAlarm.getState());
        System.out.println("Event Uid: " + firstEvent.getId());
        System.out.println("Agent: " + firstEvent.getAgentName());
        System.out.println("Type: " + firstEvent.getEventType());
        System.out.println("Severity: " + firstEvent.getSeverity());
        System.out.println("Subsystem: " + firstEvent.getSubsystem());
        System.out.println("Event Message: " + firstEvent.getRendered());
        System.out.println("Is Alarm: " + firstEvent.getAlarm());
        System.out.println("Time: " + new Date(firstEvent.getTime()));
        System.out.println("-----------\n");

        // List active Alarms
        for (Events.Alarm alarm : alarmList) {
            System.out.println("Alarm: " + alarm.getState() + ", " + alarm.getEvent().getRendered() + ", " + new Date(alarm.getEvent().getTime()).toString());
        }
    }

    /**
     * Alarm Lifecycle
     *
     * Demonstrates the lifecycle of an alarm. Alarms begin in the state UNACK_AUDIBLE or UNACK_SILENT,
     * are acknowledged by an operator and transition to the state ACKNOWLEDGED, and are removed to the
     * state REMOVED when no longer relevant.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void alarmLifecycle(Session session) throws Exception {

        System.out.print("\n=== Alarm Lifecycle ===\n\n");

        // Get service interface for alarms
        final EventService.Client client = EventService.client(session);

        // Get the first active alarm
        final EventRequests.AlarmQuery query = EventRequests.AlarmQuery.newBuilder()
                .addAlarmStates(Events.Alarm.State.UNACK_AUDIBLE)
                .addAlarmStates(Events.Alarm.State.UNACK_SILENT)
                .setPageSize(1)
                .build();

        final Events.Alarm alarm = client.alarmQuery(query)
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        System.out.println("Original: ");
        System.out.println("Alarm: " + alarm.getState() + ", " + alarm.getEvent().getRendered() + ", " + new Date(alarm.getEvent().getTime()).toString() + "\n");

        // Acknowledges alarm, changing state from UNACK_* to ACKNOWLEDGED
        final EventRequests.AlarmStateUpdate ackUpdate = EventRequests.AlarmStateUpdate.newBuilder()
                .setAlarmId(alarm.getId())
                .setAlarmState(Events.Alarm.State.ACKNOWLEDGED)
                .build();

        final Events.Alarm acked = client.putAlarmState(Arrays.asList(ackUpdate))
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        System.out.println("Acknowledged: ");
        System.out.println("Alarm: " + acked.getState() + ", " + acked.getEvent().getRendered() + ", " + new Date(acked.getEvent().getTime()).toString() + "\n");

        // Removes alarm, changing state from ACKNOWLEDGED to REMOVED
        final EventRequests.AlarmStateUpdate removeUpdate = EventRequests.AlarmStateUpdate.newBuilder()
                .setAlarmId(alarm.getId())
                .setAlarmState(Events.Alarm.State.REMOVED)
                .build();

        final Events.Alarm removed = client.putAlarmState(Arrays.asList(removeUpdate))
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        System.out.println("Removed: ");
        System.out.println("Alarm: " + removed.getState() + ", " + removed.getEvent().getRendered() + ", " + new Date(removed.getEvent().getTime()).toString() + "\n");
    }

}
