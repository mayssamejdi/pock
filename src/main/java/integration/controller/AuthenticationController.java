//package integration.controller;
//
//
//import integration.config.UserDetail;
//import integration.message.JwtRequest;
//import integration.message.JwtResponse;
//import integration.service.UserService;
//import integration.utility.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping(path = "/auth")
//@CrossOrigin(origins = "http://localhost:4200")
//public class AuthenticationController {
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @CrossOrigin(origins = "http://localhost:4200")
//    @PostMapping(path = "authenticate")
//    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest jwtRequest) throws Exception {
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
//        } catch (BadCredentialsException e) {
//            throw new Exception("Incorrect username or password", e);
//        }
//        final UserDetail userDetails = (UserDetail) userService.loadUserByUsername(jwtRequest.getUsername());
//        final String token = jwtUtil.generateToken(userDetails);
//        return ResponseEntity.ok(new JwtResponse(token));
//
//    }
//}
