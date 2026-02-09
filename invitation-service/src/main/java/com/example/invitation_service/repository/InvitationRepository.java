package com.example.invitation_service.repository;


import com.campuslink.common.enums.Status;
import com.example.invitation_service.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation,Long> {


    List<Invitation> findAllByToUserIdOrderByCreatedAtDesc(Long toUserId);


    @Query("""
        SELECT i
        FROM Invitation i
        WHERE i.status = :status
          AND i.createdAt < :before
    """)
    List<Invitation> findAllByStatusAndCreatedAtBefore(
            @Param("status") Status status,
            @Param("before") LocalDateTime before
    );


    @Query("""
        SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END
        FROM Invitation i
        WHERE i.clubId = :clubId
          AND i.toUserId = :toUserId
          AND i.status = :status
    """)
    boolean existsByClubAndToUserWithStatus(
            @Param("clubId") Long clubId,
            @Param("toUserId") Long toUserId,
            @Param("status") Status status
    );
}
