package roadmap.backend.image_processing_service.transforms.domain.usecase.implement;

import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import roadmap.backend.image_processing_service.transforms.domain.dto.ImageDTO;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Crop;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Filters;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.FormatImage;
import roadmap.backend.image_processing_service.transforms.domain.transformation.components.Resize;
import roadmap.backend.image_processing_service.transforms.domain.usecase.TransformationsUsecase;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;

@Service
@Primary
public class TrasformationsUsecaseImpl implements TransformationsUsecase {


    @Override
    public BufferedImage resize(@NonNull BufferedImage image,@NonNull Resize resize) throws Exception {
        BufferedImage newImage = new BufferedImage(resize.height(),resize.width(), image.getType());
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, resize.width(), resize.height(), null);
        g.dispose();
        return newImage;
    }

    @Override
    public BufferedImage crop(@NonNull BufferedImage image,@NonNull Crop crop) throws Exception {
        if(crop.width() + crop.x() > image.getWidth() || crop.height() + crop.y() > image.getHeight()){
            throw new Exception("The crop dimensions are out of range");
        }
        return image.getSubimage(crop.x(), crop.y(), crop.width(), crop.height());
    }

    @Override
    public BufferedImage rotate(@NonNull BufferedImage image, Integer angle) throws Exception {
        Graphics2D g = image.createGraphics();
        g.rotate(Math.toRadians(angle), (double) image.getWidth() / 2, (double) image.getHeight() / 2);
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(angle), (double) image.getWidth() / 2, (double) image.getHeight() / 2);
        g.setTransform(at);

        g.dispose();

        return image;
    }
    @Override
    public ImageDTO format(@NonNull ImageDTO image, FormatImage format) throws Exception {
        BufferedImage newImage;
        try(ByteArrayInputStream inputStream = new ByteArrayInputStream(image.getImage())){
            newImage = ImageIO.read(inputStream);
        }
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            ImageIO.write(newImage, format.toString(), outputStream);
            image.setImage(outputStream.toByteArray());
        }
        image.setFormat(format.toString());
        return image;
    }

    @Override
    public BufferedImage grayscale(@NonNull BufferedImage image) throws Exception {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        WritableRaster rasterIn = image.getRaster();
        WritableRaster rasterOut = newImage.getRaster();
        int[] rgb = new int[3];
        int[] gray = new int[1];
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                rasterIn.getPixel(x, y, rgb);
                gray[0] = (int) (rgb[0] * 0.299 + rgb[1] * 0.587 + rgb[2] * 0.114);
                rasterOut.setPixel(x, y, gray);
            }
        }
        return newImage;
    }
    @Override
    public BufferedImage sepia(BufferedImage image) throws Exception {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster rasterIn = image.getRaster();
        WritableRaster rasterOut = newImage.getRaster();
        int[] rgb = new int[3];
        int[] gray = new int[1];
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                rasterIn.getPixel(x, y, rgb);
                gray[0] = (int) (rgb[0] * 0.393 + rgb[1] * 0.769 + rgb[2] * 0.189);
                rasterOut.setPixel(x, y, gray);
            }
        }
        return newImage;
    }
}
