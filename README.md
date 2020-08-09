# traffic-light
Actor based service to control a traffic light.

## Basic API calls

### Start
```bash
curl -X GET http://localhost:8080/start
```

### Update config
```bash
curl -X POST http://localhost:8080/config -d '{"upperIntervalBorder":8.0,"lowerIntervalBorder":10.0,"greenLightDuration":5.0,"yellowLightDuration":5.0,"yellowRedLightDuration":5.0}'
```

### Stop
```bash
curl -X GET http://localhost:8080/stop
```

### Terminate
```bash
curl -X GET http://localhost:8080/terminate
```

