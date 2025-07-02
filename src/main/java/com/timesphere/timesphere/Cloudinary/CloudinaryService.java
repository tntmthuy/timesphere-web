package com.timesphere.timesphere.Cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final CloudinaryProperties properties;

    private Cloudinary getCloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", properties.getCloudName(),
                "api_key", properties.getApiKey(),
                "api_secret", properties.getApiSecret()
        ));
    }

    public CloudinaryUploadResult uploadFile(MultipartFile file, String folder) throws IOException {
        Cloudinary cloudinary = getCloudinary();

        Map<String, Object> options = new HashMap<>();
        options.put("folder", folder); // ví dụ: "avatars/", "attachments/comments/"

        Map<String, Object> uploadResult = cloudinary.uploader()
                .upload(file.getBytes(), options);

        return CloudinaryUploadResult.builder()
                .url(uploadResult.get("secure_url").toString())
                .publicId(uploadResult.get("public_id").toString())
                .format(uploadResult.get("format").toString())
                .resourceType(uploadResult.get("resource_type").toString())
                .build();
    }

    public void deleteFile(String publicId) throws IOException {
        getCloudinary().uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
