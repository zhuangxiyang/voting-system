package com.vote.server.dto.req;

import com.vote.common.dto.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 候选人记录
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
@ApiModel(value = "CandidateRecordReqDTO", description = "候选人记录")
public class CandidateRecordReqDTO extends Entity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 投票编码
     */
    @ApiModelProperty(value = "投票编码")
    private String actNo;
    /**
     * 候选人名称
     */
    @ApiModelProperty(value = "候选人名称")
    private String candidateName;
    /**
     * 候选人身份证
     */
    @ApiModelProperty(value = "候选人身份证")
    private String candidateIdCard;
    /**
     * 候选人邮箱
     */
    @ApiModelProperty(value = "候选人邮箱")
    private String candidateMail;

}
