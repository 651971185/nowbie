package com.mengge.gatewayfunction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 测试
 * </p>
 *
 * @author zxw
 * @since 2021-11-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class QueryData extends Model<QueryData> {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 路径
     */
    private String path;

    /**
     * body体
     */
    private String bodyData;

    /**
     * 请求header
     */
    private String reqHeader;

    /**
     * 路径参数
     */
    private String pathParameters;

    /**
     * 请求参数
     */
    private String queryParameters;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 创建人
     */
    private Long creator;

    /**
     * 修改人
     */
    private Long modifier;

    /**
     * 任务状态，0未开始1开始2完成
     */
    private Integer state;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
