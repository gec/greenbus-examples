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
package io.greenbus.examples.protocol;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.greenbus.client.service.proto.Commands;
import io.greenbus.client.service.proto.FrontEnd;
import io.greenbus.client.service.proto.Model;
import io.greenbus.japi.frontend.MasterProtocol;
import io.greenbus.japi.frontend.ProtocolCommandAcceptor;
import io.greenbus.japi.frontend.ProtocolUpdater;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An implementation of the interface between a particular front-end protocol and the library that manages
 * the connection to the services and retrieves the configuration for Endpoints. The library calls the
 * add() and remove() methods to inform the protocol implementation which Endpoints should have front-end
 * connections.
 *
 * For this example we store the protocol configuration parameters.
 *
 * add(), remove(), and shutdown() are guaranteed to be called by a single thread.
 *
 */
public class ExampleProtocolMaster implements MasterProtocol<ExampleProtocolConfiguration> {

    private final Map<Model.ModelUUID, ProtocolInstance> instanceMap = new ConcurrentHashMap<Model.ModelUUID, ProtocolInstance>();

    /**
     * Called by the library to notify user code that service is requested for a particular Endpoint.
     *
     * A protocol implementation should initialize the front-end connection when it receives this callback.
     *
     * The ProtocolUpdater interface can be used to push measurement and communications status updates to
     * the system.
     *
     * The establishment of a connection should be followed by a COMMS_UP FrontEndConnectionStatus.Status
     * notification, loss of a connection with COMMS_DOWN, and errors with ERROR.
     *
     * The implementation of the method returns an instance of the ProtocolCommandAcceptor interface, which
     * provides the library with a callback for forwarding command requests to the protocol implementation.
     *
     * @param endpoint Endpoint the front-end connection is for.
     * @param exampleProtocolConfiguration The assembled configuration for the protocol.
     * @param updater An interface for the front-end to notify the library of measurement and status updates.
     * @return An interface for the underlying library to notify the front-end of command requests.
     */
    @Override
    public ProtocolCommandAcceptor add(Model.Endpoint endpoint, ExampleProtocolConfiguration exampleProtocolConfiguration, ProtocolUpdater updater) {

        System.out.println("Adding protocol master for Endpoint " + endpoint.getName());

        // Update the front-end connection's status to COMMS_UP
        updater.updateStatus(FrontEnd.FrontEndConnectionStatus.Status.COMMS_UP);

        // Build a command acceptor to be passed back to the library
        final ExampleProtocolCommandAcceptor commandAcceptor = new ExampleProtocolCommandAcceptor(endpoint.getName());

        // Store the front-end connection's variables for later reference
        final ProtocolInstance protocolInstance = new ProtocolInstance(endpoint, exampleProtocolConfiguration, updater, commandAcceptor);

        instanceMap.put(endpoint.getUuid(), protocolInstance);

        // Return the command acceptor
        return commandAcceptor;
    }

    /**
     * Called by the library to notify user code that service should no longer be provided for a particular Endpoint.
     *
     * A protocol implementation can shutdown the front-end connection and cleanup any resources here.
     *
     * @param endpointUuid UUID of the Endpoint service should no longer be provided for.
     */
    @Override
    public void remove(Model.ModelUUID endpointUuid) {

        final ProtocolInstance instance = instanceMap.remove(endpointUuid);
        if (instance != null) {
            System.out.println("Removed protocol master for endpoint " + instance.getEndpoint().getName());
        }
    }

    /**
     * Called when the entire system is shutting down. All front-end connections should be closed and cleaned up.
     */
    @Override
    public void shutdown() {
        System.out.println("Shutdown called on ExampleProtocolMaster");
    }

    /**
     *  An implementation of the ProtocolCommandAcceptor, this provides the library with a callback for
     *  forwarding command requests to the protocol implementation.
     */
    public static class ExampleProtocolCommandAcceptor implements ProtocolCommandAcceptor {
        private final String endpointName;

        public ExampleProtocolCommandAcceptor(String endpointName) {
            this.endpointName = endpointName;
        }

        // A counter for the number of command requests we've handled.
        private final AtomicLong commandCount = new AtomicLong(0);

        /**
         * Notifies the protocol implementation of command requests.
         *
         * @param commandName Name of the Command the request applies to.
         * @param request The CommandRequest object the protocol implementation should handle.
         * @return A future that can be used by the library to receive the result of the command request when it arrives.
         */
        @Override
        public ListenableFuture<Commands.CommandResult> issue(String commandName, Commands.CommandRequest request) {

            System.out.println("Endpoint " + endpointName + " saw a command request for " + commandName);
            System.out.println("Endpoint " + endpointName + " has handled " + commandCount.incrementAndGet() + " command requests");

            // The CommandResult message is used to communicate the status of the command request, potentially with
            // an additional error message.
            final Commands.CommandResult result = Commands.CommandResult.newBuilder()
                    .setStatus(Commands.CommandStatus.SUCCESS)
                    .build();

            // For this example, we return an immediately completed future with the result.
            return Futures.immediateFuture(result);
        }
    }

    public static class ProtocolInstance {
        private final Model.Endpoint endpoint;
        private final ExampleProtocolConfiguration configuration;
        private final ProtocolUpdater updater;
        private final ExampleProtocolCommandAcceptor commandAcceptor;

        public ProtocolInstance(Model.Endpoint endpoint, ExampleProtocolConfiguration configuration, ProtocolUpdater updater, ExampleProtocolCommandAcceptor commandAcceptor) {
            this.endpoint = endpoint;
            this.configuration = configuration;
            this.updater = updater;
            this.commandAcceptor = commandAcceptor;
        }

        public Model.Endpoint getEndpoint() {
            return endpoint;
        }

        public ExampleProtocolConfiguration getConfiguration() {
            return configuration;
        }

        public ProtocolUpdater getUpdater() {
            return updater;
        }

        public ExampleProtocolCommandAcceptor getCommandAcceptor() {
            return commandAcceptor;
        }
    }
}
