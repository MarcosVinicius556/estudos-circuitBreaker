package com.techprimers.circuitbreaker.controller;

import com.techprimers.circuitbreaker.model.Activity;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequestMapping("/activity")
@RestController
public class ActivityController {

    private RestTemplate restTemplate;

    private final String BORED_API = "https://www.boredapi.com/api/activity";

    public ActivityController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    /**
     * Aqui iremos ativar e mapear o circuitBreaker 
     * para termos uma forma de tratar esta requisição 
     * quando houver alguma falha no meio
     * 
     * Esta tratativa pode ser tanto um erro personalizado, 
     * com uma resposta em cache, ou até mesmo mockada
     */
    @CircuitBreaker(
            name = "randomActivity", //Nome deste circuitBreaker (Utilizando nas configurações do resilience4j)
            fallbackMethod = "fallbackRandomActivity" //Nome do método que ira ser chamado quando houver falha neste
            )
    public String getRandomActivity() {
        ResponseEntity<Activity> responseEntity = restTemplate.getForEntity(BORED_API, Activity.class);
        Activity activity = responseEntity.getBody();
        log.info("Activity received: " + activity.getActivity());
        return activity.getActivity();
    }

    /**
     * Método para retornar quando houver falha na requisição
     */
    public String fallbackRandomActivity(Throwable throwable) {
        return "PPlay your favorite Video Game";
    }

}

