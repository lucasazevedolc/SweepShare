package project.sweepshare.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.sweepshare.database.model.RoomsAssignmentsEntity;
import project.sweepshare.database.model.UsersEntity;
import project.sweepshare.database.model.WgsEntity;
import project.sweepshare.database.repository.IRoomsAssignmentsRepository;
import project.sweepshare.database.repository.IUsersRepository;
import project.sweepshare.database.repository.IWgsRepository;
import project.sweepshare.enums.CleaningStyle;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomRotationService {
    private final IWgsRepository wgsRepository;
    private final IUsersRepository usersRepository;
    private final IRoomsAssignmentsRepository assignmentsRepository;

    @Transactional
    public void rotateAllWgs() {
        int targetStyle = CleaningStyle.WEEKLY_ROTATION.ordinal();
        List<WgsEntity> rotationWgs = wgsRepository.findByCleaningStyle(targetStyle);

        for (WgsEntity wg : rotationWgs) {
            try {
                rotateSingleWg(wg);
            } catch (Exception e) {
                System.err.println(("Failed to rotate room for WgId: " + wg.getId()));
            }
        }
    }

    public void rotateSingleWg(WgsEntity wg){
        List<UsersEntity> members = usersRepository.findByWgIdAndActiveTrueOrderByNameAsc(wg.getId());

        if(members.isEmpty()){
            System.err.println("Fail to fetch Wg Members, WgId: " + wg.getId());
        }

        List<RoomsAssignmentsEntity> assignments = assignmentsRepository.findByRoomWgId(wg.getId());
        if(assignments.isEmpty()){
            System.err.println("WG has no active room assignment to rotate, WgId: " + wg.getId());
        }

        int previousUserIndex = 0;
        if(assignments.get(0).getUser() != null){
            previousUserIndex = members.indexOf(assignments.get(0).getUser());
        }

        int startingIndex = (previousUserIndex + 1) % assignments.size();
        if(assignments.size() % members.size() != 0){
            startingIndex = (startingIndex + (assignments.size() % members.size()) -1) % members.size();
        }

        for(int i=0; i<assignments.size(); i++){
            RoomsAssignmentsEntity assignment = assignments.get(i);

            int memberIndex = (startingIndex + i) % members.size();
            assignment.setUser(members.get(memberIndex));
        }

        assignmentsRepository.saveAll(assignments);
    }

    @Transactional
    public void forceRotation(Long wgId, String userEmail){
        String  email = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersEntity  user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.getWg() == null){
            throw new RuntimeException("You do not belong to a WG");
        }

        if(!user.getWg().getId().equals(wgId)){
            throw new RuntimeException("You don't have permission to do this");
        }

        WgsEntity wg = wgsRepository.findById(wgId)
                .orElseThrow(() -> new RuntimeException("WG not found"));

        rotateSingleWg(wg);
    }
}
