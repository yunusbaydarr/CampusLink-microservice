package com.CampusLink.entity;


import com.campuslink.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "club_member", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "club_id"})
})@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClubMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
