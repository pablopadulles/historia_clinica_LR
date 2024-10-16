package net.pladema.address.repository.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AddressVo {
    private Integer id;

    private String street;

    private String number;

    private String floor;

    private String apartment;

    private String postcode;

    private Integer cityId;

    private String cityDescription;

	private Short countryId;

	private Short provinceId;

	private Short departmentId;

	private String provinceName;

}
