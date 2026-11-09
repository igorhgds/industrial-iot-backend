package henrique.igor.iiot.domain.ports;

import henrique.igor.iiot.domain.entities.Alert;

public interface AlertEventPublisher {

    void publishAlertTriggered(Alert alert);
}
