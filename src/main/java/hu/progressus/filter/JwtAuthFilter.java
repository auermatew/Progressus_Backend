package hu.progressus.filter;

import hu.progressus.entity.User;
import hu.progressus.repository.UserRepository;
import hu.progressus.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
    throws ServletException, IOException {
    System.out.println("Request URI:" + request.getRequestURI());

    if(Objects.equals(request.getRequestURI(), "/api/v1/auth/logout")){
     Cookie cookie = new Cookie("token", null);
      cookie.setMaxAge(0);
      cookie.setPath("/");
      response.addCookie(cookie);
      Cookie refreshCookie = new Cookie("refreshToken", null);
      refreshCookie.setMaxAge(0);
      refreshCookie.setPath("/");
      response.addCookie(refreshCookie);
      filterChain.doFilter(request,response);
      System.out.println(cookie.getName() + cookie.getValue() + refreshCookie.getName() + refreshCookie.getValue());
      return;
    }

    final String jwt;
    final long userId;

    var tokenCookie = WebUtils.getCookie(request,"token");

    if(tokenCookie == null){
      var refreshTokenCookie = WebUtils.getCookie(request,"refreshToken");
      System.out.println("no cookie");
      if(refreshTokenCookie == null){
        filterChain.doFilter(request,response);
        System.out.println("no refresh cookie");
        return;
      } else {
        jwt = refreshTokenCookie.getValue();
        System.out.println("refreshTokenCookie: " + jwt);
      }
    }
    else {
      jwt = tokenCookie.getValue();
      System.out.println("tokenCookie: " + jwt);
    }

    userId = Long.parseLong( jwtService.extractUserId(jwt));
    Optional<User> user = userRepository.findById(userId);

    if (user.isEmpty()) {
      filterChain.doFilter(request,response);
      return;
    }

    if (SecurityContextHolder.getContext().getAuthentication() == null){
      if(jwtService.isTokenValid(jwt,user.get()))
      {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            user.get(),
            null,
            user.get().getAuthorities()
        );
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request,response);
  }
}
