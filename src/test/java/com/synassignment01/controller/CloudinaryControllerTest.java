package com.synassignment01.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synassignment01.dto.CloudinaryResponse;
import com.synassignment01.exceptions.CloudinaryException;
import com.synassignment01.model.UserInfo;
import com.synassignment01.repository.UserRepository;
import com.synassignment01.security.JWTUtil;
import com.synassignment01.service.CloudinaryService;
import com.synassignment01.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CloudinaryController.class)
@Import(CloudinaryControllerTest.TestConfig.class)
class CloudinaryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private UserRepository userRepository;

    private UserInfo mockUser;

    @BeforeEach
    void setup() {
        mockUser = new UserInfo();
        mockUser.setUsername("testuser");

        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(context);

        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);
    }

    @Test
    void uploadImage_shouldReturnOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "image".getBytes());

        CloudinaryResponse response = new CloudinaryResponse("http://url.com/image.jpg", "public_id_123");
        when(cloudinaryService.uploadImage(any(), eq(mockUser))).thenReturn(response);

        mockMvc.perform(multipart("/synassignment/images/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value("http://url.com/image.jpg"))
                .andExpect(jsonPath("$.publicId").value("public_id_123"));
    }

    @Test
    void uploadImage_shouldReturnServerError_onException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "image".getBytes());

        when(cloudinaryService.uploadImage(any(MultipartFile.class), any()))
                .thenThrow(new RuntimeException("Simulated upload failure"));


        mockMvc.perform(multipart("/synassignment/images/upload")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Upload failed: Simulated upload failure"));
    }

    @Test
    void deleteImage_shouldReturnOk() throws Exception {
        when(cloudinaryService.deleteImage("publicId123")).thenReturn("ok");

        mockMvc.perform(delete("/synassignment/images/delete")
                        .param("publicId", "publicId123"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }

    @Test
    void viewImage_shouldReturnOk() throws Exception {
        when(cloudinaryService.viewImage("testuser"))
                .thenReturn(new com.synassignment01.dto.CloudinaryViewResponse(List.of("http://url.com/img1.jpg")));

        mockMvc.perform(get("/synassignment/images/view")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userAssociatedImageurls[0]").value("http://url.com/img1.jpg"));
    }

    @Test
    void viewImage_shouldReturnServerError_onException() throws Exception {
        when(cloudinaryService.viewImage("testuser")).thenThrow(new CloudinaryException("View failed"));

        mockMvc.perform(get("/synassignment/images/view")
                        .param("username", "testuser"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Image view failed: Cloudinary Exception was thrown: View failed"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CloudinaryService cloudinaryService() {
            return mock(CloudinaryService.class);  // Mockito mock here
        }

        @Bean
        public UserRepository userRepository() {
            return mock(UserRepository.class); // Mockito mock here
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable()) // Important for POST tests, since WebMvc does not do cors
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }
}