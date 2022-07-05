package com.vote.server.dto.resp;

import com.vote.common.dto.BaseRespDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
@ApiModel(value = "ActivityRespDTO", description = "投票")
public class ActivityRespDTO extends BaseRespDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 投票编码
     */
    @ApiModelProperty(value = "投票编码")
    private String actNo;
    /**
     * 投票名称
     */
    @ApiModelProperty(value = "投票名称")
    private String actName;
    /**
     * 投票开始时间
     */
    @ApiModelProperty(value = "投票开始时间")
    private Date actStartTime;
    /**
     * 投票结束时间
     */
    @ApiModelProperty(value = "投票结束时间")
    private Date actEndTime;
    /**
     * 投票状态
     */
    @ApiModelProperty(value = "投票状态")
    private String actStatus;
    /**
     * 获选人
     */
    @ApiModelProperty(value = "获选人")
    private String winner;
    /**
     * 获选人票数
     */
    @ApiModelProperty(value = "获选人票数")
    private String winnerNum;

    /**
     * 获选人身份证
     */
    @ApiModelProperty(value = "获选人身份证")
    private String winnerIdCard;

}
