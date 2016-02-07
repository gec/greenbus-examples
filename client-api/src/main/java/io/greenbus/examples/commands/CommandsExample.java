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
package io.greenbus.examples.commands;

import io.greenbus.msg.japi.Session;
import io.greenbus.client.service.proto.CommandRequests;
import io.greenbus.client.service.proto.Commands;
import io.greenbus.client.service.proto.Model;
import io.greenbus.client.service.proto.ModelRequests;
import io.greenbus.japi.client.service.CommandService;
import io.greenbus.japi.client.service.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Example: Commands
 *
 *
 */
public class CommandsExample {

    /**
     * Get Commands
     *
     * Retrieves a list of commands configured in the system.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void getCommands(Session session) throws Exception {

        System.out.print("\n=== Get Commands ===\n\n");

        // Get service interface for commands
        final ModelService.Client modelClient = ModelService.client(session);

        // Get full list of Command objects in the system
        List<Model.Command> commandList = modelClient.commandQuery(ModelRequests.CommandQuery.newBuilder().build()).get(5000, TimeUnit.MILLISECONDS);

        // Inspect the first Command object
        Model.Command command = commandList.get(0);

        // Display properties of the Command object
        System.out.println("Command");
        System.out.println("-----------");
        System.out.println("Uuid: " + command.getUuid().getValue());
        System.out.println("Name: " + command.getName());
        System.out.println("Display name: " + command.getDisplayName());
        System.out.println("Command category: " + command.getCommandCategory());
        System.out.println("-----------\n");

        // List the Command objects
        for (Model.Command cmd : commandList) {
            System.out.println("Command: " + cmd.getName());
        }
    }

    /**
     * Execution Lock
     *
     * Before executing a command, agents must acquire exclusive access to prevent
     * simultaneous executions from other agents. This example acquires a lock
     * for a single Command.
     *
     * Execution locks are CommandLock objects with the mode "ALLOWED"
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void executionLock(Session session) throws Exception {

        System.out.print("\n=== Execution Lock ===\n\n");

        // Get service interface for commands
        final ModelService.Client modelClient = ModelService.client(session);
        final CommandService.Client commandClient = CommandService.client(session);

        // Get a single Command object
        Model.Command command = modelClient.commandQuery(ModelRequests.CommandQuery.newBuilder().build())
                .get(5000, TimeUnit.MILLISECONDS)
                .get(0);

        // Create a command execution lock for the Command object
        // CommandLock objects describe executions locks
        final CommandRequests.CommandSelect commandSelect = CommandRequests.CommandSelect.newBuilder()
                .addCommandUuids(command.getUuid())
                .build();

        final Commands.CommandLock commandLock = commandClient.selectCommands(commandSelect)
                .get(5000, TimeUnit.MILLISECONDS);

        // Display the properties of the CommandLock object
        System.out.println("Command Access");
        System.out.println("-----------");
        System.out.println("Id: " + commandLock.getId());
        System.out.println("Agent uuid: " + commandLock.getAgentUuid().getValue());
        System.out.println("Access: " + commandLock.getAccess());
        System.out.println("Expire Time: " + new Date(commandLock.getExpireTime()));

        for (Model.ModelUUID cmdUuid : commandLock.getCommandUuidsList()) {
            System.out.println("Command: " + cmdUuid.getValue());
        }
        System.out.println("-----------\n");

        // Remove the command lock from the system, cleaning up
        commandClient.deleteCommandLocks(Arrays.asList(commandLock.getId())).get(5000, TimeUnit.MILLISECONDS);
    }

    /**
     * Multiple Execution Lock
     *
     * Locks may be acquired for multiple Commands to ensure exclusive access during operations
     * across different objects. This example acquires a lock for three Commands.
     *
     * Execution locks are CommandLock objects with the mode "ALLOWED"
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void multipleExecutionLock(Session session) throws Exception {

        System.out.print("\n=== Multiple Execution Lock ===\n\n");

        // Get service interface for commands
        final ModelService.Client modelClient = ModelService.client(session);
        final CommandService.Client commandClient = CommandService.client(session);

        // Get three Command objects
        final List<Model.Command> commandList = modelClient.commandQuery(ModelRequests.CommandQuery.newBuilder()
                .setPagingParams(
                        ModelRequests.EntityPagingParams.newBuilder()
                                .setPageSize(3)
                                .build())
                .build())
                .get(5000, TimeUnit.MILLISECONDS)
                .subList(0, 3);

        // Create a command execution lock for the Command objects
        // CommandLock objects describe executions locks
        System.out.print("Locking commands: ");
        final List<Model.ModelUUID> commandUuids = new ArrayList<Model.ModelUUID>();
        for (Model.Command command: commandList) {
            commandUuids.add(command.getUuid());
            System.out.print(command.getName() + " ");
        }
        System.out.print("\n\n");

        final CommandRequests.CommandSelect commandSelect = CommandRequests.CommandSelect.newBuilder()
                .addAllCommandUuids(commandUuids)
                .build();

        final Commands.CommandLock commandLock = commandClient.selectCommands(commandSelect).get(5000, TimeUnit.MILLISECONDS);

        // Display the properties of the CommandLock object
        System.out.println("Command Access");
        System.out.println("-----------");
        System.out.println("Id: " + commandLock.getId());
        System.out.println("Agent uuid: " + commandLock.getAgentUuid().getValue());
        System.out.println("Access: " + commandLock.getAccess());
        System.out.println("Expire Time: " + new Date(commandLock.getExpireTime()));

        for (Model.ModelUUID cmdUuid : commandLock.getCommandUuidsList()) {
            System.out.println("Command: " + cmdUuid.getValue());
        }
        System.out.println("-----------\n");

        // Remove the command lock from the system, cleaning up
        commandClient.deleteCommandLocks(Arrays.asList(commandLock.getId())).get(5000, TimeUnit.MILLISECONDS);
    }

    /**
     * Command Blocking
     *
     * Command denial locks prevent all command execution. This examples acquires a denial lock
     * for three Commands.
     *
     * Execution locks are CommandLock objects with the mode "BLOCKED"
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void commandBlocking(Session session) throws Exception {

        System.out.print("\n=== Execution Denial Lock ===\n\n");

        // Get service interface for commands
        final ModelService.Client modelClient = ModelService.client(session);
        final CommandService.Client commandClient = CommandService.client(session);

        // Get three Command objects
        final List<Model.Command> commandList = modelClient.commandQuery(ModelRequests.CommandQuery.newBuilder()
                .setPagingParams(
                ModelRequests.EntityPagingParams.newBuilder()
                        .setPageSize(3)
                        .build()).build())
                .get(5000, TimeUnit.MILLISECONDS)
                .subList(0, 3);

        // Create a command block lock for the Command objects
        // CommandLock objects describe execution locks
        System.out.print("Locking commands: ");
        final List<Model.ModelUUID> commandUuids = new ArrayList<Model.ModelUUID>();
        for (Model.Command command: commandList) {
            commandUuids.add(command.getUuid());
            System.out.print(command.getName() + " ");
        }
        System.out.print("\n\n");

        final CommandRequests.CommandBlock commandBlock = CommandRequests.CommandBlock.newBuilder()
                .addAllCommandUuids(commandUuids)
                .build();

        final Commands.CommandLock commandLock = commandClient.blockCommands(commandBlock).get(5000, TimeUnit.MILLISECONDS);

        // Display the properties of the CommandLock object
        System.out.println("Command Access");
        System.out.println("-----------");
        System.out.println("Id: " + commandLock.getId());
        System.out.println("Agent uuid: " + commandLock.getAgentUuid().getValue());
        System.out.println("Access: " + commandLock.getAccess());
        System.out.println("Expire Time: " + new Date(commandLock.getExpireTime()));

        for (Model.ModelUUID cmdUuid : commandLock.getCommandUuidsList()) {
            System.out.println("Command: " + cmdUuid.getValue());
        }
        System.out.println("-----------\n");

        // Remove the command lock from the system, cleaning up
        commandClient.deleteCommandLocks(Arrays.asList(commandLock.getId())).get(5000, TimeUnit.MILLISECONDS);
    }

    /**
     * Execute Control
     *
     * Controls are Commands that do not have a value associated with them.
     * The procedure is to acquire an execution lock, then execute the control.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void executeControl(Session session) throws Exception {

        System.out.print("\n=== Execute Control ===\n\n");

        // Get service interface for commands
        final ModelService.Client modelClient = ModelService.client(session);
        final CommandService.Client commandClient = CommandService.client(session);

        // Get three Command objects
        final ModelRequests.CommandQuery commandQuery = ModelRequests.CommandQuery.newBuilder()
                .addCommandCategories(Model.CommandCategory.CONTROL)
                .setPagingParams(
                        ModelRequests.EntityPagingParams.newBuilder()
                                .setPageSize(3)
                                .build())
                .build();

        final List<Model.Command> commandList = modelClient.commandQuery(commandQuery)
                .get(5000, TimeUnit.MILLISECONDS);

        if (commandList.size() == 0) {
            System.out.println("No controls configured.");
            return;
        }

        final Model.Command command = commandList.get(0);

        // Create a command execution lock for the Command object
        // CommandLock objects describe executions locks
        final CommandRequests.CommandSelect commandSelect = CommandRequests.CommandSelect.newBuilder()
                .addCommandUuids(command.getUuid())
                .build();

        final Commands.CommandLock commandLock = commandClient.selectCommands(commandSelect)
                .get(5000, TimeUnit.MILLISECONDS);

        System.out.println("Command access: " + commandLock.getAccess());

        // Execute the control. The CommandStatus enumeration describes the result of the
        // execution ("SUCCESS" if successful)
        final Commands.CommandRequest commandRequest = Commands.CommandRequest.newBuilder()
                .setCommandUuid(command.getUuid())
                .build();

        try {

            final Commands.CommandResult commandResult = commandClient.issueCommandRequest(commandRequest).get(5000, TimeUnit.MILLISECONDS);

            System.out.println("Command result: " + commandResult.getStatus());

        } catch (TimeoutException ex) {

            System.out.println("Command timed out. Front-end may not be connected.");

        }


        // Remove the command lock from the system, cleaning up
        commandClient.deleteCommandLocks(Arrays.asList(commandLock.getId())).get(5000, TimeUnit.MILLISECONDS);
    }

    /**
     * Execute Setpoint
     *
     * Setpoints are Commands that have a value associated with them.
     * The procedure is to acquire an execution lock, then execute the setpoint.
     *
     * @param session Logged-in Session object
     * @throws Exception
     */
    public static void executeSetpoint(Session session) throws Exception {

        System.out.print("\n=== Execute Setpoint ===\n\n");

        // Get service interface for commands
        final ModelService.Client modelClient = ModelService.client(session);
        final CommandService.Client commandClient = CommandService.client(session);

        // Get three Command objects
        final ModelRequests.CommandQuery commandQuery = ModelRequests.CommandQuery.newBuilder()
                .addCommandCategories(Model.CommandCategory.SETPOINT_DOUBLE)
                .setPagingParams(
                        ModelRequests.EntityPagingParams.newBuilder()
                                .setPageSize(3)
                                .build())
                .build();

        final List<Model.Command> commandList = modelClient.commandQuery(commandQuery)
                .get(5000, TimeUnit.MILLISECONDS);

        if (commandList.size() == 0) {
            System.out.println("No setpoints configured.");
            return;
        }

        final Model.Command command = commandList.get(0);

        // Create a command execution lock for the Command object
        // CommandLock objects describe executions locks
        final CommandRequests.CommandSelect commandSelect = CommandRequests.CommandSelect.newBuilder()
                .addCommandUuids(command.getUuid())
                .build();

        final Commands.CommandLock commandLock = commandClient.selectCommands(commandSelect)
                .get(5000, TimeUnit.MILLISECONDS);

        System.out.println("Command access: " + commandLock.getAccess());

        // Execute the control. The CommandStatus enumeration describes the result of the
        // execution ("SUCCESS" if successful)
        final Commands.CommandRequest commandRequest = Commands.CommandRequest.newBuilder()
                .setCommandUuid(command.getUuid())
                .setType(Commands.CommandRequest.ValType.DOUBLE)
                .setDoubleVal(35.323)
                .build();

        try {

            final Commands.CommandResult commandResult = commandClient.issueCommandRequest(commandRequest).get(5000, TimeUnit.MILLISECONDS);

            System.out.println("Command result: " + commandResult.getStatus());

        } catch (TimeoutException ex) {

            System.out.println("Command timed out. Front-end may not be connected.");

        }

        // Remove the command lock from the system, cleaning up
        commandClient.deleteCommandLocks(Arrays.asList(commandLock.getId())).get(5000, TimeUnit.MILLISECONDS);
    }

}
