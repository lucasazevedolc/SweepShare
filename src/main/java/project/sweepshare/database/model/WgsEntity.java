package project.sweepshare.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "wgs")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class WgsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "cleaning_style",nullable = false)
    private int cleaningStyle;
    //0 1 Person fixed per room
    //1 Every week each person gets a different room
    //2 Every person gets a weekly amount of tasks

    @Column(name = "rent_style")
    private Integer rentStyle;
    //0 Each has a contract
    //1 1 Person pays it all

    @OneToMany(mappedBy = "wg", fetch = FetchType.LAZY)
    @Builder.Default
    private List<UsersEntity> members = new ArrayList<>();
}
