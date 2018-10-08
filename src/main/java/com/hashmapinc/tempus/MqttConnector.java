package com.hashmapinc.tempus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqttConnector {
    private String mqttUrl;
    private String accessToken;
    final public static String MQTT_TOPIC_TS = "v1/gateway/telemetry";
    final public static String MQTT_TOPIC_DS = "v1/gateway/depth/telemetry";

    public void publish(String data, Optional<Long> ts, Optional<Double> ds, String deviceName) throws MqttException, IOException {
        MqttMessage msg = new MqttMessage();
        String json = toDataJson(ts, ds, data, deviceName);
        msg.setPayload(json.getBytes());
        MqttClient client = connect();
        if (ts.isPresent())
            client.publish(MQTT_TOPIC_TS, msg);
        else if (ds.isPresent())
            client.publish(MQTT_TOPIC_DS, msg);
        disconnect(client);
    }

    private void disconnect(MqttClient client) throws MqttException {
        client.disconnect();
    }

    private MqttClient connect() throws MqttException{
        MqttClient client = new MqttClient(mqttUrl, MqttClient.generateClientId(), new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(accessToken);
        client.connect(options);
        return client;
    }

    private String toDataJson(Optional<Long> ts, Optional<Double> ds, String json, String deviceName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        ArrayNode an = objectNode.putArray(deviceName);
        ObjectNode objectNode2 = an.addObject();
        if(ts.isPresent())
            objectNode2.put("ts", ts.get());
        else if (ds.isPresent())
            objectNode2.put("ds", ds.get());
        objectNode2.put("values", mapper.readTree(json));
        return mapper.writeValueAsString(objectNode);
    }
}
