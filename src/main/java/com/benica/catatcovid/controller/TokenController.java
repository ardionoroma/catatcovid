package com.benica.catatcovid.controller;

import javax.validation.Valid;

import com.benica.catatcovid.dbo.User;
import com.benica.catatcovid.dbo.UserRepository;
import com.benica.catatcovid.dto.RefreshTokenRequest;
import com.benica.catatcovid.dto.RefreshTokenResponse;
import com.benica.catatcovid.dto.TokenRequest;
import com.benica.catatcovid.dto.UserDTO;
import com.benica.catatcovid.exception.InvalidAuthorizationTokenException;
import com.benica.catatcovid.service.AccessTokenProvider;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController
{
    @Autowired private AccessTokenProvider tokenProvider;
    @Autowired private UserRepository userRepository;

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "validate_token", method = RequestMethod.POST)
    public ResponseEntity<?> validate(@Valid @RequestBody TokenRequest request) throws Exception
    {
        UserDTO dto = this.tokenProvider.verifyAndDecode(request.getToken());

        User user = this.userRepository.findById(dto.getId()).orElseThrow(() -> new InvalidAuthorizationTokenException());
        dto.setLastLoggedIn(user.getLastLoggedIn());
        dto.setAlertMeter(user.getAlertMeter());
        dto.setDistrict(user.getDistrict());

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @CrossOrigin(value = "*")
    @RequestMapping(value = "refresh_token", method = RequestMethod.POST)
    public ResponseEntity<?> refreshToken(
        @RequestHeader("Authorization") String token,
        @Valid @RequestBody RefreshTokenRequest request) throws Exception
    {
        UserDTO dto = this.tokenProvider.verifyAndDecode(token);
        User user = this.userRepository.findById(dto.getId()).orElseThrow(() -> new InvalidAuthorizationTokenException());

        if (user.getRefreshToken().compareTo(request.getRefreshToken()) != 0) throw new InvalidAuthorizationTokenException();

        user.setRefreshToken(RandomStringUtils.randomAlphanumeric(20));
        this.userRepository.save(user);

        String newToken = this.tokenProvider.encode(user);
        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setToken(newToken);
        response.setRefreshToken(user.getRefreshToken());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}