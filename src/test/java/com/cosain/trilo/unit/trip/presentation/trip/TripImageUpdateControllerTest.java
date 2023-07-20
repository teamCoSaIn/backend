package com.cosain.trilo.unit.trip.presentation.trip;

import com.cosain.trilo.support.RestControllerTest;
import com.cosain.trilo.trip.application.trip.service.trip_image_update.TripImageUpdateCommand;
import com.cosain.trilo.trip.application.trip.service.trip_image_update.TripImageUpdateService;
import com.cosain.trilo.trip.presentation.trip.TripImageUpdateController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("여행 이미지 수정 API 테스트")
@WebMvcTest(TripImageUpdateController.class)
public class TripImageUpdateControllerTest extends RestControllerTest {

    @MockBean
    private TripImageUpdateService tripImageUpdateService;

    private final static String ACCESS_TOKEN = "Bearer accessToken";

    @Test
    @DisplayName("실제 jpeg -> 성공")
    public void testRealJpegImage() throws Exception {
        long tripperId = 2L;
        mockingForLoginUserAnnotation(tripperId);
        Long tripId = 1L;

        String name = "image";
        String fileName = "test-jpeg-image.jpeg";
        String filePath = "src/test/resources/testFiles/" + fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "image/jpeg";
        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);

        String imageURL = String.format("https://{이미지 파일 저장소 주소}/trips/%s/{이미지 파일명}.jpeg", tripId);
        given(tripImageUpdateService.updateTripImage(any(TripImageUpdateCommand.class)))
                .willReturn(imageURL);

        mockMvc.perform(multipart(POST, "/api/trips/{tripId}/image/update", tripId)
                        .file(multipartFile)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(tripId))
                .andExpect(jsonPath("$.imageURL").value(imageURL));

        verify(tripImageUpdateService, times(1)).updateTripImage(any(TripImageUpdateCommand.class));
    }

    @Test
    @DisplayName("실제 gif -> 성공")
    public void testRealGifImage() throws Exception {
        long tripperId = 2L;
        mockingForLoginUserAnnotation(tripperId);
        Long tripId = 1L;

        String name = "image";
        String fileName = "test-gif-image.gif";
        String filePath = "src/test/resources/testFiles/" + fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "image/gif";


        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);

        String imageURL = String.format("https://{이미지 파일 저장소 주소}/trips/%s/{이미지 파일명}.gif", tripId);
        given(tripImageUpdateService.updateTripImage(any(TripImageUpdateCommand.class))).willReturn(imageURL);

        mockMvc.perform(multipart(POST, "/api/trips/{tripId}/image/update", tripId)
                        .file(multipartFile)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(tripId))
                .andExpect(jsonPath("$.imageURL").value(imageURL));

        verify(tripImageUpdateService, times(1)).updateTripImage(any(TripImageUpdateCommand.class));
    }

    @Test
    @DisplayName("실제 png -> 성공")
    public void testRealPngImage() throws Exception {
        long tripperId = 2L;
        mockingForLoginUserAnnotation(tripperId);
        Long tripId = 1L;

        String name = "image";
        String fileName = "test-png-image.png";
        String filePath = "src/test/resources/testFiles/" + fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "image/png";
        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);

        String imageURL = String.format("https://{이미지 파일 저장소 주소}/trips/%s/{이미지 파일명}.png", tripId);
        given(tripImageUpdateService.updateTripImage(any(TripImageUpdateCommand.class)))
                .willReturn(imageURL);

        mockMvc.perform(multipart(POST, "/api/trips/{tripId}/image/update", tripId)
                        .file(multipartFile)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(tripId))
                .andExpect(jsonPath("$.imageURL").value(imageURL));

        verify(tripImageUpdateService, times(1)).updateTripImage(any(TripImageUpdateCommand.class));
    }

    @Test
    @DisplayName("실제 webp -> 성공")
    public void testRealWebpImage() throws Exception {
        Long tripperId = 2L;
        mockingForLoginUserAnnotation(tripperId);
        Long tripId = 1L;

        String name = "image";
        String fileName = "test-webp-image.webp";
        String filePath = "src/test/resources/testFiles/" + fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "image/webp";

        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);
        String imageURL = String.format("https://{이미지 파일 저장소 주소}/trips/%s/{이미지 파일명}.webp", tripId);
        given(tripImageUpdateService.updateTripImage(any(TripImageUpdateCommand.class)))
                .willReturn(imageURL);

        mockMvc.perform(multipart(POST, "/api/trips/{tripId}/image/update", tripId)
                        .file(multipartFile)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(tripId))
                .andExpect(jsonPath("$.imageURL").value(imageURL));

        verify(tripImageUpdateService, times(1)).updateTripImage(any(TripImageUpdateCommand.class));
    }

    @Test
    @DisplayName("미인증 사용자 -> 인증 실패 401")
    public void testUnAuthorizedUser() throws Exception {
        Long tripId = 1L;

        String name = "image";
        String fileName = "test-jpeg-image.jpeg";
        String filePath = "src/test/resources/testFiles/" + fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "image/jpeg";

        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);
        mockMvc.perform(multipart(POST, "/api/trips/{tripId}/image/update", tripId)
                        .file(multipartFile)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value("auth-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(tripImageUpdateService, times(0)).updateTripImage(any(TripImageUpdateCommand.class));
    }

    @Test
    @DisplayName("비어 있는 파일 -> 파일 비어 있음 관련 400 에러 발생")
    public void emptyFileTest() throws Exception {
        long tripperId = 2L;
        mockingForLoginUserAnnotation(tripperId);

        // given
        Long tripId = 1L;
        String name = "image";
        String fileName = "empty-file.jpg";
        String filePath = "src/test/resources/testFiles/" + fileName;

        FileInputStream fileInputStream = new FileInputStream(filePath);
        MockMultipartFile multipartFile = new MockMultipartFile(name, fileInputStream);

        // when & then
        mockMvc.perform(multipart(POST, "/api/trips/{tripId}/image/update", tripId)
                        .file(multipartFile)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("file-0001"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(tripImageUpdateService, times(0)).updateTripImage(any(TripImageUpdateCommand.class));
    }

    @Test
    @DisplayName("tripId으로 숫자가 아닌 문자열 주입 -> 올바르지 않은 경로 변수 타입 400 에러")
    public void testInvalidTripId() throws Exception {
        mockingForLoginUserAnnotation();
        String invalidTripId = "가가가";

        String name = "image";
        String fileName = "test-jpeg-image.jpeg";
        String filePath = "src/test/resources/testFiles/" + fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "image/jpeg";

        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);
        mockMvc.perform(multipart(POST, "/api/trips/{tripId}/image/update", invalidTripId)
                        .file(multipartFile)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0004"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(tripImageUpdateService, times(0)).updateTripImage(any(TripImageUpdateCommand.class));
    }

    @Test
    @DisplayName("파일을 보내지 않음 -> 검증 오류 400 에러")
    public void testNotMultipartRequest() throws Exception {
        long tripperId = 2L;
        mockingForLoginUserAnnotation(tripperId);
        Long tripId = 1L;

        mockMvc.perform(post("/api/trips/{tripId}/image/update", tripId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("request-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].errorCode").value("file-0001"))
                .andExpect(jsonPath("$.errors[0].errorMessage").exists())
                .andExpect(jsonPath("$.errors[0].errorDetail").exists())
                .andExpect(jsonPath("$.errors[0].field").value("image"));

        verify(tripImageUpdateService, times(0)).updateTripImage(any(TripImageUpdateCommand.class));
    }

    @Test
    @DisplayName("MultipartFile에 파일 이름 없음 -> NoFileName 400 에러 발생")
    public void noFileName() throws Exception {
        long tripperId = 2L;
        mockingForLoginUserAnnotation(tripperId);
        Long tripId = 1L;

        String name = "image";
        String fileName = "test-jpeg-image.jpeg";
        String filePath = "src/test/resources/testFiles/" + fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);

        // MultipartFile에 파일 이름 정보가 전달되지 않은 특수상황 가정
        MockMultipartFile multipartFile = new MockMultipartFile(name, fileInputStream);

        mockMvc.perform(multipart(POST, "/api/trips/{tripId}/image/update", tripId)
                        .file(multipartFile)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("file-0002"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(tripImageUpdateService, times(0)).updateTripImage(any(TripImageUpdateCommand.class));
    }

    @Test
    @DisplayName("확장자 없음 -> NoFileExtension 400 예외 발생")
    public void noFileExtension() throws Exception {
        long tripperId = 2L;
        mockingForLoginUserAnnotation(tripperId);
        Long tripId = 1L;

        String name = "image";
        String fileName = "no-extension"; // 확장자 없음
        String filePath = "src/test/resources/testFiles/" + fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "application/octet-stream";

        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);

        mockMvc.perform(multipart(POST, "/api/trips/{tripId}/image/update", tripId)
                        .file(multipartFile)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("file-0003"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(tripImageUpdateService, times(0)).updateTripImage(any(TripImageUpdateCommand.class));
    }

    @Test
    @DisplayName("이미지 파일 확장자 아님 -> NotImageFileExtension 400 에러 발생")
    public void notImageFileExtension() throws Exception {
        long tripperId = 2L;
        mockingForLoginUserAnnotation(tripperId);
        Long tripId = 1L;

        String name = "image";
        String fileName = "not-image-extension.txt"; // 확장자가 이미지가 아님
        String filePath = "src/test/resources/testFiles/" + fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "text/plain";

        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);

        mockMvc.perform(multipart(POST, "/api/trips/{tripId}/image/update", tripId)
                        .file(multipartFile)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("file-0004"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(tripImageUpdateService, times(0)).updateTripImage(any(TripImageUpdateCommand.class));
    }

    @Test
    @DisplayName("이미지 아님 -> NotImageFile 400 예외 발생")
    public void noImageFile() throws Exception {
        long tripperId = 2L;
        mockingForLoginUserAnnotation(tripperId);
        Long tripId = 1L;

        String name = "image";
        String fileName = "no-image.jpg"; // 확장자는 jpg인데 실제로 이미지가 아님
        String filePath = "src/test/resources/testFiles/" + fileName;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        String contentType = "image/jpg";

        MockMultipartFile multipartFile = new MockMultipartFile(name, fileName, contentType, fileInputStream);

        mockMvc.perform(multipart(POST, "/api/trips/{tripId}/image/update", tripId)
                        .file(multipartFile)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("file-0005"))
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorDetail").exists());

        verify(tripImageUpdateService, times(0)).updateTripImage(any(TripImageUpdateCommand.class));
    }
}
