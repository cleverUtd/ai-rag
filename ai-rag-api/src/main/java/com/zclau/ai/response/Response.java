package com.zclau.ai.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO
 *
 * @author 刘梓聪
 * @email liuzicong@aulton.com
 * @date 2025/6/26 15:31
 * @Copyright Copyright(c) aulton Inc.AllRightsReserved.
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T>  {

    private String code;
    private String info;
    private T data;

}
