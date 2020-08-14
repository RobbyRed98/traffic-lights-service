# traffic-lights-service
Actor based service REST-service to control a real traffic light which is connected to a raspberry pi.

## Basic REST-API calls

### Start
```bash
curl -X GET http://localhost:8080/start
```

### Update config
```bash
curl -X POST http://localhost:8080/config -d '{"upperIntervalBorder":8.0,"lowerIntervalBorder":10.0,"greenLightDuration":5.0,"yellowLightDuration":5.0,"yellowRedLightDuration":5.0}'
```

### Get config
```bash
curl -X GET http://localhost:8080/config
```

### Stop
```bash
curl -X GET http://localhost:8080/stop
```

### Check heartbeat
```bash
curl -X GET http://localhost:8080/heartbeat
```

### Terminate
```bash
curl -X GET http://localhost:8080/terminate
```

