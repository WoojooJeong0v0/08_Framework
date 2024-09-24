package com.kh.test.customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.test.customer.dto.Customer;
import com.kh.test.customer.mapper.CustomerMapper;

@Service
public class CustomerServiceImpl implements CustomerService {
 
  @Autowired 
  private CustomerMapper mapper;

  @Override
  public int updateCustomer(Customer customer) {
		return mapper.updateCustomer(customer);
	}

}
