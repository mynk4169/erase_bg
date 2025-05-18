package in.mynk.erase_Bg.service;

import in.mynk.erase_Bg.dto.UserDto;

public interface UserService {

    UserDto saveUser(UserDto userDto);

    UserDto getUserByClerkId(String clerkId);

    void deleteUserByClerkId(String clerkId);


}
