package com.twitter.common.metrics;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.twitter.common.base.MorePreconditions;
import com.twitter.common.quantity.Amount;
import com.twitter.common.quantity.Data;
import com.twitter.common.stats.ApproximateHistogram;
import com.twitter.common.stats.Precision;
import com.twitter.common.stats.Statistics;

import java.util.logging.Logger;

/**
 * A Histogram is a represention of a distribution of values.
 * It can be queried for quantiles or basic statistics (min, max, avg, count).
 */
public class Histogram {
  @VisibleForTesting
  static final double[] DEFAULT_QUANTILES = {.25, .50, .75, .90, .95, .99, .999, .9999};
  private static final Logger LOG = Logger.getLogger(Histogram.class.getName());

  private final com.twitter.common.stats.Histogram histogram;
  private final String name;
  private final double[] quantiles;
  private final MetricRegistry registry;
  private Statistics stats;

  /**
   * Construct an histogram, create gauges and register it into the registry.
   *
   * @param name is the name of the histogram used for Gauge name.
   * @param histogram is the inner common.stats.Histogram used for storing data.
   * @param quantiles is the quantiles values that will be used for the gauges.
   * @param registry is the registry in which gauges will be registered.
   */
  private Histogram(
    String name,
    com.twitter.common.stats.Histogram histogram,
    double[] quantiles,
    MetricRegistry registry) {

    MorePreconditions.checkNotBlank(name);
    Preconditions.checkNotNull(quantiles);
    Preconditions.checkArgument(0 < quantiles.length);
    Preconditions.checkNotNull(registry);

    this.name = name;
    this.histogram = histogram;
    this.quantiles = quantiles;
    this.registry = registry;
    this.stats = new Statistics();

    registerInto(registry);
  }

  /**
   * Construct a Histogram with default arguments except name.
   * @see #Histogram(String, Histogram, double[], MetricRegistry).
   */
  public Histogram(String name, MetricRegistry registry) {
    this(name, new ApproximateHistogram(), DEFAULT_QUANTILES, registry);
  }

  /**
   * Construct a Histogram with default arguments except name and precision.
   * @see #Histogram(String, Histogram, double[], MetricRegistry).
   */
  public Histogram(String name, Precision precision, MetricRegistry registry) {
    this(name, new ApproximateHistogram(precision), DEFAULT_QUANTILES, registry);
  }

  /**
   * Construct a Histogram with default arguments except name and maxMemory.
   * @see #Histogram(String, Histogram, double[], MetricRegistry).
   */
  public Histogram(String name, Amount<Long, Data> maxMemory, MetricRegistry registry) {
    this(name, new ApproximateHistogram(maxMemory), DEFAULT_QUANTILES, registry);
  }

  /**
   * Resets the state of this Histogram. Clears all data points collected so far.
   */
  public synchronized void clear() {
    stats = new Statistics();
    histogram.clear();
  }

  /**
   * Adds a data point.
   */
  public synchronized void add(long n) {
    stats.accumulate(n);
    histogram.add(n);
  }

  /**
   * Create multiple Gauges and register them into the MetricRegistry.
   */
  private void registerInto(final MetricRegistry metricRegistry) {
    metricRegistry.register(new AbstractGauge<Long>(name + "_count") {
      @Override public Long read() {
        return stats.populationSize();
      }
    });
    metricRegistry.register(new AbstractGauge<Long>(name + "_min") {
      @Override public Long read() {
        if (stats.populationSize() == 0) {
          return 0L;
        } else {
          return stats.min();
        }
      }
    });
    metricRegistry.register(new AbstractGauge<Long>(name + "_max") {
      @Override public Long read() {
        if (stats.populationSize() == 0) {
          return 0L;
        } else {
          return stats.max();
        }
      }
    });
    for (final double p : quantiles) {
      metricRegistry.register(new AbstractGauge<Long>(name + "_" + gaugeName(p)) {
        @Override public Long read() {
          double[] qs = {p};
          return histogram.getQuantiles(qs)[0];
        }
      });
    }
  }

  @VisibleForTesting
  static String gaugeName(double quantile) {
    String gname = "p" + (int) (quantile * 10000);
    if (3 < gname.length() && "00".equals(gname.substring(3))) {
      gname = gname.substring(0, 3);
    }
    return gname;
  }
}
