package com.synassignment01.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO representing a Cloudinary view response with userAssociatedImageurls.
 */
@Getter
@Setter
@AllArgsConstructor
public class CloudinaryViewResponse {

    List<String> userAssociatedImageurls;
}
