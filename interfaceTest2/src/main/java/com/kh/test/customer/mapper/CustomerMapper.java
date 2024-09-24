package com.kh.test.customer.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.kh.test.customer.dto.Customer;

@Mapper
public interface CustomerMapper {

  int updateCustomer(Customer customer);
}
