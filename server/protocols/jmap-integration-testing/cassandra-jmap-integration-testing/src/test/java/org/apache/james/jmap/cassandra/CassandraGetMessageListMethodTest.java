/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.jmap.cassandra;

import org.apache.james.CassandraJamesServerMain;
import org.apache.james.GuiceJamesServer;
import org.apache.james.backends.cassandra.EmbeddedCassandra;
import org.apache.james.jmap.methods.integration.GetMessageListMethodTest;
import org.apache.james.modules.CassandraJmapServerModule;
import org.apache.james.mailbox.cassandra.CassandraId;
import org.apache.james.mailbox.elasticsearch.EmbeddedElasticSearch;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;

import com.google.inject.TypeLiteral;

public class CassandraGetMessageListMethodTest extends GetMessageListMethodTest {

    private TemporaryFolder temporaryFolder = new TemporaryFolder();
    private EmbeddedElasticSearch embeddedElasticSearch = new EmbeddedElasticSearch(temporaryFolder);
    private EmbeddedCassandra cassandra = EmbeddedCassandra.createStartServer();

    @Rule
    public RuleChain chain = RuleChain
        .outerRule(temporaryFolder)
        .around(embeddedElasticSearch);
    
    @Override
    protected GuiceJamesServer<?> createJmapServer() {
        return new GuiceJamesServer<>(new TypeLiteral<CassandraId>(){})
                .combineWith(CassandraJamesServerMain.cassandraServerModule)
                .overrideWith(new CassandraJmapServerModule(temporaryFolder, embeddedElasticSearch, cassandra));
    }

    @Override
    protected void await() {
        embeddedElasticSearch.awaitForElasticSearch();
    }
}