/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.curator.x.async.modeled;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.x.async.AsyncStage;
import org.apache.curator.x.async.modeled.caching.Caching;
import org.apache.zookeeper.data.Stat;
import java.util.List;

public interface ModeledCuratorFramework<T>
{
    /**
     * Return a new ModeledCuratorFramework for the given model
     *
     * @param client Curator client
     * @param model the model
     * @return new Modeled Curator instance
     */
    static <T> ModeledCuratorFramework<T> wrap(CuratorFramework client, CuratorModelSpec<T> model)
    {
        return builder(client, model).build();
    }

    /**
     * Start a new ModeledCuratorFrameworkBuilder for the given model
     *
     * @param client Curator client
     * @param model the model
     * @return builder
     */
    static <T> ModeledCuratorFrameworkBuilder<T> builder(CuratorFramework client, CuratorModelSpec<T> model)
    {
        return new ModeledCuratorFrameworkBuilder<>(client, model);
    }

    /**
     * Returns the client that was originally passed to {@link #wrap(org.apache.curator.framework.CuratorFramework, CuratorModelSpec)} or
     * the builder.
     *
     * @return original client
     */
    CuratorFramework unwrap();

    /**
     * Return the caching APIs. Only valid if {@link ModeledCuratorFrameworkBuilder#cached()} or
     * {@link ModeledCuratorFrameworkBuilder#cached(java.util.Set)} was called when building the instance.
     *
     * @return caching APIs
     * @throws java.lang.IllegalStateException if caching was not enabled when building the instance
     */
    Caching<T> caching();

    /**
     * Return a new Modeled Curator instance with all the same options but applying to the given child node of this Modeled Curator's
     * path. E.g. if this Modeled Curator instance applies to "/a/b", calling <code>modeled.at("c")</code> returns an instance that applies to
     * "/a/b/c".
     *
     * @param child child node.
     * @return new Modeled Curator instance
     */
    ModeledCuratorFramework<T> at(String child);

    /**
     * Create (or update depending on build options) a ZNode at this instance's path with a serialized
     * version of the given model
     *
     * @param model model to write
     * @return AsyncStage
     * @see org.apache.curator.x.async.AsyncStage
     */
    AsyncStage<String> set(T model);

    /**
     * Create (or update depending on build options) a ZNode at this instance's path with a serialized
     * form of the given model
     *
     * @param model model to write
     * @param storingStatIn the stat for the new ZNode is stored here
     * @return AsyncStage
     * @see org.apache.curator.x.async.AsyncStage
     */
    AsyncStage<String> set(T model, Stat storingStatIn);

    /**
     * Read the ZNode at this instance's path and deserialize into a model
     *
     * @return AsyncStage
     * @see org.apache.curator.x.async.AsyncStage
     */
    AsyncStage<T> read();

    /**
     * Read the ZNode at this instance's path and deserialize into a model
     *
     * @param storingStatIn the stat for the new ZNode is stored here
     * @return AsyncStage
     * @see org.apache.curator.x.async.AsyncStage
     */
    AsyncStage<T> read(Stat storingStatIn);

    /**
     * Update the ZNode at this instance's path with a serialized
     * form of the given model passing "-1" for the update version
     *
     * @param model model to write
     * @return AsyncStage
     * @see org.apache.curator.x.async.AsyncStage
     */
    AsyncStage<Stat> update(T model);

    /**
     * Update the ZNode at this instance's path with a serialized
     * form of the given model passing the given update version
     *
     * @param model model to write
     * @param version update version to use
     * @return AsyncStage
     * @see org.apache.curator.x.async.AsyncStage
     */
    AsyncStage<Stat> update(T model, int version);

    /**
     * Delete the ZNode at this instance's path passing -1 for the delete version
     *
     * @return AsyncStage
     * @see org.apache.curator.x.async.AsyncStage
     */
    AsyncStage<Void> delete();

    /**
     * Delete the ZNode at this instance's path passing the given delete version
     *
     * @param version update version to use
     * @return AsyncStage
     * @see org.apache.curator.x.async.AsyncStage
     */
    AsyncStage<Void> delete(int version);

    /**
     * Check to see if the ZNode at this instance's path exists
     *
     * @return AsyncStage
     * @see org.apache.curator.x.async.AsyncStage
     */
    AsyncStage<Stat> checkExists();

    /**
     * Return the child paths of this instance's paths (in no particular order)
     *
     * @return AsyncStage
     * @see org.apache.curator.x.async.AsyncStage
     */
    AsyncStage<List<ZPath>> getChildren();

    /**
     * Create operation instance that can be passed among other operations to
     * {@link #inTransaction(java.util.List)} to be executed as a single transaction. Note:
     * due to ZooKeeper transaction limits, this is a _not_ a "set or update" operation but only
     * a create operation and will generate an error if the node already exists.
     *
     * @param model the model
     * @return operation
     */
    CuratorOp createOp(T model);

    /**
     * Update operation instance that can be passed among other operations to
     * {@link #inTransaction(java.util.List)} to be executed as a single transaction.
     *
     * @param model the model
     * @return operation
     */
    CuratorOp updateOp(T model);

    /**
     * Create operation instance that can be passed among other operations to
     * {@link #inTransaction(java.util.List)} to be executed as a single transaction.
     *
     * @param model the model
     * @param version update version to use
     * @return operation
     */
    CuratorOp updateOp(T model, int version);

    /**
     * Delete operation instance that can be passed among other operations to
     * {@link #inTransaction(java.util.List)} to be executed as a single transaction.
     *
     * @return operation
     */
    CuratorOp deleteOp();

    /**
     * Delete operation instance that can be passed among other operations to
     * {@link #inTransaction(java.util.List)} to be executed as a single transaction.
     *
     * @param version delete version to use
     * @return operation
     */
    CuratorOp deleteOp(int version);

    /**
     * Check exists operation instance that can be passed among other operations to
     * {@link #inTransaction(java.util.List)} to be executed as a single transaction.
     *
     * @return operation
     */
    CuratorOp checkExistsOp();

    /**
     * Check exists operation instance that can be passed among other operations to
     * {@link #inTransaction(java.util.List)} to be executed as a single transaction.
     *
     * @param version version to use
     * @return operation
     */
    CuratorOp checkExistsOp(int version);

    /**
     * Invoke ZooKeeper to commit the given operations as a single transaction.
     *
     * @param operations operations that make up the transaction.
     * @return AsyncStage instance for managing the completion
     */
    AsyncStage<List<CuratorTransactionResult>> inTransaction(List<CuratorOp> operations);
}