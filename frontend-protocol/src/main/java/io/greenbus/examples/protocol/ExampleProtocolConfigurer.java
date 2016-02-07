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

import io.greenbus.client.service.proto.Model;
import io.greenbus.japi.frontend.ProtocolConfigurer;

import java.util.List;

/**
 * Example of a configurer object that interprets key value objects, potentially
 * extracting a valid protocol configuration for an Endpoint.
 */
public class ExampleProtocolConfigurer implements ProtocolConfigurer<ExampleProtocolConfiguration> {

    /**
     * Evaluates the key values retrieved for an Endpoint in order to extract a protocol
     * configuration instance.
     *
     * Should return null if no valid protocol configuration can be found. A front-end connection
     * will not be added for a null configuration.
     *
     * @param endpoint Endpoint the configuration is associated with.
     * @param keyValues List of configuration objects retrieved from the services.
     * @return An instance of the protocol configuration, or null.
     */
    @Override
    public ExampleProtocolConfiguration evaluate(Model.Endpoint endpoint, List<Model.EntityKeyValue> keyValues) {

        for (Model.EntityKeyValue keyValue: keyValues) {
            System.out.println("Saw config file: " + keyValue.getKey());
        }

        return new ExampleProtocolConfiguration("stub data");
    }

    /**
     * Compares a new protocol configuration with a previous one to determine if updates, if they exist,
     * necessitate reloading the front-end connection.
     *
     * @param latest The latest protocol configuration received from the services.
     * @param previous The previous protocol configuration used to create a front-end connection
     * @return
     */
    @Override
    public boolean equivalent(ExampleProtocolConfiguration latest, ExampleProtocolConfiguration previous) {
        return latest.getStubData().equals(previous.getStubData());
    }
}
