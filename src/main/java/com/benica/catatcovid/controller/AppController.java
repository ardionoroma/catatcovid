package com.benica.catatcovid.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;

import javax.validation.Valid;

import com.benica.catatcovid.dbo.Contact;
import com.benica.catatcovid.dbo.ContactRepository;
import com.benica.catatcovid.dbo.District;
import com.benica.catatcovid.dbo.DistrictRepository;
import com.benica.catatcovid.dbo.User;
import com.benica.catatcovid.dbo.UserRepository;
import com.benica.catatcovid.dto.ContactRequest;
import com.benica.catatcovid.dto.ContactResponse;
import com.benica.catatcovid.dto.UserDTO;
import com.benica.catatcovid.exception.InvalidAuthorizationTokenException;
import com.benica.catatcovid.service.AccessTokenProvider;

import org.modelmapper.ModelMapper;
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
public class AppController
{
    private static final ModelMapper modelMapper = new ModelMapper();
    @Autowired private AccessTokenProvider tokenProvider;
    @Autowired private UserRepository userRepository;
    @Autowired private ContactRepository contactRepository;
    @Autowired private DistrictRepository districtRepository;

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "home", method = RequestMethod.GET)
    public ResponseEntity<?> home(@RequestHeader("Authorization") String token) throws Exception
    {
        UserDTO dto = this.tokenProvider.verifyAndDecode(token);

        User user = this.userRepository.findById(dto.getId()).orElseThrow(() -> new InvalidAuthorizationTokenException());
        Long contacts = this.contactRepository.countByUserIdAndContactDate(user.getId(), new Date(System.currentTimeMillis()));
        dto = modelMapper.map(user, UserDTO.class);
        if (contacts > 0) {
            dto.setIsNoted(true);
        } else {
            dto.setIsNoted(false);
        }
        dto.setContacts(contacts);
        dto.setToken(null);
        dto.setRefreshToken(null);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "contact", method = RequestMethod.POST)
    public ResponseEntity<?> createContact(@RequestHeader("Authorization") String token,
        @Valid @RequestBody ContactRequest request) throws Exception
    {
        UserDTO dto = this.tokenProvider.verifyAndDecode(token);
        User user = this.userRepository.findById(dto.getId()).orElseThrow(() -> new InvalidAuthorizationTokenException());
        District district = user.getDistrict();

        Integer score = 0;
        if (!request.getIsHealthy()) ++score;
        if (request.getIsCoughSneeze()) ++score;
        if (!request.getIsMasked()) ++score;
        if (request.getIsCrowded()) ++score;
        if (!request.getIsPhydist()) ++score;
        BigDecimal bigScore = new BigDecimal(score);

        Contact contact = new Contact();
        contact.setName(request.getName());
        contact.setIsHealthy(request.getIsHealthy());
        contact.setIsCoughSneeze(request.getIsCoughSneeze());
        contact.setIsMasked(request.getIsMasked());
        contact.setIsCrowded(request.getIsCrowded());
        contact.setIsPhydist(request.getIsPhydist());
        contact.setContactDate(new Date(System.currentTimeMillis()));
        contact.setScore(bigScore);
        contact.setDistrict(district);
        contact.setUser(user);
        contact.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        this.contactRepository.save(contact);

        BigDecimal userAlertMeter = this.calculateAlertMeter(user, null, bigScore);
        user.setAlertMeter(userAlertMeter);
        this.userRepository.save(user);

        BigDecimal districtAlertMeter = this.calculateAlertMeter(null, district, bigScore);
        district.setAlertMeter(districtAlertMeter);
        this.districtRepository.save(district);

        ContactResponse response = modelMapper.map(contact, ContactResponse.class);
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private BigDecimal calculateAlertMeter(User user, District district, BigDecimal alertMeter)
    {
        if (user != null) {
            Long contacts = this.contactRepository.countByUserIdAndContactDate(user.getId(), new Date(System.currentTimeMillis()));
            BigDecimal numerator = user.getAlertMeter().add(alertMeter);
            BigDecimal denominator = new BigDecimal(contacts);
            return numerator.divide(denominator, 3, RoundingMode.HALF_UP);
        }

        if (district != null) {
            Long contacts = this.contactRepository.countByDistrictIdAndContactDate(district.getId(), new Date(System.currentTimeMillis()));
            BigDecimal numerator = district.getAlertMeter().add(alertMeter);
            BigDecimal denominator = new BigDecimal(contacts);
            return numerator.divide(denominator, 3, RoundingMode.HALF_UP);
        }

        return new BigDecimal(0);
    }
}