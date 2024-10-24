package roadmap.backend.image_processing_service.transforms.application.service;

import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roadmap.backend.image_processing_service.transforms.application.event.component.MessagePropertiesConstants;
import roadmap.backend.image_processing_service.transforms.application.event.message.KafkaMessage;
import roadmap.backend.image_processing_service.transforms.application.event.message.implement.KafkaMessageImage;
import roadmap.backend.image_processing_service.transforms.application.event.message.implement.KafkaMessageTransforms;
import roadmap.backend.image_processing_service.transforms.application.interfaces.KafkaServiceTransforms;
import roadmap.backend.image_processing_service.transforms.domain.dto.ImageDTO;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Crop;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Filters;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.FormatImage;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Resize;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

@Service
@Primary
public class KafkaServicesTransformsImp implements KafkaServiceTransforms {

    @Transactional
    @Async
    @Override
    public CompletableFuture<KafkaMessage> execute(KafkaMessageTransforms message) {
        CompletableFuture<KafkaMessage> future = new CompletableFuture<>();
        CompletableFuture<Void>[] states = new CompletableFuture[5];
        ImageDTO image = new ImageDTO(message.name(), message.format().toString(), message.image());
        Semaphore semaphore = new Semaphore(1);

        if(message.transformation().resize() != null)
            states[0] = resize(message.transformation().resize(), image, semaphore);
        else
            states[0].complete(null);

        if(message.transformation().crop() != null)
            states[1] = crop(message.transformation().crop(), image, semaphore);
        else
            states[1].complete(null);

        if(message.transformation().rotate() != null)
            states[2] = rotate(message.transformation().rotate(), image, semaphore);
        else
            states[2].complete(null);

        if(message.transformation().format() != null)
            states[3] = format(message.transformation().format(), image, semaphore);
        else
            states[3].complete(null);

        if(message.transformation().filters() != null)
            states[4] = filter(message.transformation().filters(), image, semaphore);
        else
            states[4].complete(null);

        CompletableFuture.allOf(states).thenRun(() -> {
            semaphore.release();
            future.complete(new KafkaMessageImage(
                    message.destinationEvent(),
                    message.event(),
                    Map.of(
                            MessagePropertiesConstants.NAME, message.name(),
                            MessagePropertiesConstants.FORMAT, message.format(),
                            MessagePropertiesConstants.IMAGE, message.image()
                    ),
                    message.UUID()
            ));
        });
        return future;
    }

    @Override
    public CompletableFuture<Void> resize(Resize value, ImageDTO image ,Semaphore semaphore) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        semaphore.acquireUninterruptibly();
        // TODO: Implement resize
        semaphore.release();
        future.complete(null);
        return future;
    }

    @Override
    public CompletableFuture<Void> crop(Crop value, ImageDTO image ,Semaphore semaphore) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        semaphore.acquireUninterruptibly();
        // TODO: Implement crop
        semaphore.release();
        future.complete(null);
        return future;
    }

    @Override
    public CompletableFuture<Void> rotate(Integer value, ImageDTO image ,Semaphore semaphore) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        semaphore.acquireUninterruptibly();
        // TODO: Implement rotate
        semaphore.release();
        future.complete(null);
        return future;
    }

    @Override
    public CompletableFuture<Void> format(FormatImage value, ImageDTO image ,Semaphore semaphore) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        semaphore.acquireUninterruptibly();
        // TODO: Implement format
        semaphore.release();
        future.complete(null);
        return future;
    }
    @Async
    @Override
    public CompletableFuture<Void> filter(Filters value, ImageDTO image, Semaphore semaphore) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        CompletableFuture[] filtersFuture = new CompletableFuture[2];
        Semaphore semaphore1 = new Semaphore(1);
        semaphore.acquireUninterruptibly();
        filtersFuture[0] = grayscale(image.image(), semaphore1);
        filtersFuture[1] = sepia(image.image(), semaphore1);
        CompletableFuture.allOf(filtersFuture).thenRun(() -> {
            semaphore.release();
            future.complete(null);
        });
        return future;
    }

    @Override
    public CompletableFuture<Void> grayscale(byte[] image, Semaphore semaphore) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        semaphore.acquireUninterruptibly();
        // TODO: Implement grayscale
        semaphore.release();
        future.complete(null);
        return future;
    }

    @Override
    public CompletableFuture<Void> sepia(byte[] image, Semaphore semaphore) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        semaphore.acquireUninterruptibly();
        // TODO: Implement sepia
        semaphore.release();
        future.complete(null);
        return future;
    }
}
