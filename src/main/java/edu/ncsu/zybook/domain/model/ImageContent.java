package edu.ncsu.zybook.domain.model;

import jakarta.persistence.Lob;
import jdk.jfr.Label;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageContent extends Content {
    @Lob
    private byte[] data;
}
