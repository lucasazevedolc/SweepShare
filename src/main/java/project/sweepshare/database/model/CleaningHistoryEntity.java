package project.sweepshare.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cleaning_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CleaningHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wg_id", nullable = false)
    private WgsEntity wg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UsersEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomsEntity room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private TasksEntity task;

    @Column(name = "cleaned_at", nullable = false)
    private LocalDateTime cleanedAt;

    @Column(name = "was_completed", nullable = false)
    private Boolean wasCompleted;

}
