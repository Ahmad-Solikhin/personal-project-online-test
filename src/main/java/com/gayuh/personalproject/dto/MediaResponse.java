package com.gayuh.personalproject.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Arrays;
import java.util.Objects;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MediaResponse(
        String contentType,
        String fileName,
        byte[] media
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaResponse response = (MediaResponse) o;
        return Objects.equals(contentType, response.contentType) && Objects.equals(fileName, response.fileName) && Arrays.equals(media, response.media);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(contentType, fileName);
        result = 31 * result + Arrays.hashCode(media);
        return result;
    }

    @Override
    public String toString() {
        return "MediaResponse{" +
                "contentType='" + contentType + '\'' +
                ", fileName='" + fileName + '\'' +
                ", media=" + Arrays.toString(media) +
                '}';
    }
}
