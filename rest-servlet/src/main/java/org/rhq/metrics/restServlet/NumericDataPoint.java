package org.rhq.metrics.restServlet;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;

/**
 * @author John Sanda
 */
public class NumericDataPoint {

    private long timestamp;

    private double value;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    private Set<String> tags;

    public NumericDataPoint() {
    }

    public NumericDataPoint(long timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NumericDataPoint that = (NumericDataPoint) o;

        if (timestamp != that.timestamp) return false;
        if (Double.compare(that.value, value) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (timestamp ^ (timestamp >>> 32));
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("timestamp", timestamp)
            .add("value", value)
            .add("tags", tags)
            .toString();
    }
}