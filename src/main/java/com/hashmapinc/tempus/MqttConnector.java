package com.hashmapinc.tempus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqttConnector {
    private String mqttUrl;
    private String accessToken;
    final public static String MQTT_TOPIC = "v1/gateway/telemetry";

    public void publish(String data, Long ts, String deviceName) throws MqttException, JsonProcessingException {
        MqttMessage msg = new MqttMessage();
        toDataJson(ts, data, deviceName);
        msg.setPayload(data.getBytes());
        MqttClient client = connect();
        client.publish(MQTT_TOPIC, msg);
        disconnect(client);
    }

    private void disconnect(MqttClient client) throws MqttException {
        client.disconnect();
    }

    private MqttClient connect() throws MqttException{
        MqttClient client = new MqttClient(mqttUrl, MqttAsyncClient.generateClientId());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(accessToken);
        client.connect(options);
        return client;
    }

    private String toDataJson(Long ts, String json, String deviceName) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        ArrayNode an = objectNode.putArray(deviceName);
        ObjectNode objectNode2 = an.addObject();
        objectNode2.put("ts", ts);
        objectNode2.put("values", json);
        return mapper.writeValueAsString(objectNode2);
    }

    private String toDataJson(String json, String deviceName) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode on1 = mapper.createObjectNode();
        ArrayNode an = on1.putArray(deviceName);
        ObjectNode on2 = an.addObject();
        on2.put("ts", System.currentTimeMillis());
        on2.put("values", json);
        return mapper.writeValueAsString(on2);
    }
}
