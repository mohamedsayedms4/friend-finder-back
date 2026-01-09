package org.example.friendfinder.mapper;

import org.example.friendfinder.dto.RegisterRequest;
import org.example.friendfinder.dto.UserResponse;
import org.example.friendfinder.model.User;
import org.mapstruct.*;

/**
 * User mapping layer.
 *
 * @author Mohamed Sayed
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps a User entity to its public response DTO.
     *
     * @param user entity
     * @return user response
     */
    @Mapping(target = "role", expression = "java(user.getRole().getName())")
    UserResponse toUserResponse(User user);

    /**
     * Creates a User entity template from register request.
     * Password must be hashed separately (service layer).
     *
     * @param req registration request
     * @return user entity skeleton
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User fromRegister(RegisterRequest req);
}
