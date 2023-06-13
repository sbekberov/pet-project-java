package spd.trello.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spd.trello.domain.Attachment;
import spd.trello.domain.Board;
import spd.trello.exception.BadRequestException;
import spd.trello.exception.ResourceNotFoundException;
import spd.trello.service.AttachmentService;

import java.time.LocalDateTime;
import java.util.UUID;


@RestController
@RequestMapping("/attachments")
@Log4j2
public class AttachmentController extends AbstractController<Attachment, AttachmentService> {
    public AttachmentController(AttachmentService service) {
        super(service);
    }

    @Autowired
    AttachmentService attachmentService;

    @PostMapping("/upload")
    public ResponseEntity<Attachment> create(@RequestParam(value = "file") MultipartFile multipartFile,
                                             @RequestParam(required = false) UUID cardId,
                                             @RequestParam(value = "createdBy") String createdBy) {
            return new ResponseEntity<>(attachmentService.save(multipartFile, cardId, createdBy), HttpStatus.OK);
        }



    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> getFile(@PathVariable UUID id) {
        Attachment attachment = attachmentService.load(id);
        log.debug("Downloading attachment.");
        return ResponseEntity.ok()
                .contentType(MediaType.MULTIPART_MIXED)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getName() + "\"")
                .body(new ByteArrayResource(attachment.getMultiPartBytes()));
    }



}




