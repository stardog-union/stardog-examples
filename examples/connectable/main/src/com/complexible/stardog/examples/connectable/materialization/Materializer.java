package com.complexible.stardog.examples.connectable.materialization;

import java.util.Collection;
import java.util.Properties;
import java.util.function.Consumer;

import com.clarkparsia.pellet.api.term.axiom.Axiom;
import com.complexible.common.collect.BufferList;
import com.complexible.stardog.index.IndexReader;
import com.complexible.stardog.index.Quad;

/**
 * To be implemented by an external system.
 *
 * @author Pavel Klinov
 * @since
 */
public interface Materializer {

	void initialize(final Collection<Axiom> schema, final Properties properties);

	void materialize(final IndexReader data, final Consumer<BufferList<Quad>> batchConsumer);
}
