package roadmap.backend.image_processing_service.transforms.application.service;

import lombok.NonNull;
import lombok.SneakyThrows;
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
import roadmap.backend.image_processing_service.transforms.domain.usecase.TransformationsUsecase;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@Primary
public class KafkaServicesTransformsImp implements KafkaServiceTransforms {
    final private TransformationsUsecase transformUseCase;
    final private HashMap<String, Map<String, String>> errors = new HashMap<>();
    public KafkaServicesTransformsImp(TransformationsUsecase transformationsUsecase) {
        this.transformUseCase = transformationsUsecase;
    }
    private void setImage(
            @NonNull ImageDTO image,
            @NonNull BufferedImage bufferedImage
    ) throws RuntimeException, IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, image.getFormat(), outputStream);
            image.setImage(outputStream.toByteArray());
        }
    }
    @NonNull
    private CompletableFuture<Void> errorDetected(
            @NonNull String uuid,
            @NonNull String method,
            @NonNull CompletableFuture<Void> future,
            @NonNull Exception e,
            @NonNull Semaphore semaphore
    ){
        semaphore.release();
        Map<String, String> error = errors.getOrDefault(uuid, new HashMap<>());
        error.put(method, e.getMessage());
        errors.put(uuid, error);
        future.completeExceptionally(e);
        return future;
    }

    @SneakyThrows
    @Transactional
    @Async
    @Override
    public CompletableFuture<KafkaMessage> execute(@NonNull KafkaMessageTransforms message) {
        CompletableFuture<KafkaMessage> future = new CompletableFuture<>();
        CompletableFuture[] states = new CompletableFuture[5];
        ImageDTO image = new ImageDTO(message.name(), message.format().toString(), message.image());
        Semaphore semaphore = new Semaphore(1);
        Runnable[] transformations = {
                () -> states[0] = (message.transformation().resize() != null)
                        ? resize(message.UUID(), message.transformation().resize(), image, semaphore)
                        : CompletableFuture.completedFuture(null),

                () -> states[1] = (message.transformation().crop() != null)
                        ? crop(message.UUID(), message.transformation().crop(), image, semaphore)
                        : CompletableFuture.completedFuture(null),

                () -> states[2] = (message.transformation().rotate() != null)
                        ? rotate(message.UUID(), message.transformation().rotate(), image, semaphore)
                        : CompletableFuture.completedFuture(null),

                () -> states[3] = (message.transformation().format() != null)
                        ? format(message.UUID(), message.transformation().format(), image, semaphore)
                        : CompletableFuture.completedFuture(null),

                () -> states[4] = (message.transformation().filters() != null)
                        ? filter(message.UUID(), message.transformation().filters(), image, semaphore)
                        : CompletableFuture.completedFuture(null)
        };

        for (Runnable transformationOp : transformations) {
            transformationOp.run();
            System.err.println(errors);
        }

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
    public CompletableFuture<Void> resize(
            @NonNull String uuid,
            @NonNull Resize value,
            @NonNull ImageDTO image,
            @NonNull Semaphore semaphore
    ) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        BufferedImage resizedImage;
        semaphore.acquireUninterruptibly();

        try(InputStream inputStream = new ByteArrayInputStream(image.getImage())) {
            resizedImage = transformUseCase.resize(ImageIO.read(inputStream), value);
            setImage(image, resizedImage);
        }catch (Exception e) {
            return errorDetected(uuid, "resize", future, e, semaphore);
        }

        semaphore.release();
        future.complete(null);
        return future;
    }
    @Override
    public CompletableFuture<Void> crop(String uuid,Crop value, ImageDTO image ,Semaphore semaphore) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        semaphore.acquireUninterruptibly();

        try(InputStream inputStream = new ByteArrayInputStream(image.getImage())) {
            BufferedImage resizedImage = transformUseCase.crop(ImageIO.read(inputStream), value);
            setImage(image, resizedImage);
        }catch (Exception e){
            return errorDetected(uuid,"crop",future,e,semaphore);
        }

        semaphore.release();
        future.complete(null);
        return future;
    }

    @Override
    public CompletableFuture<Void> rotate(
            @NonNull String uuid,
            @NonNull Integer value,
            @NonNull ImageDTO image,
            @NonNull Semaphore semaphore
    ) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        semaphore.acquireUninterruptibly();

        try(InputStream inputStream = new ByteArrayInputStream(image.getImage())) {
            BufferedImage resizedImage = transformUseCase.rotate(ImageIO.read(inputStream), value);
            setImage(image, resizedImage);
        }catch (Exception e){
            return errorDetected(uuid,"rotate",future,e,semaphore);
        }

        semaphore.release();
        future.complete(null);
        return future;
    }

    @Override
    public CompletableFuture<Void> format(
            @NonNull String uuid,
            @NonNull FormatImage value,
            @NonNull ImageDTO image,
            @NonNull Semaphore semaphore
    ) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        semaphore.acquireUninterruptibly();

        try{
            transformUseCase.format(image, value);
        }catch (Exception e) {
            return errorDetected(uuid,"format",future,e,semaphore);
        }

        semaphore.release();
        future.complete(null);
        return future;
    }
    @NonNull
    @Override
    public CompletableFuture<Void> filter(
            @NonNull String uuid,
            @NonNull Filters value,
            @NonNull ImageDTO image,
            @NonNull Semaphore semaphore
    ) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        CompletableFuture[] filtersFuture = new CompletableFuture[2];
        Semaphore semaphore1 = new Semaphore(1);

        semaphore.acquireUninterruptibly();
        filtersFuture[0] = grayscale(uuid,image, semaphore1);
        filtersFuture[1] = sepia(uuid,image, semaphore1);

        CompletableFuture.allOf(filtersFuture).thenRun(() -> {
            semaphore.release();
            future.complete(null);
        });

        return future;
    }
    @NonNull
    @Override
    public CompletableFuture<Void> grayscale(
            @NonNull String uuid,
            @NonNull ImageDTO image,
            @NonNull Semaphore semaphore
    ) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        semaphore.acquireUninterruptibly();

        try(InputStream inputStream = new ByteArrayInputStream(image.getImage())) {
            BufferedImage resizedImage = transformUseCase.grayscale(ImageIO.read(inputStream));
            setImage(image, resizedImage);
        }catch (Exception e){
            return errorDetected(uuid,"grayscale",future,e,semaphore);
        }

        semaphore.release();
        future.complete(null);
        return future;
    }
    @NonNull
    @Override
    public CompletableFuture<Void> sepia(
            @NonNull String uuid,
            @NonNull ImageDTO image,
            @NonNull Semaphore semaphore
    ) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        semaphore.acquireUninterruptibly();

        try(InputStream inputStream = new ByteArrayInputStream(image.getImage())) {
            BufferedImage resizedImage = transformUseCase.sepia(ImageIO.read(inputStream));
            setImage(image, resizedImage);
        }catch (Exception e){
            return errorDetected(uuid,"sepia",future,e,semaphore);
        }

        semaphore.release();
        future.complete(null);
        return future;
    }
}
