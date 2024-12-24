package edu.ncsu.zybook.mapper;

import edu.ncsu.zybook.DTO.ContentReadDTO;
import edu.ncsu.zybook.domain.model.Content;
import edu.ncsu.zybook.domain.model.ImageContent;
import edu.ncsu.zybook.domain.model.TextContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Mapper(componentModel = "spring")
public interface ContentReadDTOMapper {


    default ContentReadDTO toDTO(Content content) {
        if (content instanceof TextContent) {
            return toDTO((TextContent) content);
        } else if (content instanceof ImageContent) {
            return toDTO((ImageContent) content);
        } else {
            // Log unsupported content type if needed
            return null;
        }
    }

    // Map TextContent to ContentReadDTO
    ContentReadDTO toDTO(TextContent content);

    // Map ImageContent to ContentReadDTO
    @Mapping(source = "data", target = "data", qualifiedByName = "byteArrayToBase64")
    ContentReadDTO toDTO(ImageContent content);

    @Mapping(source = "image", target = "data",  qualifiedByName = "multipartToByteArray")
    ImageContent toEntity(ContentReadDTO content);

    // Define a custom mapping method for byte[] to Base64 String conversion
    @Named("byteArrayToBase64")
    default String byteArrayToBase64(byte[] data) {
        return data != null ? Base64.getEncoder().encodeToString(data) : null;
    }

    @Named("multipartToByteArray")
    default byte[] multipartToByteArray(MultipartFile file) {
        try {
            String contentType = file.getContentType();
//            if (!"image/png".equals(contentType) && !"image/jpeg".equals(contentType)) {
//                throw new IllegalArgumentException("File must be a PNG or JPEG image");
//            }
            // Convert MultipartFile to byte array
            return file != null ? file.getBytes() : null;
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert MultipartFile to byte array", e);
        }
    }
}
