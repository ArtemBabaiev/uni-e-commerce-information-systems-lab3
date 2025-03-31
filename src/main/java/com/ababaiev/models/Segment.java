package com.ababaiev.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Segment {
    private String name;
    private String value;
}
