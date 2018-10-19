# Tempus-Connectors

Repo for library that allows kubeless to communicate with Tempus.

There are two utilities provided in this repo:

##InputParserUtility

It is used to parse the json data with some mandatory parameters/fields. "id" and either "ts" or "ds" are mandatory parameters in the json.
The user can add more fields whose presence he wants to validate. Refer to code snippet. Here "key" is extra field which the user wants to validate.

```
    import com.hashmapinc.tempus.InputParserUtility;

    InputParserUtility inputParserUtility = new InputParserUtility();
            String jsonStr = "{\"id\":\"1\", \"ts\":1483228800000, \"key\":\"val\"}";
            inputParserUtility.validateJson(jsonStr, Collections.singletonList("key"));
```

##MqttConnector

It is used to publish timeseries and depthseries data to tempus through a gateway device via mqtt.
The json format for timeseries data to be published is

```json
    {
      "Device A": [
        {
          "ts": 1483228800000,
          "values": {
            "temperature": 42,
            "humidity": 80
          }
        },
        {
          "ts": 1483228801000,
          "values": {
            "temperature": 43,
            "humidity": 82
          }
        }
      ]
    }
```
Json format for depthseries data is
```json
    {
      "Device A": [
        {
          "ds": 200.4,
          "values": {
            "temperature": 42,
            "humidity": 80
          }
        },
        {
          "ds": 300.3,
          "values": {
            "temperature": 43,
            "humidity": 82
          }
        }
      ]
    }
```
Inorder to publish data see the code snippets.

```java 
    import com.hashmapinc.tempus.MqttConnector;
```
Import MqttConnector.

```java 
    private static final String MQTT_URL = "tcp://tempus.hashmapinc.com:1883";
    private static final String ACCESS_TOKEN = "DEVICE_GATEWAY_TOKEN";
```

Give mqtt url and access token of tempus gateway device.

```java
    Optional<Long> empty = Optional.empty();
    String json = "{\"calValue\":\"result\"}";
    new MqttConnector(MQTT_URL, ACCESS_TOKEN).publish(json, empty, Optional.of(200.6), "device_name" + inputData.id);
```
This will publish depthseries data i.e "json" in above code to a new device whose name is combination of "device_name" and inputData.id.
Note: In publish method empty is passed to "ts" long value. When publishing "ds" empty would be passed to "ds" double value.
