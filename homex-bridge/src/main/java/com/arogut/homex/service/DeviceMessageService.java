package com.arogut.homex.service;

import com.arogut.homex.model.DeviceMessage;
import com.arogut.homex.model.Measurement;
import lombok.RequiredArgsConstructor;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DeviceMessageService {

    private final InfluxDB influxDB;

    public void handle(DeviceMessage deviceMessage) {
        persist(deviceMessage.getDeviceId(), deviceMessage.getData(), deviceMessage.getMeasuredTime());
    }

    private void persist(String deviceId, List<Measurement> measurements, long measuredTime) {
        BatchPoints.Builder batchPoints = BatchPoints.builder().tag("deviceId", deviceId);
        measurements.forEach(m -> batchPoints.point(Point.measurement("measurement")
                .addField(m.getName(), m.getValue())
                .time(measuredTime, TimeUnit.MILLISECONDS)
                .build()));

        influxDB.write(batchPoints.build());
    }

}
