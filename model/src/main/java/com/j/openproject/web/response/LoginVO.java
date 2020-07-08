package com.j.openproject.web.response;

import com.j.openproject.base.ToString;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Joyuce
 * @Type LoginVo
 * @Desc 登录返回对象
 * @date 2019年11月28日
 * @Version V1.0
 */
@ApiModel("登录返回对象")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginVO extends ToString {

    @ApiModelProperty("token")
    private String token;


}
