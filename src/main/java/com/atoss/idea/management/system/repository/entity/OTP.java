package com.atoss.idea.management.system.repository.entity;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class OTP {
    String code;
    Long creationDate;
}
