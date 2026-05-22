package project.sweepshare.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoomsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private Integer frequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wg_id", nullable = false)
    private WgsEntity wg;

    @OneToMany(mappedBy = "room",cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<TasksEntity> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<RoomsAssignmentsEntity> roomAssignment = new ArrayList<>();
}
