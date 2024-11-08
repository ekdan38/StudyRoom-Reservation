package com.jeong.studyroomreservation.domain.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.jeong.studyroomreservation.domain.error.ErrorCode;
import com.jeong.studyroomreservation.domain.error.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "[S3ImageUtil]")
public class S3ImageUtil {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.S3.bucketName}")
    private String bucketName;

    // MultipartFile 받고 null인지 originalFileName이 존재하는지 검사
    public String upload(MultipartFile image){
        if(image.isEmpty() || Objects.isNull(image.getOriginalFilename())){
            throw new S3Exception(ErrorCode.S3_EMPTY_FILE);
        }
        return uploadImage(image);
    }

    // validateFileExtention호출해서 확장자명 존재하는지 확인.
    // 이후 s3에 이미지 전송
    private String uploadImage(MultipartFile image){
        validateImageFileExtention(image.getOriginalFilename());
        try{
            return uploadImageToS3(image);

        } catch (IOException e) {
            throw new S3Exception(ErrorCode.S3_EXCEPTION_ON_IMAGE_UPLOAD);

        }
    }


    private void validateImageFileExtention(String originalFileName){
        int pos = originalFileName.lastIndexOf(".");
        if(pos == -1){
            throw new S3Exception(ErrorCode.S3_NO_FILE_EXTENTION);
        }

        String extention = originalFileName.substring(pos + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if(!allowedExtentionList.contains(extention)){
            throw new S3Exception(ErrorCode.S3_INVALID_EXTENTION);
        }
    }

    private String uploadImageToS3(MultipartFile image) throws IOException{
        String originalFilename = image.getOriginalFilename();
        String extention = originalFilename.substring(originalFilename.lastIndexOf(".")); //.jpg //확장자 명
        String s3FileName = UUID.randomUUID().toString() + extention; // uuid + .jpg

        InputStream inputStream = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(inputStream); //실제 이미지

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/" + extention);
        metadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try{
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest); // s3에 저장.
        } catch (Exception e){
            throw new S3Exception(ErrorCode.S3_EXCEPTION_PUT_OBJECT);
        } finally {
            byteArrayInputStream.close();
            inputStream.close();
        }
        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    private String createStoreName(MultipartFile image){
        String originalFilename = image.getOriginalFilename();
        String extention = originalFilename.substring(originalFilename.lastIndexOf(".")); //.jpg //확장자 명
        return UUID.randomUUID().toString() + extention; // uuid + .jpg
    }

    public void deleteImageFromS3(String imageAddress){
        String key = getKeyFromImageAddress(imageAddress);
        try{
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        }catch (Exception e){
            throw new S3Exception(ErrorCode.S3_EXCEPTION_DELETE);
        }
    }

    private String getKeyFromImageAddress(String imageAddress){
        try{
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        }catch (MalformedURLException | UnsupportedEncodingException e){
            throw new S3Exception(ErrorCode.S3_EXCEPTION_DELETE);
        }
    }
}
