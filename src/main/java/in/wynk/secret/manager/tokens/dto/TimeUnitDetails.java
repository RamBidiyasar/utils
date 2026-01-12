package in.wynk.secret.manager.tokens.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.DurationFormatUtils;

/**
 * A utility class that represents a time duration with a specific {@link TimeUnit} and its corresponding value.
 */
public class TimeUnitDetails implements Serializable {
    @Serial
    private static final long serialVersionUID = 407263101909166486L;

    private TimeUnit unit;
    private Long value;

    public TimeUnitDetails() {}

    public TimeUnitDetails(final TimeUnit unit, final Long value) {
        this.unit = unit;
        this.value = value;
    }

    public TimeUnit getUnit() { return unit; }
    public void setUnit(TimeUnit unit) { this.unit = unit; }
    public Long getValue() { return value; }
    public void setValue(Long value) { this.value = value; }

    public static TimeUnitDetails ofMilli(final long time) {
        final TimeUnitDetails unitDetails = new TimeUnitDetails();
        unitDetails.setUnit(TimeUnit.MILLISECONDS);
        unitDetails.setValue(time);
        return unitDetails;
    }

    public static TimeUnitDetails ofSeconds(final long time) {
        final TimeUnitDetails unitDetails = new TimeUnitDetails();
        unitDetails.setUnit(TimeUnit.SECONDS);
        unitDetails.setValue(time);
        return unitDetails;
    }

    public static TimeUnitDetails ofMinutes(final long time) {
        final TimeUnitDetails unitDetails = new TimeUnitDetails();
        unitDetails.setUnit(TimeUnit.MINUTES);
        unitDetails.setValue(time);
        return unitDetails;
    }

    public static TimeUnitDetails ofHours(final long time) {
        final TimeUnitDetails unitDetails = new TimeUnitDetails();
        unitDetails.setUnit(TimeUnit.HOURS);
        unitDetails.setValue(time);
        return unitDetails;
    }

    public static TimeUnitDetails ofDays(final long time) {
        final TimeUnitDetails unitDetails = new TimeUnitDetails();
        unitDetails.setUnit(TimeUnit.DAYS);
        unitDetails.setValue(time);
        return unitDetails;
    }

    public Long toMillis() {
        return unit.toMillis(getValue());
    }

    @JsonIgnore
    public String getReadableDuration() {
        return DurationFormatUtils.formatDurationWords(toMillis(), true, true);
    }

    public static LocalDateTime calculateNextTime(TimeUnitDetails timeUnitDetails, LocalDateTime localDateTime) {
        return localDateTime.plus(timeUnitDetails.toMillis(),ChronoUnit.MILLIS);
    }

    @JsonIgnore
    public String getSingleUnitReadableDuration(TimeUnit lowestUnit) {
        long milliseconds = toMillis();
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);

        switch (lowestUnit) {
            case MINUTES -> seconds = 0;
            case HOURS -> seconds = minutes = 0;
            case DAYS -> seconds = minutes = hours = 0;
        }

        if (days >= 2) {
            return days + " DAYS";
        } else if (days == 1) {
            return "1 DAY";
        } else if (hours >= 2) {
            return hours + " HOURS";
        } else if (hours == 1) {
            return "1 HOUR";
        } else if (minutes >= 2) {
            return minutes + " MINUTES";
        } else if (minutes == 1) {
            return "1 MINUTE";
        } else if (seconds >= 2) {
            return seconds + " SECONDS";
        } else if (seconds == 1) {
            return "1 SECOND";
        } else {
            return "0 " + lowestUnit.name();
        }
    }
}