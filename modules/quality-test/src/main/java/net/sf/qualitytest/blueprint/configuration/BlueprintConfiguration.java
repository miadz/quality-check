/*******************************************************************************
 * Copyright 2013 Dominik Seichter
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sf.qualitytest.blueprint.configuration;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.qualitycheck.Check;
import net.sf.qualitycheck.Throws;
import net.sf.qualitycheck.exception.IllegalNullArgumentException;
import net.sf.qualitytest.blueprint.Blueprint;
import net.sf.qualitytest.blueprint.ValueCreationStrategy;
import net.sf.qualitytest.blueprint.ValueMatchingStrategy;
import net.sf.qualitytest.blueprint.strategy.creation.SingleValueCreationStrategy;
import net.sf.qualitytest.blueprint.strategy.matching.CaseInsensitiveValueMatchingStrategy;
import net.sf.qualitytest.blueprint.strategy.matching.TypeValueMatchingStrategy;

/**
 * Configure how blueprinting is done. A BlueprintConfiguration defines how the values for certain attributes are
 * generated.
 * 
 * This class is immutable all modifier methods have to return a new instance of this class.
 * 
 * @author Dominik Seichter
 */
public class BlueprintConfiguration {

	private final Map<ValueMatchingStrategy, ValueCreationStrategy<?>> attributeMapping = new HashMap<ValueMatchingStrategy, ValueCreationStrategy<?>>();

	public BlueprintConfiguration() {
		// Empty default constructor
	}

	private BlueprintConfiguration(final BlueprintConfiguration configuration) {
		Check.notNull(configuration, "configuration");
		attributeMapping.putAll(configuration.attributeMapping);
	}

	protected BlueprintConfiguration(final Map<ValueMatchingStrategy, ValueCreationStrategy<?>> attributeMapping) {
		Check.notNull(attributeMapping, "attributeMapping");
		this.attributeMapping.putAll(attributeMapping);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.qualitytest.blueprint.configuration.udih#findCreationStrategyForMethod(java.lang.reflect.Method)
	 */
	public ValueCreationStrategy<?> findCreationStrategyForMethod(final Method method) {
		Check.notNull(method, "method");

		for (final Map.Entry<ValueMatchingStrategy, ValueCreationStrategy<?>> entry : attributeMapping.entrySet()) {
			if (entry.getKey().matches(method.getName())) {
				return entry.getValue();
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.qualitytest.blueprint.configuration.udih#findCreationStrategyForType(java.lang.Class)
	 */
	public ValueCreationStrategy<?> findCreationStrategyForType(final Class<?> clazz) {
		Check.notNull(clazz, "clazz");

		for (final Map.Entry<ValueMatchingStrategy, ValueCreationStrategy<?>> entry : attributeMapping.entrySet()) {
			if (entry.getKey().matches(clazz)) {
				return entry.getValue();
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.qualitytest.blueprint.configuration.udih#getAttributeMappings()
	 */
	public Map<ValueMatchingStrategy, ValueCreationStrategy<?>> getAttributeMappings() {
		return Collections.unmodifiableMap(attributeMapping);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.qualitytest.blueprint.configuration.udih#object(java.lang.Class)
	 */
	@Throws(IllegalNullArgumentException.class)
	public <T> T object(final Class<T> clazz) {
		return Blueprint.object(clazz, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.qualitytest.blueprint.configuration.udih#with(java.lang.Class, T)
	 */
	public <T> BlueprintConfiguration with(final Class<T> type, final T value) {
		return this.with(new TypeValueMatchingStrategy(type), new SingleValueCreationStrategy<T>(value));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.qualitytest.blueprint.configuration.udih#with(java.lang.String, T)
	 */
	public <T> BlueprintConfiguration with(final String name, final T value) {
		return this.with(new CaseInsensitiveValueMatchingStrategy(name), new SingleValueCreationStrategy<T>(value));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.qualitytest.blueprint.configuration.udih#with(net.sf.qualitytest.blueprint.ValueMatchingStrategy,
	 * net.sf.qualitytest.blueprint.ValueCreationStrategy)
	 */
	public BlueprintConfiguration with(final ValueMatchingStrategy matcher, final ValueCreationStrategy<?> creator) {
		final BlueprintConfiguration config = new BlueprintConfiguration(this);
		config.attributeMapping.put(Check.notNull(matcher, "matcher"), Check.notNull(creator, "creator"));
		return config;
	}

}
