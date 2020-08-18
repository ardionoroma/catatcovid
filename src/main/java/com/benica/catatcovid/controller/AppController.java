package com.benica.catatcovid.controller;

import java.sql.Date;

import com.benica.catatcovid.dbo.ContactRepository;
import com.benica.catatcovid.dbo.User;
import com.benica.catatcovid.dbo.UserRepository;
import com.benica.catatcovid.dto.UserDTO;
import com.benica.catatcovid.exception.InvalidAuthorizationTokenException;
import com.benica.catatcovid.service.AccessTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController
{    
    @Autowired private AccessTokenProvider tokenProvider;
    @Autowired private UserRepository userRepository;
    @Autowired private ContactRepository contactRepository;

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "home", method = RequestMethod.GET)
    public ResponseEntity<?> home(@RequestHeader("Authorization") String token) throws Exception
    {
        UserDTO dto = this.tokenProvider.verifyAndDecode(token);

        User user = this.userRepository.findById(dto.getId()).orElseThrow(() -> new InvalidAuthorizationTokenException());
        Long contacts = this.contactRepository.countByUserIdAndContactDate(user.getId(), new Date(System.currentTimeMillis()));
        dto.setIsLoggedIn(user.getIsLoggedIn());
        dto.setLastLoggedIn(user.getLastLoggedIn());
        dto.setAlertMeter(user.getAlertMeter());
        dto.setDistrictName(user.getDistrict().getName());
        dto.setDistrictAlertMeter(user.getDistrict().getAlertMeter());
        if (contacts > 0) {
            dto.setIsNoted(true);
        } else {
            dto.setIsNoted(false);
        }
        dto.setContacts(contacts);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }
}