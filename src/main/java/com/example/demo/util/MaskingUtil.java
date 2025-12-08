package com.example.demo.util;

import org.springframework.stereotype.Component;

@Component
public class MaskingUtil {

    /**
     * 이름 마스킹 규칙:
     * - 2글자: 첫 번째 글자 마스킹 (예: 이슬 -> *슬)
     * - 3글자 이상: 첫 번째와 마지막 글자 제외 마스킹 (예: 홍길동 -> 홍*동)
     */
    public static String maskName(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        int len = name.length();
        
        if (len == 2) {
            // 2글자: 두 번째 글자 마스킹
            return name.charAt(0)+"*" ;// 예) 이슬 -> 이*
        } else if (len >= 3) {
            // 3글자 이상: 첫 번째와 마지막 글자 제외 마스킹
            char first = name.charAt(0);
            char last = name.charAt(len - 1);
            
            // 중간 글자 수만큼 '*' 생성
            String middleMask = "*".repeat(len - 2); 
            
            return first + middleMask + last; // 예) 홍길동 -> 홍*동, 이순신 -> 이*신
        } else {
            // 1글자 등 예외적인 경우 (마스킹하지 않거나 전부 * 처리)
            return name; // 1글자인 경우 그대로 반환
        }
    }

    /**
     * 아이디 마스킹 규칙:
     * - 2글자 이하: 마스킹 없음
     * - 3글자 이상: 처음 2글자를 제외하고 ******을 붙여 마스킹 (예: usid -> us******)
     */
    public static String maskUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            return "";
        }

        String prefix = userId.substring(0, 2);
        
        // 아이디 길이에 관계없이 ******을 붙입니다.
        return prefix + "******"; // 예) usid -> us******, user123 -> us******
    }
}