package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;

import java.time.Duration;

/**
 * 캐시 관련 설정 클래스
 * 다양한 캐시 정책을 Bean으로 등록하여 재사용
 */
@Configuration
public class CacheConfig {

    /**
     * 기본 캐시 정책 (5분)
     * 일반적인 데이터에 사용
     */
    @Bean
    CacheControl defaultCacheControl() {
        return CacheControl.maxAge(Duration.ofMinutes(5))
                .mustRevalidate()
                .cachePublic();
    }

    /**
     * 짧은 캐시 정책 (1분)
     * 자주 변경되는 데이터에 사용
     */
    @Bean
    CacheControl shortCacheControl() {
        return CacheControl.maxAge(Duration.ofMinutes(1))
                .mustRevalidate();
    }

    /**
     * 긴 캐시 정책 (1시간)
     * 거의 변경되지 않는 데이터에 사용
     */
    @Bean
    CacheControl longCacheControl() {
        return CacheControl.maxAge(Duration.ofHours(1))
                .cachePublic();
    }

    /**
     * 캐시 금지 정책
     * 실시간 데이터나 민감한 정보에 사용
     */
    @Bean
    CacheControl noCacheControl() {
        return CacheControl.noStore();  // 수정: noCache().noStore() → noStore()
    }
}
