package com.example.demo.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * HTTP 캐시 관련 유틸리티 클래스
 * ETag 생성 및 조건부 요청 처리를 위한 헬퍼 메서드 제공
 */
@Component
public class CacheUtil {

    /**
     * 여러 값을 조합하여 ETag 생성
     * @param values ETag 생성에 사용할 값들
     * @return 생성된 ETag 문자열 (따옴표 포함)
     */
    public String generateETag(Object... values) {
        int hash = Objects.hash(values);
        return "\"" + Integer.toHexString(hash) + "\"";
    }

    /**
     * ID와 수정시간을 조합하여 ETag 생성
     * @param id 엔티티 ID
     * @param updateTime 마지막 수정 시간
     * @return 생성된 ETag 문자열
     */
    public String generateETag(Long id, LocalDateTime updateTime) {
        if (id == null || updateTime == null) {
            return null;
        }
        long timestamp = updateTime.toEpochSecond(ZoneOffset.UTC);
        return "\"" + id + "-" + timestamp + "\"";
    }

    /**
     * ETag 기반 조건부 요청 확인
     * @param etag 현재 리소스의 ETag
     * @param ifNoneMatch 클라이언트가 보낸 If-None-Match 헤더 값
     * @return 수정되지 않았으면 true
     */
    public boolean isNotModified(String etag, String ifNoneMatch) {
        return etag != null && etag.equals(ifNoneMatch);
    }

    /**
     * Last-Modified 기반 조건부 요청 확인
     * @param lastModifiedMillis 리소스의 마지막 수정 시간 (밀리초)
     * @param ifModifiedSince 클라이언트가 보낸 If-Modified-Since 헤더 값 (밀리초)
     * @return 수정되지 않았으면 true
     */
    public boolean isNotModified(long lastModifiedMillis, long ifModifiedSince) {
        return ifModifiedSince != -1 && lastModifiedMillis <= ifModifiedSince;
    }
}