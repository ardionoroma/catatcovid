package com.benica.catatcovid.controller;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.validation.Valid;

import com.benica.catatcovid.dbo.City;
import com.benica.catatcovid.dbo.CityRepository;
import com.benica.catatcovid.dbo.ContactRepository;
import com.benica.catatcovid.dbo.District;
import com.benica.catatcovid.dbo.DistrictRepository;
import com.benica.catatcovid.dbo.Province;
import com.benica.catatcovid.dbo.ProvinceRepository;
import com.benica.catatcovid.dbo.User;
import com.benica.catatcovid.dbo.UserRepository;
import com.benica.catatcovid.dto.LoginRequest;
import com.benica.catatcovid.dto.RegisterRequest;
import com.benica.catatcovid.dto.UserDTO;
import com.benica.catatcovid.exception.InvalidAuthorizationTokenException;
import com.benica.catatcovid.exception.InvalidUserCredentialException;
import com.benica.catatcovid.exception.UsernameAlreadyExistException;
import com.benica.catatcovid.service.AccessTokenProvider;

import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController
{
    private static final ModelMapper modelMapper = new ModelMapper();

    @Autowired private AccessTokenProvider tokenProvider;
    @Autowired private UserRepository userRepository;
    @Autowired private ProvinceRepository provinceRepository;
    @Autowired private CityRepository cityRepository;
    @Autowired private DistrictRepository districtRepository;
    @Autowired private ContactRepository contactRepository;

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) throws Exception
    {
        if (this.userRepository.findByUsername(request.getUsername()).isPresent()) throw new UsernameAlreadyExistException();

        Province province = new Province();
        City city = new City();
        District district = new District();

        Integer provinceId = request.getProvinceId();
        Integer cityId = request.getCityId();
        Integer districtId = request.getDistrictId();
        String provinceName = request.getProvince();
        String cityName = request.getCity();
        String districtName = request.getDistrict();

        if (!this.districtRepository.findById(districtId).isPresent()) {
            if (!this.cityRepository.findById(cityId).isPresent()) {
                if (!this.provinceRepository.findById(provinceId).isPresent()) {
                    province = this.createProvince(provinceId, provinceName);
                    city = this.createCity(cityId, cityName, province);
                    district = this.createDistrict(districtId, districtName, city);
                } else {
                    city = this.createCity(cityId, cityName, this.provinceRepository.findById(provinceId).get());
                    district = this.createDistrict(districtId, districtName, city);
                }
            } else {
                district = this.createDistrict(districtId, districtName, this.cityRepository.findById(cityId).get());
            }
        } else {
            district = this.districtRepository.findById(districtId).get();
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(this.generatePassword(request.getPassword()));
        user.setIsLoggedIn(true);
        user.setLastLoggedIn(new Timestamp(System.currentTimeMillis()));
        user.setAlertMeter(new BigDecimal(0));
        user.setDistrict(district);
        user.setRefreshToken(RandomStringUtils.randomAlphanumeric(20));
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        this.userRepository.save(user);

        UserDTO dto = this.convertToDto(user);
        dto.setIsNoted(false);
        dto.setToken(this.tokenProvider.encode(user));

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) throws Exception
    {
        String username = request.getUsername();
        String password = "MhYQmHsu" + (1597149286 % 67501) + "d0QnL/5Prh6H34EP" + request.getPassword() + "EsurAcvdoh9+bv0q";

        User user = this.userRepository.findByUsername(username).orElseThrow(() -> new InvalidUserCredentialException());
        if(!BCrypt.checkpw(password, user.getPassword())) throw new InvalidUserCredentialException();

        user.setIsLoggedIn(true);
        user.setLastLoggedIn(new Timestamp(System.currentTimeMillis()));
        user.setRefreshToken(RandomStringUtils.randomAlphanumeric(20));
        this.userRepository.save(user);

        UserDTO dto = this.convertToDto(user);
        if (this.contactRepository.countByUserIdAndContactDate(user.getId(), new Date(System.currentTimeMillis())) > 0) {
            dto.setIsNoted(true);
        } else {
            dto.setIsNoted(false);
        }
        dto.setToken(this.tokenProvider.encode(user));

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public ResponseEntity<?> logout(@Valid @RequestHeader("Authorization") String token) throws Exception
    {
        UserDTO dto = this.tokenProvider.verifyAndDecode(token);
        User user = this.userRepository.findById(dto.getId()).orElseThrow(() -> new InvalidAuthorizationTokenException());

        user.setLastLoggedIn(new Timestamp(System.currentTimeMillis()));
        user.setIsLoggedIn(false);
        this.userRepository.save(user);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    private Province createProvince(Integer id, String name)
    {
        Province province = new Province();
        province.setId(id);
        province.setName(name);
        province.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        this.provinceRepository.save(province);

        return province;
    }

    private City createCity(Integer id, String name, Province province)
    {
        City city = new City();
        city.setId(id);
        city.setName(name);
        city.setProvince(province);
        city.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        this.cityRepository.save(city);
        
        return city;
    }

    private District createDistrict(Integer id, String name, City city)
    {
        District district = new District();
        district.setId(id);
        district.setName(name);
        district.setAlertMeter(new BigDecimal(0));
        district.setCity(city);
        district.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        this.districtRepository.save(district);
        
        return district;
    }

    private String generatePassword(String plainPassword)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = "MhYQmHsu" + (1597149286 % 67501) + "d0QnL/5Prh6H34EP" + plainPassword + "EsurAcvdoh9+bv0q";
        String hashedPassword = passwordEncoder.encode(password);
        
        return hashedPassword;
    }

    private UserDTO convertToDto(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return userDTO;
    }
}