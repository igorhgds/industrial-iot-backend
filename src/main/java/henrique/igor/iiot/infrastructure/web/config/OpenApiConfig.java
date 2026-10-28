package henrique.igor.iiot.infrastructure.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI industrialIiotOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Industrial IIoT Backend API")
                        .description("REST API for Motor Telemetry, Machinery Health Monitoring, Dynamic Rules Engine, Anomaly Alerts, Actuation Commands and Work Orders.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Igor Henrique")
                                .url("https://github.com/igorhgds/industrial-iot-backend"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
