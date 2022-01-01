package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() { //아무나 부를 수 없게 public 대신 protected, jpa 특성 상 기본 생성자가 필요함
    }

    //생성할 때만 값이 들어오고 막는게 좋음 -> 생성자 사용, setter 막기
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

}
