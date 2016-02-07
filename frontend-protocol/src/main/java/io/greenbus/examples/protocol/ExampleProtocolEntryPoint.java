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

import io.greenbus.app.actor.EndpointCollectionStrategy;
import io.greenbus.japi.frontend.EndpointCollectionStrategyFactory;
import io.greenbus.japi.frontend.FrontendProtocolManager;

import java.util.Arrays;
import java.util.List;

/**
 *  Entry point for a front-end protocol implementation.
 *
 *  Instantiates a FrontEndProtocolManager with an interface to the custom protocol
 *  implementation.
 */
public class ExampleProtocolEntryPoint {

    public static void main(String[] args) throws Exception {

        // Load configuration files from paths provided in environment variables or in default locations
        final String configBaseDir = System.getProperty("io.greenbus.config.base", "");
        final String amqpConfigPath = System.getProperty("io.greenbus.config.amqp", configBaseDir + "io.greenbus.msg.amqp.cfg");
        final String userConfigPath = System.getProperty("io.greenbus.config.user", configBaseDir + "io.greenbus.user.cfg");

        // Provide service for all endpoints with the protocol "example-protocol"
        final EndpointCollectionStrategy endpointSelectionStrategy = EndpointCollectionStrategyFactory.protocolStrategy(Arrays.asList("example-protocol"), null);

        // Provide an implementation that reads configuration from the system into configuration specific to this protocol
        final ExampleProtocolConfigurer exampleProtocolConfigurer = new ExampleProtocolConfigurer();

        // Provide an implementation of a manager of protocol instances
        final ExampleProtocolMaster exampleProtocolMaster = new ExampleProtocolMaster();

        // Specify keys for the key-values that represent the protocol configuration
        List<String> protocolConfigKeys = Arrays.asList("protocolConfig");

        // Instantiate the management library for front-end protocols.
        final FrontendProtocolManager<ExampleProtocolConfiguration> protocolManager = new FrontendProtocolManager<ExampleProtocolConfiguration>(
                exampleProtocolMaster,
                exampleProtocolConfigurer,
                protocolConfigKeys,
                endpointSelectionStrategy,
                amqpConfigPath,
                userConfigPath);

        // Start the management library; returns immediately
        protocolManager.start();

        System.out.println("Press any key to quit...");
        System.in.read();

        // Shutdown all protocols and the connection to the services
        protocolManager.shutdown();

    }
}
