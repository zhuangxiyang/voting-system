package com.vote.server.dto.resp;

import com.vote.common.dto.BaseRespDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "VotingRecordRespDTO", description = "投票记录")
public class VotingRecordRespDTO extends BaseRespDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 投票编码
     */
    @ApiModelProperty(value = "投票编码")
    private String actNo;
    /**
     * 候选人身份证
     */
    @ApiModelProperty(value = "候选人身份证")
    private String candidateIdCard;
    /**
     * 候选人名称
     */
    @ApiModelProperty(value = "候选人名称")
    private String candidateName;
    /**
     * 投票人名称
     */
    @ApiModelProperty(value = "投票人名称")
    private String voterName;
    /**
     * 投票人身份证
     */
    @ApiModelProperty(value = "投票人身份证")
    private String voterIdCard;
    /**
     * 投票人邮箱
     */
    @ApiModelProperty(value = "投票人邮箱")
    private String voterMail;
    /**
     * 票数
     */
    @ApiModelProperty(value = "票数")
    private Integer num;
    /**
     * 候选人是否获选
     */
    @ApiModelProperty(value = "候选人是否获选")
    private Boolean isWin;
    /**
     * 候选人邮箱
     */
    @ApiModelProperty(value = "候选人邮箱")
    private String candidateMail;

}
