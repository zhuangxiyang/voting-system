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
 * 投票
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
@TableName("bu_activity")
@ApiModel(value = "Activity", description = "投票")
@AllArgsConstructor
public class Activity extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 投票编码
     */
    @ApiModelProperty(value = "投票编码")
    @TableField(value = "act_no", condition = LIKE)
    private String actNo;

    /**
     * 投票名称
     */
    @ApiModelProperty(value = "投票名称")
    @TableField(value = "act_name", condition = LIKE)
    private String actName;

    /**
     * 投票开始时间
     */
    @ApiModelProperty(value = "投票开始时间")
    @TableField("act_start_time")
    private Date actStartTime;

    /**
     * 投票结束时间
     */
    @ApiModelProperty(value = "投票结束时间")
    @TableField("act_end_time")
    private Date actEndTime;

    /**
     * 投票状态
     */
    @ApiModelProperty(value = "投票状态")
    @TableField(value = "act_status", condition = LIKE)
    private String actStatus;

    /**
     * 获选人
     */
    @ApiModelProperty(value = "获选人")
    @TableField(value = "winner", condition = LIKE)
    private String winner;

    /**
     * 获选人票数
     */
    @ApiModelProperty(value = "获选人票数")
    @TableField(value = "winner_num", condition = LIKE)
    private String winnerNum;

    /**
     * 获选人身份证
     */
    @ApiModelProperty(value = "获选人身份证")
    @TableField(value = "winner_id_card", condition = LIKE)
    private String winnerIdCard;

    @Builder
    public Activity(Long id, String createUser, String updateUser, Date createTime, Date updateTime, Integer delFlag, 
                    String actNo, String actName, Date actStartTime, Date actEndTime, String actStatus, 
                    String winner, String winnerNum, String winnerIdCard) {
        this.id = id;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.delFlag = delFlag;
        this.actNo = actNo;
        this.actName = actName;
        this.actStartTime = actStartTime;
        this.actEndTime = actEndTime;
        this.actStatus = actStatus;
        this.winner = winner;
        this.winnerNum = winnerNum;
        this.winnerIdCard = winnerIdCard;
    }

}
