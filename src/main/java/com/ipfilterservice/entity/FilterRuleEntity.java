package com.ipfilterservice.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(
    name = "ip_filter_rule",
    uniqueConstraints = @UniqueConstraint(columnNames = {"source_cidr", "destination_cidr"}))
public class FilterRuleEntity {
  @Id
  @Column(name = "rule_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "source_cidr", nullable = false)
  private String sourceCidr;

  @Column(name = "destination_cidr", nullable = false)
  private String destinationCidr;

  @Column(name = "allow_deny_ind", nullable = false)
  private int allowDenyInd;
}
