package com.arogut.homex.rest;

import com.arogut.homex.model.DeviceMessage;
import com.arogut.homex.model.Measurement;
import com.arogut.homex.repository.DeviceRepository;
import com.arogut.homex.service.DeviceMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {HomexBridgeApplication.class})
@SpringBootTest
@AutoConfigureWebTestClient
public class DeviceMessageControllerTest {

    @MockBean
    private DeviceMessageService deviceMessageService;

    @MockBean
    private DeviceRepository deviceRepository;

    @Autowired
    private WebTestClient webClient;

    @Test
    @WithMockUser
    void shouldAcceptMessageAndReturn200OK() {
        DeviceMessage msg = DeviceMessage.builder()
                .deviceId("dummy")
                .measuredTime(Instant.now().toEpochMilli())
                .receivedTime(Instant.now().toEpochMilli())
                .data(List.of(Measurement.builder()
                        .name("temp")
                        .value("25").build()
                ))
                .build();

        Mockito.when(deviceRepository.existsById("dummy")).thenReturn(true);

        webClient.post()
                .uri("/devices/message")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(msg), DeviceMessage.class)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody().isEmpty();
    }

    @Test
    @WithMockUser
    void shouldNotAcceptInvalidMessageAndReturn400() {
        DeviceMessage msg = DeviceMessage.builder()
                .deviceId("dummy")
                .measuredTime(Instant.now().toEpochMilli())
                .receivedTime(Instant.now().toEpochMilli())
                .data(List.of())
                .build();

        webClient.post()
                .uri("/devices/message")
                .body(Mono.just(msg), DeviceMessage.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @WithMockUser
    void shouldNotAcceptMessageFromInvalidSourceAndReturn400() {
        DeviceMessage msg = DeviceMessage.builder()
                .deviceId("dummy")
                .measuredTime(Instant.now().toEpochMilli())
                .receivedTime(Instant.now().toEpochMilli())
                .data(List.of(Measurement.builder()
                        .name("temp")
                        .value("25").build()
                ))
                .build();

        Mockito.when(deviceRepository.existsById("dummy")).thenReturn(false);

        webClient.post()
                .uri("/devices/message")
                .body(Mono.just(msg), DeviceMessage.class)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
