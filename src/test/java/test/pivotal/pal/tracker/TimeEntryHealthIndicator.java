package test.pivotal.pal.tracker;

import io.pivotal.pal.tracker.TimeEntryRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TimeEntryHealthIndicator implements HealthIndicator {

    private TimeEntryRepository timeEntryRepository;

    public TimeEntryHealthIndicator(TimeEntryRepository timeEntryRepository) {
        this.timeEntryRepository = timeEntryRepository;
    }

    @Override
    public Health health() {
        return timeEntryRepository.list().size() < 5 ? Health.up().build() : Health.down().build();
    }
}
