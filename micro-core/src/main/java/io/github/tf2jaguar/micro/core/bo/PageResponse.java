package io.github.tf2jaguar.micro.core.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 分页响应类
 *
 * @author zhangguodong
 */
@Data
public class PageResponse<T> {

    @ApiModelProperty(value = "页码", required = true)
    private Integer page;
    @ApiModelProperty(value = "单页大小", required = true)
    private Integer pageSize;
    @ApiModelProperty(value = "总条数", required = true)
    private Long totalCount;
    @ApiModelProperty(value = "总页数", required = true)
    private Integer pageCount;
    @ApiModelProperty("内容")
    private List<T> itemList;

    public PageResponse() {
        super();
    }

    public PageResponse(int totalCount, List<T> itemList, PageRequest page) {
        this((long) totalCount, itemList, page);
    }

    public PageResponse(long totalCount, List<T> itemList, PageRequest page) {
        this.page = page.getPage();
        this.pageSize = page.getPageSize();
        this.totalCount = totalCount;
        this.pageCount = Long.valueOf(page.getPageSize() <= 0 ? 0 :
                (totalCount % page.getPageSize() == 0) ? totalCount / page.getPageSize() : totalCount / page.getPageSize() + 1).intValue();
        this.itemList = itemList;
    }

}