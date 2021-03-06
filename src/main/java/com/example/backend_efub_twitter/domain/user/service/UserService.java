package com.example.backend_efub_twitter.domain.user.service;

import com.example.backend_efub_twitter.domain.profiile.domain.ProfileResDto;
import com.example.backend_efub_twitter.domain.profiile.entity.Profile;
import com.example.backend_efub_twitter.domain.profiile.repository.ProfileRepository;
import com.example.backend_efub_twitter.domain.user.dto.LoginReqDto;
import com.example.backend_efub_twitter.domain.user.dto.SignupReqDto;
import com.example.backend_efub_twitter.domain.user.dto.UserResDto;
import com.example.backend_efub_twitter.global.config.Account;
import com.example.backend_efub_twitter.domain.user.entity.User;
import com.example.backend_efub_twitter.domain.user.exception.UserNotFoundException;
import com.example.backend_efub_twitter.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final ProfileRepository profileRepository;

	@Transactional
	public ResponseEntity<Object> joinUser(SignupReqDto signupReqDto){
		User user = signupReqDto.toEntity(signupReqDto);

		String[] array = signupReqDto.getEmail().split("@");
		Profile profile = Profile.builder()
			.nickname(array[0])
			.user(user)
			.bio("자기소개를 해주세요.")
			.build();

		user.setProfile(profile);

		userRepository.save(user);
		profileRepository.save(profile);
		return ResponseEntity.status(HttpStatus.CREATED).body(user.getFullName()+"님이 성공적으로 가입되었습니다.");
	}

	public UserResDto.Response getUserInfoByProfile(String nickname){
		Profile profile = profileRepository.findByNickname(nickname)
				.orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));

		User user = profile.getUser();

		ProfileResDto.Response profileResDto = ProfileResDto.Response
				.builder()
				.nickname(nickname)
				.bio(profile.getBio())
				.build();


		return UserResDto.Response
				.builder()
				.id(user.getId())
				.email(user.getEmail())
				.fullName(user.getFullName())
				.profile(profileResDto)
				.build();
	}

	@Transactional(readOnly = true)
	public User findUser(LoginReqDto loginReqDto) throws UserNotFoundException {
		User user = userRepository.findByEmail(loginReqDto.getEmail())
			.orElseThrow(() -> new UserNotFoundException("해당하는 유저가 없습니다."));
		return user;
	}

	public Boolean checkDuplicateUsers(SignupReqDto signupReqDto){
		return userRepository.existsByEmail(signupReqDto.getEmail());
	}

	@Override
	public Account loadUserByUsername(String email) throws UsernameNotFoundException {
		System.out.println(email);
		return userRepository.findByEmail(email).map(User::toAccount)
			.orElseThrow(() -> new UsernameNotFoundException("등록되지 않은 사용자입니다."));

	}

}