package com.cosain.trilo.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(of = {"fileName"})
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Embeddable
public class MyPageImage {

    private final static String DEFAULT_IMAGE_NAME = "users/defaultBadge.png";

    @Column(name = "my_page_image_file_name")
    private final String fileName;

    private MyPageImage(final String fileName) {
        this.fileName = fileName;
    }

    public static MyPageImage initializeMyPageImage(){
        return new MyPageImage(DEFAULT_IMAGE_NAME);
    }
}
