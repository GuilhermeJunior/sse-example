package dev.stormgui.sseexample;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class EventService {

    private final List<SseEmitter> sseEmitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public EventService() {
        executorService.scheduleAtFixedRate(this::sendEvent, 1, 1, TimeUnit.SECONDS);
    }

    public synchronized SseEmitter subscribe() {
        SseEmitter sseEmitter = new SseEmitter();
        sseEmitters.add(sseEmitter);
        sseEmitter.onCompletion(() -> sseEmitters.remove(sseEmitter));
        return sseEmitter;
    }

    private void sendEvent() {
        for (SseEmitter sseEmitter : sseEmitters) {
            try {
                sseEmitter.send(SseEmitter.event().data("Mensagem do servidor").build());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


