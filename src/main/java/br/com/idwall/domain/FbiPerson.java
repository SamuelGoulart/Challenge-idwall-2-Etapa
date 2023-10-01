package br.com.idwall.domain;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_fbi_wanted_person")
public class FbiPerson {
    @Id
    @Column(name = "fbi_person_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fbi_person")
    @SequenceGenerator(name = "fbi_person", sequenceName = "SQ_T_FBI_PERSON", allocationSize = 1)
    private int fbiPersonId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "aliases", length = 255)
    private String aliases;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "sex", length = 20)
    private String sex;

    @Column(name = "age")
    private int age;

    @Column(name = "colored_person", length = 50)
    private String coloredPerson;

    @Column(name = "images", length = 1000)
    private String images;

    @Column(name = "occupations", length = 100)
    private String occupations;

    @Column(name = "place_of_birth", length = 100)
    private String placeOfBirth;

    @Column(name = "eyes", length = 80)
    private String eyes;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "update_at")
    private Date updateAt;

    @Column(name = "delete_at")
    private Date deleteAt;
}
