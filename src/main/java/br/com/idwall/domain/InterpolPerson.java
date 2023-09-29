package br.com.idwall.domain;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_interpol_wanted_person")
public class InterpolPerson {
    @Id
    @Column(name = "person_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person")
    @SequenceGenerator(name = "person", sequenceName = "SQ_T_PERSON", allocationSize = 1)
    private int personId;

    @Column(name = "name", nullable = false)
    @NotEmpty(message = "O campo name é obrigatório")
    @Size(min = 3, max = 100)
    private String name;

    @Column(name = "sex")
    private String sex;

    @Column(name = "age")
    private Integer age;

 	@Temporal(TemporalType.DATE)
    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "place_of_birth")
    private String placeOfBirth;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "languages")
    private String languages;

    @Column(name = "image_url")
    private String imageUrl;
    
    @CreationTimestamp
 	@Temporal(TemporalType.TIMESTAMP)
 	@Column(name="created_at", nullable=false)
     private Date createdAt;
 	
 	@Column(name="update_at")
     private Date updateAt;
 	
 	@Column(name="delete_at")
     private Date deleteAt;
}

