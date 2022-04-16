package com.ipfilterservice.model;

import com.ipfilterservice.validator.CIDR;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class FilterRule {
  private Long id;
  @CIDR private String sourceCidr;
  @CIDR private String destinationCidr;
  @NotNull private int allowedDenyInd;
  private String service;
}
