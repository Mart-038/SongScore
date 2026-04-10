package nl.miwnn.ch19.mart.songscore.service;

/*
 * @author Mart Stukje
 * Handles business logic regarding images
 * */

import jakarta.persistence.EntityNotFoundException;
import nl.miwnn.ch19.mart.songscore.model.Image;
import nl.miwnn.ch19.mart.songscore.repository.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found in database"));
    }

    public Image saveImage(MultipartFile imageFile, String usedFor) {
        Image image = new Image();
        try {
            image.setData(imageFile.getBytes());
        } catch (IOException ioException) {
            throw new IllegalStateException("Dit bestand kon niet worden opgeslagen", ioException);
        }
        image.setContentType(imageFile.getContentType());
        image.setImageType(usedFor);
        return imageRepository.save(image);
    }

    public List<Image> getImagesUsedFor(String usedFor) {
        return imageRepository.findByImageTypeIs(usedFor);
    }
}
