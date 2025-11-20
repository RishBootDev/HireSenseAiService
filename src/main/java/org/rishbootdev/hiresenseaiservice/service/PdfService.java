package org.rishbootdev.hiresenseaiservice.service;


import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PdfService {

    public String getTextFromFile(MultipartFile file) {
        try {
            Resource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            TikaDocumentReader reader = new TikaDocumentReader(resource);
            List<org.springframework.ai.document.Document> docs = reader.read();

            StringBuilder sb = new StringBuilder();
            for (Document doc : docs) {
                sb.append(doc.getText()).append("\n\n");
                System.out.println(sb.toString());
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
           throw new RuntimeException(e.getMessage());
        }
    }
}

