package in.wynk.secret.manager.tokens.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DurationFormatUtils;

/**
 * A utility class that represents a time duration with a specific {@link TimeUnit} and its corresponding value. This class provides methods to create
 * instances of time durations for different {@link TimeUnit}s and convert them to milliseconds or obtain a human-readable format for the duration.
 */
@Data
@NoArgsConstructor
public class TimeUnitDetails implements Serializable {
    @Serial
    private static final long serialVersionUID = 407263101909166486L;

    private TimeUnit unit;
    private Long value;

    /**
     * Constructor
     */
    public TimeUnitDetails(final TimeUnit unit, final Long value) {
        this.unit = unit;
        this.value = value;
    }

    /**
     * Creates a new {@code TimeUnitDetails} instance with the specified duration in milliseconds.
     *
     * @param time The duration in milliseconds.
     * @return A {@code TimeUnitDetails} instance with the specified duration in milliseconds.
     */
    public static TimeUnitDetails ofMilli(final long time) {
        final TimeUnitDetails unitDetails = new TimeUnitDetails();
        unitDetails.setUnit(TimeUnit.MILLISECONDS);
        unitDetails.setValue(time);
        return unitDetails;
    }

    /**
     * Creates a new {@code TimeUnitDetails} instance with the specified duration in seconds.
     *
     * @param time The duration in seconds.
     * @return A {@code TimeUnitDetails} instance with the specified duration in seconds.
     */
    public static TimeUnitDetails ofSeconds(final long time) {
        final TimeUnitDetails unitDetails = new TimeUnitDetails();
        unitDetails.setUnit(TimeUnit.SECONDS);
        unitDetails.setValue(time);
        return unitDetails;
    }

    /**
     * Creates a new {@code TimeUnitDetails} instance with the specified duration in minutes.
     *
     * @param time The duration in minutes.
     * @return A {@code TimeUnitDetails} instance with the specified duration in minutes.
     */
    public static TimeUnitDetails ofMinutes(final long time) {
        final TimeUnitDetails unitDetails = new TimeUnitDetails();
        unitDetails.setUnit(TimeUnit.MINUTES);
        unitDetails.setValue(time);
        return unitDetails;
    }

    /**
     * Creates a new {@code TimeUnitDetails} instance with the specified duration in hours.
     *
     * @param time The duration in hours.
     * @return A {@code TimeUnitDetails} instance with the specified duration in hours.
     */
    public static TimeUnitDetails ofHours(final long time) {
        final TimeUnitDetails unitDetails = new TimeUnitDetails();
        unitDetails.setUnit(TimeUnit.HOURS);
        unitDetails.setValue(time);
        return unitDetails;
    }

    /**
     * Creates a new {@code TimeUnitDetails} instance with the specified duration in days.
     *
     * @param time The duration in days.
     * @return A {@code TimeUnitDetails} instance with the specified duration in days.
     */
    public static TimeUnitDetails ofDays(final long time) {
        final TimeUnitDetails unitDetails = new TimeUnitDetails();
        unitDetails.setUnit(TimeUnit.DAYS);
        unitDetails.setValue(time);
        return unitDetails;
    }

    /**
     * This will return the current object time details in millis
     *
     * @return : time in millis
     */
    public Long toMillis() {
        return unit.toMillis(getValue());
    }

    /**
     * Gets the readable duration in words format for the current TimeUnitDetails object. The method converts the time value represented by the
     * TimeUnitDetails object into a human-readable format, providing an approximate representation of the time duration using words like "2 days 5
     * hours 30 minutes".
     *
     * @return the readable duration in words format.
     * @see TimeUnitDetails#toMillis() for converting TimeUnitDetails to milliseconds.
     * @see DurationFormatUtils#formatDurationWords(long, boolean, boolean) for formatting the duration in words.
     */
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
            return "0 " + lowestUnit.name(); // Handle the case when milliseconds are less than 1 unit
        }
    }
}
