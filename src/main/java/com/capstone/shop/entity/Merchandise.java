package com.capstone.shop.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Merchandise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private int price;

    @Column
    private String location;

    @Column(name = "like_count") //sql 의 like 문과 충돌 발생해서 like_count 로 컬럼 생성
    private int like;

    @OneToMany
    private List<Image> images;
}