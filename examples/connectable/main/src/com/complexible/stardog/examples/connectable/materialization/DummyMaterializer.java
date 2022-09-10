package com.complexible.stardog.examples.connectable.materialization;

import java.util.Collection;
import java.util.Properties;
import java.util.function.Consumer;

import com.clarkparsia.pellet.api.term.axiom.Axiom;
import com.complexible.common.collect.BufferList;
import com.complexible.stardog.index.IndexReader;
import com.complexible.stardog.index.Quad;

/**
 * @author Pavel Klinov
 * @since
 */
public class DummyMaterializer implements Materializer {

	@Override
	public void initialize(final Collection<Axiom> schema, final Properties properties) {
		System.err.println("Saved " + properties.size() + " properties");
	}

	@Override
	public void materialize(final IndexReader data, final Consumer<BufferList<Quad>> batchConsumer) {
		throw new UnsupportedOperationException("i'm a dummy, man!");
	}
}
