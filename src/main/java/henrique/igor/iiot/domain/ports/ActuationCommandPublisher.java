package henrique.igor.iiot.domain.ports;

public interface ActuationCommandPublisher {

    void publishCommand(String topic, String payload);
}
