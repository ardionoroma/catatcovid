package com.benica.catatcovid.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.benica.catatcovid.dbo.Contact;
import com.benica.catatcovid.dbo.ContactRepository;
import com.benica.catatcovid.dbo.District;
import com.benica.catatcovid.dbo.DistrictRepository;
import com.benica.catatcovid.dbo.User;
import com.benica.catatcovid.dbo.UserRepository;
import com.benica.catatcovid.dto.ContactRequest;
import com.benica.catatcovid.dto.ContactResponse;
import com.benica.catatcovid.dto.ContactsResponse;
import com.benica.catatcovid.dto.UserDTO;
import com.benica.catatcovid.dto.enumerate.Contacts;
import com.benica.catatcovid.exception.InvalidAuthorizationTokenException;
import com.benica.catatcovid.service.AccessTokenProvider;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
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

        BigDecimal userAlertMeter = this.contactRepository.averageByUserAndContactDate(user, new Date(System.currentTimeMillis())).setScale(3, RoundingMode.HALF_UP);
        user.setAlertMeter(userAlertMeter);
        this.userRepository.save(user);

        BigDecimal districtAlertMeter = this.contactRepository.averageByDistrictAndContactDate(district, new Date(System.currentTimeMillis())).setScale(3, RoundingMode.HALF_UP);
        district.setAlertMeter(districtAlertMeter);
        this.districtRepository.save(district);

        ContactResponse response = modelMapper.map(contact, ContactResponse.class);
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "contacts", method = RequestMethod.GET)
    public ResponseEntity<?> getContacts(@RequestHeader("Authorization") String token) throws Exception
    {
        UserDTO dto = this.tokenProvider.verifyAndDecode(token);
        User user = this.userRepository.findById(dto.getId()).orElseThrow(() -> new InvalidAuthorizationTokenException());
        List<ContactsResponse> responses = new ArrayList<>();
        ContactsResponse responseToday = new ContactsResponse();
        ContactsResponse responseYesterday = new ContactsResponse();
        ContactsResponse responseWeekly = new ContactsResponse();
        ContactsResponse responseTwoWeeks = new ContactsResponse();
        Date today = new Date(System.currentTimeMillis());

        responseToday.setType(Contacts.TODAY.toString().toLowerCase());
        responseToday.setTypeName(Contacts.TODAY.getName());
        responseToday.setAlertMeter(user.getAlertMeter().setScale(3, RoundingMode.HALF_UP));
        responseToday.setDistrictAlertMeter(user.getDistrict().getAlertMeter().setScale(3, RoundingMode.HALF_UP));
        responses.add(responseToday);

        responseYesterday.setType(Contacts.YESTERDAY.toString().toLowerCase());
        responseYesterday.setTypeName(Contacts.YESTERDAY.getName());
        if (this.contactRepository.countByUserIdAndContactDate(user.getId(), Date.valueOf(today.toLocalDate().minusDays(1))) > 0){
            responseYesterday.setAlertMeter(this.contactRepository.averageByUserAndContactDate(
                user,
                Date.valueOf(today.toLocalDate().minusDays(1))
            ).setScale(3, RoundingMode.HALF_UP));
        } else {
            responseYesterday.setAlertMeter(new BigDecimal(0));
        }
        if (this.contactRepository.countByDistrictIdAndContactDate(user.getDistrict().getId(), Date.valueOf(today.toLocalDate().minusDays(1))) > 0){
            responseYesterday.setDistrictAlertMeter(this.contactRepository.averageByDistrictAndContactDate(
                user.getDistrict(),
                Date.valueOf(today.toLocalDate().minusDays(1))
            ).setScale(3, RoundingMode.HALF_UP));
        } else {
            responseYesterday.setDistrictAlertMeter(new BigDecimal(0));
        }
        responses.add(responseYesterday);

        responseWeekly.setType(Contacts.WEEKLY.toString().toLowerCase());
        responseWeekly.setTypeName(Contacts.WEEKLY.getName());
        if (this.contactRepository.countWeeksAgo(user, today, Date.valueOf(today.toLocalDate().minusWeeks(1))) > 0) {
            responseWeekly.setAlertMeter(this.contactRepository.averageWeeksAgo(
                user,
                today,
                Date.valueOf(today.toLocalDate().minusWeeks(1))
            ).setScale(3, RoundingMode.HALF_UP));
        } else {
            responseWeekly.setAlertMeter(new BigDecimal(0));
        }
        if (this.contactRepository.countDistrictWeeksAgo(user.getDistrict(), today, Date.valueOf(today.toLocalDate().minusWeeks(1))) > 0) {
            responseWeekly.setDistrictAlertMeter(this.contactRepository.averageDistrictWeeksAgo(
                user.getDistrict(),
                today,
                Date.valueOf(today.toLocalDate().minusWeeks(1))
            ).setScale(3, RoundingMode.HALF_UP));
        } else {
            responseWeekly.setDistrictAlertMeter(new BigDecimal(0));
        }
        responses.add(responseWeekly);

        responseTwoWeeks.setType(Contacts.TWOWEEKS.toString().toLowerCase());
        responseTwoWeeks.setTypeName(Contacts.TWOWEEKS.getName());
        if (this.contactRepository.countWeeksAgo(user, today, Date.valueOf(today.toLocalDate().minusWeeks(2))) > 0) {
            responseTwoWeeks.setAlertMeter(this.contactRepository.averageWeeksAgo(
                user,
                today,
                Date.valueOf(today.toLocalDate().minusWeeks(2))
            ).setScale(3, RoundingMode.HALF_UP));
        } else {
            responseTwoWeeks.setAlertMeter(new BigDecimal(0));
        }
        if (this.contactRepository.countDistrictWeeksAgo(user.getDistrict(), today, Date.valueOf(today.toLocalDate().minusWeeks(2))) > 0) {
            responseTwoWeeks.setDistrictAlertMeter(this.contactRepository.averageDistrictWeeksAgo(
                user.getDistrict(),
                today,
                Date.valueOf(today.toLocalDate().minusWeeks(2))
            ).setScale(3, RoundingMode.HALF_UP));
        } else {
            responseTwoWeeks.setDistrictAlertMeter(new BigDecimal(0));
        }
        responses.add(responseTwoWeeks);

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "contacts/{type}", method = RequestMethod.GET)
    public ResponseEntity<?> getContactsDetail(@RequestHeader("Authorization") String token,
        @PathVariable String type) throws Exception
    {
        UserDTO dto = this.tokenProvider.verifyAndDecode(token);
        User user = this.userRepository.findById(dto.getId()).orElseThrow(() -> new InvalidAuthorizationTokenException());
        List<Contact> contacts = new ArrayList<>();
        List<ContactResponse> responses = new ArrayList<>();
        Date today = new Date(System.currentTimeMillis());

        switch (type.toLowerCase()) {
            case "today":
                contacts = this.contactRepository.findByUserIdAndContactDate(user.getId(), today);
                break;
            case "yesterday":
                contacts = this.contactRepository.findByUserIdAndContactDate(user.getId(), Date.valueOf(today.toLocalDate().minusDays(1)));
                break;
            case "weekly":
                contacts = this.contactRepository.findWeeksAgo(user, today, Date.valueOf(today.toLocalDate().minusWeeks(1)));
                break;
            case "twoweeks":
            contacts = this.contactRepository.findWeeksAgo(user, today, Date.valueOf(today.toLocalDate().minusWeeks(2)));
                break;
            default:
                break;
        }

        for (Contact contact : contacts) {
            ContactResponse response = modelMapper.map(contact, ContactResponse.class);
            responses.add(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}