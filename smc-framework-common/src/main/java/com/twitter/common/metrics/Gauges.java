package com.twitter.common.metrics;

import com.google.common.base.Supplier;

import java.util.Collection;

/**
 * Convenience functions for working with {@link Gauge}s.
 */
public final class Gauges {

  private Gauges() {
    // Utility class.
  }

  /**
   * Creates a supplier that serves as an accessor for gauge values.
   *
   * @param gauge Gauge to turn into a supplier.
   * @return Supplier of values from {@code gauge}.
   */
  public static Supplier<Number> asSupplier(final Gauge gauge) {
    return new Supplier<Number>() {
      @Override public Number get() {
        return gauge.read();
      }
    };
  }

  /**
   * Registers the size of a collection as a gauge.
   *
   * @param registry Registry to register the gauge with.
   * @param name Name for the gauge.
   * @param collection Collection to register size of.
   */
  public static void registerSize(MetricRegistry registry, String name,
      final Collection collection) {
    registry.register(new AbstractGauge<Integer>(name) {
      @Override public Integer read() {
        return collection.size();
      }
    });
  }
}
