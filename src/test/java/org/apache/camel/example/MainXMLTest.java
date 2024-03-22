/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.example;

import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.main.MainConfigurationProperties;
import org.apache.camel.test.main.junit5.CamelMainTestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A unit test checking that Camel supports XML routes.
 */
class MainXMLTest extends CamelMainTestSupport {

    @Test
    void should_support_xml_routes() {
        NotifyBuilder notify = new NotifyBuilder(context).whenCompleted(1).whenBodiesDone("Bye World").create();
        assertTrue(
            notify.matches(20, TimeUnit.SECONDS), "1 message should be completed"
        );
    }

    @Override
    protected void configure(MainConfigurationProperties configuration) {
        configuration.addConfiguration(MyConfiguration.class);
        configuration.withRoutesIncludePattern("routes/*.xml");
    }

    @Test
    public void normalCompletion() {
        final ProducerTemplate producerTemplate = context.createProducerTemplate();

        final Exchange resultExchange = producerTemplate.send("direct:start", exchange -> {
            exchange.getIn().setBody("start");
            exchange.setVariable("nextRoute", "normalCompletion");
        });

        Assertions.assertEquals("normalCompletion", resultExchange.getVariable("targetVar"));
        Assertions.assertEquals("after toD", resultExchange.getMessage().getBody(String.class));
    }

    @Test
    public void withOnException() {
        final ProducerTemplate producerTemplate = context.createProducerTemplate();

        final Exchange resultExchange = producerTemplate.send("direct:start", exchange -> {
            exchange.getIn().setBody("start");
            exchange.setVariable("nextRoute", "withOnException");
        });

        Assertions.assertNull(resultExchange.getVariable("targetVar"));
        Assertions.assertEquals("onException", resultExchange.getMessage().getBody(String.class));
    }

    @Test
    public void withStop() {
        final ProducerTemplate producerTemplate = context.createProducerTemplate();

        final Exchange resultExchange = producerTemplate.send("direct:start", exchange -> {
            exchange.getIn().setBody("start");
            exchange.setVariable("nextRoute", "withStop");
        });

        Assertions.assertNull(resultExchange.getVariable("targetVar"));
        Assertions.assertEquals("stop", resultExchange.getMessage().getBody(String.class));
    }

    @Test
    public void withOnExceptionContinued() {
        final ProducerTemplate producerTemplate = context.createProducerTemplate();

        final Exchange resultExchange = producerTemplate.send("direct:start", exchange -> {
            exchange.getIn().setBody("start");
            exchange.setVariable("nextRoute", "withOnExceptionContinued");
        });

        Assertions.assertEquals("onExceptionContinued", resultExchange.getVariable("targetVar"));
        Assertions.assertEquals("after toD", resultExchange.getMessage().getBody(String.class));
    }

    @Test
    public void withUncaughtException() {
        final ProducerTemplate producerTemplate = context.createProducerTemplate();

        final Exchange resultExchange = producerTemplate.send("direct:start", exchange -> {
            exchange.getIn().setBody("start");
            exchange.setVariable("nextRoute", "withUncaughtException");
        });

        Assertions.assertNull(resultExchange.getVariable("targetVar"));
        Assertions.assertEquals("uncaughtException", resultExchange.getMessage().getBody(String.class));
    }

}
