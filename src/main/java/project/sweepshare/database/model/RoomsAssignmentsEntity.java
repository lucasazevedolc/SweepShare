package project.sweepshare.database.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms_assignments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoomsAssignmentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="room_id",nullable = false)
    private RoomsEntity room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private UsersEntity user;
}
