package org.example.friendfinder.repository;

import org.example.friendfinder.model.FriendRequest;
import org.example.friendfinder.model.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    Optional<FriendRequest> findByRequester_IdAndAddressee_Id(Long requesterId, Long addresseeId);

    @Query("""
       select fr from FriendRequest fr
       where (
          (fr.requester.id = :a and fr.addressee.id = :b)
          or
          (fr.requester.id = :b and fr.addressee.id = :a)
       )
       and fr.status = :status
    """)
    Optional<FriendRequest> findBetweenWithStatus(@Param("a") Long a,
                                                  @Param("b") Long b,
                                                  @Param("status") FriendRequestStatus status);

    @Query("""
       select fr from FriendRequest fr
       where (
          (fr.requester.id = :a and fr.addressee.id = :b)
          or
          (fr.requester.id = :b and fr.addressee.id = :a)
       )
    """)
    Optional<FriendRequest> findAnyBetween(@Param("a") Long a,
                                           @Param("b") Long b);

    @Query("""
        select fr from FriendRequest fr
        join fetch fr.requester r
        left join fetch r.profile
        join fetch fr.addressee a
        left join fetch a.profile
        where fr.status = :status
          and (r.id = :meId or a.id = :meId)
    """)
    List<FriendRequest> findAllAcceptedForUser(@Param("meId") Long meId,
                                               @Param("status") FriendRequestStatus status);

    @Query("""
        select case when count(fr) > 0 then true else false end
        from FriendRequest fr
        where fr.status = 'ACCEPTED'
          and (
                (fr.requester.id = :a and fr.addressee.id = :b)
             or (fr.requester.id = :b and fr.addressee.id = :a)
          )
    """)
    boolean existsAcceptedBetween(@Param("a") Long a,
                                  @Param("b") Long b);

    @Query("""
        select case
            when fr.requester.id = :meId then fr.addressee.id
            else fr.requester.id
        end
        from FriendRequest fr
        where fr.status = :status
          and (fr.requester.id = :meId or fr.addressee.id = :meId)
    """)
    List<Long> findAcceptedFriendIds(@Param("meId") Long meId,
                                     @Param("status") FriendRequestStatus status);

    List<FriendRequest> findAllByAddressee_IdAndStatus(Long addresseeId, FriendRequestStatus status);

    List<FriendRequest> findAllByRequester_IdAndStatus(Long requesterId, FriendRequestStatus status);

    /**
     * Convenience method: returns friend IDs for ACCEPTED relations only.
     * (Same logic as findAcceptedFriendIds(meId, ACCEPTED) ولكن بدون باراميتر status)
     */
    @Query("""
        select
            case
                when fr.requester.id = :me then fr.addressee.id
                else fr.requester.id
            end
        from FriendRequest fr
        where fr.status = 'ACCEPTED'
          and (fr.requester.id = :me or fr.addressee.id = :me)
    """)
    List<Long> findFriendIds(@Param("me") Long me);
}
