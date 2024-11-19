// package web.thaiticketmajor.configuration;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.Cookie;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;
// import web.thaiticketmajor.Services.AuthenticationService;

// import java.io.IOException;
// import java.util.Arrays;
// import java.util.List;
// import java.util.stream.Collectors;

// import com.nimbusds.jwt.SignedJWT;

// @Component
// @RequiredArgsConstructor
// @Slf4j
// public class JwtCookieFilter extends OncePerRequestFilter {

//     private final AuthenticationService authenticationService;

//     @Override
// protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//         throws ServletException, IOException {

//     String token = getJwtFromCookies(request);
//     log.info("Token từ cookie: {}", token);

//     if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//         try {
//             SignedJWT signedJWT = authenticationService.verifyToken(token);
//             String email = signedJWT.getJWTClaimsSet().getSubject();
//             List<String> scopes;

//             // Kiểm tra xem "scope" là chuỗi hay danh sách
//             if (signedJWT.getJWTClaimsSet().getClaim("scope") instanceof String) {
//                 String scopeString = (String) signedJWT.getJWTClaimsSet().getClaim("scope");
//                 scopes = Arrays.asList(scopeString.split(",")); // Tách chuỗi thành danh sách
//             } else {
//                 scopes = (List<String>) signedJWT.getJWTClaimsSet().getClaim("scope");
//             }

//             List<SimpleGrantedAuthority> authorities = scopes.stream()
//                     .map(SimpleGrantedAuthority::new)
//                     .collect(Collectors.toList());

//             Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
//             SecurityContextHolder.getContext().setAuthentication(authentication);

//             log.info("Đã xác thực người dùng: {} với quyền: {}", email, authorities);
//         } catch (Exception e) {
//             log.error("Lỗi xác thực JWT từ cookie", e);
//             Cookie cookie = new Cookie("auth_token", null);
//             cookie.setMaxAge(0);
//             cookie.setPath("/");
//             response.addCookie(cookie);
//         }
//     }

//     filterChain.doFilter(request, response);
// }


//     private String getJwtFromCookies(HttpServletRequest request) {
//         if (request.getCookies() != null) {
//             return Arrays.stream(request.getCookies())
//                     .filter(cookie -> "auth_token".equals(cookie.getName()))
//                     .map(Cookie::getValue)
//                     .findFirst()
//                     .orElse(null);
//         }
//         return null;
//     }
// }
package web.thaiticketmajor.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import web.thaiticketmajor.Services.AuthenticationService;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.nimbusds.jwt.SignedJWT;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtCookieFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Lấy token từ cookie
        String token = getJwtFromCookies(request);
        log.info("Token từ cookie: {}", token); // Log token từ cookie

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Xác thực và giải mã token
                SignedJWT signedJWT = authenticationService.verifyToken(token);
                String email = signedJWT.getJWTClaimsSet().getSubject();
                log.info("Email từ JWT: {}", email); // Log email

                // Lấy danh sách quyền (scope)
                List<String> scopes;
                if (signedJWT.getJWTClaimsSet().getClaim("scope") instanceof String) {
                    String scopeString = (String) signedJWT.getJWTClaimsSet().getClaim("scope");
                    scopes = Arrays.asList(scopeString.split(",")); // Tách chuỗi thành danh sách
                } else {
                    scopes = (List<String>) signedJWT.getJWTClaimsSet().getClaim("scope");
                }

                // Log danh sách quyền
                log.info("Danh sách quyền từ token: {}", scopes);

                // Ánh xạ danh sách quyền vào authorities
                List<SimpleGrantedAuthority> authorities = scopes.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // Log danh sách authorities
                log.info("Authorities ánh xạ từ scopes: {}", authorities);

                // Tạo đối tượng Authentication và thiết lập vào SecurityContext
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("Đã xác thực người dùng: {} với quyền: {}", email, authorities);
            } catch (ParseException e) {
                log.error("Lỗi giải mã JWT", e);
                xoaCookie(response); // Xóa cookie nếu có lỗi
            } catch (Exception e) {
                log.error("Lỗi xác thực JWT từ cookie", e);
                xoaCookie(response); // Xóa cookie nếu có lỗi
            }
        }

        // Chuyển tiếp request và response
        filterChain.doFilter(request, response);
    }

    // Hàm lấy JWT từ cookie
    private String getJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            String jwt = Arrays.stream(request.getCookies())
                    .filter(cookie -> "auth_token".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
            log.info("Lấy JWT từ cookies: {}", jwt);
            return jwt;
        }
        log.warn("Không tìm thấy cookies trong request");
        return null;
    }

    // Hàm xóa cookie
    private void xoaCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("auth_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        log.info("Xóa cookie auth_token");
        response.addCookie(cookie);
    }
}
