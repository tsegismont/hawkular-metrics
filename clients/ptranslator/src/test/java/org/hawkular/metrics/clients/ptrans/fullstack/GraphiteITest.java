/*
 * Copyright 2014-2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hawkular.metrics.clients.ptrans.fullstack;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.hawkular.metrics.clients.ptrans.ConfigurationKey;
import org.hawkular.metrics.clients.ptrans.Service;
import org.hawkular.metrics.clients.ptrans.data.Point;
import org.jmxtrans.embedded.EmbeddedJmxTrans;
import org.jmxtrans.embedded.QueryResult;
import org.jmxtrans.embedded.config.ConfigurationParser;
import org.jmxtrans.embedded.output.AbstractOutputWriter;
import org.junit.After;

/**
 * @author Thomas Segismont
 */
public class GraphiteITest extends FullStackITest {

    private EmbeddedJmxTrans embeddedJmxTrans;

    @Override
    protected void configureSource() throws Exception {
        ConfigurationParser configurationParser = new ConfigurationParser();
        embeddedJmxTrans = configurationParser.newEmbeddedJmxTrans("classpath:jmxtrans.json");
    }

    @Override
    protected void changePTransConfig(Properties properties) {
        properties.setProperty(ConfigurationKey.SERVICES.toString(), Service.GRAPHITE.getExternalForm());
    }

    @Override
    protected void startSource() throws Exception {
        embeddedJmxTrans.start();
    }

    @Override
    protected void waitForSourceValues() throws Exception {
        do {
            Thread.sleep(MILLISECONDS.convert(1, SECONDS));
        } while (ListAppenderWriter.results.size() < 10);
    }

    @Override
    protected void stopSource() throws Exception {
        embeddedJmxTrans.stop();
    }

    @Override
    protected List<Point> getExpectedData() {
        List<QueryResult> results = ListAppenderWriter.results;
        return results.stream()
                      .map(this::queryResultToPoint)
                      .collect(toList());
    }

    private Point queryResultToPoint(QueryResult result) {
        return new Point(
                result.getName(),
                MILLISECONDS.convert(result.getEpoch(SECONDS), SECONDS),
                Double.valueOf(String.valueOf(result.getValue()))
        );
    }

    @Override
    protected void checkTimestamps(String failureMsg, long expected, long actual) {
        assertEquals(failureMsg, expected, actual);
    }

    @After
    public void tearDown() throws Exception {
        if (embeddedJmxTrans != null) {
            embeddedJmxTrans.stop();
        }
    }

    public static final class ListAppenderWriter extends AbstractOutputWriter {
        private static final List<QueryResult> results = Collections.synchronizedList(new ArrayList<>());

        @Override
        public void write(Iterable<QueryResult> results) {
            results.forEach(ListAppenderWriter.results::add);
        }
    }
}
