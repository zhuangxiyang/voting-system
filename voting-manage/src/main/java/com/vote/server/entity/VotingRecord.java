package com.vote.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vote.common.dto.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 投票记录
 * </p>
 *
 * @author vote
 * @since 2022-07-02
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("bu_voting_record")
@ApiModel(value = "VotingRecord", description = "投票记录")
@AllArgsConstructor
public class VotingRecord extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 投票编码
     */
    @ApiModelProperty(value = "投票编码")
    @TableField(value = "act_no", condition = LIKE)
    private String actNo;

    /**
     * 候选人身份证
     */
    @ApiModelProperty(value = "候选人身份证")
    @TableField(value = "candidate_id_card", condition = LIKE)
    private String candidateIdCard;

    /**
     * 候选人名称
     */
    @ApiModelProperty(value = "候选人名称")
    @TableField(value = "candidate_name", condition = LIKE)
    private String candidateName;

    /**
     * 投票人名称
     */
    @ApiModelProperty(value = "投票人名称")
    @TableField(value = "voter_name", condition = LIKE)
    private String voterName;

    /**
     * 投票人身份证
     */
    @ApiModelProperty(value = "投票人身份证")
    @TableField(value = "voter_id_card", condition = LIKE)
    private String voterIdCard;

    /**
     * 投票人邮箱
     */
    @ApiModelProperty(value = "投票人邮箱")
    @TableField(value = "voter_mail", condition = LIKE)
    private String voterMail;


    @Builder
    public VotingRecord(Long id, String createUser, String updateUser, Date createTime, Date updateTime, Integer delFlag, 
                    String actNo, String candidateIdCard, String candidateName, String voterName, String voterIdCard, String voterMail) {
        this.id = id;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.delFlag = delFlag;
        this.actNo = actNo;
        this.candidateIdCard = candidateIdCard;
        this.candidateName = candidateName;
        this.voterName = voterName;
        this.voterIdCard = voterIdCard;
        this.voterMail = voterMail;
    }

}
