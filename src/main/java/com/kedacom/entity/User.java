package com.kedacom.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 朱朝阳
 * @date 2019/9/18 17:06
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trans_user")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Basic
    private String name;

    @Basic
    private String password;

    private Date startTime;

    private Date endTime;

}
