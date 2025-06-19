package com.hh23.car4u.services.impl;

import com.hh23.car4u.dtos.request.DriverLicenseRequest;
import com.hh23.car4u.dtos.request.UserAddressRequest;
import com.hh23.car4u.dtos.request.UserInfoUpdate;
import com.hh23.car4u.dtos.response.CarResponse;
import com.hh23.car4u.dtos.response.UserResponse;
import com.hh23.car4u.entities.DriverLicense;
import com.hh23.car4u.entities.UserAddress;
import com.hh23.car4u.exception.AppException;
import com.hh23.car4u.exception.ErrorCode;
import com.hh23.car4u.mappers.CarMapper;
import com.hh23.car4u.mappers.UserMapper;
import com.hh23.car4u.repositories.CarRepository;
import com.hh23.car4u.repositories.UserRepository;
import com.hh23.car4u.services.S3Service;
import com.hh23.car4u.services.UserService;
import com.hh23.car4u.utils.ObjectIdUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    CarRepository carRepository;
    UserRepository userRepository;
    UserMapper userMapper;
    CarMapper carMapper;

    S3Service s3Service;

    @Override
    public UserResponse getUserById(String userId) {
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        ObjectId userIdObj = ObjectIdUtil.toObjectId(userId);
        return userRepository.findById(userIdObj)
                .map(userMapper::toResponse)
                .orElse(null);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        return userRepository.findByEmail(email)
                .map(userMapper::toResponse)
                .orElse(null);
    }

    @Override
    public UserResponse getUserByPhone(String phone) {
        return null;
    }

    @Override
    public List<CarResponse> getMyCars() {
        var context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = context.getAuthentication().getName();
        ObjectId objectUserId = ObjectIdUtil.toObjectId(userId);
        var user = userRepository.findById(objectUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getMyCars() != null && !user.getMyCars().isEmpty()) {
            return carRepository.findAllById(user.getMyCars())
                    .stream()
                    .map(carMapper::toResponse)
                    .toList();
        }
        return List.of();
    }

    @Override
    public List<CarResponse> getMyFavorites() {
    var context = SecurityContextHolder.getContext();
    if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
    String userId = context.getAuthentication().getName();
    ObjectId objectUserId = ObjectIdUtil.toObjectId(userId);
    var user = userRepository.findById(objectUserId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    if (user.getMyFavorites() != null && !user.getMyFavorites().isEmpty()) {
        return carRepository.findAllById(user.getMyFavorites())
                .stream()
                .map(carMapper::toResponse)
                .toList();
    }
        return List.of();
    }

    @Override
    public UserResponse likeCar(String carId) {
        var context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = context.getAuthentication().getName();
        ObjectId objectUserId = ObjectIdUtil.toObjectId(userId);
        var user = userRepository.findById(objectUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        ObjectId objectCarId = ObjectIdUtil.toObjectId(carId);
        if (user.getMyFavorites() == null) {
            user.setMyFavorites(List.of(objectCarId));
        } else if (!user.getMyFavorites().contains(objectCarId)) {
            user.getMyFavorites().add(objectCarId);
        }
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse unlikeCar(String carId) {
        var context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = context.getAuthentication().getName();
        ObjectId objectUserId = ObjectIdUtil.toObjectId(userId);
        var user = userRepository.findById(objectUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        ObjectId objectCarId = ObjectIdUtil.toObjectId(carId);
        if (user.getMyFavorites() != null && user.getMyFavorites().contains(objectCarId)) {
            user.getMyFavorites().remove(objectCarId);
        }
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse addAddress(UserAddressRequest request) {
        var context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = context.getAuthentication().getName();
        ObjectId objectUserId = ObjectIdUtil.toObjectId(userId);
        var user = userRepository.findById(objectUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        UserAddress userAddress =  UserAddress.builder()
                .id(UUID.randomUUID().toString())
                .no(request.no())
                .street(request.street())
                .city(request.city())
                .ward(request.ward())
                .district(request.district())
                .reminder(request.reminder())
                .isDefault(request.isDefault())
                .coordinates(request.coordinates())
                .type(request.type())
                .build();
        if (user.getAddresses() == null) {
            userAddress.setDefault(true);
            user.setAddresses(List.of(userAddress));
        } else {
            if (userAddress.isDefault()) {
                user.getAddresses().forEach(address -> address.setDefault(false));
            }
            user.getAddresses().add(userAddress);
            user.getAddresses().sort((a, b) -> Boolean.compare(b.isDefault(), a.isDefault()));
        }
        return  userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse updateAddress(UserAddressRequest request, String addressId) {
        var context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = context.getAuthentication().getName();
        ObjectId objectUserId = ObjectIdUtil.toObjectId(userId);
        var user = userRepository.findById(objectUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
            throw new AppException(ErrorCode.ADDRESS_NOT_FOUND);
        }
        UserAddress addressToUpdate = user.getAddresses()
                .stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        addressToUpdate.setNo(request.no());
        addressToUpdate.setStreet(request.street());
        addressToUpdate.setCity(request.city());
        addressToUpdate.setWard(request.ward());
        addressToUpdate.setDistrict(request.district());
        addressToUpdate.setReminder(request.reminder());
        addressToUpdate.setDefault(request.isDefault());
        addressToUpdate.setCoordinates(request.coordinates());
        addressToUpdate.setType(request.type());
        if (request.isDefault()) {
            user.getAddresses().forEach(address -> {
                if (!address.getId().equals(addressId)) {
                    address.setDefault(false);
                }
            });
            addressToUpdate.setDefault(true);
        } else {
            // Nếu không đặt mặc định nhưng đây là địa chỉ mặc định hiện tại -> giữ nguyên
            boolean hasOtherDefault = user.getAddresses().stream()
                    .anyMatch(addr -> !addr.getId().equals(addressId) && addr.isDefault());
            if (!hasOtherDefault) {
                addressToUpdate.setDefault(true); // đảm bảo luôn có 1 default
            } else {
                addressToUpdate.setDefault(false);
            }
        }
        // Cập nhật danh sách địa chỉ (nếu cần clone list, tránh trường hợp List.of())
        List<UserAddress> updatedAddresses = user.getAddresses().stream()
                .map(address -> address.getId().equals(addressId) ? addressToUpdate : address)
                .collect(Collectors.toList());

        // ⭐️ Sắp xếp lại: default lên đầu
        updatedAddresses.sort((a, b) -> Boolean.compare(b.isDefault(), a.isDefault()));

        user.setAddresses(updatedAddresses);
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse deleteAddress(String addressId) {
        var context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String userId = context.getAuthentication().getName();
        ObjectId objectUserId = ObjectIdUtil.toObjectId(userId);
        var user = userRepository.findById(objectUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
            throw new AppException(ErrorCode.ADDRESS_NOT_FOUND);
        }

        List<UserAddress> originalAddresses = user.getAddresses();

        // Tìm địa chỉ cần xóa
        Optional<UserAddress> addressToDeleteOpt = originalAddresses.stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst();

        if (addressToDeleteOpt.isEmpty()) {
            throw new AppException(ErrorCode.ADDRESS_NOT_FOUND);
        }

        UserAddress addressToDelete = addressToDeleteOpt.get();

        // Xóa địa chỉ khỏi danh sách
        List<UserAddress> updatedAddresses = originalAddresses.stream()
                .filter(address -> !address.getId().equals(addressId))
                .collect(Collectors.toCollection(ArrayList::new));

        // Nếu địa chỉ bị xóa là default, gán địa chỉ đầu tiên (nếu còn) làm mặc định
        if (addressToDelete.isDefault() && !updatedAddresses.isEmpty()) {
            updatedAddresses.get(0).setDefault(true);
        }

        // Sắp xếp lại: mặc định nằm trên cùng
        updatedAddresses.sort((a, b) -> Boolean.compare(b.isDefault(), a.isDefault()));

        user.setAddresses(updatedAddresses);
        userRepository.save(user);

        return userMapper.toResponse(user);
    }


    @Override
    public UserResponse makeAddressDefault(String addressId) {
        var context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = context.getAuthentication().getName();
        ObjectId objectUserId = ObjectIdUtil.toObjectId(userId);
        var user = userRepository.findById(objectUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
            throw new AppException(ErrorCode.ADDRESS_NOT_FOUND);
        }
        List<UserAddress> updatedAddresses = user.getAddresses()
                .stream()
                .map(address -> {
                    if (address.getId().equals(addressId)) {
                        address.setDefault(true);
                    } else {
                        address.setDefault(false);
                    }
                    return address;
                })
                .toList();
        user.setAddresses(updatedAddresses);
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateDriverLicense(DriverLicenseRequest request) throws IOException {
        var context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = context.getAuthentication().getName();
        ObjectId objectUserId = ObjectIdUtil.toObjectId(userId);
        var user = userRepository.findById(objectUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        DriverLicense driverLicense = user.getDriverLicense();
        String imageUrl = null;
        System.out.println("Driver License: " + request);
        if (driverLicense == null) {
            if (request.file() != null && !request.file().isEmpty()) {
                imageUrl = s3Service.uploadFile(request.file());
            }
            driverLicense = DriverLicense.builder()
                    .licenseNumber(request.licenseNumber())
                    .name(request.name())
                    .dob(request.dob())
                    .image(imageUrl)
                    .isVerified(false)
                    .build();
        } else {
            if (request.file() != null && !request.file().isEmpty()) {
                imageUrl = s3Service.uploadFile(request.file());
            } else {
                imageUrl = driverLicense.getImage();
            }
            driverLicense.setLicenseNumber(request.licenseNumber());
            driverLicense.setName(request.name());
            driverLicense.setDob(request.dob());
            driverLicense.setImage(imageUrl);
        }
        user.setDriverLicense(driverLicense);
        return userMapper.toResponse(
                userRepository.save(user)
        );
    }

    @Override
    public UserResponse updatePhone(String phone) {
        var context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = context.getAuthentication().getName();
        ObjectId objectUserId = ObjectIdUtil.toObjectId(userId);
        var user = userRepository.findById(objectUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (phone == null || phone.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_PHONE_NUMBER);
        }
        if (user.getPhone() != null && user.getPhone().equals(phone)) {
            return userMapper.toResponse(user);
        }
        if (userRepository.existsByPhone(phone)) {
            throw new AppException(ErrorCode.PHONE_ALREADY_EXISTS);
        }
        user.setPhone(phone);
        return userMapper.toResponse(
                userRepository.save(user)
        );
    }

    @Override
    public UserResponse updateInfo(UserInfoUpdate request) {
        var context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = context.getAuthentication().getName();
        ObjectId objectUserId = ObjectIdUtil.toObjectId(userId);
        var user = userRepository.findById(objectUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (request.name() != null && !request.name().isEmpty()) {
            user.setName(request.name());
        }
        if (request.dob() != null) {
            user.setDob(request.dob());
        }
        if (request.gender() != null && !request.gender().isEmpty()) {
            user.setGender(request.gender());
        }
        return userMapper.toResponse(
                userRepository.save(user)
        );
    }
}
