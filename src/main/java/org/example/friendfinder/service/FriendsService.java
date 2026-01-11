package org.example.friendfinder.service;

import lombok.RequiredArgsConstructor;
import org.example.friendfinder.dto.FriendDTO;
import org.example.friendfinder.dto.FriendRequestItemDTO;
import org.example.friendfinder.dto.RelationState;
import org.example.friendfinder.dto.UserRelationDTO;
import org.example.friendfinder.model.FriendRequest;
import org.example.friendfinder.model.FriendRequestStatus;
import org.example.friendfinder.model.User;
import org.example.friendfinder.model.UserBlock;
import org.example.friendfinder.model.UserProfile;
import org.example.friendfinder.repository.FriendRequestRepository;
import org.example.friendfinder.repository.UserBlockRepository;
import org.example.friendfinder.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FriendsService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserBlockRepository userBlockRepository;

    private User mustGetUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void assertNotBlocked(Long meId, Long otherId) {
        if (userBlockRepository.existsByBlocker_IdAndBlocked_Id(meId, otherId)) {
            throw new RuntimeException("You blocked this user");
        }
        if (userBlockRepository.existsByBlocker_IdAndBlocked_Id(otherId, meId)) {
            throw new RuntimeException("You are blocked by this user");
        }
    }

    private String safeFirstName(User u) {
        UserProfile p = u.getProfile();
        return p != null ? p.getFirstName() : null;
    }

    private String safeLastName(User u) {
        UserProfile p = u.getProfile();
        return p != null ? p.getLastName() : null;
    }

    private String safeProfilePicture(User u) {
        UserProfile p = u.getProfile();
        return p != null ? p.getProfilePicture() : null;
    }

    // =========================
    // Friend Requests actions
    // =========================

    @Transactional
    public void sendRequest(Long meId, Long targetId) {
        if (meId.equals(targetId)) throw new RuntimeException("Cannot friend yourself");

        mustGetUser(meId);
        mustGetUser(targetId);

        assertNotBlocked(meId, targetId);

        // already friend?
        friendRequestRepository.findBetweenWithStatus(meId, targetId, FriendRequestStatus.ACCEPTED)
                .ifPresent(fr -> { throw new RuntimeException("Already friends"); });

        // pending exists any direction?
        friendRequestRepository.findBetweenWithStatus(meId, targetId, FriendRequestStatus.PENDING)
                .ifPresent(fr -> { throw new RuntimeException("Request already pending"); });

        FriendRequest fr = FriendRequest.builder()
                .requester(mustGetUser(meId))
                .addressee(mustGetUser(targetId))
                .status(FriendRequestStatus.PENDING)
                .build();
        friendRequestRepository.save(fr);
    }

    @Transactional
    public void cancelOutgoing(Long meId, Long targetId) {
        FriendRequest fr = friendRequestRepository
                .findByRequester_IdAndAddressee_Id(meId, targetId)
                .orElseThrow(() -> new RuntimeException("No outgoing request"));

        if (fr.getStatus() != FriendRequestStatus.PENDING) throw new RuntimeException("Not pending");
        friendRequestRepository.delete(fr);
    }

    @Transactional
    public void accept(Long meId, Long requestId) {
        FriendRequest fr = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!fr.getAddressee().getId().equals(meId)) throw new RuntimeException("Not allowed");
        if (fr.getStatus() != FriendRequestStatus.PENDING) throw new RuntimeException("Not pending");

        Long otherId = fr.getRequester().getId();
        assertNotBlocked(meId, otherId);

        fr.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(fr);
    }

    @Transactional
    public void reject(Long meId, Long requestId) {
        FriendRequest fr = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!fr.getAddressee().getId().equals(meId)) throw new RuntimeException("Not allowed");
        if (fr.getStatus() != FriendRequestStatus.PENDING) throw new RuntimeException("Not pending");

        fr.setStatus(FriendRequestStatus.REJECTED);
        friendRequestRepository.save(fr);
    }

    @Transactional
    public void unfriend(Long meId, Long otherId) {
        FriendRequest fr = friendRequestRepository
                .findBetweenWithStatus(meId, otherId, FriendRequestStatus.ACCEPTED)
                .orElseThrow(() -> new RuntimeException("Not friends"));
        friendRequestRepository.delete(fr);
    }

    @Transactional
    public void block(Long meId, Long otherId) {
        if (meId.equals(otherId)) throw new RuntimeException("Cannot block yourself");

        User me = mustGetUser(meId);
        User other = mustGetUser(otherId);

        // delete any relation / requests between
        friendRequestRepository.findAnyBetween(meId, otherId).ifPresent(friendRequestRepository::delete);

        userBlockRepository.findByBlocker_IdAndBlocked_Id(meId, otherId)
                .ifPresent(b -> { throw new RuntimeException("Already blocked"); });

        userBlockRepository.save(UserBlock.builder().blocker(me).blocked(other).build());
    }

    @Transactional
    public void unblock(Long meId, Long otherId) {
        userBlockRepository.deleteByBlocker_IdAndBlocked_Id(meId, otherId);
    }

    // =========================
    // Requests listing
    // =========================

    @Transactional(readOnly = true)
    public List<FriendRequestItemDTO> incomingRequestItems(Long meId) {
        return friendRequestRepository
                .findAllByAddressee_IdAndStatus(meId, FriendRequestStatus.PENDING)
                .stream()
                .filter(fr ->
                        !userBlockRepository.existsByBlocker_IdAndBlocked_Id(meId, fr.getRequester().getId()) &&
                                !userBlockRepository.existsByBlocker_IdAndBlocked_Id(fr.getRequester().getId(), meId)
                )
                .map(fr -> {
                    User u = fr.getRequester();
                    return FriendRequestItemDTO.builder()
                            .requestId(fr.getId())
                            .userId(u.getId())
                            .firstName(safeFirstName(u))
                            .lastName(safeLastName(u))
                            .email(u.getEmail())
                            .profilePicture(safeProfilePicture(u))
                            .state(RelationState.INCOMING)
                            .build();
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FriendRequestItemDTO> outgoingRequestItems(Long meId) {
        return friendRequestRepository
                .findAllByRequester_IdAndStatus(meId, FriendRequestStatus.PENDING)
                .stream()
                .filter(fr ->
                        !userBlockRepository.existsByBlocker_IdAndBlocked_Id(meId, fr.getAddressee().getId()) &&
                                !userBlockRepository.existsByBlocker_IdAndBlocked_Id(fr.getAddressee().getId(), meId)
                )
                .map(fr -> {
                    User u = fr.getAddressee();
                    return FriendRequestItemDTO.builder()
                            .requestId(fr.getId())
                            .userId(u.getId())
                            .firstName(safeFirstName(u))
                            .lastName(safeLastName(u))
                            .email(u.getEmail())
                            .profilePicture(safeProfilePicture(u))
                            .state(RelationState.OUTGOING)
                            .build();
                })
                .toList();
    }

    // =========================
    // Relation
    // =========================

    @Transactional(readOnly = true)
    public UserRelationDTO relation(Long meId, Long otherId) {
        // blocked states
        if (userBlockRepository.existsByBlocker_IdAndBlocked_Id(meId, otherId)) {
            return new UserRelationDTO(otherId, RelationState.BLOCKED, null);
        }
        if (userBlockRepository.existsByBlocker_IdAndBlocked_Id(otherId, meId)) {
            return new UserRelationDTO(otherId, RelationState.BLOCKING, null);
        }

        // friend
        var accepted = friendRequestRepository.findBetweenWithStatus(meId, otherId, FriendRequestStatus.ACCEPTED);
        if (accepted.isPresent()) {
            return new UserRelationDTO(otherId, RelationState.FRIEND, accepted.get().getId());
        }

        // pending
        var pending = friendRequestRepository.findBetweenWithStatus(meId, otherId, FriendRequestStatus.PENDING);
        if (pending.isPresent()) {
            FriendRequest fr = pending.get();
            if (fr.getRequester().getId().equals(meId)) {
                return new UserRelationDTO(otherId, RelationState.OUTGOING, fr.getId());
            } else {
                return new UserRelationDTO(otherId, RelationState.INCOMING, fr.getId());
            }
        }

        return new UserRelationDTO(otherId, RelationState.NONE, null);
    }

    // =========================
    // Friends list (ACCEPTED)
    // =========================

    @Transactional(readOnly = true)
    public List<FriendDTO> friends(Long meId) {
        mustGetUser(meId);

        return friendRequestRepository
                .findAllAcceptedForUser(meId, FriendRequestStatus.ACCEPTED)
                .stream()
                .map(fr -> {
                    User other = fr.getRequester().getId().equals(meId)
                            ? fr.getAddressee()
                            : fr.getRequester();

                    var p = other.getProfile();
                    return FriendDTO.builder()
                            .userId(other.getId())
                            .email(other.getEmail())
                            .firstName(p != null ? p.getFirstName() : null)
                            .lastName(p != null ? p.getLastName() : null)
                            .profilePicture(p != null ? p.getProfilePicture() : null)
                            .build();
                })
                .toList();
    }

    // =========================
    // ✅ NEW: Exclude friends/pending from suggestions
    // =========================
    @Transactional(readOnly = true)
    public Set<Long> excludedIdsForSuggestions(Long meId) {
        mustGetUser(meId);

        Set<Long> excluded = new HashSet<>();
        excluded.add(meId);

        // 1) exclude ACCEPTED friends
        friendRequestRepository.findAllAcceptedForUser(meId, FriendRequestStatus.ACCEPTED)
                .forEach(fr -> {
                    Long otherId = fr.getRequester().getId().equals(meId)
                            ? fr.getAddressee().getId()
                            : fr.getRequester().getId();
                    excluded.add(otherId);
                });

        // 2) exclude PENDING outgoing
        friendRequestRepository.findAllByRequester_IdAndStatus(meId, FriendRequestStatus.PENDING)
                .forEach(fr -> excluded.add(fr.getAddressee().getId()));

        // 3) exclude PENDING incoming
        friendRequestRepository.findAllByAddressee_IdAndStatus(meId, FriendRequestStatus.PENDING)
                .forEach(fr -> excluded.add(fr.getRequester().getId()));

        return excluded;
    }


    public boolean areFriends(Long a, Long b) {
        if (a.equals(b)) return true;
        return friendRequestRepository.existsAcceptedBetween(a, b);
    }

    public Set<Long> friendIds(Long meId) {
        List<Long> ids = friendRequestRepository.findAcceptedFriendIds(meId, FriendRequestStatus.ACCEPTED);
        return new HashSet<>(ids);
    }





    public List<User> listFriendsUsers(Long meId) {

        List<Long> friendIds = friendRequestRepository.findFriendIds(meId);

        if (friendIds.isEmpty()) {
            return List.of();
        }

        return userRepository.findAllById(friendIds);
    }
}
