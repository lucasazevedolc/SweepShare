package project.sweepshare.database.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tasks_assignments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TasksAssignmentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="task_id", nullable = false)
    private TasksEntity task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private UsersEntity user;
}
