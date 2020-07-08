package com.j.openproject.web.requset;

import javax.validation.constraints.NotBlank;

import com.j.openproject.base.CommonRq;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Joyuce
 * @Type WeixinRq
 * @Desc 微信登录
 * @date 2019年11月28日
 * @Version V1.0
 */
@Getter
@Setter
@ApiModel(value = "微信登录")
public class WeixinRq extends CommonRq {

    private static final long serialVersionUID = -2225077284092140171L;

    @NotBlank(message = "wxCode不为空字符串")
    @ApiModelProperty("微信授权码")
    private String wxCode;

}
