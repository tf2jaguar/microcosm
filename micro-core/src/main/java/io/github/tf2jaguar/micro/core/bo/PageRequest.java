package io.github.tf2jaguar.micro.core.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

/**
 * 分页请求类
 *
 * @author zhangguodong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

    @Min(0)
    @ApiModelProperty(value = "页码， 默认 1", example = "1")
    private Integer page = 1;
    @Min(1)
    @ApiModelProperty(value = "单页大小，默认 10", example = "10")
    private Integer pageSize = 10;
}