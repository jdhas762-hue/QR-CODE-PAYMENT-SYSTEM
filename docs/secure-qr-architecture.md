# Secure Dynamic QR-Based UPI Payment System (Production Blueprint)

## 1) Modular Project Folder Structure

```text
src/main/java/example/secureqr
├── config
│   ├── ApiSecurityInterceptor.java
│   └── WebSecurityConfig.java
├── controller
│   ├── MerchantRiskController.java
│   └── SecureQrController.java
├── dto
│   ├── MerchantLoginRequest.java
│   ├── QrGenerationRequest.java
│   ├── QrScanRequest.java
│   ├── RiskScoreBreakdown.java
│   └── SignedPayload.java
├── model
│   ├── MerchantSession.java
│   ├── QRTransaction.java
│   ├── QrStatus.java
│   └── RiskLevel.java
├── service
│   ├── MerchantRiskService.java
│   ├── QrSecurityService.java
│   ├── RiskScoringService.java
│   └── SignedTokenService.java
├── store
│   └── SecurityDataStore.java
└── util
    ├── GeoLocationResolver.java
    └── HmacSignatureUtil.java

src/main/resources
├── db/secure_qr_schema.sql
└── templates/admin/risk-dashboard.html
```

## 2) Database Schema

Use `src/main/resources/db/secure_qr_schema.sql` for:
- `merchant_sessions`
- `qr_transactions`
- `qr_security_events`
- graph/data aggregation queries for admin dashboard.

## 3) API Contract (v2)

### Dynamic QR + Smart Expiry
- `POST /api/v2/secure-qr/generate`
  - Body: `merchantId`, `amount`, `deviceFingerprint`, `deviceBound`
  - Stores all required data including nonce, device, ip, location.
- `POST /api/v2/secure-qr/scan`
  - Body: `transactionId`, `scannedAmount`, `scannedDeviceFingerprint`, `encodedToken`
  - Validates:
    - 2-minute expiry
    - amount integrity
    - device binding
    - HMAC token signature
    - replay check

### Fraud Session & Risk
- `POST /api/v2/risk/merchant/login`
  - Body: `merchantId`, `deviceFingerprint`, `ipAddress`, `location`
  - Returns weighted risk scores + risk level.
- `GET /api/v2/risk/dashboard`
  - Returns merchant-wise dashboard metrics and trend arrays.
- `GET /api/v2/risk/dashboard/view`
  - Renders admin dashboard page.

### Middleware Security Headers (for `/api/v2/**`)
- `X-Request-Nonce`
- `X-Request-Timestamp`
- `X-Merchant-Id`

## 4) HMAC Token Format (Offline Secure QR)

Payload fields encoded into Base64 JSON:
- `merchant_id`
- `amount`
- `timestamp`
- `unique_transaction_id`
- `nonce`
- `signature`

Canonical signature string:

```text
merchantId|amount|timestamp|uniqueTransactionId|nonce
```

Algorithm:
- HMAC SHA256 with server-side secret (`app.security.hmac.secret`)

## 5) Risk Scoring Formula

Weighted model in `RiskScoringService`:

```text
Final Fraud Probability
= 0.30 * DeviceChangeScore
+ 0.25 * LocationDeviationScore
+ 0.25 * QRFrequencyScore
+ 0.20 * TimePatternAnomalyScore
```

Risk bands:
- `LOW_RISK`: score < 40
- `MEDIUM_RISK`: 40 <= score < 70
- `HIGH_RISK`: score >= 70

## 6) Production Security Notes

- Replay prevention: unique transaction token IDs tracked in `usedTokens`.
- Token tamper prevention: request rejected when HMAC mismatch occurs.
- Dynamic expiry: hard-fail after 120s window.
- Device-bound scan mode: rejects scanner device mismatch.
- Abuse counters tracked at transaction level:
  - `expiredAttempts`
  - `deviceMismatchAttempts`
  - `amountMismatchAttempts`
  - `replayAttempts`

## 7) Step-by-Step Integration Plan

1. **Migrate DB** using schema SQL and create indexes.
2. **Set secrets and config**:
   - `app.security.hmac.secret`
   - logging sink and SIEM forwarding.
3. **Replace in-memory store** (`SecurityDataStore`) with repository adapters (JPA/MyBatis).
4. **Enable Redis** for nonce replay + short-lived expiry cache.
5. **Wire geo-IP provider** to replace `GeoLocationResolver` stub.
6. **Instrument metrics**: suspicious scans, replay blocks, risk-score drift.
7. **Add alerting rules** for HIGH_RISK sessions and unusual QR bursts.
8. **Harden APIs** with JWT auth, mTLS between services, and rate limiting.
9. **Scale by decomposition**:
   - QR generation service
   - Risk engine service
   - Admin reporting service.
10. **Test strategy**:
    - unit tests for token verification, scoring, and expiry logic
    - integration tests for `/api/v2/**`
    - chaos tests for replay floods and clock skew.
