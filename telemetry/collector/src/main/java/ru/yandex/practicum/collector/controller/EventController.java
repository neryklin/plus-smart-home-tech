package ru.yandex.practicum.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.collector.handler.hub.HubEventHandler;
import ru.yandex.practicum.collector.handler.sensor.SensorEventHandler;
import ru.yandex.practicum.collector.service.CollectorService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
@Slf4j
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {
    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;
    private final CollectorService collectorService;

    public EventController(Set<SensorEventHandler> sensorEventHandlers,
                           Set<HubEventHandler> hubEventHandlers,
                           CollectorService collectorService) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getMessageType,
                        Function.identity()
                ));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getMessageType,
                        Function.identity()
                ));
        this.collectorService = collectorService;
    }

    @Override
    public void collectSensorEvent(SensorEventProto event, StreamObserver<Empty> responseObserver) {
        try {
            if (sensorEventHandlers.containsKey(event.getPayloadCase())) {
                log.info("controller start sensor event:.", event);
                SensorEventAvro eventAvro = sensorEventHandlers.get(event.getPayloadCase()).handle(event);
                log.info("mappint to avro", eventAvro);
                collectorService.collectSensorEvent(eventAvro);
                log.info("send to kafka");
            } else {
                throw new IllegalArgumentException("error type event sensor " + event.getPayloadCase());
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL
                    .withDescription(e.getLocalizedMessage())
                    .withCause(e)));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto event, StreamObserver<Empty> responseObserver) {
        try {
            if (hubEventHandlers.containsKey(event.getPayloadCase())) {
                log.info("controller start sensor event:.", event);
                HubEventAvro eventAvro = hubEventHandlers.get(event.getPayloadCase()).handle(event);
                log.info("mappint to avro", eventAvro);
                collectorService.collectHubEvent(eventAvro);
                log.info("send to kafka");
            } else {
                throw new IllegalArgumentException("error type event hub" + event.getPayloadCase());
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL
                    .withDescription(e.getLocalizedMessage())
                    .withCause(e)));
        }
    }
}