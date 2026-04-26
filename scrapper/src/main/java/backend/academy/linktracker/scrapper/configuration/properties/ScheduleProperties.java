package backend.academy.linktracker.scrapper.configuration.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.schedule")
@Validated
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ScheduleProperties {

    @Positive
    long interval;

    @Positive
    @Min(50)
    @Max(500)
    private int batchSize;

    @Positive
    @Min(1)
    private int threads;
}
