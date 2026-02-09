package com.CampusLink.repository;


import com.CampusLink.entity.Club;
import com.CampusLink.entity.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubMemberRepository extends JpaRepository<ClubMember,Long> {


    @Query("""
        SELECT cm
        FROM ClubMember cm
        WHERE cm.club = :club
          AND cm.userId = :userId
    """)
    Optional<ClubMember> findByClubAndUserId(
            @Param("club") Club club,
            @Param("userId") Long userId
    );

    @Query("""
        SELECT CASE WHEN COUNT(cm) > 0 THEN true ELSE false END
        FROM ClubMember cm
        WHERE cm.club.id = :clubId
          AND cm.userId = :userId
    """)
    boolean existsByClubIdAndUserId(
            @Param("clubId") Long clubId,
            @Param("userId") Long userId
    );

    @Query("""
        SELECT cm
        FROM ClubMember cm
        WHERE cm.club.id = :clubId
    """)
    List<ClubMember> findAllByClubId(@Param("clubId") Long clubId);


    @Query("""
    SELECT cm
    FROM ClubMember cm
    WHERE cm.club.id = :clubId
      AND cm.userId = :userId
""")
    Optional<ClubMember> findByClubIdAndUserId(
            @Param("clubId") Long clubId,
            @Param("userId") Long userId
    );

}
