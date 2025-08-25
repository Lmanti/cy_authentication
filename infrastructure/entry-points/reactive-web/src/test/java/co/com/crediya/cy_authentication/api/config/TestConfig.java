package co.com.crediya.cy_authentication.api.config;

import co.com.crediya.cy_authentication.api.mapper.UserDTOMapper;
import co.com.crediya.cy_authentication.usecase.user.IUserUseCase;
import co.com.crediya.cy_authentication.usecase.user.UserUseCase;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public IUserUseCase userUseCase() {
        return Mockito.mock(UserUseCase.class);
    }

    @Bean
    @Primary
    public UserDTOMapper userDTOMapper() {
        return Mockito.mock(UserDTOMapper.class);
    }
}