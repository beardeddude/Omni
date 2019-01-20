package com.omni.omni.repository;


import com.omni.omni.datamodel.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserRepository extends ElasticsearchRepository<User, String> {


}
