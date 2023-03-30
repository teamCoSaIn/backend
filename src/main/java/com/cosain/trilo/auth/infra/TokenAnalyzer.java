package com.cosain.trilo.auth.infra;

import java.time.LocalDateTime;

public interface TokenAnalyzer {
    boolean validateToken(String token);
    String getEmailFromToken(String token);
    LocalDateTime getTokenExpiryDateTime(String token);
    Long getTokenRemainExpiryFrom(String token);
}