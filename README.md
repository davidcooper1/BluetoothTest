# BluetoothTest
Simple bluetooth test for school project.

## Creating a New TemperatureFetcher:
```java
TemperatureFetcher fetcher = new TemperatureFetcher();
```

## Setting a Callback Function:
```java
fetcher.setCallback(callbackId, callbackRunnable);
```
Notes:
callbackId can be one of four values: CALLBACK_DISCONNECT, CALLBACK_CONNECTING, CALLBACK_CONNECT, CALLBACK_DATA_RECEIVED.
Setting a callback disables callbacks until re-enabled.

## Enabling/Disabling Callbacks:
```java
fetcher.setCallbacksEnabled(bool)
```
## Checking Bluetooth Data:
``` java
fetcher.getData()
```
