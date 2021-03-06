package com.example.backend_efub_twitter.domain.profiile.entity;

import com.example.backend_efub_twitter.domain.UploadedFile.entity.UploadedFile;
import com.example.backend_efub_twitter.domain.user.entity.User;
import com.example.backend_efub_twitter.domain.profiile.domain.ProfileModifyReqDto;
import com.example.backend_efub_twitter.global.entity.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Profile extends BaseTimeEntity {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(length = 16)
	private UUID id;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "profile_file_id", nullable = true)
	private UploadedFile uploadedFile;

	@NotNull
	@Column(unique = true, nullable = false)
	private String nickname;

	@NotNull
	private String bio;

	@Builder
	public Profile(User user, String nickname, String bio){
		this.user = user;
		this.nickname = nickname;
		this.bio = bio;
	}

	public void updateProfile(ProfileModifyReqDto profileModifyReqDto){
		this.nickname = profileModifyReqDto.getNickname();
		this.bio = profileModifyReqDto.getBio();
	}

}
