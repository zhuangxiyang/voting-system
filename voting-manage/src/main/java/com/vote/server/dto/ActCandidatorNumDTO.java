package com.vote.server.dto;

import com.vote.common.dto.BaseRespDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ActCandidatorNumDTO", description = "投票候选人票数")
public class ActCandidatorNumDTO extends BaseRespDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 候选人身份证
     */
    @ApiModelProperty(value = "候选人身份证")
    private String candidateIdCard;


    /**
     * 票数
     */
    @ApiModelProperty(value = "票数")
    private Integer num;

}
