package com.asianaidt.dutyfree.domain.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Departure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "departureId")
    private Long id;
    private String flightDate;
    private String boarding;
    private String airline;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;
    @OneToOne
    @JoinColumn(name = "flightId")
    private Flight flight;
    private LocalDateTime regDate;
}
