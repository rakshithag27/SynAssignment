package com.synassignment01.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.synassignment01.dto.CloudinaryResponse;
import com.synassignment01.dto.CloudinaryViewResponse;
import com.synassignment01.exceptions.CloudinaryException;
import com.synassignment01.model.CloudinaryInfo;
import com.synassignment01.model.UserInfo;
import com.synassignment01.repository.CloudinaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private CloudinaryRepository cloudinaryRepository;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Helper to mock Cloudinary uploader
    private Uploader mockUploader(Map<String, Object> response) throws IOException {
        Uploader uploader = mock(Uploader.class);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(response);
        when(uploader.destroy(anyString(), anyMap())).thenReturn(response);
        when(cloudinary.uploader()).thenReturn(uploader);
        return uploader;
    }

    @Test
    void uploadImage_shouldReturnCloudinaryResponse_whenSuccess() throws Exception {
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("public_id", "public123");
        uploadResult.put("secure_url", "https://image.url/image.jpg");
        mockUploader(uploadResult);

        UserInfo user = new UserInfo();
        user.setUsername("testuser");

        CloudinaryResponse response = cloudinaryService.uploadImage(
                new MockMultipartFile("file", "image.jpg", "image/jpeg", "data".getBytes()), user);

        assertEquals("public123", response.getPublicId());
        assertEquals("https://image.url/image.jpg", response.getImageUrl());
        verify(cloudinaryRepository, times(1)).save(any(CloudinaryInfo.class));
    }

    @Test
    void uploadImage_shouldThrowIOException_whenCloudinaryThrowsIOException() throws Exception {
        Uploader uploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenThrow(new IOException("IO error"));

        UserInfo user = new UserInfo();

        CloudinaryException ex = assertThrows(CloudinaryException.class, () -> {
            cloudinaryService.uploadImage(mock(MultipartFile.class), user);
        });
        assertTrue(ex.getMessage().contains("Cloudinary Exception was thrown"));
    }

    @Test
    void uploadImage_shouldThrowCloudinaryException_whenOtherExceptionOccurs() throws Exception {
        Uploader uploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenThrow(new RuntimeException("Some error"));

        UserInfo user = new UserInfo();

        CloudinaryException ex = assertThrows(CloudinaryException.class, () -> {
            cloudinaryService.uploadImage(mock(MultipartFile.class), user);
        });
        assertEquals("Cloudinary Exception was thrown: Cannot invoke \"Object.toString()\" because the return value of \"java.util.Map.get(Object)\" is null", ex.getMessage());
    }

    @Test
    void deleteImage_shouldReturnOkString_whenSuccess() throws Exception {
        Map<String, Object> deleteResult = new HashMap<>();
        deleteResult.put("result", "ok");
        mockUploader(deleteResult);

        CloudinaryInfo savedInfo = new CloudinaryInfo();
        savedInfo.setDeleted(false);
        when(cloudinaryRepository.findByPublicId("public123")).thenReturn(savedInfo);

        String result = cloudinaryService.deleteImage("public123");

        assertEquals("ok", result);
        assertTrue(savedInfo.isDeleted());
        verify(cloudinaryRepository, times(1)).save(savedInfo);
    }

    @Test
    void deleteImage_shouldThrowIOException_whenCloudinaryThrowsIOException() throws Exception {
        Uploader uploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(anyString(), anyMap())).thenThrow(new IOException("IO delete error"));

        IOException ex = assertThrows(IOException.class, () -> {
            cloudinaryService.deleteImage("public123");
        });
        assertTrue(ex.getMessage().contains("IO delete error"));
    }

    @Test
    void deleteImage_shouldThrowCloudinaryException_whenOtherExceptionOccurs() throws Exception {
        Uploader uploader = mock(Uploader.class);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(anyString(), anyMap())).thenThrow(new RuntimeException("Delete error"));

        CloudinaryException ex = assertThrows(CloudinaryException.class, () -> {
            cloudinaryService.deleteImage("public123");
        });
        assertEquals("Cloudinary Exception was thrown: Delete error", ex.getMessage());
    }

    @Test
    void viewImage_shouldReturnCloudinaryViewResponse_whenSuccess() throws CloudinaryException {
        CloudinaryInfo image1 = new CloudinaryInfo();
        image1.setImageUrl("http://url1.com/image.jpg");
        CloudinaryInfo image2 = new CloudinaryInfo();
        image2.setImageUrl("http://url2.com/image2.jpg");

        List<CloudinaryInfo> images = Arrays.asList(image1, image2);
        when(cloudinaryRepository.findAllByUserUsername("testuser")).thenReturn(images);

        CloudinaryViewResponse response = cloudinaryService.viewImage("testuser");

        assertNotNull(response);
        assertEquals(2, response.getUserAssociatedImageurls().size());
        assertEquals("http://url1.com/image.jpg", response.getUserAssociatedImageurls().get(0));
        assertEquals("http://url2.com/image2.jpg", response.getUserAssociatedImageurls().get(1));
    }

    @Test
    void viewImage_shouldThrowCloudinaryException_whenExceptionOccurs() throws CloudinaryException {
        when(cloudinaryRepository.findAllByUserUsername("testuser")).thenThrow(new RuntimeException("DB error"));

        CloudinaryException ex = assertThrows(CloudinaryException.class, () -> {
            cloudinaryService.viewImage("testuser");
        });
        assertEquals("Cloudinary Exception was thrown: DB error", ex.getMessage());
    }
}
