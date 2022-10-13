package com.arunachala.um.response;

import java.io.Serializable;
import java.util.List;

import com.arunachala.um.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto implements Serializable{

	private static final long serialVersionUID = 3123713080052838756L;

	private List<UserResponse> userResponseList;
	private long totalElements;
	private int totalPages;
	private int currentSliceNumber;
	private int currentSliceSize;
	private boolean hasPrevious;
	private boolean hasNext;
	private boolean isFirst;
	private boolean isLast;

}
