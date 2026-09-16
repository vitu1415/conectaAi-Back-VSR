package com.example.conectaaivrs.service;

import com.example.conectaaivrs.domain.storage.dto.UploadResponse;
import com.example.conectaaivrs.infra.config.AwsS3Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@Service
public class StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageService.class);

    private final AwsS3Config awsS3Config;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.cloudfront.domain-name}")
    private String cloudFront;

    public StorageService(AwsS3Config awsS3Config) {
        this.awsS3Config = awsS3Config;
    }

    public UploadResponse uploadImagem(MultipartFile arquivo, String pasta) {
        try {
            String nomeArquivo = gerarNomeArquivo(arquivo.getOriginalFilename());
            String chaveS3 = pasta + "/" + nomeArquivo;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(chaveS3)
                    .contentType(arquivo.getContentType())
                    .build();

            S3Client s3Client = awsS3Config.s3Client();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(arquivo.getInputStream(), arquivo.getSize()));

            String url = gerarUrl(chaveS3);

            return new UploadResponse(url, nomeArquivo, arquivo.getContentType());
        } catch (IOException e) {
            log.error("Erro ao ler arquivo para upload: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao ler o arquivo enviado");
        } catch (Exception e) {
            log.error("Erro ao fazer upload para S3: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro ao fazer upload para o armazenamento");
        }
    }

    public UploadResponse uploadMidiaPost(MultipartFile arquivo) {
        try {
            String nomeArquivo = gerarNomeArquivo(arquivo.getOriginalFilename());
            String chaveS3 = "posts/" + nomeArquivo;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(chaveS3)
                    .contentType(arquivo.getContentType())
                    .build();

            S3Client s3Client = awsS3Config.s3Client();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(arquivo.getInputStream(), arquivo.getSize()));

            String url = gerarUrl(chaveS3);

            return new UploadResponse(url, nomeArquivo, arquivo.getContentType());
        } catch (IOException e) {
            log.error("Erro ao ler arquivo para upload: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao ler o arquivo enviado");
        } catch (Exception e) {
            log.error("Erro ao fazer upload para S3: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro ao fazer upload para o armazenamento");
        }
    }

    private String gerarNomeArquivo(String nomeOriginal) {
        String extensao = "";
        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        }
        return UUID.randomUUID() + extensao;
    }

    private String gerarUrl(String chaveS3) {
        return String.format("https://%s/%s", cloudFront, chaveS3);
    }
}
