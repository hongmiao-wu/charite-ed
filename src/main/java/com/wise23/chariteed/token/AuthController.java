//package com.wise23.chariteed.token;
//
//import com.wise23.chariteed.model.UserData;
//import com.wise23.chariteed.repository.UserDataRepository;
//import com.wise23.chariteed.service.UserDataService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.validation.Valid;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//    @Autowired
//    AuthenticationManager authenticationManager;
//    @Autowired
//    RefreshTokenRepository refreshTokenRepository;
//    @Autowired
//    UserDataRepository userRepository;
//    @Autowired
//    JwtHelper jwtHelper;
//    @Autowired
//    PasswordEncoder passwordEncoder;
//    @Autowired
//    UserDataService userService;
//
//    @PostMapping("/login")
//    @Transactional
//    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO dto) {
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getFirstname(), dto.getPassword()));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        UserData user = (UserData) authentication.getPrincipal();
//
//        RefreshToken refreshToken = new RefreshToken();
//        refreshToken.setUser(user);
//        refreshTokenRepository.save(refreshToken);
//
//        String accessToken = jwtHelper.generateAccessToken(user);
//        String refreshTokenString = jwtHelper.generateRefreshToken(user, refreshToken);
//
//        return ResponseEntity.ok(new TokenDTO(user.getId(), accessToken, refreshTokenString));
//    }
//
//    @PostMapping("signup")
//    @Transactional
//    public ResponseEntity<?> signup(@Valid @RequestBody SignupDTO dto) {
//        UserData user = new UserData(dto.getFirstname(), dto.getLastname(), dto.getEmail(), passwordEncoder.encode(dto.getPassword()), dto.getMobile(), dto.getRole());
//        userRepository.save(user);
//
//        RefreshToken refreshToken = new RefreshToken();
//        refreshToken.setUser(user);
//        refreshTokenRepository.save(refreshToken);
//
//        String accessToken = jwtHelper.generateAccessToken(user);
//        String refreshTokenString = jwtHelper.generateRefreshToken(user, refreshToken);
//
//        return ResponseEntity.ok(new TokenDTO(user.getId(), accessToken, refreshTokenString));
//    }
//
//    @PostMapping("logout")
//    public ResponseEntity<?> logout(@RequestBody TokenDTO dto) {
//        String refreshTokenString = dto.getRefreshToken();
//        if (jwtHelper.validateRefreshToken(refreshTokenString) && refreshTokenRepository.existsById(jwtHelper.getTokenIdFromRefreshToken(refreshTokenString))) {
//            // valid and exists in db
//            refreshTokenRepository.deleteById(jwtHelper.getTokenIdFromRefreshToken(refreshTokenString));
//            return ResponseEntity.ok().build();
//        }
//
//        throw new BadCredentialsException("invalid token");
//    }
//
//    @PostMapping("logout-all")
//    public ResponseEntity<?> logoutAll(@RequestBody TokenDTO dto) {
//        String refreshTokenString = dto.getRefreshToken();
//        if (jwtHelper.validateRefreshToken(refreshTokenString) && refreshTokenRepository.existsById(jwtHelper.getTokenIdFromRefreshToken(refreshTokenString))) {
//            // valid and exists in db
//
//            refreshTokenRepository.deleteRefreshTokenByUserId(jwtHelper.getUserIdFromRefreshToken(refreshTokenString));
//            return ResponseEntity.ok().build();
//        }
//
//        throw new BadCredentialsException("invalid token");
//    }
//
//    @PostMapping("access-token")
//    public ResponseEntity<?> accessToken(@RequestBody TokenDTO dto) {
//        String refreshTokenString = dto.getRefreshToken();
//        if (jwtHelper.validateRefreshToken(refreshTokenString) && refreshTokenRepository.existsById(jwtHelper.getTokenIdFromRefreshToken(refreshTokenString))) {
//            // valid and exists in db
//
//            UserData user = userService.findById(jwtHelper.getUserIdFromRefreshToken(refreshTokenString));
//            String accessToken = jwtHelper.generateAccessToken(user);
//
//            return ResponseEntity.ok(new TokenDTO(user.getId(), accessToken, refreshTokenString));
//        }
//
//        throw new BadCredentialsException("invalid token");
//    }
//
//    @PostMapping("refresh-token")
//    public ResponseEntity<?> refreshToken(@RequestBody TokenDTO dto) {
//        String refreshTokenString = dto.getRefreshToken();
//        if (jwtHelper.validateRefreshToken(refreshTokenString) && refreshTokenRepository.existsById(jwtHelper.getTokenIdFromRefreshToken(refreshTokenString))) {
//            // valid and exists in db
//
//            refreshTokenRepository.deleteById(jwtHelper.getTokenIdFromRefreshToken(refreshTokenString));
//
//            UserData user = userService.findById(jwtHelper.getUserIdFromRefreshToken(refreshTokenString));
//
//            RefreshToken refreshToken = new RefreshToken();
//            refreshToken.setUser(user);
//            refreshTokenRepository.save(refreshToken);
//
//            String accessToken = jwtHelper.generateAccessToken(user);
//            String newRefreshTokenString = jwtHelper.generateRefreshToken(user, refreshToken);
//
//            return ResponseEntity.ok(new TokenDTO(user.getId(), accessToken, newRefreshTokenString));
//        }
//
//        throw new BadCredentialsException("invalid token");
//    }
//}
