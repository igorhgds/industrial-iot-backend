package henrique.igor.iiot.infrastructure.messaging.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = {
                "henrique.igor.iiot.application.usecases",
                "henrique.igor.iiot.domain.services"
        },
        includeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = ".*(UseCase|Generator)"
        ),
        useDefaultFilters = false
)
public class UseCaseConfig {
}
