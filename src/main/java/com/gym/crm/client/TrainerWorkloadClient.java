package com.gym.crm.client;

import com.gym.crm.dto.request.TrainerWorkloadRequest;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.User;
import com.gym.crm.logging.TransactionContext;
import com.gym.crm.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TrainerWorkloadClient {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TrainerWorkloadClient.class);

    private final RestClient restClient;
    private final String workloadServiceUrl;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private final JwtService jwtService;

    public TrainerWorkloadClient(
            RestClient.Builder restClientBuilder,
            @Value("${workload.service.url:http://localhost:8081}") String workloadServiceUrl,
            ObjectProvider<CircuitBreakerFactory<?, ?>> circuitBreakerFactoryProvider,
            ObjectProvider<JwtService> jwtServiceProvider
    ) {
        this.restClient = restClientBuilder.build();
        this.workloadServiceUrl = workloadServiceUrl;
        this.circuitBreakerFactory = circuitBreakerFactoryProvider.getIfAvailable();
        this.jwtService = jwtServiceProvider.getIfAvailable();
    }

    public void notifyWorkload(Training training, TrainerWorkloadRequest.ActionType actionType) {
        User trainerUser = training.getTrainer().getUser();

        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                trainerUser.getUsername(),
                trainerUser.getFirstName(),
                trainerUser.getLastName(),
                trainerUser.getActive(),
                training.getTrainingDate(),
                training.getTrainingDuration(),
                actionType
        );

        if (circuitBreakerFactory == null) {
            sendWorkloadRequest(request, training);
            return;
        }

        circuitBreakerFactory.create("workloadService").run(
                () -> {
                    sendWorkloadRequest(request, training);
                    return null;
                },
                throwable -> {
                    fallbackNotifyWorkload(request, throwable);
                    return null;
                }
        );
    }

    private void sendWorkloadRequest(TrainerWorkloadRequest request, Training training) {
        RestClient.RequestBodySpec requestSpec = restClient.post()
                .uri(workloadServiceUrl + "/api/v1/trainer-workload");

        if (jwtService != null) {
            String token = jwtService.generateToken("gym-crm-service");
            requestSpec.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }

        String transactionId = TransactionContext.current();
        if (transactionId != null) {
            requestSpec.header("X-Transaction-Id", transactionId);
        }

        requestSpec.body(request)
                .retrieve()
                .toBodilessEntity();

        LOGGER.info(
                "Sent {} workload update for trainer={}, date={}, duration={}.",
                request.actionType(),
                request.trainerUsername(),
                training.getTrainingDate(),
                training.getTrainingDuration()
        );
    }

    private void fallbackNotifyWorkload(TrainerWorkloadRequest request, Throwable throwable) {
        LOGGER.error(
                "Circuit breaker fallback triggered for {} workload update for trainer={}. Reason: {}",
                request.actionType(),
                request.trainerUsername(),
                throwable.getMessage()
        );
    }
}