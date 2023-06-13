package spd.trello.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import spd.trello.domain.Attachment;
import spd.trello.exception.BadRequestException;
import spd.trello.exception.FileCanNotBeUpload;
import spd.trello.repository.AttachmentRepository;
import spd.trello.validators.AttachmentValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Service
@Log4j2
public class AttachmentService extends AbstractService<Attachment, AttachmentRepository, AttachmentValidator> {
    @Autowired
    public AttachmentService(AttachmentRepository repository,AttachmentValidator attachmentValidator) {
        super(repository,attachmentValidator);
    }

    @Value("${app.saveFolder}")
    private String path;

    public Attachment createAttachment(String name, UUID cardId, String createdBy) {
        Attachment attachment = new Attachment();
        attachment.setName(name);
        attachment.setCreatedBy(createdBy);
        attachment.setCreatedDate(LocalDateTime.now().withNano(0));
        attachment.setLink(path);
        attachment.setCardId(cardId);
        return repository.save(attachment);
    }
    @Value("${app.saveToDb}")
    boolean saveToDb;
    @Value("${file.maxSize}")
    double maxSize;
    public Attachment save (MultipartFile multipartFile, UUID cardId, String createdBy){
        if(saveToDb && multipartFile.getSize()<maxSize){
           return saveToDb(multipartFile, cardId, createdBy);
        }
        return saveToFile(multipartFile, cardId, createdBy);
    }
    public Attachment saveToDb(MultipartFile multipartFile, UUID cardId, String createdBy) {
        Attachment attachment = new Attachment();
        try {
            attachment.setMultiPartBytes(multipartFile.getBytes());
            attachment.setName(multipartFile.getOriginalFilename());
            attachment.setCardId(cardId);
            attachment.setCreatedBy(createdBy);
            attachment.setCreatedDate(LocalDateTime.now().withNano(0));
        } catch (Exception e) {
            e.printStackTrace();
            return attachment;
        }
        return repository.save(attachment);
    }

    public Attachment saveToFile(MultipartFile file, UUID cardId, String createdBy) {
        try {
            Path root = Paths.get(path);
            Files.copy(file.getInputStream(), root.resolve(Objects.requireNonNull(file.getOriginalFilename())));
            return createAttachment(file.getOriginalFilename(), cardId, createdBy);
        } catch (Exception e) {
            log.error("File can not be upload ");
            throw new FileCanNotBeUpload();
        }
    }


    public Attachment load(UUID id) {
        Attachment attachment = repository.findById(id).orElse(null);
        if (!(attachment.getLink() == null)) {
            try {
                attachment.setMultiPartBytes(Files.readAllBytes(Paths.get(attachment.getLink() + attachment.getName())));
            } catch (IOException e) {
                log.error("File can not be download");
                e.printStackTrace();
            }
        }
        return attachment;
    }

    @Override
    public Attachment update(Attachment entity) {
        Attachment oldAttachment = findById(entity.getId());
        entity.setUpdatedDate(LocalDateTime.now().withNano(0));
        entity.setCreatedBy(oldAttachment.getCreatedBy());
        entity.setCreatedDate(oldAttachment.getCreatedDate());
        entity.setCardId(oldAttachment.getCardId());

        if (entity.getName() == null && oldAttachment.getName() != null) {
            entity.setName(oldAttachment.getName());
        }
        if (entity.getLink() == null && oldAttachment.getLink() != null) {
            entity.setLink(oldAttachment.getLink());
        }
        if (entity.getMultiPartBytes() == null && oldAttachment.getMultiPartBytes() != null) {
            entity.setMultiPartBytes(oldAttachment.getMultiPartBytes());
        }

        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public void deleteAttachmentsForCard(UUID cardId) {
        repository.findAllByCardId(cardId).forEach(attachment -> delete(attachment.getId()));
    }
}


