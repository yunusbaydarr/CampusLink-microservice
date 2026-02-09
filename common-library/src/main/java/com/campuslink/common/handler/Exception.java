package com.campuslink.common.handler;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Exception<E> {
    private E message;
    private String path;
    private String hostname;
    private String ip;
    private Date createTime;

}
