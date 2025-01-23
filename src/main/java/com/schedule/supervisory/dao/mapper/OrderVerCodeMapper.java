package com.schedule.supervisory.dao.mapper;

import com.schedule.supervisory.dto.OrderVerCodeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

@Mapper
public interface OrderVerCodeMapper {
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OrderVerCodeDTO verificationCode);

    OrderVerCodeDTO selectByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    void markAsExpired(@Param("id") Long id);
    @Update("UPDATE order_vercode SET expired = '1' WHERE create_time < #{expirationThreshold}")
    void updateVerCodeExpiration(@Param("expirationThreshold") Date expirationThreshold);
}
