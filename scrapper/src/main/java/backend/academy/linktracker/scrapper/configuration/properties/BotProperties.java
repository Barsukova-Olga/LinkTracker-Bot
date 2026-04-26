package backend.academy.linktracker.scrapper.configuration.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.bot")
@Validated
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class BotProperties {

    @NotEmpty
    String baseUrl;
}
