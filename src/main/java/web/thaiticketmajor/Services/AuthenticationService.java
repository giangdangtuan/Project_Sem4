    package web.thaiticketmajor.Services;

    import web.thaiticketmajor.dto.request.AuthenticationRequest;
    import web.thaiticketmajor.dto.request.IntrospectRequest;
    import web.thaiticketmajor.dto.request.LogoutRequest;
    import web.thaiticketmajor.dto.response.AuthenticationResponse;
    import web.thaiticketmajor.dto.response.IntrospectResponse;
    import web.thaiticketmajor.exception.AppException;
    import web.thaiticketmajor.exception.ErrorCode;
    import web.thaiticketmajor.Models.InvalidatedToken;
    import web.thaiticketmajor.Models.Role;
    import web.thaiticketmajor.Models.User;
    import web.thaiticketmajor.Repositories.InvalidatedTokenRepository;
    import web.thaiticketmajor.Repositories.UserRepository;
    import com.nimbusds.jose.*;
    import com.nimbusds.jose.crypto.MACSigner;
    import com.nimbusds.jose.crypto.MACVerifier;
    import com.nimbusds.jwt.JWTClaimsSet;
    import com.nimbusds.jwt.SignedJWT;
    import lombok.AccessLevel;
    import lombok.RequiredArgsConstructor;
    import lombok.experimental.FieldDefaults;
    import lombok.experimental.NonFinal;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;
    import org.springframework.util.CollectionUtils;

    import java.text.ParseException;
    import java.time.Instant;
    import java.time.temporal.ChronoUnit;
    import java.util.Date;
    import java.util.StringJoiner;
    import java.util.UUID;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public class AuthenticationService {

        private final InvalidatedTokenRepository invalidatedTokenRepository;
        @NonFinal
        @Value("${jwt.signerKey}")
        protected String SIGN_KEY;

        UserRepository userRepository;
        InvalidatedTokenRepository tokenRepository;

        public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
            var token = request.getToken();
            boolean isValid = true;
            try {
                verifyToken(token);
            } catch (AppException e) {
                isValid = false;
            }
            IntrospectResponse response = new IntrospectResponse();
            response.setValid(isValid);

            return response;
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
            log.info("Xác thực người dùng: {}", request.getEmail());
            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        
            if (!authenticated) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
        
            var token = generateToken(user);
        
            AuthenticationResponse response = new AuthenticationResponse();
            response.setToken(token);
            response.setAuthenticated(true);
            return response;
        }
        

        public void logout(LogoutRequest request) throws ParseException, JOSEException {
            var signToken = verifyToken(request.getToken());

            String jwtId = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = new InvalidatedToken();
            invalidatedToken.setId(jwtId);
            invalidatedToken.setExpiryTime(expiryTime);

            invalidatedTokenRepository.save(invalidatedToken);
        }

        public SignedJWT verifyToken(String token) throws JOSEException, ParseException {
            JWSVerifier verifier = new MACVerifier(SIGN_KEY.getBytes());

            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            var verified = signedJWT.verify(verifier);

            if(!(verified && expityTime.after(new Date()))) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            if(invalidatedTokenRepository
                    .existsById(signedJWT.getJWTClaimsSet().getJWTID()))
                throw new AppException(ErrorCode.UNAUTHENTICATED);

            return signedJWT;
        }

        private String generateToken(User user) {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getEmail())
                    .issuer("its")
                    .issueTime(new Date())
                    .expirationTime(new Date(
                            Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                    ))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("scope", buildScope(user))
                    .build();

            Payload payload = new Payload(jwtClaimsSet.toJSONObject());

            JWSObject jwsObject = new JWSObject(header, payload);

            try {
                jwsObject.sign(new MACSigner(SIGN_KEY));
                return jwsObject.serialize();
            } catch (JOSEException e) {
    //            log.error("Cannot create token", e);
                throw new RuntimeException(e);
            }
        }

        private String buildScope(User user) {
            StringJoiner stringJoiner = new StringJoiner(" ");
        
            // Lấy vai trò của người dùng (vì người dùng chỉ có một vai trò)
            Role role = user.getRole();
        
            if (role != null) {
                // Thêm vai trò với tiền tố "ROLE_"
                stringJoiner.add("ROLE_" + role.getName());
        
                // Kiểm tra và thêm các quyền từ vai trò
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add(permission.getName());
                    });

                }

            }
        
            return stringJoiner.toString();
        }
        
    }