package com.gayuh.personalproject.dto;

import lombok.Data;

@Data
public class WebResponse<T, K> {
    private K message;
    private T data;
    private Integer status;
}
