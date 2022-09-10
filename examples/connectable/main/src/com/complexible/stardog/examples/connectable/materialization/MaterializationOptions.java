package com.complexible.stardog.examples.connectable.materialization;

import com.complexible.stardog.metadata.ConfigProperty;
import com.complexible.stardog.metadata.MetaProperty;
import com.complexible.stardog.metadata.MetaPropertyProvider;

/**
 * @author Pavel Klinov
 * @since
 */
public class MaterializationOptions implements MetaPropertyProvider {

	final static String PREFIX = "reasoning.materialization.";

	private final static String OPTION_MATERIALIZATION_ENABLED = PREFIX + "enabled";

	private final static String OPTION_MATERIALIZATION_WRITE_BATCH = PREFIX + "write.batch";

	private final static String OPTION_MATERIALIZATION_PRINT_STATISTICS = PREFIX + "statistics.print";

	public static final ConfigProperty<Boolean> MATERIALIZATION_ENABLED = MetaProperty.config(OPTION_MATERIALIZATION_ENABLED, false)
	                                                                                  .database()
	                                                                                  .readable()
	                                                                                  .index()
	                                                                                  .writable()
	                                                                                  .creatable()
	                                                                                  .build();

	public static final ConfigProperty<Integer> MATERIALIZATION_WRITE_BATCH = MetaProperty.config(OPTION_MATERIALIZATION_WRITE_BATCH, 1000000)
	                                                                                  .database()
	                                                                                  .readable()
	                                                                                  //.index()
	                                                                                  .writable()
	                                                                                  .creatable()
	                                                                                  .build();

	public static final ConfigProperty<Boolean> MATERIALIZATION_PRINT_STATISTICS = MetaProperty.config(OPTION_MATERIALIZATION_PRINT_STATISTICS, false)
	                                                                                  .database()
	                                                                                  .readable()
	                                                                                  .index()
	                                                                                  .writable()
	                                                                                  .creatable()
	                                                                                  .build();
}
